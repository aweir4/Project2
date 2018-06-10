/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@Named(value = "iteminventoryview")
@ManagedBean
@ViewScoped
public class ItemInventoryView implements Serializable {
    private LazyDataModel<Item> lazyModel;
    private Item selectedItem;
    private Item newItem = new Item();
    private String absoluteWebPath;
    
    @ManagedProperty("#{itemservice}")
    private ItemService service;
    
    @ManagedProperty("#{categorydropdown}")
    private CategoryDropdown categoryDropdown;
    
    //@ManagedProperty("#{discountdropdown}")
    //private DiscountDropdown dropdown;
    
    @PostConstruct
    public void init() {
        lazyModel = new LazyItemDataModel(service.init());
        absoluteWebPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        //newItem = new Item();
    }
    
    public LazyDataModel<Item> getLazyModel() {
        return lazyModel;
    }
    
    public Item getSelectedItem() {
        return selectedItem;
    }
    
    public void setSelectedItem(Item item) {
        this.selectedItem = item;
    }
    
    public Item getNewItem() {
        return newItem;
    }
    
    public void setNewItem(Item newItem) {
        this.newItem = newItem;
    }
    
    public void setService(ItemService service) {
        this.service = service;
    }
    
    public CategoryDropdown getCategoryDropdown() {
        return categoryDropdown;
    }
    
    public void setCategoryDropdown(CategoryDropdown categoryDropdown) {
        this.categoryDropdown = categoryDropdown;
    }
    
    public void onRowSelect(SelectEvent event) {
        Item item = (Item) event.getObject();
        String msgString = "Item ID: " + String.valueOf(item.getId());
        FacesMessage msg = new FacesMessage("Item Selected", msgString);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void updateItem(final ActionEvent ae) {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        DiscountDropdown dropdown = (DiscountDropdown) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "discountdropdown");
        //ItemImage img = (ItemImage) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "itemimage");  
        FacesMessage message = null;
        boolean itemUpdated = false;   
        
        System.out.println("HERE!!!!!");
        
        try {
            String discountKey = dropdown.getDiscount();
            Map<String, Discount> discountObjects = dropdown.getDiscountObjects();
            Discount discount = discountObjects.get(discountKey);            
            
            if (discount == null) {
                selectedItem.setDiscountId(0);
                selectedItem.setDiscount(0);
            }
            else {
                Date now = new Date();
                Date startDate = discount.getStarDate();
                Date endDate = discount.getEndDate();
                
                if ((startDate.compareTo(now) <= 0) && (endDate.compareTo(now) > 0)) {
                    selectedItem.setDiscountId(discount.getId());
                    selectedItem.setDiscount(discount.getPercentage());
                }
                else {
                    selectedItem.setDiscountId(0);
                    selectedItem.setDiscount(0);
                }
            }            
            
            itemUpdated = service.updateItem(selectedItem);
            
            if (itemUpdated) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Updated", "The item has been successfully updated");
            }
            else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Update Item Error", "The item was not updated");
            }
            
        } catch(Exception e) {
            System.out.println("Error attempting to update item in database!!!");
            System.out.println(e.toString());
            e.printStackTrace();
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Add Item Error", e.toString());
        }
        
        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("itemUpdated", itemUpdated);        
       
        //System.out.println("Discount percent! : " + String.valueOf(dropdown.getDiscount().getPercentage()));
    }
    
    public void addNewItem(final ActionEvent ae) throws SQLException {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        DiscountDropdown dropdown = (DiscountDropdown) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "discountdropdown"); 
        FacesMessage message = null;
        boolean itemAdded = false;
        
        if (newItem.image == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Add Item Error", "You must upload an image for this item");  
        }
        else {
            try {
                if (service.itemAlreadyExists(newItem)) {
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Add Item Error", "You must select a different name or category for this item");
                }
            } catch (Exception e) {
                System.out.println("Error attempting to query database to check item existence!!!");
                e.printStackTrace();
                
                throw e;
            }
        }
        try {
            String discountKey = dropdown.getDiscount();
            Map<String, Discount> discountObjects = dropdown.getDiscountObjects();
            Discount discount = discountObjects.get(discountKey);
            
            if (discount == null) {
                newItem.setDiscountId(0);
                newItem.setDiscount(0);
            }
            else {
                Date now = new Date();
                Date startDate = discount.getStarDate();
                Date endDate = discount.getEndDate();
                
                if ((startDate.compareTo(now) <= 0) && (endDate.compareTo(now) > 0)) {
                    newItem.setDiscountId(discount.getId());
                    newItem.setDiscount(discount.getPercentage());
                }
                else {
                    newItem.setDiscountId(0);
                    newItem.setDiscount(0);
                }
            }
            
            int generatedId = service.addNewItem(newItem);
            newItem.setId(generatedId);
            ((LazyItemDataModel) lazyModel).addItem(newItem);
            itemAdded = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Added", "The item has been successfully added");
            
        } catch(Exception e) {
            System.out.println("Error attempting to add item to the database!!!");
            e.printStackTrace();
            
            throw e;
        }
        
        
        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("itemAdded", itemAdded);
        /*
        if (dropdown != null) {
            String key = dropdown.getDiscount();
            System.out.println("Discount KEY! : " + key);
            Map<String, Discount> discountObjects = dropdown.getDiscountObjects();
            Discount discount = discountObjects.get(key);
            System.out.println("Discount Percentage!!!: " + String.valueOf(discount.getPercentage()));
        } */
        
       // System.out.println("Attempting to ADD the following NEW item: " + newItem.getName());
    }
    
    public void handleUpdateFileUpload(FileUploadEvent event) throws IOException {
        //FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        UploadedFile uploadedFile = event.getFile();
        String uploadedName = event.getFile().getFileName();
        String fileExtension = uploadedName.substring(uploadedName.lastIndexOf('.'), uploadedName.length());
        String fileName = selectedItem.getName() + "_" + selectedItem.getCategory();
        String pathString = absoluteWebPath + fileName + fileExtension;
        Path filePath = Paths.get(pathString);
 
        System.out.println("Filename received: " + uploadedName);
        System.out.println("Filename given: " + fileName + fileExtension);
        System.out.print("PATHSTRING: " + pathString + "\n\n\n");
               
        try {
            saveToDisk(uploadedFile, filePath);
            StreamedContent imageStream = new DefaultStreamedContent(uploadedFile.getInputstream());
            
            selectedItem.setImageName(fileName + fileExtension);
            selectedItem.setImage(imageStream);
            selectedItem.setImagePath(filePath);
        } catch(Exception e) {
            System.out.println("Error attempting to save the image to disk!!!");
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }  
    
     public void handleAddFileUpload(FileUploadEvent event) throws IOException {
        //FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        UploadedFile uploadedFile = event.getFile();
        String uploadedName = event.getFile().getFileName();
        String fileExtension = uploadedName.substring(uploadedName.lastIndexOf('.'), uploadedName.length());
        String fileName = newItem.getName() + "_" + newItem.getCategory();
        String pathString = absoluteWebPath + fileName + fileExtension;
        Path filePath = Paths.get(pathString);
 
        System.out.println("Filename received: " + uploadedName);
        System.out.println("Filename given: " + fileName + fileExtension);
        System.out.print("PATHSTRING: " + pathString + "\n\n\n");
               
        try {
            saveToDisk(uploadedFile, filePath);
            StreamedContent imageStream = new DefaultStreamedContent(uploadedFile.getInputstream());
            
            newItem.setImageName(fileName + fileExtension);
            newItem.setImage(imageStream);
            newItem.setImagePath(filePath);
        } catch(Exception e) {
            System.out.println("Error attempting to save the image to disk!!!");
            System.out.println(e.toString());
            e.printStackTrace();
        }
    } 
    
    
    public void saveToDisk(UploadedFile image, Path imagePath) throws IOException {
        InputStream imageInput = image.getInputstream();
        
        if (Files.exists(imagePath, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(imagePath);
        }
        
        Files.createFile(imagePath);
        Files.copy(imageInput, imagePath, StandardCopyOption.REPLACE_EXISTING);        
    }    
}
