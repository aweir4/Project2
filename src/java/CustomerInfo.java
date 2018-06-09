import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
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
@Named(value = "customerInfo")
@SessionScoped
@ManagedBean
public class CustomerInfo implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private String login = "";
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String cardNumber;
    private Date cardExpiration;
    private String crcCode;
    private String role = "customer";
    
    private String maskedPassword;
    private String maskedCardNumber;
    private String maskedCrcCode;
    private String formattedDate;
    
// For update and relating component rendering
    private boolean editLogin = false;
    private boolean editPassword = false;
    private boolean editFName = false;
    private boolean editLName = false;
    private boolean editEmail = false;
    private boolean editAddress = false;
    private boolean editCardNumber = false;
    private boolean editCardExpiration = false;
    private boolean editCrcCode = false;

    private UIOutput loginUI;

    public UIOutput getLoginUI() {
        return loginUI;
    }
    public void setLoginUI(UIOutput loginName) {
        this.loginUI = loginName;
    }
    
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
    
        
    public String getMaskedPassword() {
        return maskedPassword;
    }
    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }
    public String getMaskedCrcCode() {
        return maskedCrcCode;
    }
    public String getformattedDate() {
        return formattedDate;
    }
    
    public boolean getEditLogin(){
        return editLogin;
    }
    public void setEditLoginTrue(){
        this.editLogin = true;
    }
    public void setEditLoginFalse(){
        this.editLogin = false;
    }
    
    public boolean getEditPassword(){
        return editPassword;
    }
    public void setEditPasswordTrue(){
        this.editPassword = true;
    }
    public void setEditPasswordFalse(){
        this.editPassword = false;
    }
    
    public boolean getEditFName(){
        return editFName;
    }
    public void setEditFNameTrue(){
        this.editFName = true;
    }
    public void setEditFNameFalse(){
        this.editFName = false;
    }
    
    public boolean getEditLName(){
        return editLName;
    }
    public void setEditLNameTrue(){
        this.editLName = true;
    }
    public void setEditLNameFalse(){
        this.editLName = false;
    }
    
    public boolean getEditEmail(){
        return editEmail;
    }
    public void setEditEmailTrue(){
        this.editEmail = true;
    }
    public void setEditEmailFalse(){
        this.editEmail = false;
    }
    
    public boolean getEditAddress(){
        return editAddress;
    }
    public void setEditAddressTrue(){
        this.editAddress = true;
    }
    public void setEditAddressFalse(){
        this.editAddress = false;
    }
    
    public boolean getEditCardNumber(){
        return editCardNumber;
    }
    public void setEditCardNumberTrue(){
        this.editCardNumber = true;
    }
    public void setEditCardNumberFalse(){
        this.editCardNumber = false;
    }
    
    public boolean getEditCardExpiration(){
        return editCardExpiration;
    }
    public void setEditCardExpirationTrue(){
        this.editCardExpiration = true;
    }
    public void setEditCardExpirationFalse(){
        this.editCardExpiration = false;
    }
    
    public boolean getEditCrcCode(){
        return editCrcCode;
    }
    public void setEditCrcCodeTrue(){
        this.editCrcCode = true;
    }
    public void setEditCrcCodeFalse(){
        this.editCrcCode = false;
    }
    
     public void initFields(String loggedIn) throws SQLException, ParseException{
                
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        String userLgn = login.isEmpty() ? loggedIn : login;
        con.setAutoCommit(false);
        PreparedStatement ps = con.prepareStatement("SELECT userpassword, "
                + "firstname, lastname FROM users where userlogin = ?");
        ps.setString(1, userLgn);
        
        ResultSet result = ps.executeQuery();
        result.next();

        this.maskedPassword = result.getString("userpassword").replaceAll(".", "*");
        this.firstName = result.getString("firstname");
        this.lastName = result.getString("lastname");
        
        if(role.equalsIgnoreCase("customer")) {
            ps = con.prepareStatement("SELECT * FROM customerdata "
                    + "where customerlogin = ?");
            ps.setString(1, userLgn);
            ResultSet result1 = ps.executeQuery();
            result1.next();
            
            this.address = result1.getString("address");
            this.email = result1.getString("email");
            this.maskedCardNumber = result1.getString("cardNumber");
            this.maskedCardNumber = maskedCardNumber.
                    substring(0, maskedCardNumber.length()- 4)
                    .replaceAll(".", "*") + maskedCardNumber.
                    substring(maskedCardNumber.length()- 4);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
            this.formattedDate = 
                    formatter.format(result1.getDate("cardexpiration"));
            this.maskedCrcCode = result1.getString("cardCode").replaceAll(".", "*");
        }
    }
    
    public String createUser() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement(
                "Insert into Users values(?,?,?,?,?)");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, role);
        preparedStatement.setString(4, firstName);
        preparedStatement.setString(5, lastName);
        
        preparedStatement.executeUpdate();
    System.out.println("SELECTED DATE:" + cardExpiration);
        if(role.equals("customer")) {
            preparedStatement = con.prepareStatement(
            "Insert into CustomerData values(?,?,?,?,?,?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.setDate(3, new java.sql.Date(cardExpiration.getTime()));
            preparedStatement.setString(4, crcCode);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, email);
            preparedStatement.executeUpdate();
        }
        preparedStatement.close();
        con.commit();
        con.close();
        return "login";
    }
    
    public void updateUser() throws SQLException {
        String loggedInUser = loginUI.getValue().toString();
        if(editLogin) {
            commitUpdates("users", "userlogin", login, loggedInUser);
            loggedInUser = login;
        }
        if(editPassword) {
            commitUpdates("users", "userpassword", password, loggedInUser);
            this.maskedPassword = password.replaceAll(".", "*");            
        }  
        
        if(editEmail) {
            commitUpdates("customerdata", "email", email, loggedInUser);
        }
        
        if(editFName) {
            commitUpdates("users", "firstname", firstName, loggedInUser);            
        }
        
        if(editLName) {
            commitUpdates("users", "lastname", lastName, loggedInUser);
        }
        
        if(editAddress) {
            commitUpdates("customerdata", "address", address, loggedInUser);
        }
        
        if(editCardNumber) {
            commitUpdates("customerdata", "cardnumber", cardNumber, loggedInUser);
            this.maskedCardNumber = cardNumber.
                    substring(0, cardNumber.length()- 4)
                    .replaceAll(".", "*") + cardNumber.
                    substring(cardNumber.length()- 4);
        }
        
        if(editCardExpiration){
            commitUpdates("customerdata", "cardexpiration", cardExpiration.toString(), loggedInUser);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
            this.formattedDate = formatter.format(cardExpiration);
        }
        
        if(editCrcCode) {
            commitUpdates("customerdata", "cardcode", crcCode, loggedInUser);
            this.maskedCrcCode = crcCode.replaceAll(".", "*");
        }
        
        setAllEditToFalse();
    }
    
    private void commitUpdates(String tblName, String colName, String value, 
            String userLogged) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        con.setAutoCommit(false);
        String userNameCol = tblName.equalsIgnoreCase("users") ? 
                "userlogin" : "customerlogin";
        
        String qry= "Update " + tblName + " set " + colName +
                " = ? where "+ userNameCol +" = ?";
        PreparedStatement ps = con.prepareStatement(qry);

        if(colName.equalsIgnoreCase("cardexpiration")) {
            try { 
                Date date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
                        .parse(value);
                ps.setDate(1, new java.sql.Date(date1.getTime()));
            } catch (ParseException ex) {
                Logger.getLogger(CustomerInfo.class.getName()).log(Level.SEVERE, null, ex);
            } 
        } else {
            ps.setString(1, value);
        }
        ps.setString(2, userLogged);
System.out.println("RUNNING QUERY" + qry);
        ps.executeUpdate();

        con.commit();        
        ps.close();
        con.close();
    }
    
    private void setAllEditToFalse(){
        this.setEditLoginFalse();
        this.setEditPasswordFalse();
        this.setEditFNameFalse();
        this.setEditLNameFalse();
        
        this.setEditEmailFalse();
        this.setEditAddressFalse();
        this.setEditCardNumberFalse();
        this.setEditCardExpirationFalse();
        this.setEditCrcCodeFalse();
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
    
//    public void validateCardNumber(FacesContext context, UIComponent componentToValidate, Object value)
//            throws ValidatorException, SQLException {
//        try {
//            long crdNum = Long.parseLong(String.valueOf(value));
//        } catch (NumberFormatException e) {
//            FacesMessage errorMessage = new FacesMessage("Please enter valid card number");
//            throw new ValidatorException(errorMessage);
//        }
//    }
}
