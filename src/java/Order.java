
import java.io.Serializable;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class Order implements Serializable {
    private int orderId;
    private Date saleDate;
    private String purchaser;
    private String shippingAddress;

    
    // Added default constructor for simplicity
    public Order() {
    }
    
    public int getOrderId() {
        return this.orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public Date getSaleDate() {
        return this.saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getPurchaser() {
        return this.purchaser;
    }
    
    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }
    
    public String getShippingAddress() {
        return this.shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress= shippingAddress;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.orderId);
    }
}