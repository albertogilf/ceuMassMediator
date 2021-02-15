package services.rest.api.response;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class ViewDataModel {


    private Map<String, Object> dataModel = new HashMap();



    public ViewDataModel(View view) {
        this.dataModel.put("state", view);
        this.dataModel.put("success", (view != null));
    }



    public Object put(String id, Object value) {
        return this.dataModel.put(id, value);
    }



    public Map getDataModel() {
        return this.dataModel;
    }

}

