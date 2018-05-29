/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 *
 * @author austinweir
 */
@Named(value = "login")
@ManagedBean
@ViewScoped
public class AddItemView {
    private int itemId;
    private String itemName;
    private String itemDescription;
    private String itemCategory;
    private int itemDiscount;
    private String itemImage;
    private double itemPrice;
    private int itemStock;
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int id) {
        itemId = id;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String name) {
        itemName = name;
    }
    
    public String getItemDescription() {
        return itemDescription;
    }
    
    public void setItemDescription(String description) {
        itemDescription = description;
    }
    
    public String getItemCategory() {
        return itemCategory;
    }
    
    
}
