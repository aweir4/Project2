
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
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;


@Named(value = "admin")
@ViewScoped
@ManagedBean
public class Admin implements Serializable {

    private DBConnect dbConnect = new DBConnect();
    
    private List<Employee> employees;
    private Employee tempEmployee;
    private Employee selectedEmployee;
    public boolean isSelected = false;
 
    @PostConstruct
    public void init() {
        System.out.println("getting employees");
//        List<Employee> list = new ArrayList<Employee>();
//        Employee test1 = new Employee();
//        test1.setFirstName("first yo");
//        test1.setLastName("last yo");
//        test1.setUserLogin("hi");
//        test1.setPassword("passme");
//        list.add(test1);
        this.tempEmployee = new Employee();
        try {
            this.employees = this.getEmployeeService();
        }
        catch (SQLException ex) {
            System.out.println("Unable to get employees");
        }
    }
     
    public List<Employee> getEmployees() {
        return this.employees;
    }
 
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
    
    public boolean getIsSelected() {
        return this.isSelected;
    }
    
    public void setIsSelected(boolean select) {
        this.isSelected = select;
    }
    
    public Employee getTempEmployee() {
        return this.tempEmployee;
    }
 
    public void setTempEmployee(Employee e) {
        this.tempEmployee = e;
    }
    
    public Employee getSelectedEmployee() {
        return this.selectedEmployee;
    }
 
    public void setSelectedEmployee(Employee e) {
        System.out.println("selected employee set!");
        this.selectedEmployee = e;
    }
    
    public void onRowSelect(SelectEvent event) {
        this.selectedEmployee = ((Employee) event.getObject());
        this.isSelected = true;
        System.out.println("row selected!");
        System.out.println(this.selectedEmployee.getFirstName());

    }
    
    public void handleClose(CloseEvent event) {
        System.out.println("resetting employee");
        this.tempEmployee = new Employee();
    }
    
    public void addEmployee() throws SQLException, ParseException {
        System.out.println("adding employee");
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("Insert into users values(?,?,?,?, ?)");
            preparedStatement.setString(1, this.tempEmployee.getUserLogin());
            preparedStatement.setString(2, this.tempEmployee.getPassword());
            preparedStatement.setString(3, "employee");
            preparedStatement.setString(4, this.tempEmployee.getFirstName());
            preparedStatement.setString(5, this.tempEmployee.getLastName());
            preparedStatement.executeUpdate();
            statement.close();
            con.commit();
            con.close();
            this.employees = null;
            this.employees = this.getEmployeeService();
        }
        finally {
            PrimeFaces.current().executeScript("PF('dlg2').hide();");
        }
    }
    
    public void deleteEmployee(ActionEvent event) throws SQLException, ParseException {
        System.out.println("deleting employee");
        if (this.selectedEmployee == null) {
            System.out.println("selected is null");
            return;
        }
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        try {
            String query = "Delete from users where userLogin = '" + this.selectedEmployee.getUserLogin() + "'";
            System.out.println(query);
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            statement.close();
            con.commit();
            con.close();
            this.employees = null;
            this.employees = this.getEmployeeService();
        }
        finally {
            this.isSelected = false;
            this.selectedEmployee = null;
        }
    }
    
    public List<Employee> getEmployeeService() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from users where users.userrole = 'employee'");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Employee> list = new ArrayList<Employee>();

        while (result.next()) {
            Employee emp = new Employee();
            emp.setUserLogin(result.getString("userlogin"));
            emp.setPassword(result.getString("userpassword"));
            emp.setFirstName(result.getString("firstname"));
            emp.setLastName(result.getString("lastname"));
            list.add(emp);
        }
        result.close();
        con.close();
        
        return list;
    }
    
}
