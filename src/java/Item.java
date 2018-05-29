import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
 
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 */

@ManagedBean(name = "item", eager = true)
public class Item {
    public static final String COTTONELLE_IMG = "https://www.dropbox.com/home?preview=cottonelle.jpg";
    public static final String TPLINK_IMG = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimages-na.ssl-images-amazon.com%2Fimages%2FI%2F81luWxE11XL._SL500_SR200%2C200_.jpg&imgrefurl=https%3A%2F%2Fwww.amazon.com%2Fgp%2Fmost-gifted%2Fhi&docid=7svxgdKljuxCAM&tbnid=xOYToIV0EGpDWM%3A&vet=10ahUKEwjt8pesrYPbAhWqi1QKHUyZBhMQMwiVASgMMAw..i&w=200&h=200&client=firefox-b-1-ab&bih=1007&biw=1439&q=amazon%20items&ved=0ahUKEwjt8pesrYPbAhWqi1QKHUyZBhMQMwiVASgMMAw&iact=mrc&uact=8";
    public static final String ECHO_IMG = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fak1.ostkcdn.com%2Fimages%2Fproducts%2Fis%2Fimages%2Fdirect%2F9b089fdd44e52e18477c8ee9a379f086dac4f0d1%2FAmazon-Echo-Dot.jpg%3Fimpolicy%3Dmedium%26imwidth%3D200&imgrefurl=https%3A%2F%2Fwww.overstock.com%2FElectronics%2FAmazon-Echo-Smart-Speaker%2F13849568%2Fproduct.html&docid=Dm85FYC1YJXWWM&tbnid=5yP0caA9NIGG7M%3A&vet=12ahUKEwiXyarxroPbAhVY5GMKHTdzCkg4ZBAzKFIwUnoECAEQWg..i&w=200&h=200&client=firefox-b-1-ab&bih=1007&biw=1439&q=amazon%20items&ved=2ahUKEwiXyarxroPbAhVY5GMKHTdzCkg4ZBAzKFIwUnoECAEQWg&iact=mrc&uact=8";
        
    private List<String> images;
    private DBConnect dbConnect = new DBConnect();
    
    public void addImage() throws FileNotFoundException, SQLException, IOException {
        Connection connection = dbConnect.getConnection();
        File file = new File("/Users/austinweir/366/Project2/cottonelle.jpg");
        FileInputStream fis = new FileInputStream(file);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO images VALUES (?, ?)");
        ps.setString(1, file.getName());
        ps.setBinaryStream(2, fis, file.length());
        ps.executeUpdate();
        ps.close();
        fis.close();
    }     
    
    public String getImage() {
        Connection connection = dbConnect.getConnection();
        OutputStream out = null;
        
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT img FROM images WHERE imgname = ?");
            ps.setString(1, "myimage.gif");
            ResultSet rs = ps.executeQuery();
            
            if (rs != null) {
                while (rs.next()) {
                    byte[] imgBytes = rs.getBytes(1);
                    out = new BufferedOutputStream(new FileOutputStream("./cottonelle.jpg"));
                    out.write(imgBytes);
                    return "./cottonelle.jpg";
                }
            } 
        } catch (Exception e) {
            return "";
        }
        
     return "";
    }
    
    @PostConstruct
    public void init() {
        images = new ArrayList<String>();
        //images.add(COTTONELLE_IMG);
        
        try {
            addImage();
            images.add(getImage());
        } catch (Exception e) {
            return;
        }
    }
 
    public List<String> getImages() {
        return images;
    }
    
    
  
}
