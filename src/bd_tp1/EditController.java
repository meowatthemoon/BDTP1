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
import java.util.ArrayList;
import java.util.List;
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
    String ref;
    String nivelisolamento;
    @FXML
    GridPane gridProdutos;
    @FXML
    TextField txtNome;
    databaseConnection dbc = new databaseConnection();
    int contador = 0;
    List<String> lista_produtos = new ArrayList<String>();
    List<String> lista_quantidades = new ArrayList<String>();
    
    @FXML TextField txtteste;
    @FXML TextField txtdescricao;

 

    //função para ir buscar elemento segundo colunas e linhas da gridpane.
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
    for (Node node : gridPane.getChildren()) {
        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
            return node;
        }
    }
    return null;
    }
    
    @FXML
    private void handleActioVoltar(ActionEvent event) {
        
        dbc.createSettingQuery("ROLLBACK");
        dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('U','Rollback',GetDate(),'"+ref+"')");
        
        Parent window3; //we need to load the layout that we want to swap
        try {
            window3 = FXMLLoader.load(getClass().getResource("Edit_Escolher.fxml"));
            Scene newScene; //then we create a new scene with our new layout
            newScene = new Scene(window3);
            Stage mainWindow; //Here is the magic. We get the reference to main Stage.
            mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindow.setScene(newScene); //here we simply set the new scene
            mainWindow.setResizable(true);
            mainWindow.setTitle("Edit");            
        } catch (IOException ex) {
        }
    }

    @FXML
    private void handleActionUpdateNome(ActionEvent event){
        
        String sql1;
        //Testar se não está vazio
        if ( txtNome.getText().toString().equals("") ||  txtNome.getText().toString().equals(" ")){
            System.out.println("Não pode ser Nome vazio!");
        }
        else{
            //Vamos mudar na Base de Dados, temos a faturaID
            sql1 = "Update Factura " + "Set Nome = '" + txtNome.getText().toString() + "'\n Where FacturaID = " + facturaID;
            System.out.println(sql1);
            dbc.createModificationQuery(sql1);
        }
    }
    
    @FXML
    private void handleActionCommit(ActionEvent event) {
        /*String sql;
        //Testar se não está vazio 
        
        if ( txtNome.getText().toString().equals("") ||  txtNome.getText().toString().equals(" ")){
            System.out.println("Não pode ser Nome vazio!");
        }
        else{
            
            //Vamos mudar na Base de Dados, temos a faturaID
            sql = "Update Factura " + "Set Nome = '" + txtNome.getText().toString() + "'\n Where FacturaID = " + facturaID;
            dbc.createModificationQuery(sql);
            
            
            
            //Temos de atualizar os valores nos textfields...
            for(int i = 0; i < contador; i++){

                txtteste = (TextField) getNodeFromGridPane(gridProdutos,2,i+1);
                txtdescricao = (TextField) getNodeFromGridPane(gridProdutos,1,i+1);
                sql = "Update FactLinha " + "Set Qtd = '" + txtteste.getText() + "'\n Where FacturaID = '" + facturaID + "' and ProdutoID = " + lista_produtos.get(i);
                System.out.println(sql);
                System.out.println(dbc.createModificationQuery(sql));
                sql = "Update FactLinha " + "Set Designacao = '" + txtdescricao.getText() + "'\n Where FacturaID = '" + facturaID + "' and ProdutoID = " + lista_produtos.get(i);
                System.out.println(sql);
                System.out.println(dbc.createModificationQuery(sql));
                
            }

            */
            
            //Voltar à página original
            Parent window3; //we need to load the layout that we want to swap
            try {
                
                //commit transaction
                dbc.createSettingQuery("COMMIT");
                dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('U','Commit',GetDate(),'"+ref+"')");
                
                window3 = FXMLLoader.load(getClass().getResource("Edit_Escolher.fxml"));
                Scene newScene; //then we create a new scene with our new layout
                newScene = new Scene(window3);
                Stage mainWindow; //Here is the magic. We get the reference to main Stage.
                mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainWindow.setScene(newScene); //here we simply set the new scene
                mainWindow.setResizable(true);
                mainWindow.setTitle("Edit");            
            } catch (IOException ex) {
            }
            
        //}   
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        facturaID = editFactura.getFacturaID();
        nivelisolamento = editFactura.getNIFactura();
        System.out.println(nivelisolamento);
        
        //Quando entra neste fxml, têm de indicar que começou uma transação, e que os outros não podem acede-la.
        
        //referência da transação para o log
        try {
            ResultSet date=dbc.createQuery("select GetDate()");
            date.next();
            ref = "G1-"+date.getString(1);
            System.out.println("REFERÊNCIA : "+ref);
        } catch (SQLException ex) {
            System.out.println("ERRRRRRRRO!!!!! THIS SHIT SHUD NEVER DISPLAY - OBTER DATA PARA CONSTRUIR REFERENCIA NO INSERT :"+ex.getMessage());
            return;
        }

        //Log
        dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('U','Begin',GetDate(),'"+ref+"')");
        //Begin Transaction
        dbc.createSettingQuery("BEGIN TRANSACTION");
        System.out.println("**************BEGIN TRANSACTION *************");
        //Isolation Level
        System.out.println(nivelisolamento);
        if(!dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL " + nivelisolamento)){
            System.out.println("Failed to set isolation level");
            dbc.createSettingQuery("ROLLBACK");
            return;
        }
        
        
        ResultSet rs = new databaseConnection().createQuery("Select Nome from Factura where FacturaID=" + facturaID);
        try {
            rs.next();
            txtNome.setText(rs.getString("Nome"));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        gridProdutos.add(new Label("Produto ID"), 0, 0);
        gridProdutos.add(new Label("Designação"), 1, 0);
        gridProdutos.add(new Label("Quantidade"), 2, 0);
        ResultSet produtos = new databaseConnection().createQuery("Select ProdutoID,Designacao,Qtd from FactLinha where FacturaID=" + facturaID);
        int index_linha = 1;
        try {
            while (produtos.next()) {
                System.out.println(index_linha);
                Label produtoid = new Label(produtos.getInt("ProdutoID")+""); 
                lista_produtos.add(contador,produtoid.getText());
                gridProdutos.add(produtoid, 0, index_linha);
                TextField txt2=new TextField(produtos.getString("Designacao")+"");
                gridProdutos.add(txt2, 1, index_linha);
                TextField txt=new TextField(produtos.getInt("Qtd")+"");
                gridProdutos.add(txt, 2, index_linha);
                Label seta = new Label(" -> ");
                gridProdutos.add(seta,3,index_linha);
                Button b = new Button("Atualizar");
                b.setDefaultButton(true);
                
                b.setOnAction(new EventHandler<ActionEvent>(){
                    @Override public void handle(ActionEvent e) {
                        
                        String sql;
                        sql = "Update FactLinha " + "Set Qtd = '" + txt.getText() + "'\n Where FacturaID = '" + facturaID + "' and ProdutoID = " + produtoid.getText();
                        System.out.println(sql);
                        System.out.println(dbc.createModificationQuery(sql));
                        sql = "Update FactLinha " + "Set Designacao = '" + txt2.getText() + "'\n Where FacturaID = '" + facturaID + "' and ProdutoID = " + produtoid.getText();
                        System.out.println(sql);
                        System.out.println(dbc.createModificationQuery(sql));
                        
                    }
                });
                
                gridProdutos.add(b,4,index_linha);

                //lista_quantidades.add(contador, txt.getText());
                
                //gridProdutos.add(b, 3, index_linha);
                index_linha++;                 contador++;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    



}
