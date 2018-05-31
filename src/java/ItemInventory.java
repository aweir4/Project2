/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
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

@Named(value="iteminventory")
@ManagedBean
//@ManagedBean(name = "iteminventory", eager = true)
@SessionScoped
public class ItemInventory implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private String absoluteWebPath;
    private ArrayList<Item> items;
    private Item item;
    
    public class Item implements Serializable {
        public int itemId;
        public String itemName;
        public String itemDescription;
        public String itemCategory;
        public double itemDiscountPercent;
        public Path itemImagePath;
        public double itemPrice;
        public int itemStock;
        
        public Item(int id, String name, String description, String category, double discount, Path path, double price, int stock) {
            itemId = id;
            itemName = name;
            itemDescription = description;
            itemCategory = category;
            itemDiscountPercent = discount;
            itemImagePath = path;
            itemPrice = price;
            itemStock = stock;
        }
        
        public String getItemId() {
            return String.valueOf(itemId);
        }
        public String getItemName() {
            return itemName;
        }
        public String getItemDescription() {
            return itemDescription;
        }
        public String getItemCategory() {
            return itemCategory;
        }
        public String getItemDiscountPercent() {
            return String.valueOf(itemDiscountPercent) + "%";
        }
        public String getItemImagePath() {
            return itemImagePath.toString();
        } 
        public String getItemPrice() {
            return "$" + String.valueOf(itemPrice);
        }
        public String getItemStock() {
            return String.valueOf(itemStock);
        }
    }
    
    @PostConstruct
    public void init(){
        Connection connection = dbConnect.getConnection();
        absoluteWebPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        
        if (connection == null) {
            System.out.println("\nThere was an issue attempting to load the items from the database!!!");
            return;
        }
        
        // Try and load all categories from the database upon session start
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Items INNER JOIN Discounts ON Items.itemDiscount = Discounts.discountId ORDER BY itemName ASC");
            ResultSet results = preparedStatement.executeQuery();
            items = new ArrayList<Item>();
            
            while(results.next()) {
                int id = results.getInt("itemId");
                String name = results.getString("itemName");
                String description = results.getString("itemDescription");
                String category = results.getString("itemCategory");
                int discountId = results.getInt("itemDiscount");
                Date discountStart = results.getDate("startDate");
                Date discountEnd = results.getDate("endDate");
                Date now = new Date();
                double discount = 0;
                String pathString = absoluteWebPath + results.getString("itemImage");
                Path path = Paths.get(pathString);
                double price = results.getDouble("itemPrice");
                int stock = results.getInt("itemStock");
                
                // If there is the applied discount is still running
                if ((now.compareTo(discountStart) >= 0) && (now.compareTo(discountEnd) < 0)) {
                    discount = results.getDouble("discountPercentage");
                }
                
                Item item = new Item(id, name, description, category, discount, path, price, stock);
                items.add(item);
            }
        } catch(Exception e) {
            System.out.println("\nErrors occurred while attempting to query database for items!!!");
            return;
        }

    }
    
    public ArrayList<Item> getItems() {
        return items;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
}
