/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class WorkController implements Initializable {
    
    databaseConnection dbc = new databaseConnection();;
    
    @FXML
    TextField txtNumero;
    public void insert(){
        String sql;
        Random random = new Random();
        int factID = Math.abs(random.nextInt()) + 1;
        int clientID = Math.abs(random.nextInt()) + 1;
        int leftLimit = 65; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetNameStringLength = 6;
        int targetAddressStringLength = 20;
        StringBuilder buffer = new StringBuilder(targetNameStringLength);
        for (int i = 0; i < targetNameStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String nome = buffer.toString();
        for (int i = 0; i < targetAddressStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String address = buffer.toString();

        System.out.println(nome + "   " + address);
        
        
        sql = "INSERT INTO Factura VALUES (" + factID + "," + clientID + ",'" + nome + "','" + address + "')";
        System.out.println(sql);
        
        ResultSet rs = dbc.createQuery(sql);
        System.out.println(rs.toString());
        
        
    }
    public void update(){
        //TODO
    }
    public void delete(){
        try {
            ResultSet rs=new databaseConnection().createQuery("Select max(FacturaID) from Factura");
            rs.next();
            int maxID=rs.getInt(1);
            
            Random r = new Random();
            int aleat = r.nextInt(maxID) + 1;
            dbc.createQuery("delete from FactLinha where FacturaID="+maxID);
            dbc.createQuery("delete from Factura where FacturaID="+maxID);
            System.out.println("elimnei "+maxID+"?");
        } catch (SQLException ex) {
            Logger.getLogger(WorkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void handleActioWork(ActionEvent event) {
        System.out.println("entrei");
        try {
            int num_acoes = Integer.parseInt(txtNumero.getText());
            System.out.println("num acoes:"+num_acoes);
            //Se numero for <1
            if (num_acoes < 1) {
                txtNumero.setText("");
                return;
            }
            for (int i = 0; i < num_acoes; i++) {
                Random r = new Random();
                System.out.println("acao "+i);
                int aleat = r.nextInt(101 - 1) + 1;
                char operacao;
                if(aleat<20){
                    System.out.println("calling insert");
                    insert();
                }
                else if(aleat<70)
                    update();
                }
                else{
                    System.out.println("delete"+i);
                    delete();
                }
            }

        } catch (Exception e) {//Se não for um numero
            txtNumero.setText("");
        }
    }

    @FXML
    private void handleActioVoltar(ActionEvent event) {
        Parent window3; //we need to load the layout that we want to swap
        try {
            window3 = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
            Scene newScene; //then we create a new scene with our new layout
            newScene = new Scene(window3);
            Stage mainWindow; //Here is the magic. We get the reference to main Stage.
            mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindow.setScene(newScene); //here we simply set the new scene
        } catch (IOException ex) {
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
