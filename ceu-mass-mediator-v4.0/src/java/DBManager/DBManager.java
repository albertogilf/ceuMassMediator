/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBManager;

import java.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;

/**
 *
 * @author: San Pablo-CEU, Maria Postigo Fliquete
 * @version: 4.0, 31/12/2016
 */
public class DBManager {

    private Context ctx;
    private Connection conn;

    public DBManager() {
        try {
            ctx = new InitialContext();
            //ds = (DataSource) ctx.lookup(dsName);
        } catch (NamingException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection connect() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            //conn = DriverManager.getConnection("jdbc:mysql://localhost/DATABASE_NAME", "password", "password");

            DataSource ds;
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/linkToNewDataModel");
            this.conn = ds.getConnection();

        } catch (NamingException | SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.conn;
    }

    public void disconnect() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            System.out.println("exception: " + e);
        }
    }


}
