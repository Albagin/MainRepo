package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import albag98.Entities.BookIssue;
import albag98.Entities.Client;
import albag98.Entities.Order;
import albag98.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderController
{
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private MainScreenAdminController con;

    public void setCon(MainScreenAdminController con) {
        this.con = con;
    }

    private List<Order> lista;

    private EntityManager manager = Main.factory.createEntityManager();

    @FXML
    private TableView<Order> tableView;

    @FXML
    private TableColumn IdColumn;
    @FXML
    private TableColumn DateOfCreationColumn;
    @FXML
    private TableColumn DateOfFinalizingColumn;
    @FXML
    private TableColumn ClientIdColumn;
    @FXML
    private TableColumn IssueIdColumn;
    @FXML
    private TableColumn BookTitleColumn;
    @FXML
    private TableColumn ClientLoginColumn;
    @FXML
    private TextField loginSearch;
    @FXML
    private TextField issueSearch;
    @FXML
    private TextField idSearch;

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
        IdColumn.setCellValueFactory(new PropertyValueFactory<Order,Long>("idOrder"));

        DateOfCreationColumn.setCellFactory(column -> {
            TableCell<Order, Date> cell = new TableCell<Order, Date>()
            {
                //private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                @Override
                protected void updateItem(Date item, boolean empty)
                {

                    super.updateItem(item, empty);
                    if(empty)
                    {
                        setText(null);
                    }
                    else
                    {
                        this.setText(format.format(item));
                    }
                }
            };

            return cell;
        });

        DateOfCreationColumn.setCellValueFactory(new PropertyValueFactory<Order,Date>("dateOfCreation"));

        DateOfFinalizingColumn.setCellFactory(column -> {
            TableCell<Order, Date> cell = new TableCell<Order, Date>()
            {
                //private SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");

                @Override
                protected void updateItem(Date item, boolean empty)
                {

                    super.updateItem(item, empty);
                    if(item == null || empty)
                    {
                        setText(null);
                    }
                    else
                    {
                        this.setText(format.format(item));
                    }
                }
            };

            return cell;
        });

        DateOfFinalizingColumn.setCellValueFactory(new PropertyValueFactory<Order, Date>("dateOfFinalizing"));

        ClientIdColumn.setCellFactory(column -> {
        TableCell<Order, Client> cell = new TableCell<Order, Client>()
        {
            //private SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");

            @Override
            protected void updateItem(Client item, boolean empty)
            {

                super.updateItem(item, empty);
                if(empty)
                {
                    setText(null);
                }
                else
                {
                    this.setText("" + item.getIdClient());
                }
            }
        };

        return cell;
    });
        ClientIdColumn.setCellValueFactory(new PropertyValueFactory<Order, Client>("client"));


        IssueIdColumn.setCellFactory(column -> {
            TableCell<Order, BookIssue> cell = new TableCell<Order, BookIssue>()
            {
                //private SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");

                @Override
                protected void updateItem(BookIssue item, boolean empty)
                {

                    super.updateItem(item, empty);
                    if(empty)
                    {
                        setText(null);
                    }
                    else
                    {

                        this.setText("" + item.getIdBookIssue());
                    }
                }
            };

            return cell;
        });
        IssueIdColumn.setCellValueFactory(new PropertyValueFactory<Order, BookIssue>("issue"));

        BookTitleColumn.setCellFactory(column -> {
            TableCell<Order, BookIssue> cell = new TableCell<Order, BookIssue>()
            {
                //private SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");

                @Override
                protected void updateItem(BookIssue item, boolean empty)
                {

                    super.updateItem(item, empty);
                    if(empty)
                    {
                        setText(null);
                    }
                    else
                    {

                        this.setText("" + item.getDefinition().getTitle());
                    }
                }
            };

            return cell;
        });
        BookTitleColumn.setCellValueFactory(new PropertyValueFactory<Order, BookIssue>("issue"));


        ClientLoginColumn.setCellFactory(column -> {
            TableCell<Order, Client> cell = new TableCell<Order, Client>()
            {
                @Override
                protected void updateItem(Client item, boolean empty)
                {

                    super.updateItem(item, empty);
                    if(empty)
                    {
                        setText(null);
                    }
                    else
                    {
                        this.setText("" + item.getLogin());
                    }
                }
            };

            return cell;
        });
        ClientLoginColumn.setCellValueFactory(new PropertyValueFactory<Order, Client>("client"));


        mapTable();
    }


    /**
     * this method will set selected Order object as finalized, set finalization date as current and free corresponding BookIssue
     * object
     */
    public void finalize()
    {


        Order order = tableView.getSelectionModel().getSelectedItem();
        Date date = new Date();


        if (order != null)
        {
            manager.getTransaction().begin();

            try
            {
                format.format(date);

                order.getIssue().setAway(false);
                order.setDateOfFinalizing(date);

                manager.merge(order);
                manager.getTransaction().commit();
            }
            catch (Exception e)
            {
                System.out.println("Error with finalizing order!\n");
                manager.getTransaction().rollback();
            }
        }
        else
        {
            label.setText("Select order to finalize first!");
        }
        refresh();
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

    /**
     * this method will run query method with selected parameters from filled textfields and insert the results into tableview
     */
    @FXML
    private void refresh()
    {
        Query q;

        if (issueSearch.getText().isEmpty() && idSearch.getText().isEmpty())// z jakiegos powodu porownanie stringa z logniem komendą like działa w SQL ale nie tutaj
        {
            q = manager.createQuery("select c from Order c where c.client.login like ?1")
                    .setParameter(1, ("%"+loginSearch.getText()+"%"))
                    .setMaxResults(quantity)
                    .setFirstResult((page-1)*quantity);
        }
        else if (issueSearch.getText().isEmpty() && !idSearch.getText().isEmpty())
        {
            q = manager.createQuery("select c from Order c where c.client.login like ?1" +
                    "AND c.idOrder = ?2 ")
                    .setParameter(1, ("%"+loginSearch.getText()+"%"))
                    .setParameter(2, Long.parseLong(idSearch.getText()))
                    .setMaxResults(quantity)
                    .setFirstResult((page-1)*quantity);
        }
        else if (!issueSearch.getText().isEmpty() && idSearch.getText().isEmpty())
        {
            q = manager.createQuery("select c from Order c where c.client.login like ?1" +
                    "AND c.issue.idBookIssue = ?2 ")
                    .setParameter(1, ("%"+loginSearch.getText()+"%"))
                    .setParameter(2, Long.parseLong(issueSearch.getText()))
                    .setMaxResults(quantity)
                    .setFirstResult((page-1)*quantity);
        }
        else
        {
            q = manager.createQuery("select c from Order c where c.client.login like ?1" +
                    "AND c.issue.idBookIssue = ?2 AND c.idOrder = ?3 ")
                    .setParameter(1, ("%"+loginSearch.getText()+"%"))
                    .setParameter(2, Integer.parseInt(issueSearch.getText()))
                    .setParameter(3, Integer.parseInt(idSearch.getText()))
                    .setMaxResults(quantity)
                    .setFirstResult((page-1)*quantity);
        }



        lista = q.getResultList();

        tableView.getItems().clear();

        tableView.getItems().addAll(lista);
    }

    private void mapTable()
    {
        Query q = manager.createQuery("select c from Order c")
                .setMaxResults(quantity)
                .setFirstResult((page-1)*quantity);



        lista = q.getResultList();

        tableView.getItems().addAll(lista);
    }


}
