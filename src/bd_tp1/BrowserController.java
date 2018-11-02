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
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
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
    
    private databaseConnection dbc = new databaseConnection();
    int facturaID_selecionada;
    volatile Thread timer;
    private ObservableList<ObservableList> data;

    @FXML
    private void handleActioRefresh(ActionEvent event) {
        mostraFacturas();
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
        } catch (IOException ex) {
        }
    }
    
    @FXML
    private void handleActionMostrarLinhas(ActionEvent event){
        mostraLinhas(Integer.parseInt(TVfatura.getSelectionModel().getSelectedItem().toString().substring(1,TVfatura.getSelectionModel().getSelectedItem().toString().indexOf(","))));
    }

    public void mostraFacturas() {
        data = FXCollections.observableArrayList();
        try{
            
            String SQL = "SELECT TOP(50) * FROM Factura";
            //ResultSet
            ResultSet rs = dbc.createQuery(SQL);

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
            TVfatura.setItems(data);
          }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data");             
          }
    }

    public void mostraLinhas(int ID) {
        data = FXCollections.observableArrayList();
        try{
            
            String SQL = "SELECT * FROM FactLinha WHERE FacturaID=" + ID;
            //ResultSet
            ResultSet rs = dbc.createQuery(SQL);

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
            TVfactLinha.setItems(data);
          }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data");             
          }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mostraFacturas();
        /*facturaID_selecionada = 0;//pk na definicao da tabela os IDS teem de ser >=1

        timer = new Thread() {
            public void run() {
                while (timer != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mostraFacturas();
                            mostraLinhas();
                        }
                    });
                    try {
                        sleep(10000);//FAZER TIMER!!!!!!
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        timer.start();*/
    }

}
