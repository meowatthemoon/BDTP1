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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class LogController implements Initializable {
    volatile Thread timer;
    @FXML
    GridPane gridLogs;
    
    @FXML
    TableView TVLog;
    @FXML
    TextField txtNumber;
    
    private ObservableList<ObservableList> data;
    int facturaID_selecionada;
    private databaseConnection dbc = new databaseConnection();
 
    @FXML
    private void handleActioRefresh(ActionEvent event) {
        mostraLogs();
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
    
    public void mostraLogs() {

        
        TVLog.getItems().clear();
        TVLog.getColumns().clear();
        
        data = FXCollections.observableArrayList();
        int number;
        try{
            number=Integer.parseInt(txtNumber.getText());
            if(number<1){
                txtNumber.setText("50");
                return;
            }
        }catch(Exception e){
            txtNumber.setText("50");
            return;
        }
        try{
            
            String SQL = "SELECT TOP("+number+") * FROM LogOperations order by DCriacao desc";
            //ResultSet
            ResultSet rs = dbc.createQuery(SQL);

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });

                TVLog.getColumns().addAll(col); 
                System.out.println("Column ["+i+"] ");
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
                System.out.println("Row [1] added "+row );
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            TVLog.setItems(data);
          }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data");             
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
                            mostraLogs();
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
