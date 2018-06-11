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
    
    public List<Item> getItems() {
        return items;
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
        
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        String query = "INSERT INTO Orders(saleDate, purchaser, shippingAddress)"
                + "VALUES(?,?,?) RETURNING orderId";
        
        PreparedStatement preparedStatement = con.prepareStatement(query);
        // Current Date
        preparedStatement.setDate(1, new java.sql.Date((new Date()).getTime()));
        preparedStatement.setString(2, query);
        
        
        
        
        
        
        
        
/*
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, newItem.name);
        preparedStatement.setString(2, newItem.description);
        preparedStatement.setString(3, newItem.category);
        preparedStatement.setInt(4, newItem.discountId);
        preparedStatement.setString(5, newItem.imageName);
        preparedStatement.setDouble(6, newItem.price);
        preparedStatement.setInt(7, newItem.stock);
        //preparedStatement.executeUpdate();
        ResultSet results = preparedStatement.executeQuery();
        statement.close();
        con.commit();
        con.close();
        
        results.next();
        return results.getInt(1);        */
    }
}
