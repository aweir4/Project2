/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
/**
 *
 * @author austinweir
 */

@Named(value="discountdropdown")
@ManagedBean
@SessionScoped
public class DiscountDropdown implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    //private ArrayList<Discount> discounts;
    private Map<String, Discount> discountObjects;
    private ArrayList<String> discounts;
    private String discount;
    
    @PostConstruct
    public void init(){
        Connection connection = dbConnect.getConnection();
        
        if (connection == null) {
            System.out.println("\nThere was an issue attempting to load the discounts from the database!!!");
            return;
        }
        
        // Try and load all categories from the database upon session start
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Discounts ORDER BY discountId ASC");
            ResultSet results = preparedStatement.executeQuery();
            discountObjects = new HashMap<>();
            discounts = new ArrayList<>();
            
            while(results.next()) {
                //categories.add(results.getString("category"));
                int id = results.getInt("discountId");
                double percentage = results.getDouble("discountPercentage");
                Date startDate = results.getDate("startDate");
                Date endDate = results.getDate("endDate");
                
                System.out.println("Current Discount: " + String.valueOf(id));
                
                Discount d = new Discount(id, percentage, startDate, endDate);
                String key = String.valueOf(id) + ": " + String.valueOf(percentage) + "%";
                System.out.println("Current Key: " + key);
                
                discountObjects.put(key, d);
                discounts.add(key);
                
                
                //discounts.add(new Discount(id, percentage, startDate, endDate));
            }
        } catch(Exception e) {
            System.out.println("\nErrors occurred while attempting to query database for discounts!!!");
            return;
        }

    }
    
    public ArrayList<String> getDiscounts() {
        return discounts;
    }
    
    public String getDiscount() {
        return discount;
    }
    
    public void setDiscount(String discount) {
        this.discount = discount;
        System.out.println("Discount KEY: " + String.valueOf(discount));
    }
    
    public Map<String, Discount> getDiscountObjects() {
        return discountObjects;
    }
}

