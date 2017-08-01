
package validators.jsf;

/**
 * Class for form validators
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class MyValidator implements Validator {

    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
            throws ValidatorException {
        // int inputTol =-1;
        float inputTol = -1;
        try 
        {
            inputTol = Float.parseFloat((String)arg2);
          //  inputTol = Integer.valueOf((String) arg2); 
        }
        catch(NumberFormatException nfe)
        {
            throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 10000"));
        }
        if (inputTol <=0 ) {
         throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 10000"));
        }
        else if (inputTol > 10000 ) {
         throw new ValidatorException(new FacesMessage("The input tolerance should be between 0 and 10000"));
        }
    }
}