
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
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

@Named(value = "customer")
@SessionScoped
@ManagedBean
public class Customer implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    private DBConnect dbConnect = new DBConnect();
    private Integer customerID;
    private String name;
    private String address;
    private Date created_date;

    public Integer getCustomerID() throws SQLException {
        if (customerID == null) {
            Connection con = dbConnect.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            PreparedStatement ps
                    = con.prepareStatement(
                            "select max(customer_id)+1 from customer");
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                return null;
            }
            customerID = result.getInt(1);
            result.close();
            con.close();
        }
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        // ELContext elContext = FacesContext.getCurrentInstance().getELContext();
    //Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
    
      //  return login.getLogin();
           return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.created_date = created_date;
    }

    public String createCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Customer values(?,?,?,?)");
        preparedStatement.setInt(1, customerID);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, address);
        preparedStatement.setDate(4, new java.sql.Date(created_date.getTime()));
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "main";
    }

    public String deleteCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        statement.executeUpdate("Delete from Customer where customer_id = " + customerID);
        statement.close();
        con.commit();
        con.close();
        Util.invalidateUserSession();
        return "main";
    }

    public String showCustomer() {
        return "showCustomer";
    }

    public Customer getCustomer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from customer where customer_id = " + customerID);

        //get customer data from database
        ResultSet result = ps.executeQuery();

        result.next();

        name = result.getString("name");
        address = result.getString("address");
        created_date = result.getDate("created_date");
        return this;
    }

    public List<Customer> getCustomerList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select customer_id, name, address, created_date from customer order by customer_id");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Customer> list = new ArrayList<Customer>();

        while (result.next()) {
            Customer cust = new Customer();

            cust.setCustomerID(result.getInt("customer_id"));
            cust.setName(result.getString("name"));
            cust.setAddress(result.getString("address"));
            cust.setCreated_date(result.getDate("created_date"));

            //store all data into a List
            list.add(cust);
        }
        result.close();
        con.close();
        return list;
    }

    public void customerIDExists(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {

        if (!existsCustomerId((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID does not exist");
            throw new ValidatorException(errorMessage);
        }
    }

    public void validateCustomerID(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        int id = (Integer) value;
        if (id < 0) {
            FacesMessage errorMessage = new FacesMessage("ID must be positive");
            throw new ValidatorException(errorMessage);
        }
        if (existsCustomerId((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID already exists");
            throw new ValidatorException(errorMessage);
        }
    }

    private boolean existsCustomerId(int id) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from customer where customer_id = " + id);

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
}
