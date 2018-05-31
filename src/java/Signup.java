import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author austin
 * @author bibek
 */
@Named(value = "signup")
@SessionScoped
@ManagedBean
public class Signup implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String cardNumber;
    private Date cardExpiration;
    private String crcCode;
    private String role = "customer";
    
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getfirstName() {
        return firstName;
    }
    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }
 
    public String getlastName() {
        return lastName;
    }
    public void setlastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public Date getCardExpiration() {
        return cardExpiration;
    }
    public void setCardExpiration(Date cardExpiration) {
        this.cardExpiration = cardExpiration;
    }
    
    public String getCrcCode() {
        return crcCode;
    }
    public void setCrcCode(String crcCode) {
        this.crcCode = crcCode;
    }
    
    public String getRole() {
        return role;
    }
    
    public String createUser() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        //Statement statement = con.createStatement();
  
        PreparedStatement preparedStatement = con.prepareStatement(
                "Insert into Users values(?,?,?,?,?)");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, role);
        preparedStatement.setString(4, firstName);
        preparedStatement.setString(5, lastName);
        
        preparedStatement.executeUpdate();
    System.out.println("SELECTED DATE:" + cardExpiration);
//        DateTimeFormatter parser = DateTimeFormatter.ofPattern("MM/yy");
//        YearMonth ym = YearMonth.parse(cardExpiration, parser);
//        Date expDate = java.sql.Date.valueOf(ym.atDay(1));

        if(role.equals("customer")) {
            preparedStatement = con.prepareStatement(
            "Insert into CustomerData values(?,?,?,?,?,?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.setDate(3, new java.sql.Date(cardExpiration.getTime()));
            preparedStatement.setInt(4, Integer.parseInt(crcCode));
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, email);
            preparedStatement.executeUpdate();
        }
        preparedStatement.close();
        con.commit();
        con.close();
        return "login";
    }
    
    public void validateLogin(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        String loginName = (String) value;
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        PreparedStatement statement = con.prepareStatement(
            "Select * from Users where userlogin = '" + loginName + "'");
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            FacesMessage errorMessage = new FacesMessage("Login already exist");
            statement.close();
            con.close();
            throw new ValidatorException(errorMessage);
        }
        statement.close();
        con.close();
    }
    
    public void validateCardNumber(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        try {
            long crdNum = Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            FacesMessage errorMessage = new FacesMessage("Please enter valid card number");
            throw new ValidatorException(errorMessage);
        }
    }
}
