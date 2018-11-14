/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.sql.ResultSet;

/**
 *
 * @author Andre
 */
public class editFactura {
    private static int facturaID;
    private static String nivelisolamento;
    private static ResultSet rs;
    public editFactura(int facturaID,ResultSet rs){
        this.facturaID=facturaID;
        this.rs=rs;
    }
    public static int getFacturaID(){
        return facturaID;
    }
    public editFactura(String nivelisolamento){
        this.nivelisolamento = nivelisolamento;
    }
    public static String getNIFactura(){
        return nivelisolamento;
    }
    public static ResultSet getResultSet(){
        return rs;
    }
}
