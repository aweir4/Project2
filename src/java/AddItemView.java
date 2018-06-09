/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.annotation.ManagedBean;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 *
 * @author austinweir
 */
@Named(value = "additemview")
@ManagedBean
@ViewScoped
public class AddItemView implements Serializable {
    private int itemId;
    private String itemName;
    private String itemDescription;
    private int itemDiscount = 0;
    private String itemImage;
    private double itemPrice = 0;
    private int itemStock = 0;
    private DBConnect dbConnect = new DBConnect();
    
    public AddItemView() {
        
    }
    public AddItemView(int id, String name, String description, int discount, String image, double price, int stock) {
        itemId = id;
        itemName = name;
        itemDescription = description;
        itemDiscount = discount;
        itemImage = image;
        itemPrice = price;
        itemStock = stock;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int id) {
        itemId = id;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String name) {
        itemName = name;
    }
    
    public String getItemDescription() {
        return itemDescription;
    }
    
    public void setItemDescription(String description) {
        itemDescription = description;
    }
    
    public int getItemDiscount() {
        return itemDiscount;
    }
    
    public void setItemDiscount(int discount) {
        itemDiscount = discount;
    }
    
    public String getItemImage() {
        return itemImage;
    }
    
    public void setItemImage(String image) {
        itemImage = image;
    }
    
    public double getItemPrice() {
        return itemPrice;
    }
    
    public void setItemPrice(double price) {
        itemPrice = price;
    }
    
    public int getItemStock() {
        return itemStock;
    }
    
    public void setItemStock(int stock) {
        itemStock = stock;
    }
    
    public String addNewItem() throws SQLException, ParseException, IOException  {
        Connection connection = dbConnect.getConnection();
        
        if (connection == null) {
            throw new SQLException("Can't get database connection");
        }
        connection.setAutoCommit(false);
        
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        CategoryDropdown dropdown = (CategoryDropdown) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "categorydropdown");
        ItemImage img = (ItemImage) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "itemimage");        
        
        if (dropdown != null) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Items(itemName, itemDescription, itemCategory, itemDiscount, itemImage, itemPrice, itemStock) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setString(1, itemName);
            preparedStatement.setString(2, itemDescription);
            preparedStatement.setString(3, dropdown.getCategory());
            preparedStatement.setInt(4, itemDiscount);
            preparedStatement.setString(5, itemImage);
            preparedStatement.setDouble(6, itemPrice);
            preparedStatement.setInt(7, itemStock);
        
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
            connection.close();
        
            // Save the image to local disk
            img.saveToDisk();
            return "addItem";
        }
        
        return "failure";
    }
}
