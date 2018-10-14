/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

/**
 *
 * @author Andre
 */
public class editFactura {
    private static int facturaID;
    public editFactura(int facturaID){
        this.facturaID=facturaID;
    }
    public static int getFacturaID(){
        return facturaID;
    }
}
