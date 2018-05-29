
import java.io.Serializable;

import java.util.*;
import java.text.SimpleDateFormat;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named(value = "util")
@SessionScoped
@ManagedBean
public class Util implements Serializable {
    
    public static final String COTTONELLE_IMG = "https://www.google.com/imgres?imgurl=http%3A%2F%2Fwww.mymoneyblog.com%2Fwordpress%2Fwp-content%2Fuploads%2F2016%2F11%2Ftp.jpg&imgrefurl=http%3A%2F%2Fwww.mymoneyblog.com%2Famazon-household-essentials-promotions.html&docid=Q5WKakmHtGo1EM&tbnid=yjfyhdpLbzaMzM%3A&vet=10ahUKEwjt8pesrYPbAhWqi1QKHUyZBhMQMwiKASgBMAE..i&w=200&h=200&itg=1&client=firefox-b-1-ab&bih=1007&biw=1439&q=amazon%20items&ved=0ahUKEwjt8pesrYPbAhWqi1QKHUyZBhMQMwiKASgBMAE&iact=mrc&uact=8";
    public static final String TPLINK_IMG = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimages-na.ssl-images-amazon.com%2Fimages%2FI%2F81luWxE11XL._SL500_SR200%2C200_.jpg&imgrefurl=https%3A%2F%2Fwww.amazon.com%2Fgp%2Fmost-gifted%2Fhi&docid=7svxgdKljuxCAM&tbnid=xOYToIV0EGpDWM%3A&vet=10ahUKEwjt8pesrYPbAhWqi1QKHUyZBhMQMwiVASgMMAw..i&w=200&h=200&client=firefox-b-1-ab&bih=1007&biw=1439&q=amazon%20items&ved=0ahUKEwjt8pesrYPbAhWqi1QKHUyZBhMQMwiVASgMMAw&iact=mrc&uact=8";
    public static final String ECHO_IMG = "https://www.google.com/imgres?imgurl=https%3A%2F%2Fak1.ostkcdn.com%2Fimages%2Fproducts%2Fis%2Fimages%2Fdirect%2F9b089fdd44e52e18477c8ee9a379f086dac4f0d1%2FAmazon-Echo-Dot.jpg%3Fimpolicy%3Dmedium%26imwidth%3D200&imgrefurl=https%3A%2F%2Fwww.overstock.com%2FElectronics%2FAmazon-Echo-Smart-Speaker%2F13849568%2Fproduct.html&docid=Dm85FYC1YJXWWM&tbnid=5yP0caA9NIGG7M%3A&vet=12ahUKEwiXyarxroPbAhVY5GMKHTdzCkg4ZBAzKFIwUnoECAEQWg..i&w=200&h=200&client=firefox-b-1-ab&bih=1007&biw=1439&q=amazon%20items&ved=2ahUKEwiXyarxroPbAhVY5GMKHTdzCkg4ZBAzKFIwUnoECAEQWg&iact=mrc&uact=8";
    
    public static void invalidateUserSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
    }

    public void validateDate(FacesContext context, UIComponent component, Object value)
            throws Exception {

        try {
            Date d = (Date) value;
        } catch (Exception e) {
            FacesMessage errorMessage = new FacesMessage("Input is not a valid date");
            throw new ValidatorException(errorMessage);
        }
    }
}
