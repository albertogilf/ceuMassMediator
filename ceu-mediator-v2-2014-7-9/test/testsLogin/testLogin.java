/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testsLogin;

import registration.LoginDatabase;

/**
 *
 * @author alberto
 */
public class testLogin {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        LoginDatabase ldb = new LoginDatabase();
        ldb.connect();
        
        ldb.validateUserPassword("user1","password1");
        ldb.close();
        
    }
}
