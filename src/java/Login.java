
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.faces.event.ActionEvent;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bibek
 * @author austin
 */
@Named(value = "login")
@SessionScoped
@ManagedBean
public class Login implements Serializable {

    private String login;
    private String password;
    private UIInput loginUI;
    private String role = "customer";
    private DBConnect dbConnect = new DBConnect();
    private CurrentCustomer currentCustomer;
    
    public class CurrentCustomer implements Serializable {
        public String login;
        public String cardNumber;
        public Date cardExpiration;
        public String address;
        public String email;
        
        public CurrentCustomer(String login, String cardNumber, Date cardExpiration, String address, String email) {
            this.login = login;
            this.cardNumber = cardNumber;
            this.cardExpiration = cardExpiration;
            this.address = address;
            this.email = email;
        }
        
        public String getLogin() {
            return login;
        }
        
        public String getCardNumber() {
            return cardNumber;
        }
        
        public Date getCardExpiration() {
            return cardExpiration;
        }
        
        public String getAddress() {
            return address;
        }
        
        public String getEmail() {
            return email;
        }
    }
    
    public CurrentCustomer getCurrentCustomer() {
        return currentCustomer;
    }

    public UIInput getLoginUI() {
        return loginUI;
    }

    public void setLoginUI(UIInput loginUI) {
        this.loginUI = loginUI; 
    }
    
    public String getRole() {
        return role;
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
//    
//    public void validateLogin(FacesContext context, UIComponent component,
//            Object value) throws ValidatorException, SQLException {
//        Connection con = dbConnect.getConnection();
//        
//        if(con == null){
//            throw new SQLException("Can't get database connection");
//        }
//        
//        login = value.toString();
//        String qry = "Select * from users where userlogin = '" + login + "'";
//        PreparedStatement ps = con.prepareStatement(qry);
//        ResultSet result = ps.executeQuery();
//        
//        if(!result.next()) {
//            FacesMessage errorMessage = new FacesMessage("Wrong login");
//            throw new ValidatorException(errorMessage);
//        };
//        ps.close();
//        con.close();
//    }
    
    public void validate(FacesContext context, UIComponent component, 
            Object value) throws ValidatorException, SQLException {
        Connection con = dbConnect.getConnection();
        
        if(con == null){
            throw new SQLException("Can't get database connection");
        }
        
        login = loginUI.getLocalValue().toString();
        password = value.toString();
        
        String qry = "Select userpassword, userrole from users where userlogin = '" + login + "'";
        PreparedStatement ps = con.prepareStatement(qry);
        System.out.println("Running query" + qry);
        ResultSet result = ps.executeQuery();
        
        if(!result.next()) {
            FacesMessage errorMessage = new FacesMessage("Wrong login");
            throw new ValidatorException(errorMessage);
        };
        
        String password_actual = result.getString("userpassword");
        role = result.getString("userrole");
        System.out.println("hello i got " + password_actual);
        
        if (role.equals("customer")) {
            setCustomer(login);
        }
        
        if (!password.equals(password_actual)) {
            FacesMessage errorMessage = new FacesMessage(
                    "Wrong password");
            throw new ValidatorException(errorMessage);
        } 
        ps.close();
        con.close();
    }
    
    public void setCustomer(String login) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        String query = "SELECT * FROM CustomerData WHERE customerLogin = '" + login + "'";

        PreparedStatement ps = con.prepareStatement(query);

        ResultSet result = ps.executeQuery();
        
        while(result.next()) {
            String cardNumber = result.getString("cardNumber");
            Date cardExpiration = result.getDate("cardExpiration");
            String address = result.getString("address");
            String email = result.getString("email");
                    
            currentCustomer = new CurrentCustomer(login, cardNumber, cardExpiration, address, email);      
        }
        result.close();
        con.close();      
    }
    
    public void logout() {
        System.out.println("LOGOUT USER");
     	FacesContext context = FacesContext.getCurrentInstance();
     	context.getExternalContext().invalidateSession();
        
        try {
            context.getExternalContext().redirect("/Project2/faces/index.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        } 
     }

    public String go() {
      //  Util.invalidateUserSession();
        if(role.equalsIgnoreCase("admin"))
            return "admin";
        return "success";
    }
}
