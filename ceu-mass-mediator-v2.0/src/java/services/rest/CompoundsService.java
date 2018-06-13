package services.rest;

import javax.ws.rs.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.tomcat.jdbc.pool.DataSource;
import persistence.theoreticalCompound.NewCompound;
import facades.TheoreticalCompoundsFacade;
import utilities.Cadena;
import com.google.gson.Gson;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import persistence.compoundsFactories.NewCompoundFactory;
import persistence.theoreticalCompound.TheoreticalCompounds;
import persistence.theoreticalGroup.TheoreticalCompoundsGroup;

/**
 * Rest Service to get the compounds through the experimental mass
 *
 * @author alberto Gil de la Fuente. San Pablo-CEU
 * @version: 3.1, 26/10/2016
 */
@Path("/compound")
public class CompoundsService {

    Gson gson;
    @EJB
    private facades.TheoreticalCompoundsFacade ejbFacade;

    public CompoundsService() {
        gson = new Gson();
    }

    @GET
    @Path("/{experimentalmass}/{tolerance}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCompounds(@PathParam("experimentalmass") String experimentalMass,
            @PathParam("tolerance") String tolerance) {

        Double expMas = Cadena.extractFirstDouble(experimentalMass);
        String toleranceMode = "ppm";
        Double tol = Double.parseDouble(tolerance);
        List<TheoreticalCompounds> compounds;
        List<Double> queryMasses = new ArrayList<Double>();
        List<Double> queryRetentionTimes = new ArrayList<Double>();
        List<Boolean> significativeCompounds = new ArrayList<Boolean>();
        List<Map<Double, Integer>> queryCompositeSpectrum = new ArrayList<Map<Double, Integer>>();
        queryMasses.add(expMas);
        significativeCompounds.add(true);
        queryRetentionTimes.add(0d);
        String chemicalAlphabet = "ALL";
        String ionMode = "neutral";
        String MassesMode = "neutral";
        List<String> adducts = new LinkedList<String>();
        adducts.add("allNeutral");
        List<TheoreticalCompoundsGroup> listCompoundsGroup = new LinkedList<TheoreticalCompoundsGroup>();
        List<String> databases = new LinkedList<String>();
        String metabolitesType = "All except peptides";
        TheoreticalCompounds compound = new NewCompoundFactory().construct(
                            null, 200d, 10d, 200d, "M+2H", false, "", true);
        databases.add("All");
        compounds = ejbFacade.findRangeAdvanced(
                queryMasses, 
                queryRetentionTimes, 
                queryCompositeSpectrum,
                significativeCompounds,
                toleranceMode,
                tol, 
                chemicalAlphabet, 
                ionMode, MassesMode, 
                adducts, 
                listCompoundsGroup, 
                databases, 
                metabolitesType);
        compound = compounds.get(0);
        String jsoncompoundName = gson.toJson(listCompoundsGroup);
        return jsoncompoundName;
    }

    /**
     * Get the compounds extracted from the database with a tolerance of 10 ppm
     *
     * @param experimentalMass
     * @return
     */
    @POST
    @Path("/test/{experimentalmass}")
    @Produces({"application/xml", "application/json"})
    @Consumes({"application/xml", "application/json", "application/x-www-form-urlencoded"})
    public Response getBucket(@PathParam("experimentalmass") String experimentalMass) {
        System.out.println("Input Mass : " + experimentalMass);
        compoundForAPIRest compound = null;
        Double test = 0.0d;
        compound = new compoundForAPIRest(100.d, "METABOLITE 1");
        // compound=null;

        Context ctx;
        Connection conn;
        try {

            // Create a context
            ctx = new InitialContext();
            // Get the datasource from the conext of the application
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/newDataModel");
            //DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/testConnection");
            System.out.println("Created Link to Datasource");
            // Connection to DB
            conn = ds.getConnection();
            System.out.println("Created Connection");
            Statement stmt = conn.createStatement();
            System.out.println("Created Statement");

            String experimentalMassDecoded = URLDecoder.decode(experimentalMass, "UTF-8");
            test = 757.5667d;
            // test = Cadena.extractFirstDouble(experimentalMassDecoded);

            ResultSet rs = stmt.executeQuery("SELECT * FROM compounds WHERE compound_id = 1");
            //System.out.println("Query Executed");
            while (rs.next()) {
                String lastName = rs.getString("mass");
                System.out.println(lastName + "\n");
            }
            // compounds = HashDB.getBook(URLDecoder.decode(experimentalMass, "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SQLException | NamingException ex) {
            Logger.getLogger(CompoundsService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (compound == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            return Response.ok(compound).build();
        }
    }

    private TheoreticalCompoundsFacade lookupTheoreticalCompoundsFacadeBean() {
        try {
            Context c = new InitialContext();
            return (TheoreticalCompoundsFacade) c.lookup("java:global/ceuMassMediator/TheoreticalCompoundsFacade!presentation.TheoreticalCompoundsFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
