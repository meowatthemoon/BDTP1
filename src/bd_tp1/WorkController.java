/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Random;
import java.util.ResourceBundle;
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

    @FXML
    TextField txtNumero;
    public void insert(){
        //TODO
    }
    public void update(){
        //TODO
    }
    public void delete(){
        //TODO
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
            databaseConnection dbc = new databaseConnection("localhost:1433", "TBD_TRAB1", "user", ""); //don't hardcore the values
            if(dbc.connect())
                System.out.println("connection created --- Working in " + txtNumero.getText() + " rows of data");
            else{
                System.out.println("Connection failed");
                return;
            }
            for (int i = 0; i < num_acoes; i++) {
                Random r = new Random();
                int aleat = r.nextInt(101 - 1) + 1;
                char operacao;
                if(aleat<20)
                    insert();
                else if(aleat<70)
                    update();
                else
                    delete();
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
