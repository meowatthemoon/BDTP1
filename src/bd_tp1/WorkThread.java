/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

/**
 *
 * @author Andre
 */
public class WorkThread extends Thread {
    private int num_acoes;
    private String type;
    private databaseConnection dbc;
    private String isolationLevel;
    private Button btnWork;
    private Button btnVoltar;
    private ChoiceBox CBopType;
    ChoiceBox CBIsolLevel;
    
    public WorkThread(int num_acoes,String type,String isolationLevel,Button btnWork, Button btnVoltar, ChoiceBox CBopType, ChoiceBox CBIsolLevel){
        this.num_acoes=num_acoes;
        this.type=type;
        this.isolationLevel=isolationLevel;
        this.btnWork=btnWork;
        this.btnVoltar = btnVoltar;
        this.CBIsolLevel = CBIsolLevel;
        this.CBopType = CBopType;
        dbc = new databaseConnection();
    }
    public void run(){
        switch (type) {
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
        Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnWork.setDisable(false);
                            btnVoltar.setDisable(false);
                            CBopType.setDisable(false);
                            CBIsolLevel.setDisable(false);
                        }
                    });
    }
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
        sql = "SET TRANSACTION ISOLATION LEVEL " + isolationLevel;
        System.out.println(sql);
        if(!dbc.createSettingQuery(sql)){
            System.out.println("Failed to set isolation level");
            return;
        }
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
            String sql = "SET TRANSACTION ISOLATION LEVEL " + isolationLevel;
            System.out.println(sql);
            if(!dbc.createSettingQuery(sql)){
                System.out.println("Failed to set isolation level");
                return;
            }
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
            sql = "SET TRANSACTION ISOLATION LEVEL " + isolationLevel;
            System.out.println(sql);
            if(!dbc.createSettingQuery(sql)){
                System.out.println("Failed to set isolation level");
                return;
            }
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
}
