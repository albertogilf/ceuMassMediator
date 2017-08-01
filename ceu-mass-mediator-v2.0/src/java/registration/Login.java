package registration;

import java.io.Serializable;
import javax.ejb.SessionBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
@ManagedBean(name = "login")
@SessionScoped
public class Login implements Serializable {

    private static final long serialVersionUID = 1094801825228386363L;
    private String username;
    private String firstName;
    private String lastName;
    private String email = "asd";
    private String password;
    private boolean isAdmin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String login() {
        String redirect;
        LoginDatabase.connect();
        boolean valid = LoginDatabase.validateUserPassword(username, password);
        if (valid) {
            HttpSession session;
            session = MySessionBean.getSession();
            session.setAttribute("username", username);
            int admin;
            admin = LoginDatabase.isAdmin(username);
            session.setAttribute("isAdmin", admin);
            System.out.println("\n Session Attributes: user: " + (String) session.getAttribute("username")
             + " isAdmin: " + session.getAttribute("isAdmin"));
            redirect = "index";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Incorrect Username and Password",
                            "Please enter correct username and Password"));
            redirect = "login";
        }
        LoginDatabase.close();
        return redirect;
    }

    public String logout() {
        HttpSession session = MySessionBean.getSession();
        session.invalidate();
        return "login";
    }

    public String register() {
        String redirect;
        LoginDatabase.connect();
        int result = LoginDatabase.register(username, firstName, lastName, email, password);
        if (result == 0) {
            redirect = "index";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Invalid Username or email",
                            "Please enter correct Information"));
            redirect = "register";
        }
        LoginDatabase.close();
        return redirect;
    }

    /**
     * Validates the input Tolerance to be a float between 0 and 10000
     *
     * @param arg0 FacesContext of the form
     * @param arg1 Component of the form
     * @param arg2 Input of the user in the component arg1
     *
     */
    /*
    public void userExists(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        String usernameInput;
        try {
            usernameInput = (String) arg2;
            //  inputTol = Integer.valueOf((String) arg2); 
        } catch (NumberFormatException nfe) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be a number between 0 and 1000"));
        }
        if (inputTol <= 0) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 1000"));
        } else if (inputTol > 1000) {
            throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 1000"));
        }
    }*/
}
