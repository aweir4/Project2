/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*import java.io.Serializable;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
/**
 *
 * @author austinweir
 */

/*@Named(value="iteminventory")
@ManagedBean
//@ManagedBean(name = "iteminventory", eager = true)
@SessionScoped
public class ItemInventory implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private String absoluteWebPath;
    private ArrayList<Item> items;
    private Item selectedItem;
    
    @ManagedProperty(value="#{additemview}")
    private AddItemView addItemView;
    
    public class Item implements Serializable {
        public int itemId;
        public String itemName;
        public String itemDescription;
        public String itemCategory;
        public double itemDiscountPercent;
        public Path itemImagePath;
        public StreamedContent itemImage;
        public double itemPrice;
        public int itemStock;
        public int itemDiscountId;
        public String itemImageName;
        
        public Item(int id, String name, String description, String category, double discount, int discountId, Path path, String imageName, double price, int stock) throws FileNotFoundException {
            itemId = id;
            itemName = name;
            itemDescription = description;
            itemCategory = category;
            itemDiscountPercent = discount;
            itemImagePath = path;
            itemPrice = price;
            itemStock = stock;
            itemDiscountId = discountId;
            itemImageName = imageName;
            
            File file = path.toFile();
            
            if (file.exists() && !file.isDirectory()) {
                itemImage = new DefaultStreamedContent(new FileInputStream(file), "image/jpeg");
            }
            //itemImage = new DefaultStreamedContent(new FileInputStream(path.toFile()), "image/jpeg");
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
        public StreamedContent getItemImage() {
            return itemImage;
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
        //absoluteWebPath = "/build/web/";
        
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
                
                System.out.println("Item Path: " + path.toString());
                
                // If there is the applied discount is still running
                if ((now.compareTo(discountStart) >= 0) && (now.compareTo(discountEnd) < 0)) {
                    discount = results.getDouble("discountPercentage");
                }
                
                Item item = new Item(id, name, description, category, discount, discountId, path, results.getString("itemImage"), price, stock);
                items.add(item);
            }
        } catch(Exception e) {
            System.out.println("\nErrors occurred while attempting to query database for items!!!");
            System.out.println(e.toString());
            return;
        }

    }
    
    public ArrayList<Item> getItems() {
        return items;
    }
    
    public Item getSelectedItem() {
        return selectedItem;
    }
    
    public void setSelectedItem(Item item) throws IOException {
        this.selectedItem = selectedItem;
    }
    
    public void onRowSelect(SelectEvent event) throws IOException {
        //FacesMessage msg = new FacesMessage("Item Selected", ((Item) event.getObject()).itemName);
        //FacesContext.getCurrentInstance().addMessage(null, msg);
        
        //Item item = ((Item) event.getObject());
        
        addItemView.setItemId(selectedItem.itemId);
        addItemView.setItemName(selectedItem.itemName);
        addItemView.setItemDescription(selectedItem.itemDescription);
        addItemView.setItemDiscount(selectedItem.itemDiscountId);
        addItemView.setItemImage(selectedItem.itemImageName);
        addItemView.setItemPrice(selectedItem.itemPrice);
        addItemView.setItemStock(selectedItem.itemStock);
        FacesContext.getCurrentInstance().getExternalContext().redirect("addItem.xhtml?itemId=" + selectedItem.itemId);
        //RequestContext.getCurrentInstance().update(":form:itemDetail");
        //RequestContext.getCurrentInstance().execute("PF('itemDialog').show()");
    } 
}*/
