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
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class EditController implements Initializable {
    @FXML
    Label lbl;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbl.setText(""+editFactura.getFacturaID());
    }    
    
}
