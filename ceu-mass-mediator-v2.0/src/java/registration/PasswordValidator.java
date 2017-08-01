package registration;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {

        String password = value.toString();

        UIInput uiInputConfirmPassword = (UIInput) component.getAttributes()
                .get("confirmPassword");
        String confirmPassword = uiInputConfirmPassword.getSubmittedValue()
                .toString();

        // Let required="true" do its job.
        if (password == null || password.isEmpty() || confirmPassword == null
                || confirmPassword.isEmpty()) {
            return;
        }

        if (password.length() < 8 || password.length() > 20) {
            uiInputConfirmPassword.setValid(false);
            throw new ValidatorException(new FacesMessage(
                    "Password should have between 8 and 20 characters"));
        } else if (!password.equals(confirmPassword)) {
            uiInputConfirmPassword.setValid(false);
            throw new ValidatorException(new FacesMessage(
                    "Password must match confirm password."));
        }

    }
}
