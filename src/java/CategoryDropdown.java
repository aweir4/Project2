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
import java.util.List;
/**
 *
 * @author austinweir
 */

@Named(value="categorydropdown")
@ManagedBean
@SessionScoped
public class CategoryDropdown implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private ArrayList<String> categories;
    private String category;
    
    @PostConstruct
    public void init(){
        Connection connection = dbConnect.getConnection();
        
        if (connection == null) {
            System.out.println("\nThere was an issue attempting to load the categories from the database!!!");
            return;
        }
        
        // Try and load all categories from the database upon session start
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Categories ORDER BY category ASC");
            ResultSet results = preparedStatement.executeQuery();
            categories = new ArrayList<String>();
            
            while(results.next()) {
                categories.add(results.getString("category"));
            }
        } catch(Exception e) {
            System.out.println("\nErrors occurred while attempting to query database for categories!!!");
            return;
        }

    }
    
    public ArrayList<String> getCategories() {
        return categories;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}
