/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class LogController implements Initializable {
    volatile Thread timer;
    @FXML
    GridPane gridLogs;
    @FXML
    private void handleActioRefresh(ActionEvent event) {
        mostraLogs();
    }
    @FXML
    private void handleActioVoltar(ActionEvent event) {
        timer = null;//stop
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
    public void mostraLogs() {
        gridLogs.getChildren().clear();
        gridLogs.add(new Label("NumReg"), 0, 0);
        gridLogs.add(new Label("Tipo Evento"), 1, 0);
        gridLogs.add(new Label("Objeto"), 2, 0);
        gridLogs.add(new Label("Valor"), 3, 0);
        gridLogs.add(new Label("Referência"), 4, 0);
        gridLogs.add(new Label("UserID"), 5, 0);
        gridLogs.add(new Label("TerminalID"), 6, 0);
        gridLogs.add(new Label("TerminalName"), 7, 0);
        gridLogs.add(new Label("DCriacao"), 8, 0);
        ResultSet linhas = new databaseConnection().createQuery("Select * from LogOperations");
        try {
            int index_linha = 1;
            while (linhas.next()) {
                gridLogs.add(new Label(linhas.getInt("NumReg") + ""), 0, index_linha);
                gridLogs.add(new Label(linhas.getString("EventType")), 1, index_linha);
                gridLogs.add(new Label(linhas.getString("Objeto") + ""), 2, index_linha);
                gridLogs.add(new Label(linhas.getString("Valor") + ""), 3, index_linha);
                gridLogs.add(new Label(linhas.getString("Referencia") + ""), 4, index_linha);
                gridLogs.add(new Label(linhas.getString("UserID") + ""), 5, index_linha);
                gridLogs.add(new Label(linhas.getString("TerminalID") + ""), 6, index_linha);
                gridLogs.add(new Label(linhas.getString("TerminalName") + ""), 7, index_linha);
                gridLogs.add(new Label(linhas.getString("DCriacao") + ""), 8, index_linha);
                index_linha++;
            }
        } catch (SQLException ex) {
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        timer = new Thread() {
            public void run() {
                while (timer != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mostraLogs();
                        }
                    });
                    try {
                        sleep(10000);//FAZER TIMER!!!!!!
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        timer.start();
    }    
    
}
