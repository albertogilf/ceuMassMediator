/*
 * KBSystemQuery.java
 *
 * Created on 26-dic-2018, 21:37:31
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package services.rest.external;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.kbsystem.KBSystemRequest;

/**
 * Class to send request to the KBSystem created in Prolog
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build 3.0.0 26-dic-2018
 *
 * @author Alberto Gil de la Fuente
 */
public class KBSystemQuery {

    /**
     * Creates a new instance of KBSystemQuery
     */
    public KBSystemQuery() {

    }

    public static String KBRequestFromString(String JSONQuery) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\ceu\\Desktop\\alberto\\repo_CMM\\test\\testJSON.json"))) {
//            writer.write(JSONQuery);
//        } catch (IOException ex) {
//            Logger.getLogger(KBSystemQuery.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        Client c = ClientBuilder.newClient();
        WebTarget target = c.target("http://localhost:8081/");
        Entity.json(JSONQuery);
        Response response
                = target.path("process_experiment").request(MediaType.APPLICATION_JSON).post(Entity.json(JSONQuery));
        String JSONResult = response.readEntity(String.class);
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/home/alberto/alberto/repo/test/testResultsJSON.json"))) {
//            writer.write(JSONResult);
//        } catch (IOException ex) {
//            Logger.getLogger(KBSystemQuery.class.getName()).log(Level.SEVERE, null, ex);
//        }
        response.close();
        return JSONResult;
    }
    
    public static String KBRequestFromDOM(KBSystemRequest requestDOM) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\ceu\\Desktop\\alberto\\repo_CMM\\test\\testJSON.json"))) {
//            writer.write(JSONQuery);
//        } catch (IOException ex) {
//            Logger.getLogger(KBSystemQuery.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        Client c = ClientBuilder.newClient();
        WebTarget target = c.target("http://localhost:8081/process_experiment");
        Response response
                = target.path("lipididentification").request(MediaType.APPLICATION_JSON).post(Entity.json(requestDOM));
        String JSONResult = response.readEntity(String.class);
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("/home/alberto/alberto/repo/test/testResultsJSON.json"))) {
//            writer.write(JSONResult);
//        } catch (IOException ex) {
//            Logger.getLogger(KBSystemQuery.class.getName()).log(Level.SEVERE, null, ex);
//        }
        response.close();
        return JSONResult;
    }

}
