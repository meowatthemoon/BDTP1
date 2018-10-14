/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author ramoa
 */
public class LogController implements Initializable {

    @FXML
    TableView tabelaLOG;
    
    @FXML
    Button voltar;
    
    @FXML
    TableColumn CNumReg;
    
    @FXML
    TableColumn CEventType;
    
    @FXML
    TableColumn CObjecto;
    
    @FXML
    TableColumn CValor;
    
    @FXML
    TableColumn CReferencia;
    
    @FXML
    TableColumn CUserID;
    
    @FXML
    TableColumn CTerminaID;
    
    @FXML
    TableColumn CTerminalName;
    
    @FXML
    TableColumn CDCriacao;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
