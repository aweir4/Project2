/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

public class Item implements Serializable {
    public int id;
    public String name;
    public String description;
    public String category;
    public int discountId;
    public double discount;
    public String imageName;
    public Path imagePath;
    public StreamedContent image;
    public double price;
    public int stock;
    
    public Item() {}
    
    public Item(int id, 
            String name, 
            String description, 
            String category, 
            int discountId, 
            double discount,
            String imageName,
            StreamedContent image, 
            double price,
            int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.discountId = discountId;
        this.discount = discount;
        this.imageName = imageName;
        this.image = image;
        this.price = price;
        this.stock = stock;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getDiscountId() {
        return discountId;
    }
    
    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    public void setImagePath(Path imagePath) {
        this.imagePath = imagePath;
    }
    
    public StreamedContent getImage() throws IOException {
        if (imagePath != null) { 
            File imageFile = imagePath.toFile();
        
            if (imageFile.isFile()) {
                return new DefaultStreamedContent(new FileInputStream(imageFile));
            }            
        }   

        return null;       
    }
    
    public void setImage(StreamedContent image) {
        this.image = image;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
}
