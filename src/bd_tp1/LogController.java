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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ramoa
 */
public class LogController implements Initializable {
    
    @FXML
    Button voltar;
    
    @FXML
    GridPane gridLog;
    
    //Ainda não sei se preciso disto:
    volatile Thread timer;
    
    //N Tabelas que o cliente quiser
    int numeroTabelas = 10;
    
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
    
    public void mostrarLogOperations(){
        
        gridLog.getChildren().clear();
        gridLog.add(new Label("NumReg"),        0, 0);
        gridLog.add(new Label("EventType"),     1, 0);
        gridLog.add(new Label("Objecto"),       2, 0);
        gridLog.add(new Label("Valor"),         3, 0);
        gridLog.add(new Label("Referência"),    4, 0);
        gridLog.add(new Label("UserID"),        5, 0);
        gridLog.add(new Label("TerminaID"),     6, 0);
        gridLog.add(new Label("TerminalName"),  7, 0);
        gridLog.add(new Label("DCriacao"),      8, 0);
       
        //  Fazer:
        // ResultSet linhasLog2 = new databaseConnection().createQuery("Select ProdutoID,Designacao,Preco,Qtd from FactLinha where FacturaID=" + facturaID_selecionada);
        
        
        ResultSet linhasLog = new databaseConnection().createQuery("Select TOP " + numeroTabelas + "* from LogOperations");

        try {
            int index_linha = 1;
            while (linhasLog.next()) {
                //Confirmar depois se está correto
                gridLog.add(new Label(linhasLog.getInt("NumReg") + ""),     0, index_linha);
                gridLog.add(new Label(linhasLog.getString("EventType")),    1, index_linha);
                gridLog.add(new Label(linhasLog.getString("Objecto")),      2, index_linha);
                gridLog.add(new Label(linhasLog.getString("Valor")),        3, index_linha);
                gridLog.add(new Label(linhasLog.getString("Referencia")),   4, index_linha);
                gridLog.add(new Label(linhasLog.getString("UserID")),       5, index_linha);
                gridLog.add(new Label(linhasLog.getString("TerminalD")),    6, index_linha);
                gridLog.add(new Label(linhasLog.getString("TerminalName")), 7, index_linha);
                gridLog.add(new Label(linhasLog.getDate("DCriacao") + ""),  8, index_linha);
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
                            mostrarLogOperations();
                        }
                    });
                    try {
                        sleep(10000);//FAZER TEMPO
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        timer.start();
    
    }    
    
    
    
}
