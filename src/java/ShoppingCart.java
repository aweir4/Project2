import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.el.ELContext;
import org.primefaces.PrimeFaces;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 */

@Named(value="shoppingcart")
@ManagedBean
@SessionScoped
public class ShoppingCart implements Serializable {
    private List<Item> items = new ArrayList<>();
    private Map<Integer, Integer> itemCounts = new HashMap<>();
    private DBConnect dbConnect = new DBConnect();
    private double cartTotal = 0;
    private boolean disableAdd=false;
    
    public List<Item> getItems() {
        return items;
    }
    
    public boolean getDisableAdd(){
        return disableAdd;
    }
    
    public void addItem(Item item) {
        // Adding item for first time
        if (!itemCounts.containsKey(item.id)) {
            items.add(item);
            itemCounts.put(item.id, 1);
        }
        // adding item
        else {
            Integer itemCount = itemCounts.get(item.id);
            if(itemCount == item.stock - 1) {
                disableAdd=true;
                //return;
            } 
            itemCounts.replace(item.id, itemCount + 1);
        }
        cartTotal += item.getPurchasePrice();
    }
    
    public void removeItem(Item item) {
        Integer itemCount = itemCounts.get(item.id);
        
        if (itemCount != null) {
            if (itemCount == 1) {
                itemCounts.remove(item.id);
                items.remove(item);
            }
            else {
                itemCounts.replace(item.id, itemCount - 1);
            }
            if(itemCounts.get(item.id) < item.stock) {
                disableAdd=false;
            }
            cartTotal -= item.getPurchasePrice();
        }
    }
    
    public double getItemTotal(Item item) {
        Integer itemCount = itemCounts.get(item.id);
        
        return item.getPurchasePrice() * itemCount;
    }
    
    public int getCount(Item item) {
        return itemCounts.get(item.id);
    }
    
    public double getCartTotal() {
        return cartTotal;
    }
    
    public void createOrder() throws SQLException {
        Connection con = dbConnect.getConnection();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "login");
        ItemInventoryView itemInventory = (ItemInventoryView) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "iteminventoryview");
        boolean orderSuccess = false;
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        try {
            
        } catch(Exception e) {
            System.out.println("Error attempting to add order to the database!!!");
            System.out.println(e.toString());
            e.printStackTrace();
            orderSuccess = false;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Order Error!", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
            PrimeFaces.current().ajax().addCallbackParam("orderSuccess", orderSuccess); 
            return;
        }
        
        String query = "INSERT INTO Orders(saleDate, purchaser, shippingAddress) "
                + "VALUES(?,?,?) RETURNING orderId";
        
        PreparedStatement preparedStatement = con.prepareStatement(query);
        // Current Date
        preparedStatement.setDate(1, new java.sql.Date((new Date()).getTime()));
        preparedStatement.setString(2, login.getCurrentCustomer().login);
        preparedStatement.setString(3, login.getCurrentCustomer().address);
        ResultSet results = preparedStatement.executeQuery();
        
        results.next();
        int orderId = results.getInt(1);
        int ordinal = 0;
        
        for (Item item : items) {
            query = "INSERT INTO OrderItems(orderId, ordinal, item) "
                    + "VALUES(?,?,?)";
            
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, ordinal);
            preparedStatement.setInt(3, item.id);
            
            preparedStatement.execute();
            
            
            query = "UPDATE Items SET itemStock = itemStock - 1 WHERE itemId = ?";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, item.id);
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows == 0) {
                orderSuccess = false;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Order Error!", "Error attempting to update itemInventory!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                PrimeFaces.current().ajax().addCallbackParam("orderSuccess", orderSuccess); 
                return;                
            }

            ordinal += 1;
        }
        
        query = "INSERT INTO Bills(orderId, total, paid) "
                + "VALUES(?,?,?)";
        preparedStatement = con.prepareStatement(query);
        preparedStatement.setInt(1, orderId);
        preparedStatement.setDouble(2, cartTotal);
        preparedStatement.setBoolean(3, true);
        
        preparedStatement.execute();
        
        orderSuccess = true;
        
        preparedStatement.close();
        con.commit();
        con.close();
        
        itemInventory.reInit();
        items.clear();
        itemCounts.clear();
        
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Completed!", "Your order has been successfully placed");
        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("orderSuccess", orderSuccess);
    } 
}
