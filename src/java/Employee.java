
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
@Named(value = "employee")
@SessionScoped
@ManagedBean
public class Employee implements Serializable {

    private String userLogin;
    private String password;
    private String role = "employee";
    private String firstName;
    private String lastName;
   

    public String getUserLogin() {
        return this.userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String first) {
        this.firstName = first;
    }
    
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String last) {
        this.lastName = last;
    }
}
