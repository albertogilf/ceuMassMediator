package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class NumberedLink extends Link {


    private final Integer number;



    public NumberedLink(String id, String name, Integer number) {
        super(id, name);
        this.number = number;
    }



    public Integer getNumber() {
        return number;
    }

}

