/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Andre
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField txtHost;
    @FXML
    private TextField txtDatabase;
    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblErro;
    
    @FXML
    private void handleActionLogin(ActionEvent event) {
        new databaseConnection(txtHost.getText(),txtDatabase.getText(),txtUser.getText(),txtPassword.getText(),lblErro).connect();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
