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
    
    
    
    int maxDelay=30000;
    int minDelay=5000;
    int timerDelay=minDelay;
    
    private databaseConnection dbc = new databaseConnection();
    int facturaID_selecionada;
    volatile Thread timer;
    private ObservableList<ObservableList> data;

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
        TVfactLinha.getItems().clear();
        TVfactLinha.getColumns().clear();
        mostraLinhas(Integer.parseInt(TVfatura.getSelectionModel().getSelectedItem().toString().substring(1,TVfatura.getSelectionModel().getSelectedItem().toString().indexOf(","))));
    }

    public void mostraFacturas() {
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
            String SQL = "SELECT TOP("+number+") * FROM Factura ORDER BY FacturaID desc";
            ResultSet rs = dbc.createQuery(SQL);
            //Query de tempo
            ResultSet media=dbc.createQuery("select DATEDIFF(MILLISECOND, min(dcriacao),GetDate()) from LogOperations where referencia IN(Select  TOP("+ number + ") Referencia from LogOperations order by DCriacao desc)");
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
        
        //facturaID_selecionada = 0;//pk na definicao da tabela os IDS teem de ser >=1

        timer = new Thread() {
            public void run() {
                while (timer != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mostraFacturas();
                            try{
                                mostraLinhas(Integer.parseInt(TVfatura.getSelectionModel().getSelectedItem().toString().substring(1,TVfatura.getSelectionModel().getSelectedItem().toString().indexOf(","))));
                            }catch(Exception e){
                                
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
