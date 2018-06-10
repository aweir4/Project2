
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


@Named(value = "discountPage")
@ViewScoped
@ManagedBean
public class DiscountPage implements Serializable {

    private DBConnect dbConnect = new DBConnect();
    private List<Discount> discounts;
    private Discount tempDiscount;
    private Discount selectedDiscount;
    public boolean isSelected = false;
 
    @PostConstruct
    public void init() {
        System.out.println("getting discounts");
        this.tempDiscount = new Discount();
        try {
            this.discounts = this.getDiscountService();
        }
        catch (SQLException ex) {
            System.out.println("Unable to get discounts");
        }
    }
     
    public List<Discount> getDiscounts() {
        return this.discounts;
    }
 
    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }
    
    public boolean getIsSelected() {
        return this.isSelected;
    }
    
    public void setIsSelected(boolean select) {
        this.isSelected = select;
    }
    
    public Discount getTempDiscount() {
        return this.tempDiscount;
    }
 
    public void setTempDiscount(Discount d) {
        this.tempDiscount = d;
    }
    
    public Discount getSelectedDiscount() {
        return this.selectedDiscount;
    }
 
    public void setSelectedDiscount(Discount d) {
        System.out.println("selected discount set!");
        this.selectedDiscount = d;
    }
    
    public void onRowSelect(SelectEvent event) {
        // check if discount 0 if it is DO NOT SET IT!
        Discount testDiscount = ((Discount) event.getObject());
        System.out.println("here is id " + testDiscount.getId());
        if ((testDiscount.getId()) != 0) {
         this.selectedDiscount = ((Discount) event.getObject());
         this.isSelected = true;   
        }
        System.out.println("row selected!");
    }
    
    public void handleClose(CloseEvent event) {
        System.out.println("resetting discount");
        this.tempDiscount = new Discount();
    }
    
    public void getMinDate() {
        
    }
    
    public void addDiscount() throws SQLException, ParseException {
        System.out.println("adding discount");
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("Insert into discounts "
                    + "(discountPercentage, startDate, endDate) values(?,?,?)");
            preparedStatement.setDouble(1, this.tempDiscount.getPercentage());
            preparedStatement.setDate(2, new java.sql.Date(this.tempDiscount.getStartDate().getTime()));
            preparedStatement.setDate(3, new java.sql.Date(this.tempDiscount.getEndDate().getTime()));
            preparedStatement.executeUpdate();
            statement.close();
            con.commit();
            con.close();
            ELContext elContext = FacesContext.getCurrentInstance().getELContext();
            DiscountDropdown dropdown = (DiscountDropdown) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "discountdropdown");
            dropdown.addDiscount(this.tempDiscount);
            
            this.discounts = null;
            this.discounts = this.getDiscountService();
        }
        finally {
            PrimeFaces.current().executeScript("PF('dlg2').hide();");
        }
    }
    
    public void deleteDiscount(ActionEvent event) throws SQLException, ParseException {
        System.out.println("deleting discount");
        if (this.selectedDiscount == null) {
            System.out.println("selected is null");
            return;
        }
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        try {
            String query = "Delete from discounts where discountId = '" + this.selectedDiscount.getId() + "'";
            System.out.println(query);
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            statement.close();
            con.commit();
            con.close();
            this.discounts = null;
            this.discounts = this.getDiscountService();
        }
        finally {
            this.isSelected = false;
            this.selectedDiscount = null;
        }
    }
    
    public List<Discount> getDiscountService() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from discounts");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Discount> list = new ArrayList<Discount>();

        while (result.next()) {
            Discount d = new Discount();
            d.setId(result.getInt("discountId"));
            d.setPercentage(result.getInt("discountPercentage"));
            d.setStartDate(result.getDate("startDate"));
            d.setEndDate(result.getDate("endDate"));
            list.add(d);
        }
        result.close();
        con.close();
        
        return list;
    }
    
}
