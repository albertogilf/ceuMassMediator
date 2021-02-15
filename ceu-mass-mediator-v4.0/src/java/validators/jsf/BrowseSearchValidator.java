/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validators.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Alberto Gil de la Fuente
 * @version: 5.0, 14/07/2017.
 */
@FacesValidator("browseSearchValidator")
public class BrowseSearchValidator implements Validator {

    /**
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    @Override
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {

        String formula = value.toString();

        UIInput iuInputName = (UIInput) component.getAttributes()
		.get("name");
        
	  String name = iuInputName.getValue().toString();
        
//        System.out.println("NAME " + name + " FORMULA " + formula);
//        System.out.println("NAME LENGTH " + name.length() + " FORMULA LENGTH " + formula.length());
        

        if (formula.length() < 3 && name.length() < 3) {
            throw new ValidatorException(new FacesMessage(
                    "Formula or Name should be larger than 3"));
        }
    }

}
