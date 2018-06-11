
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;


@Named(value = "categoryPage")
@ViewScoped
@ManagedBean
public class CategoryPage implements Serializable {

    private DBConnect dbConnect = new DBConnect();
    private List<String> categories;
    private String tempCategory;
    private String selectedCategory;
    public boolean isSelected = false;
 
    @PostConstruct
    public void init() {
        System.out.println("getting categories");
        this.tempCategory = "";
        try {
            this.categories = this.getCategoriesService();
        }
        catch (SQLException ex) {
            System.out.println("Unable to get categories");
        }
    }
     
    public List<String> getCategories() {
        return this.categories;
    }
 
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    public boolean getIsSelected() {
        return this.isSelected;
    }
    
    public void setIsSelected(boolean select) {
        this.isSelected = select;
    }
    
    public String getTempCategory() {
        return this.tempCategory;
    }
 
    public void setTempCategory(String s) {
        this.tempCategory = s;
    }
    
    public String getSelectedCategory() {
        return this.selectedCategory;
    }
 
    public void setSelectedCategory(String s) {
        System.out.println("selected category set!");
        this.selectedCategory = s;
    }
    
    public void onRowSelect(SelectEvent event) {
         this.selectedCategory = ((String) event.getObject());
         this.isSelected = true;   
        System.out.println("row selected!");
    }
    
    public void handleClose(CloseEvent event) {
        System.out.println("resetting discount");
        this.tempCategory = "";
    }
    
    public void addCategory() throws SQLException, ParseException {
        System.out.println("adding category");
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("Insert into categories values(?)");
            preparedStatement.setString(1, this.tempCategory);
            preparedStatement.executeUpdate();
            statement.close();
            con.commit();
            con.close();
            ELContext elContext = FacesContext.getCurrentInstance().getELContext();
            CategoryDropdown dropdown = (CategoryDropdown) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "categorydropdown");
            dropdown.addCategory(this.tempCategory);
            
            this.categories = null;
            this.categories = this.getCategoriesService();
        }
        finally {
            PrimeFaces.current().executeScript("PF('dlg2').hide();");
        }
    }
    
    public void deleteCategory(ActionEvent event) throws SQLException, ParseException {
        System.out.println("deleting category");
        if (this.selectedCategory == null) {
            System.out.println("selected is null");
            return;
        }
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        try {
            String query = "Delete from categories where category = '" + this.selectedCategory + "'";
            System.out.println(query);
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            statement.close();
            con.commit();
            con.close();
            this.categories = null;
            this.categories = this.getCategoriesService();
        }
        finally {
            this.isSelected = false;
            this.selectedCategory = null;
        }
    }
    
    public List<String> getCategoriesService() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from categories");

        ResultSet result = ps.executeQuery();

        List<String> list = new ArrayList<String>();

        while (result.next()) {
            String s = result.getString("category");
            list.add(s);
        }
        result.close();
        con.close();
        
        return list;
    }
    
}
