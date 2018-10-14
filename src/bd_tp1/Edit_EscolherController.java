/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class Edit_EscolherController implements Initializable {

    @FXML
    GridPane gridPane;

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
        ResultSet facturas = new databaseConnection().createQuery("Select * from Factura");
        try {
            gridPane.add(new Label("Factura ID"), 0, 0);
            gridPane.add(new Label("Cliente ID"), 1, 0);
            gridPane.add(new Label("Nome"), 2, 0);
            gridPane.add(new Label("Morada"), 3, 0);
            int index_linha = 1;
            while (facturas.next()) {
                Button b = new Button(facturas.getInt(1) + "");
                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        new editFactura(Integer.parseInt(b.getText()));
                        Parent window3; //we need to load the layout that we want to swap
                        try {
                            window3 = FXMLLoader.load(getClass().getResource("Edit.fxml"));
                            Scene newScene; //then we create a new scene with our new layout
                            newScene = new Scene(window3);
                            Stage mainWindow; //Here is the magic. We get the reference to main Stage.
                            mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            mainWindow.setScene(newScene); //here we simply set the new scene
                        } catch (IOException ex) {
                        }
                    }
                });
                gridPane.add(b, 0, index_linha);
                gridPane.add(new Label(facturas.getInt(2) + ""), 1, index_linha);
                gridPane.add(new Label(facturas.getString(3)), 2, index_linha);
                gridPane.add(new Label(facturas.getString(4)), 3, index_linha);
                index_linha++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Edit_EscolherController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
