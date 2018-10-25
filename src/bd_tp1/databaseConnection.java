package bd_tp1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Andre
 */
public class databaseConnection {

    private static String host;//localhost:1433
    private static String nomeDB;//DBProject
    private static String user;//test
    private static String password;//test
    private Label lblErro;
    public static Connection con;

    public databaseConnection() {

    }

    public databaseConnection(String host, String nomeDB, String user, String password, Label lblErro) {
        this.host = host;
        this.nomeDB = nomeDB;
        this.user = user;
        this.password = password;
        this.lblErro = lblErro;
    }
    
        public databaseConnection(String host, String nomeDB, String user, String password) {
        this.host = host;
        this.nomeDB = nomeDB;
        this.user = user;
        this.password = password;
    }
        
    public Connection getCon(){
        return this.con;
    }

    public boolean connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionURL = "jdbc:sqlserver://" + host + ";databaseName=" + nomeDB + ";user=" + user + ";password=" + password;
            con = DriverManager.getConnection(connectionURL);
        } catch (ClassNotFoundException ex) {
            lblErro.setText(ex.getMessage());
            return false;
        } catch (SQLException ex) {
            lblErro.setText(ex.getMessage());
            return false;
        }
        return true;
    }

    public ResultSet createQuery(String query) {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            return rs;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
