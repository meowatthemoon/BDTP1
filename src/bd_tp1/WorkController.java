/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class WorkController implements Initializable {
    
    databaseConnection dbc = new databaseConnection();;
    
    @FXML
    TextField txtNumero;
    @FXML
    ChoiceBox CBopType;
    @FXML
    ChoiceBox CBIsolLevel;
    
    public String randomString(int length){
        int leftLimit = 65; // letter 'a'
        int rightLimit = 122; // letter 'Z'
        StringBuilder bufferDESIG = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            bufferDESIG.append((char) randomLimitedInt);
        }
        return bufferDESIG.toString();
    }
    public void insert(){
        //setup
        String sql;
        Random random = new Random();
        
        int factID;
        int clientID = Math.abs(random.nextInt()) + 1;
        //generation of nome
        String nome = randomString(12);
        //generation of address
        String address = randomString(30);
        //testing purposes
        System.out.println(nome + "   " + address);
        
        //setup SQL
        sql = "BEGIN TRANSACTION";
        dbc.createSettingQuery(sql);
        System.out.println("Transaction Begun: insert");
        ResultSet rs= dbc.createQuery("Select max(FacturaID) from Factura");
        try{
            if(rs.next())
                factID = rs.getInt(1) + 1;
            else
                factID = 1;
        } catch (SQLException ex){
            System.out.println(ex.getMessage());
            sql = "ROLLBACK";
            dbc.createSettingQuery(sql);
            System.out.println("Transaction Ended Failed to attain max ID");
            return;
        }
        sql = "INSERT INTO Factura VALUES (" + factID + "," + clientID + ",'" + nome + "','" + address + "')";
        System.out.println(sql);
        System.out.println(dbc.createModificationQuery(sql));
               
        
        //Depois de inserimos a Fatura, temos de inserir as suas linhas, assim como os seus produtos.
        //Vamos primeiro fazer um ciclo de quantos produtos a fatura irá ter, vamos supor de 2 a 10;
        Random r = new Random();
        int Low = 2;
        int High = 10;
        int numeroProdutos = r.nextInt(High-Low) + Low;
        
        System.out.println("       --> Criaste " + numeroProdutos + " produtos");
        
        //Criar uma FactLinha para cada Produto:
        for (int i = 0; i <numeroProdutos ; i++){
            
            int produtoID = Math.abs(random.nextInt()) + 1;
            String designacao = randomString(20);

            int preco = random.nextInt(99999999) + 1;
            int quantidade = random.nextInt(99999999) + 1;
            
            System.out.println("        Designacao: " + designacao + " Preço: " + preco);
            sql = "INSERT INTO FactLinha VALUES (" + factID + "," + produtoID + ",'" + designacao + "'," + preco + "," + quantidade + ")";
            System.out.println(sql);
            System.out.println(dbc.createModificationQuery(sql));

        }
        sql = "COMMIT";
        dbc.createSettingQuery(sql);
        System.out.println("Transaction Ended");
            
    }
    public void update(){
        try {
            dbc.createSettingQuery("BEGIN TRANSACTION");
            System.out.println("Transaction Begun: Update");
            //escolher ID aleatório
            ResultSet rs= dbc.createQuery("Select max(FacturaID) from Factura");
            rs.next();
            int maxID=rs.getInt(1);
            Random r = new Random();
            int facturaID = r.nextInt(maxID) + 1;
            //mudar id, nome e morada do cliente
            r=new Random();
            int clienteID=r.nextInt(100)+1;
            String nome=randomString(20);
            String morada=randomString(20);
            //update
            dbc.createQuery("update Factura set ClienteID="+clienteID+",Nome='"+nome+"',Morada='"+morada+"' where FacturaID="+facturaID);
            //commit transaction
            dbc.createSettingQuery("COMMIT");
            System.out.println("Transaction Ended");
        } catch (SQLException ex) {
            Logger.getLogger(WorkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void delete(){ //This must be wrapped into a whole transaction
        try {
            String sql= "BEGIN TRANSACTION";
            dbc.createSettingQuery(sql);
            System.out.println("Transaction Begun: delete");
            //delete operations
            ResultSet rs= dbc.createQuery("Select max(FacturaID) from Factura");
            rs.next();
            int maxID=rs.getInt(1);
            Random r = new Random();
            int facturaID = r.nextInt(maxID) + 1;
            dbc.createQuery("delete from FactLinha where FacturaID="+facturaID);
            dbc.createQuery("delete from Factura where FacturaID="+facturaID);
            System.out.println("elimnei "+facturaID+"?");
            //commit transaction
            sql = "COMMIT";
            dbc.createSettingQuery(sql);
            System.out.println("Transaction Ended: delete");
        } catch (SQLException ex) {
            Logger.getLogger(WorkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void handleActioWork(ActionEvent event) {
        try {
            int num_acoes = Integer.parseInt(txtNumero.getText());
            //Se numero for <1
            if (num_acoes < 1) {
                txtNumero.setText("");
                return;
            }
            switch ((String) CBopType.getValue()) {
                case "Insert":
                    System.out.println("********************************************INSERT*****************");
                    for (int i = 0; i < num_acoes; i++) {
                        insert();
                    }
                    break;
                case "Update":
                    System.out.println("********************************************UPDATE*****************");
                    for (int i = 0; i < num_acoes; i++) {
                        update();
                    }
                    break;
                case "Delete":
                    System.out.println("********************************************DELETE******************");
                    for (int i = 0; i < num_acoes; i++) {
                        delete();
                    }
                    break;
                case "Random":
                    System.out.println("********************************************RANDOM*****************");                    
                    for (int i = 0; i < num_acoes; i++) {
                        Random r = new Random();
                        System.out.println("acao "+i);
                        int aleat = r.nextInt(101 - 1) + 1;
                        char operacao;
                        if(aleat<20){
                            System.out.println("insert"+i);
                            insert();
                        }
                        else if(aleat<70){
                            System.out.println("update"+i);
                            update();
                        }
                        else{
                            System.out.println("delete"+i);
                            delete();
                        }
                    }
                    break;  
                default:
                    System.out.println("it bork");
                    break;
            }
        } catch (Exception e) {//Se não for um numero
            txtNumero.setText("");
        }
    }

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
        String[] workType = new String[4];
        workType[0]="Insert";
        workType[1]="Update";
        workType[2]="Delete";
        workType[3]="Random";
        CBopType.getItems().addAll((Object[]) workType);
        CBopType.getSelectionModel().select(0);
        String[] isolLevel = new String[4];
        isolLevel[0]="Not";
        isolLevel[1]="Fucking";
        isolLevel[2]="Implemented";
        isolLevel[3]="Yet";
        CBIsolLevel.getItems().addAll((Object[]) isolLevel);
    }

}
