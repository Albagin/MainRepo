package albag98;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import albag98.DTO.BookDefinitionsIssuesDTO;
import albag98.Entities.Client;
import albag98.Entities.Order;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainScreenUserController
{
    private LoginController con;

    private Client client;

    @FXML
    private Button browse;

    @FXML
    private TableView<BookDefinitionsIssuesDTO> tableView;


    @FXML
    private TableColumn TitleColumn;
    @FXML
    private TableColumn DateColumn;
    @FXML
    private TableColumn PublisherColumn;
    @FXML
    private TableColumn GenreColumn;
    @FXML
    private TableColumn CreationColumn;



    public void setClient(Client client) {
        this.client = client;
    }

    public Button getBrowse() {
        return browse;
    }

    public void setCon(LoginController con) {
        this.con = con;
    }


    private EntityManager manager = Main.factory.createEntityManager();


    @FXML
    public void initialize()
    {
        TitleColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionsIssuesDTO,String>("title"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionsIssuesDTO, String>("dateOfReleaseS"));
        PublisherColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionsIssuesDTO, String>("publisher"));
        GenreColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionsIssuesDTO, String>("genre"));
        CreationColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionsIssuesDTO, String>("dateOfAcquisitionS"));
    }

    @FXML
    public void log_out()
    {
        Stage st = (Stage) con.getLogin().getScene().getWindow();
        Stage st2 = (Stage) browse.getScene().getWindow();


        con.clean_fields();

        st.show();
        st2.close();
    }

    /**
     * method will open new stage allowing user to see all BookDefinition objects currently in database
     * @throws IOException
     */
    @FXML
    private void browseStock() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/StockView.fxml"));

        Parent root = pom.load();

        StockViewController con = pom.getController();
        con.setCon(this);
        con.setClient(client);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 500));

        stage2.setTitle("Book Selection");

        stage2.show();
        tableView.getScene().getWindow().hide();
    }

    public void refresh()
    {
        Query q = manager.createQuery("select c from Client c where c.idClient = ?1")
                .setParameter(1, client.getIdClient());

        try
        {
            setClient((Client) q.getSingleResult());
            tableView.getItems().clear();
            mapTable();
        }
        catch (NoResultException e)
        {
            System.out.println("Error! Couldn't find client!\n (MainScreenUserController)");
        }
    }

    /**
     * method will open new stage allowing user to change his personal data
     * @throws IOException
     */
    @FXML
    private void editPersonals() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/EditPersonals.fxml"));

        Parent root = pom.load();

        EditPersonalsController con = pom.getController();
        con.setCon(this);
        con.setClient(client);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 500));

        stage2.setTitle("Edit Personals");

        stage2.show();
        tableView.getScene().getWindow().hide();
    }

    /**
     * method will open new stage allowing user to change password
     * @throws IOException
     */
    @FXML
    private void passChange() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/PassChangeView.fxml"));

        Parent root = pom.load();

        PassChangeController con = pom.getController();
        con.setCon(this);
        con.setClient(client);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 600, 400));

        stage2.setTitle("Change Password");

        stage2.show();
        tableView.getScene().getWindow().hide();
    }


    public void mapTable()
    {

        List<Order> lista;
        List<BookDefinitionsIssuesDTO> listDTO = new ArrayList<>();

        lista = client.getOrder();

        for (Order tab : lista)
        {
            if (tab.getDateOfFinalizing() == null)
            {
                BookDefinitionsIssuesDTO bookDTO = new BookDefinitionsIssuesDTO(tab.getIssue().getDefinition());

                bookDTO.setDate_of_Acquisition(tab.getIssue().getDateOfAcquisition());

                listDTO.add(bookDTO);
            }
        }
        tableView.getItems().addAll(listDTO);
    }
}
