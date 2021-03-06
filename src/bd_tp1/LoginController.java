/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Andre
 */
public class LoginController implements Initializable {

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
        if (new databaseConnection(txtHost.getText(), txtDatabase.getText(), txtUser.getText(), txtPassword.getText(), lblErro).connect()) {
            Parent window3; //we need to load the layout that we want to swap
            try {
                //maybe close the connection here since we know it's working?
                window3 = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
                Scene newScene; //then we create a new scene with our new layout
                newScene = new Scene(window3);
                Stage mainWindow; //Here is the magic. We get the reference to main Stage.
                mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainWindow.setScene(newScene); //here we simply set the new scene
            } catch (IOException ex) {
                lblErro.setText(ex.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
