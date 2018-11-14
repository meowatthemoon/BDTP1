/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_tp1;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class BrowserController implements Initializable {

    @FXML
    TableView TVfatura;
    @FXML
    TableView TVfactLinha;
    @FXML
    TextField txtNumber;
    
    
    boolean freeze = false;
    int maxDelay=30000;
    int minDelay=5000;
    int timerDelay=minDelay;
    
    private databaseConnection dbc = new databaseConnection();
    int facturaID_selecionada;
    volatile Thread timer;
    private ObservableList<ObservableList> data;

    @FXML
    private void handleActionFreezeView(){
        freeze = !freeze;
        System.out.println(freeze);
    }
    
    @FXML
    private void handleActioRefresh(ActionEvent event) {
        try{
            
            mostraFacturas();
        } catch(NumberFormatException ex){
            System.out.println(ex.getMessage());
        }
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
            mainWindow.setTitle("Controlo de Transações: André Correia, João Gomes, Miguel Brandão");
        } catch (IOException ex) {
        }
    }
    
    @FXML
    private void handleActionMostrarLinhas(ActionEvent event){
        
        mostraLinhas(Integer.parseInt(TVfatura.getSelectionModel().getSelectedItem().toString().substring(1,TVfatura.getSelectionModel().getSelectedItem().toString().indexOf(","))));
    }

    public void mostraFacturas() {
        
        
        //FAZER TIME OUT
        
        
        data = FXCollections.observableArrayList();
        int number=50;
        try{
            number=Integer.parseInt(txtNumber.getText());
        }catch(Exception e){
            txtNumber.setText("50");
        }
        try{
            
            
            /*
            //Inicio da pesquisa
            ResultSet dataInicio= dbc.createQuery("Select GetDate()");
            dataInicio.next();
            */
            //Query em si
            dbc.createSettingQuery("BEGIN TRANSACTION");
            dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL READ COMMITTED");
        }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data");             
        }
        ResultSet rs;
        //  TIME OUT
        //
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ResultSet> future = executor.submit(new Callable() {

            public ResultSet call() throws Exception {
                String SQL = "SELECT TOP("+Integer.parseInt(txtNumber.getText())+") * FROM Factura ORDER BY FacturaID desc";
                ResultSet rs = dbc.createQuery(SQL);
                return rs;
            }
        });
        try {
            rs=future.get(5, TimeUnit.SECONDS); //timeout is in 2 seconds
        } catch (TimeoutException e) {
            System.err.println("Timeout");
            dbc.createSettingQuery("ROLLBACK");
            return;
        } catch (InterruptedException ex) {
            System.out.println("error interruptedexception");
            dbc.createSettingQuery("ROLLBACK");
            return;
        } catch (ExecutionException ex) {
            System.out.println("error executionexception");
            dbc.createSettingQuery("ROLLBACK");
            return;
        }
        executor.shutdownNow();
        //
        TVfatura.getItems().clear();
        TVfatura.getColumns().clear();
        try{
            dbc.createSettingQuery("COMMIT");
            //Query de tempo
            dbc.createSettingQuery("BEGIN TRANSACTION");
            dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL READ COMMITTED");
            ResultSet media=dbc.createQuery("select DATEDIFF(MILLISECOND, min(dcriacao),GetDate()) from LogOperations where referencia IN(Select  TOP("+ number + ") Referencia from LogOperations order by DCriacao desc)");
            dbc.createSettingQuery("COMMIT");
            media.next();
            /*
            //Fim da pesquisa
            ResultSet dataFim= dbc.createQuery("Select GetDate()");
            dataFim.next();
            //Calcular tempo
            ResultSet tempoQ=dbc.createQuery("select DATEDIFF(MILLISECOND,'"+dataInicio.getString(1)+"','"+dataFim.getString(1)+"')");
            tempoQ.next();*/
            int tempo=media.getInt(1);
            if(tempo>maxDelay)
                tempo=maxDelay;
            else if(tempo<minDelay)
                tempo=minDelay;
            timerDelay=tempo;
            System.out.println(timerDelay);
            
            

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });

                TVfatura.getColumns().addAll(col); 
            }

            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            TVfatura.setItems(data);
          }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data");             
          }
    }

    public void mostraLinhas(int ID) {
        
        
        
        data = FXCollections.observableArrayList();
        try{
            dbc.createSettingQuery("BEGIN TRANSACTION");
            dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL READ COMMITTED");
            dbc.createSettingQuery("COMMIT");
        }catch(Exception e){
        }
        ResultSet rs;
        
        //  TIME OUT
        //
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ResultSet> future = executor.submit(new Callable() {

            public ResultSet call() throws Exception {
                //ResultSet
                String SQL = "SELECT * FROM FactLinha WHERE FacturaID=" + ID;
                ResultSet rs = dbc.createQuery(SQL);
                return rs;
            }
        });
        try {
            rs=future.get(5, TimeUnit.SECONDS); //timeout is in 2 seconds
        } catch (TimeoutException e) {
            System.err.println("Timeout");
            return;
        } catch (InterruptedException ex) {
            System.out.println("error interruptedexception");
            return;
        } catch (ExecutionException ex) {
            System.out.println("error executionexception");
            return;
        }
        executor.shutdownNow();
        //
        TVfactLinha.getItems().clear();
        TVfactLinha.getColumns().clear();
            
        try{

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });

                TVfactLinha.getColumns().addAll(col); 
            }

            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            TVfactLinha.setItems(data);
          }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data");             
          }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //facturaID_selecionada = 0;//pk na definicao da tabela os IDS teem de ser >=1

        TVfatura.getItems().clear();
        TVfatura.getColumns().clear();
        
        timer = new Thread() {
            public void run() {
                while (timer != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("beggining update");
                            if(!freeze){
                                System.out.println("updated");
                                mostraFacturas();
                                try{
                                    mostraLinhas(Integer.parseInt(TVfatura.getSelectionModel().getSelectedItem().toString().substring(1,TVfatura.getSelectionModel().getSelectedItem().toString().indexOf(","))));
                                }catch(Exception e){
                                    
                                }
                            }
                        }
                    });
                    try {
                        sleep(timerDelay);//FAZER TIMER!!!!!!
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        timer.start();
    }

}
