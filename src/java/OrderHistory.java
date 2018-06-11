
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;


@Named(value = "orderHistory")
@SessionScoped
@ManagedBean
public class OrderHistory implements Serializable {

    private DBConnect dbConnect = new DBConnect();
    private List<Order> orders;
    
    private String customerLogin;
    
    private int currentOrderId;
    private List<Item> items;
    
 
    @PostConstruct
    public void init() {
        System.out.println("getting order history");
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "login");
        try {
            this.customerLogin = login.getCurrentCustomer().getLogin();
            System.out.print("Logged in customer is " + this.customerLogin);
        }
        catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Are you a customer?");
            this.customerLogin = "";
        }
        
        try {
            this.orders = this.getOrdersService();
        }
        catch (SQLException ex) {
            System.out.println("Unable to get order history");
        }
    }
     
    public List<Order> getOrders() {
        return this.orders;
    }
 
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    public List<Item> getItems() {
        return this.items;
    }
 
    public void setItems(List<Item> items) {
        this.items = items;
    }

    
    public void setCustomerLogin(String login) {
        this.customerLogin = login;
    }
    
    public int getCurrentOrderId() {
        return this.currentOrderId;
    }
    
    public void setCurrentOrderId(int orderId) {
        this.currentOrderId = orderId;
    }
    
    public void showDetails(Order o) throws SQLException {
        System.out.println(o.getOrderId());
        this.currentOrderId = o.getOrderId();
        List<Item> list = new ArrayList<Item>();
        
        if (this.customerLogin.equals("")) {
            System.out.println("Customer log in not set");
            return;
        }
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from orderitems JOIN Items on orderitems.item = Items.itemid where orderid = ?");
        ps.setInt(1, this.currentOrderId);
        ResultSet result = ps.executeQuery();

        while (result.next()) {
              Item i = new Item();
              i.setId(result.getInt("itemId"));
              i.setName(result.getString("itemName"));
              i.setCategory(result.getString("itemCategory"));
              i.setPrice(result.getDouble("itemPrice"));
              list.add(i);
        }
        result.close();
        con.close();
        this.items = list;
        PrimeFaces.current().executeScript("PF('dlg').show();");
    }
    
    public List<Order> getOrdersService() throws SQLException {
        List<Order> list = new ArrayList<Order>();
        
        if (this.customerLogin.equals("")) {
            System.out.println("Customer log in not set");
            return list;
        }
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from orders where purchaser = ?");
        ps.setString(1, this.customerLogin);
        ResultSet result = ps.executeQuery();

        while (result.next()) {
            Order o = new Order();
            o.setOrderId(result.getInt("orderId"));
            o.setSaleDate(result.getDate("saleDate"));
            list.add(o);
        }
        result.close();
        con.close();
        
        return list;
    }
    
}
