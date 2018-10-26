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
import javafx.scene.control.ChoiceBox;
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
    @FXML
    ChoiceBox CBopType;
    @FXML
    ChoiceBox CBIsolLevel;
    
    public void insert(){
        //setup
        String sql;
        Random random = new Random();
        
        //factura id
        int factID = Math.abs(random.nextInt()) + 1;
        //client id
        int clientID = Math.abs(random.nextInt()) + 1;
        
        //setup for random string generation
        int leftLimit = 65; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetNameStringLength = 6;
        int targetAddressStringLength = 20;
        //generation of nome
        StringBuilder buffer = new StringBuilder(targetNameStringLength);
        for (int i = 0; i < targetNameStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String nome = buffer.toString();
        //generation of address
        for (int i = 0; i < targetAddressStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String address = buffer.toString();
        //testing purposes
        System.out.println(nome + "   " + address);
        
        //setup SQL
        sql = "BEGIN TRANSACTION";
        dbc.createQuery(sql);
        sql = "INSERT INTO Factura VALUES (" + factID + "," + clientID + ",'" + nome + "','" + address + "')";
        System.out.println(sql);
        ResultSet rs = dbc.createQuery(sql);
        sql = "COMMIT";
        dbc.createQuery(sql);
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
        try {
            int num_acoes = Integer.parseInt(txtNumero.getText());
            //Se numero for <1
            if (num_acoes < 1) {
                txtNumero.setText("");
                return;
            }
            switch ((String) CBopType.getValue()) {
                case "Insert":
                    for (int i = 0; i < num_acoes; i++) {
                        insert();
                    }
                    break;
                case "Update": 
                    for (int i = 0; i < num_acoes; i++) {
                        update();
                    }
                    break;
                case "Delete":
                    for (int i = 0; i < num_acoes; i++) {
                        delete();
                    }
                    break;
                case "Random":
                    for (int i = 0; i < num_acoes; i++) {
                        Random r = new Random();
                        System.out.println("acao "+i);
                        int aleat = r.nextInt(101 - 1) + 1;
                        char operacao;
                        if(aleat<20){
                            System.out.println("insert"+i);
                            insert();
                        }
                        else if(aleat<70){
                            System.out.println("update"+i);
                            update();
                        }
                        else{
                            System.out.println("delete"+i);
                            delete();
                        }
                    }
                    break;  
                default:
                    System.out.println("it bork");
                    break;
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
        String[] workType = new String[4];
        workType[0]="Insert";
        workType[1]="Update";
        workType[2]="Delete";
        workType[3]="Random";
        CBopType.getItems().addAll((Object[]) workType);
        CBopType.getSelectionModel().select(0);
        String[] isolLevel = new String[4];
        isolLevel[0]="Not";
        isolLevel[1]="Fucking";
        isolLevel[2]="Implemented";
        isolLevel[3]="Yet";
        CBIsolLevel.getItems().addAll((Object[]) isolLevel);
    }

}
