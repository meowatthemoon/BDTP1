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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

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
    private ChoiceBox CBopType;
    private ChoiceBox CBIsolLevel;
    private static ProgressBar PBtransProgress;
    private boolean[] disabled;
    
    
    private static double progress;
    private static boolean running;
    
    public WorkThread(int num_acoes,String type,String isolationLevel,Button btnWork, ChoiceBox CBopType, ChoiceBox CBIsolLevel, ProgressBar PBtransProgress,boolean[] disabled){
        this.num_acoes=num_acoes;
        this.type=type;
        this.isolationLevel=isolationLevel;
        this.btnWork=btnWork;
        this.CBIsolLevel = CBIsolLevel;
        this.CBopType = CBopType;
        this.PBtransProgress = PBtransProgress;
        this.disabled=disabled;
        dbc = new databaseConnection();
    }
    private static void setProgress(double progress) {
        PBtransProgress.setProgress(progress);
    }
    public void run(){
        if (running) {
            PBtransProgress.setProgress(progress);
            return;
        }
        running = true;
        
        disabled[0]=true;
        PBtransProgress.setDisable(false);
        int count=0;
        switch (type) {
            case "Insert":
                System.out.println("********************************************INSERT*****************");
                for (int i = 0; i < num_acoes; i++) {
                    insert();
                    count++;
                    progress = (double) count/num_acoes;
                    setProgress(progress);
                }
                break;
            case "Update":
                System.out.println("********************************************UPDATE*****************");
                for (int i = 0; i < num_acoes; i++) {
                    update();
                    count++;
                    progress = (double) count/num_acoes;
                    setProgress(progress);
                }
                break;
            case "Delete":
                System.out.println("********************************************DELETE******************");
                for (int i = 0; i < num_acoes; i++) {
                    delete();
                    count++;
                    progress = (double) count/num_acoes;
                    setProgress(progress);
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
                    count++;
                    progress = (double) count/num_acoes;
                    setProgress(progress);
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
                            CBopType.setDisable(false);
                            CBIsolLevel.setDisable(false);
                            progress = (double) 0;
                            setProgress(progress);
                            PBtransProgress.setDisable(true);
                        }
                    });
        disabled[0]=false;
        running = false;
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
        //referência da transação para o log
        String ref;
        try {
            ResultSet date=dbc.createQuery("select GetDate()");
            date.next();
            ref = "G1-"+date.getString(1);
            System.out.println("REFERÊNCIA : "+ref);
        } catch (SQLException ex) {
            System.out.println("ERRRRRRRRO!!!!! THIS SHUD NEVER DISPLAY - OBTER DATA PARA CONSTRUIR REFERENCIA NO INSERT :"+ex.getMessage());
            return;
        }
        //setup
        String sql;
        Random random = new Random();
        
        int factID;
        int clientID = Math.abs(random.nextInt()) + 1;
        //generation of nome
        String nome = randomString(12);
        //generation of address
        String address = randomString(30);
        //Log
        dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('I','Begin',GetDate(),'"+ref+"')");
        //Begin Transaction
        dbc.createSettingQuery("BEGIN TRANSACTION");
        //Isolation Level
        if(!dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL " + isolationLevel)){
            return;
        }
        //Inserir Fatura
        boolean sucess = false;
        try{
            for(int i=0;i<5;i++){
                ResultSet rs= dbc.createQuery("Select max(FacturaID) from Factura");
                if(rs.next())
                    factID = rs.getInt(1) + 1;
                else
                    factID = 1;
                /*if(random.nextInt(10)<8)  //this bit of code makes transactions randomly fail to insert sometimes used to test
                    factID = 1;*/           //if the try system is working!
                if(dbc.createModificationQuery("INSERT INTO Factura VALUES (" + factID + "," + clientID + ",'" + nome + "','" + address + "')")==-1){
                    continue;
                } else{
                    inserirLinhas(factID, ref);
                    sucess = true;
                    System.out.println("Sucess at try # " + (i+1));
                    break;
                }
            }
            if(!sucess){
                System.out.println("Failed to insert all 5 tries");
                dbc.createSettingQuery("ROLLBACK");
                dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('I','Rollback',GetDate(),'"+ref+"')");
            }
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    public void inserirLinhas(int factID, String ref){
        //Depois de inserimos a Fatura, temos de inserir as suas linhas, assim como os seus produtos.
        //Vamos primeiro fazer um ciclo de quantos produtos a fatura irá ter, vamos supor de 2 a 10;
        Random random = new Random();
        int Low = 2;
        int High = 10;
        int numeroProdutos = random.nextInt(High-Low) + Low;
        
        //Criar uma FactLinha para cada Produto:
        for (int y = 0; y <numeroProdutos ; y++){
            
            int produtoID = Math.abs(random.nextInt()) + 1;
            String designacao = randomString(20);

            int preco = random.nextInt(99999999) + 1;
            int quantidade = random.nextInt(99999999) + 1;
            
            if(dbc.createModificationQuery("INSERT INTO FactLinha VALUES (" + factID + "," + produtoID + ",'" + designacao + "'," + preco + "," + quantidade + ")")==-1){
                dbc.createSettingQuery("ROLLBACK");
                dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('I','Rollback',GetDate(),'"+ref+"')");
                return;
            }

        }
        dbc.createSettingQuery("COMMIT");
        dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('I','Commit',GetDate(),'"+ref+"')");
        System.out.println("Transaction Ended");
    }
    
    public void update(){
        //referência da transação para o log
        String ref;
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
        //Isolation Level
        if(!dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL " + isolationLevel)){
            System.out.println("Failed to set isolation level");
            dbc.createSettingQuery("ROLLBACK");
            return;
        }
        try {
            
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
            dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('U','Commit',GetDate(),'"+ref+"')");
            
            System.out.println("Transaction Ended");
        } catch (SQLException ex) {
            Logger.getLogger(WorkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void delete(){ //This must be wrapped into a whole transaction
        //referência da transação para o log
        String ref;
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
        dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('D','Begin',GetDate(),'"+ref+"')");
        //Begin Transaction
        dbc.createSettingQuery("BEGIN TRANSACTION");
        //Isolation Level
        if(!dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL " + isolationLevel)){
            dbc.createSettingQuery("ROLLBACK");
            System.out.println("Failed to set isolation level");
            return;
        }
        try {
            //delete operations
            ResultSet rs= dbc.createQuery("Select max(FacturaID) from Factura");
            rs.next();
            int maxID=rs.getInt(1);
            Random r = new Random();
            int facturaID = r.nextInt(maxID) + 1;
            dbc.createQuery("delete from FactLinha where FacturaID="+facturaID);
            dbc.createQuery("delete from Factura where FacturaID="+facturaID);
            //commit transaction
            dbc.createSettingQuery("COMMIT");
            dbc.createModificationQuery("insert into LogOperations(EventType, Objecto, Valor, Referencia) values('D','Commit',GetDate(),'"+ref+"')");
            System.out.println("Transaction Ended: delete");
        } catch (SQLException ex) {
            Logger.getLogger(WorkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
