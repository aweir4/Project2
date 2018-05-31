/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.UUID;
 
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
/**
 *
 * @author austinweir
 */

@Named(value="itemimage")
@ManagedBean
@ViewScoped
public class ItemImage implements Serializable {
    private ELContext elContext = FacesContext.getCurrentInstance().getELContext();
    private AddItemView addItemView = (AddItemView) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "additemview");
    private String absoluteWebPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
    private String imageName;
    private Path imagePath;
    private UploadedFile image;
    
    public void handleFileUpload(FileUploadEvent event) throws IOException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        absoluteWebPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        String fileName = event.getFile().getFileName() + UUID.randomUUID().toString();
        String pathString = absoluteWebPath + fileName;
        Path filePath = Paths.get(pathString);
        
        System.out.print("\n\n\n PATHSTRING: " + pathString + "\n\n\n");
        
        // Set the image path and image to be used when inserting item into database
        setImageName(fileName);
        setImagePath(filePath);
        setImage(event.getFile());
        addItemView.setItemImage(fileName);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void saveToDisk() throws IOException {
        InputStream imageInput = image.getInputstream();
        
        if (Files.exists(imagePath, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(imagePath);
        }
        
        Files.createFile(imagePath);
        Files.copy(imageInput, imagePath, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public String getAbsoluteWebPath() {
        return absoluteWebPath;
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public void setImageName(String name) {
        imageName = name;
    }
    
    public Path getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(Path path) {
        imagePath = path;
    }
    
    public void setImage(UploadedFile file) {
        image = file;
    }
}
