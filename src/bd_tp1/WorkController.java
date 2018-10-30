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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    @FXML
    ChoiceBox CBopType;
    @FXML
    ChoiceBox CBIsolLevel;
    @FXML
    Button btnWork;
    @FXML
    Button btnVoltar;
    
    @FXML
    private void handleActioWork(ActionEvent event) {
        try {
            int num_acoes = Integer.parseInt(txtNumero.getText());
            //Se numero for <1
            if (num_acoes < 1) {
                txtNumero.setText("");
                return;
            }
            btnWork.setDisable(true);
            btnVoltar.setDisable(true);
            CBopType.setDisable(true);
            CBIsolLevel.setDisable(true);
            new WorkThread(num_acoes,(String) CBopType.getValue(),(String)CBIsolLevel.getValue(),btnWork, btnVoltar, CBopType, CBIsolLevel).start();
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
        String[] isolLevel = new String[5];
        isolLevel[0]="READ UNCOMMITTED";
        isolLevel[1]="READ COMMITTED";
        isolLevel[2]="REPEATABLE READ";
        isolLevel[2]="READ_COMMITTED_SNAPSHOT";
        isolLevel[3]="SERIALIZABLE";
        isolLevel[4]="This is an error and should cause the transaction to abort";
        CBIsolLevel.getItems().addAll((Object[]) isolLevel);
        CBIsolLevel.getSelectionModel().select(1);
    }

}
