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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class EditController implements Initializable {

    int facturaID;
    @FXML
    GridPane gridProdutos;
    @FXML
    TextField txtNome;
    databaseConnection dbc = new databaseConnection();;

    @FXML
    private void handleActioVoltar(ActionEvent event) {
        Parent window3; //we need to load the layout that we want to swap
        try {
            window3 = FXMLLoader.load(getClass().getResource("Edit_Escolher.fxml"));
            Scene newScene; //then we create a new scene with our new layout
            newScene = new Scene(window3);
            Stage mainWindow; //Here is the magic. We get the reference to main Stage.
            mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindow.setScene(newScene); //here we simply set the new scene
        } catch (IOException ex) {
        }
    }

    @FXML
    private void handleActionUpdateNome(ActionEvent event) {
        String sql;
        //Testar se não está vazio
        if ( txtNome.getText().toString().equals("") ||  txtNome.getText().toString().equals(" ")){
            System.out.println("Não pode ser Nome vazio!");
        }
        else{
            //String no TextView
            //System.out.println(txtNome.getText().toString()); 
            //Vamos mudar na Base de Dados, temos a faturaID
            sql = "Update Factura " + "Set Nome = '" + txtNome.getText().toString() + "'\n Where FacturaID = " + facturaID;
            System.out.println(sql);
            System.out.println(dbc.createModificationQuery(sql));
            
        }

    
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        facturaID = editFactura.getFacturaID();
        ResultSet rs = new databaseConnection().createQuery("Select Nome from Factura where FacturaID=" + facturaID);
        try {
            rs.next();
            txtNome.setText(rs.getString("Nome"));
        } catch (SQLException ex) {
        }
        gridProdutos.add(new Label("Produto ID"), 0, 0);
        gridProdutos.add(new Label("Designação"), 1, 0);
        gridProdutos.add(new Label("Quantidade"), 2, 0);
        ResultSet produtos = new databaseConnection().createQuery("Select ProdutoID,Designacao,Qtd from FactLinha where FacturaID=" + facturaID);
        int index_linha = 1;
        try {
            while (produtos.next()) {
                System.out.println(index_linha);
                gridProdutos.add(new Label(produtos.getInt("ProdutoID")+""), 0, index_linha);
                gridProdutos.add(new Label(produtos.getString("Designacao")), 1, index_linha);
                gridProdutos.add(new TextField(produtos.getInt("Qtd")+""), 2, index_linha);
                Button b = new Button("(fazer)");
                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("FAZER: UPDATE QTD");
                        //FAZER
                    }
                });
                gridProdutos.add(b, 3, index_linha);
                index_linha++;
            }
        } catch (SQLException ex) {
        }
    }

}
