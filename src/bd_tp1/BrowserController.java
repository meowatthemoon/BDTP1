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
import javafx.application.Platform;
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
public class BrowserController implements Initializable {

    @FXML
    GridPane gridFacturas;
    @FXML
    GridPane gridLinhas;
    int facturaID_selecionada;
    volatile Thread timer;

    @FXML
    private void handleActioRefresh(ActionEvent event) {
        mostraFacturas();
        mostraLinhas();
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

    public void mostraFacturas() {
        gridFacturas.getChildren().clear();
        gridFacturas.add(new Label("Factura ID"), 0, 0);
        gridFacturas.add(new Label("Cliente ID"), 1, 0);
        gridFacturas.add(new Label("Nome"), 2, 0);
        gridFacturas.add(new Label("Morada"), 3, 0);
        int index_linha = 1;
        ResultSet facturas = new databaseConnection().createQuery("Select * from Factura");
        try {
            while (facturas.next()) {
                Button b = new Button(facturas.getInt("FacturaID") + "");
                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Button temp = (Button) event.getSource();
                        facturaID_selecionada = Integer.parseInt(temp.getText());
                        mostraLinhas();
                    }
                });
                gridFacturas.add(b, 0, index_linha);
                gridFacturas.add(new Label(facturas.getInt("ClienteID") + ""), 1, index_linha);
                gridFacturas.add(new Label(facturas.getString("Nome")), 2, index_linha);
                gridFacturas.add(new Label(facturas.getString("Morada")), 3, index_linha);
                index_linha++;
            }
        } catch (SQLException ex) {
        }
    }

    public void mostraLinhas() {
        gridLinhas.getChildren().clear();
        if (facturaID_selecionada < 1) {
            return;
        }
        gridLinhas.add(new Label("ProdutoID"), 0, 0);
        gridLinhas.add(new Label("Designacao"), 1, 0);
        gridLinhas.add(new Label("Preco"), 2, 0);
        gridLinhas.add(new Label("Qtd"), 3, 0);
        ResultSet linhas = new databaseConnection().createQuery("Select ProdutoID,Designacao,Preco,Qtd from FactLinha where FacturaID=" + facturaID_selecionada);
        try {
            int index_linha = 1;
            while (linhas.next()) {
                gridLinhas.add(new Label(linhas.getInt("ProdutoID") + ""), 0, index_linha);
                gridLinhas.add(new Label(linhas.getString("Designacao")), 1, index_linha);
                gridLinhas.add(new Label(linhas.getFloat("Preco") + ""), 2, index_linha);
                gridLinhas.add(new Label(linhas.getInt("Qtd") + ""), 3, index_linha);
                index_linha++;
            }
        } catch (SQLException ex) {
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        facturaID_selecionada = 0;//pk na definicao da tabela os IDS teem de ser >=1

        timer = new Thread() {
            public void run() {
                while (timer != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mostraFacturas();
                            mostraLinhas();
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
