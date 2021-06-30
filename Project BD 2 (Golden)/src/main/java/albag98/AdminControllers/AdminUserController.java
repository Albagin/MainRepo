package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import albag98.Entities.Client;
import albag98.Entities.Order;
import albag98.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminUserController
{
    private final SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

    private MainScreenAdminController con;

    public void setCon(MainScreenAdminController con) {
        this.con = con;
    }

    private List<Client> lista;

    private EntityManager manager = Main.factory.createEntityManager();

    @FXML
    private TableView<Client> tableView;

    @FXML
    private TableColumn IdColumn;
    @FXML
    private TableColumn NameColumn;
    @FXML
    private TableColumn SurnameColumn;
    @FXML
    private TableColumn LoginColumn;
    @FXML
    private TableColumn eMailColumn;
    @FXML
    private TableColumn PhoneColumn;
    @FXML
    private TableColumn StreetColumn;
    @FXML
    private TableColumn CityColumn;
    @FXML
    private TableColumn VoivodeshipColumn;
    @FXML
    private TextField eMailSearch;
    @FXML
    private TextField nameSearch;
    @FXML
    private TextField surnameSearch;
    @FXML
    private TextField loginSearch;

    @FXML
    private Button nButton;
    @FXML
    private Button pButton;


    @FXML
    private Label label;

    private int page = 1;
    private int quantity = 15;


    public void initialize()
    {
        IdColumn.setCellValueFactory(new PropertyValueFactory<Client,Long>("idClient"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<Client,String>("name"));
        SurnameColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("surname"));
        LoginColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("login"));
        eMailColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("eMail"));
        PhoneColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("phoneNumber"));
        StreetColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("street"));
        VoivodeshipColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("voivodeship"));
        CityColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("cityOfResidence"));


        mapTable();
    }

    /**
     * this method will deactivate selected user. He will not be displayed in application, but will remain in the database.
     */
    public void deleteUser()
    {
        Client client = tableView.getSelectionModel().getSelectedItem();

        if (client == null)
        {
           label.setText("Select user to delete first!");
        }
        else
        {
            manager.getTransaction().begin();

            try
            {
                client.setActive(false);
                order66(client);

                manager.merge(client);
                manager.getTransaction().commit();
            }
            catch (Exception e)
            {
                label.setText("Selected user cannot be removed!");
                manager.getTransaction().rollback();
            }
        }

        refresh();
    }

    /**
     * this method will free all BookIssue objects and Order objects assigned to Client objects given as parameter, then
     * deactivate said user
     * @param client user selected to be deactivated
     * @throws ParseException
     */
    private void order66(Client client) throws ParseException {
       Query q = manager.createQuery("select c from Order c where c.client.idClient = ?1 ")
               .setParameter(1, client.getIdClient());

       List<Order> lista = q.getResultList();

       Date date = dt.parse(new Date().toString());


       for (Order tab : lista)
       {
           manager.getTransaction().begin();

           try
           {
               tab.getIssue().setAway(false);
               tab.setDateOfFinalizing(date);
               manager.merge(tab);
               manager.getTransaction().commit();
           }
           catch (Exception c)
           {
               System.out.println("Error! Failed to terminate users orders!\n");
               manager.getTransaction().rollback();
           }
       }
    }

    @FXML
    public void back()
    {
        Stage st2 = (Stage) con.getAdd().getScene().getWindow();
        Stage st = (Stage) tableView.getScene().getWindow();

        st2.show();
        st.close();
    }

    public void next()
    {
        page++;
        if (lista.size() < quantity) nButton.setDisable(true);
        pButton.setDisable(false);
        refresh();
    }
    public void prev()
    {
        page--;
        if (page == 1) pButton.setDisable(true);
        nButton.setDisable(false);
        refresh();
    }

    @FXML
    private void refresh()
    {
        tableView.getItems().clear();

        mapTable();
    }


    private void mapTable()
    {
        Query q = manager.createQuery("select c from Client c where c.eMail like ?1 AND" +
                " c.name like ?2 AND c.surname like ?3 AND c.login like ?4 AND admin = ?5 AND active = ?6")
                .setParameter(1, ("%"+eMailSearch.getText()+"%"))
                .setParameter(2, ("%"+nameSearch.getText()+"%"))
                .setParameter(3, ("%"+surnameSearch.getText()+"%"))
                .setParameter(4, ("%"+loginSearch.getText()+"%"))
                .setParameter(5, false)
                .setParameter(6, true)
                .setMaxResults(quantity)
                .setFirstResult((page-1)*quantity);

        lista = q.getResultList();

        tableView.getItems().addAll(lista);
        System.out.println("point\n");
    }
}
