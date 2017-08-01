package registration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author: San Pablo-CEU, Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016
 */
public class LoginDatabase {

    private static Connection con;

    // TODO connect to the database with real data
    public static Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/DATABASE_NAME", "PASSWORD", "PASSWORD");
            return con;
        } catch (SQLException ex) {
            System.out.println("\n Connection error \n"
                    + ex.getMessage());
            return null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void close() {
        try {
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(LoginDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean validateUserPassword(String user, String password) {
        PreparedStatement ps;

        try {
            // System.out.println("\n username: " + user);
            // System.out.println("\n password: " + password);
            ps = (PreparedStatement) con.prepareStatement("Select username, password from users where username = ? "
                    + "and password = ENCRYPT(?,?)");
            ps.setString(1, user);
            ps.setString(2, user);
            ResultSet rs = ps.executeQuery();
            System.out.println("Trying to get username " + ps);
            if (rs.next()) {
                //result found, means valid inputs
                System.out.println("Username Exists");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Login error -->" + ex.getMessage());
            return false;
        }
        return false;
    }

    public static int isAdmin(String username) {
        PreparedStatement ps;
        try {
            // System.out.println("\n username: " + user);
            // System.out.println("\n password: " + password);
            ps = (PreparedStatement) con.prepareStatement("Select is_admin from users where username = ? ");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            // System.out.println("Trying to get username " + ps);
            if (rs.next()) {
                //result found, means valid inputs
                return 1;
            }
        } catch (SQLException ex) {
            System.out.println("Login error -->" + ex.getMessage());
            return 0;
        }
        return 0;

    }

    public static int register(String username, String firstName, String LastName,
            String email, String password) {
        int exitCode = 3;
        PreparedStatement ps;
        ResultSet rs;
//TODO
// MANAGE ALL EXCEPTIONS (CHECK HTML FORMULARY!!!) EXCEPT FOR USERNAME AND EMAIL
        try {
            ps = (PreparedStatement) con.prepareStatement("Select user_id from users where username = ? ");
            ps.setString(1, username);

            rs = ps.executeQuery();
            System.out.println("Trying to get username " + ps);
            if (rs.next()) {
                //result found, means valid inputs
                System.out.println("\nusername already exists");
                exitCode = 1;
            } else {
                ps = (PreparedStatement) con.prepareStatement("Select user_id from users where email = ? ");
                ps.setString(1, email);
                rs = ps.executeQuery();
                if (rs.next()) {
                    //result found, means valid inputs
                    System.out.println("\nemail already exists");
                    exitCode = 2;
                } else {

                    String toInsert = "INSERT INTO users(username, first_name, last_name, email, password, reg_date,is_admin)"
                            + " VALUES('" + username + "', '" + firstName + "', '" + LastName + "', '" + email + "', ENCRYPT('" + password
                            + "', '" + username + "'), " + "current_date()" + ",0);";
                    System.out.println("\n" + toInsert);
                    Statement insertion;
                    insertion = connect().createStatement();
                    insertion.executeUpdate(toInsert);
                    exitCode = 0;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Login error -->" + ex.getMessage());
            exitCode = 3;
        }
        return exitCode;
    }

}
