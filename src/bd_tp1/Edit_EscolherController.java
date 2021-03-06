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
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * FXML Controller class
 *
 * @author Andre
 */
public class Edit_EscolherController implements Initializable {

    @FXML
    GridPane gridPane;
    @FXML
    ChoiceBox CBIsolLevel;
    @FXML
    TableView TVfatura;
    @FXML
    TextField TAstartId;
    @FXML
    Button BSelecionar;

    private ObservableList<ObservableList> data;
    int facturaID_selecionada;
    private databaseConnection dbc = new databaseConnection();

    @FXML
    private void handleChoiceBox(ActionEvent event) {

        //TIME OUT
        //
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable() {

            public String call() throws Exception {
                System.out.println(CBIsolLevel.getSelectionModel().getSelectedItem().toString());
                if (!dbc.createSettingQuery("SET TRANSACTION ISOLATION LEVEL " + CBIsolLevel.getSelectionModel().getSelectedItem().toString())) {
                    dbc.createSettingQuery("ROLLBACK");
                    return "Failed to set isolation level";
                }
                return "OK";
            }
        });
        try {
            System.out.println(future.get(5, TimeUnit.SECONDS)); //timeout is in 2 seconds
        } catch (TimeoutException e) {
            System.err.println("Timeout");
        } catch (InterruptedException ex) {
            System.out.println("error interruptedexception");
        } catch (ExecutionException ex) {
            System.out.println("error executionexception");
        }
        executor.shutdownNow();
        //

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
            mainWindow.setTitle("Controlo de Transações: André Correia, João Gomes, Miguel Brandão");
        } catch (IOException ex) {
        }
    }

    @FXML
    private void handleActionRefresh(ActionEvent event) {
        try {
            refresh(Integer.parseInt(TAstartId.getText()));
            BSelecionar.setDisable(false);
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void handleActionSelectionar(ActionEvent event) {
        if (TVfatura.getSelectionModel().isEmpty()) {

            final JPanel panel = new JPanel();

            JOptionPane.showMessageDialog(panel, "Por favor selecione uma fatura", "Aviso",
                    JOptionPane.WARNING_MESSAGE);

        } else {
            int facturaID = Integer.parseInt(TVfatura.getSelectionModel().getSelectedItem().toString().substring(1, TVfatura.getSelectionModel().getSelectedItem().toString().indexOf(",")));

            //TIMEOUT
            ResultSet rs;
            //
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<ResultSet> future = executor.submit(new Callable() {

                public ResultSet call() throws Exception {
                    ResultSet rs = new databaseConnection().createQuery("Select Nome from Factura where FacturaID=" + facturaID);

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

            new editFactura(facturaID, rs);
            new editFactura(CBIsolLevel.getSelectionModel().getSelectedItem().toString());
            Parent window3; //we need to load the layout that we want to swap
            try {
                window3 = FXMLLoader.load(getClass().getResource("Edit.fxml"));
                Scene newScene; //then we create a new scene with our new layout
                newScene = new Scene(window3);
                Stage mainWindow; //Here is the magic. We get the reference to main Stage.
                mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainWindow.setScene(newScene); //here we simply set the new scene
                mainWindow.setTitle("Editar Fatura com " + CBIsolLevel.getSelectionModel().getSelectedItem().toString());
                mainWindow.setResizable(false);
                mainWindow.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void refresh(int startID) {

        //TIME OUT
        ResultSet rs;

        //
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ResultSet> future = executor.submit(new Callable() {

            public ResultSet call() throws Exception {
                String SQL = "SELECT * FROM Factura ORDER BY FacturaID OFFSET " + startID + " ROWS FETCH NEXT 50 ROWS ONLY";
                //ResultSet
                ResultSet rs = dbc.createQuery(SQL);
                return rs;
            }
        });
        try {
            rs = future.get(5, TimeUnit.SECONDS); //timeout is in 2 seconds
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

        TVfatura.getItems().clear();
        TVfatura.getColumns().clear();
        data = FXCollections.observableArrayList();
        try {

            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                TVfatura.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            TVfatura.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String[] isolLevel = new String[4];

        isolLevel[0] = "READ UNCOMMITTED";
        isolLevel[1] = "READ COMMITTED";
        isolLevel[2] = "REPEATABLE READ";
        isolLevel[3] = "SERIALIZABLE";
        CBIsolLevel.getItems().addAll((Object[]) isolLevel);
        CBIsolLevel.getSelectionModel().select(1);
        TAstartId.setText("0");

        BSelecionar.setDisable(true);
    }

}
