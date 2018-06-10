/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 * @see https://www.primefaces.org/showcase/ui/data/datatable/lazy.xhtml
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@Named(value = "itemservice")
@ManagedBean
@ApplicationScoped
public class ItemService {
    private List<Item> items;
    private DBConnect dbConnect = new DBConnect();
    private String absoluteWebPath;
    
    //@PostConstruct
    public List<Item> init() {
        Connection connection = dbConnect.getConnection();
 
        if (connection == null) {
            System.out.println("\nThere was an issue attempting to load the items from the database!!!");
            return null;
        }        
        
        absoluteWebPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");    
        return fetchItems(connection);
    }    
    
    public List<Item> fetchItems(Connection connection) {
        
        String query = "SELECT * FROM Items INNER JOIN Discounts ON Items.itemDiscount = Discounts.discountId ORDER BY itemName ASC";
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet results = preparedStatement.executeQuery();
            items = new ArrayList<>();
            
            while(results.next()) {
                int id = results.getInt("itemId");
                String name = results.getString("itemName");
                String description = results.getString("itemDescription");
                String category = results.getString("itemCategory");
                int discountId = results.getInt("itemDiscount");
                double discount = getDiscount(results);
                String imageName = results.getString("itemImage");
                Path imagePath = Paths.get(absoluteWebPath + imageName);
                StreamedContent image = getImage(imagePath);
                //StreamedContent image = getImage(imageName);
                double price = results.getDouble("itemPrice");
                int stock = results.getInt("itemStock");
                
                Item item = new Item(id, name, description, category, discountId, discount, imageName, image, price, stock); 
                item.setImagePath(imagePath);
                //item.setImagePath(absoluteWebPath + imageName);
                items.add(item);
            }
            
            return items;
            
        } catch (Exception e) {
            System.out.println("There has been an error attempting to get the list of items from the database!");
            System.out.println(e.toString());
            e.printStackTrace();
            return null;
        }    
    }  
    
    public double getDiscount(ResultSet results) throws SQLException {
        Date startDate = results.getDate("startDate");
        Date endDate = results.getDate("endDate");
        Date now = new Date();
        
        // Check to see if the discount is still active
        if ((now.compareTo(startDate) >= 0) && (now.compareTo(endDate) < 0)) {
            return results.getDouble("discountPercentage");
        }
        
        return 0;
    }
    
    // Try and get the image associated with the item
    public StreamedContent getImage(Path imagePath) throws FileNotFoundException {
        File imageFile = imagePath.toFile();
        String missingString = "File either does not exist, or is a directory at path: " + imagePath.toString();
        
        if (imageFile.isFile()) {
            //return (UploadedFile) imageFile;
            return new DefaultStreamedContent(new FileInputStream(imageFile));
        } 
        
        System.out.println(missingString);
        return null;
    }
    
    public boolean itemAlreadyExists(Item newItem) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        String query = "SELECT * FROM Items WHERE itemName = '" + newItem.name + "'"
                + " AND itemCategory = '" + newItem.category + "'";

        PreparedStatement ps = con.prepareStatement(query);

        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }  
    
    public int addNewItem(Item newItem) throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        String query = "INSERT INTO Items(itemName, itemDescription, itemCategory, itemDiscount, itemImage, itemPrice, itemStock)"
                + "VALUES(?,?,?,?,?,?,?) RETURNING itemId";

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
        return results.getInt(1);
        //Util.invalidateUserSession();
        //return true;       
    }
    
    public boolean updateItem(Item item) throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);        
 
        System.out.println("ATTEMPTING TO UPDATE!!!");
        
        Statement statement = con.createStatement();
        String query = "UPDATE Items SET itemName = ?, itemDescription = ?, itemCategory = ?, " 
                + "itemDiscount = ?, itemImage = ?, itemPrice = ?, itemStock = ? WHERE itemId = ?;";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        
        preparedStatement.setString(1, item.name);
        preparedStatement.setString(2, item.description);
        preparedStatement.setString(3, item.category);
        preparedStatement.setInt(4, item.discountId);
        preparedStatement.setString(5, item.imageName);
        preparedStatement.setDouble(6, item.price);
        preparedStatement.setInt(7, item.stock);   
        preparedStatement.setInt(8, item.id);
        
        int affectedRows = preparedStatement.executeUpdate();
        preparedStatement.close();
        con.commit();
        con.close();
        
        System.out.println("Affected Rows!!!: " + String.valueOf(affectedRows));
                
        return affectedRows > 0;
    }
    
    public List<Item> getItems() {
        return items;
    }
}
