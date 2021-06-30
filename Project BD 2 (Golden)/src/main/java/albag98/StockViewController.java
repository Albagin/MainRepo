package albag98;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import albag98.DTO.BookDefinitionAuthorIssuesDTO;
import albag98.Entities.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StockViewController
{
    final private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");



    @FXML
    private TableView<BookDefinitionAuthorIssuesDTO> tableView;

    private MainScreenUserController user_con;
    private Client client;
    private List<BookDefinition> lista;

    @FXML
    private Label label;
    @FXML
    private Button button;
    @FXML
    private TableColumn TitleColumn;
    @FXML
    private TableColumn DateColumn;
    @FXML
    private TableColumn PublisherColumn;
    @FXML
    private TableColumn GenreColumn;
    @FXML
    private TableColumn AuthorColumn;
    @FXML
    private TableColumn IssuesColumn;
    @FXML
    private TextField titleSearch;
    @FXML
    private TextField authorSearch;
    @FXML
    private TextField publisherSearch;
    @FXML
    private Button pButton;
    @FXML
    private Button nButton;



    private int page = 1;
    private int quantity = 15;


    public void setCon(MainScreenUserController con) {
        this.user_con = con;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private EntityManager manager = Main.factory.createEntityManager();



    public void initialize()
    {
        TitleColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO,String>("title"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO,String>("dateOfReleaseS"));
        PublisherColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO,String>("publisher"));
        GenreColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO,String>("genre"));
        AuthorColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO, String>("author"));
        IssuesColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO, Integer>("issues"));

        mapTable();

    }


    /**
     * this method will search for BookIssue object list corresponding to selected BookDefinition object, and if there is at
     * least one unassigned BookIssue object it will create run createOrder method for said BookIssue object
     */
    public void order()//tutaj sporo operacji, pobranie listy egzemplarzy dla wybranej ksiazki i utworzenie zamowienia
    {
        BookDefinitionAuthorIssuesDTO dummy;

        List<BookIssue> pom;

        boolean test = true;//zmienna pomocnicza, do przekazania info czy nie ma juz egzemplarzy tej książki

        dummy =  tableView.getSelectionModel().getSelectedItem();

        //System.out.println(dummy.getIdBookDef());

        if (dummy != null)
        {

            Query q = manager.createQuery("select c from BookIssue c where c.definition.idBookDef = ?1")
                    .setParameter(1, dummy.getIdBookDef());

            pom = q.getResultList();

            for (BookIssue tab: pom)
            {
                if (!tab.isAway())
                {
                    createOrder(tab);
                    test = false;
                    break;
                }
            }


            if (test)
            {
                label.setText("Alas, no more issues are in stock\n");
            }
        }
        else System.out.println("Choose a book first");
    }

    /**
     * this method will create new Order object and set corresponding BookIssue object as away
     * @param book selected BookIssue
     */
    private void createOrder(BookIssue book)
    {
        manager.getTransaction().begin();

        Date date = new Date();
        dateFormat.format(date);


        try
        {
            book.setAway(true);

            manager.merge(book);//tutaj ustawiam że dany egzemplarz jest wydany

            Order order = new Order(date, client, book);

            manager.persist(order);//tutaj dodaję nowe zamówienie

            manager.getTransaction().commit();

        }
        catch (Exception e)
        {
            System.out.println("Error! Failed to create order!\n");
            manager.getTransaction().rollback();
        }

        back();

    }

    public void  next()
    {
        page++;
        if (lista.size() < 10) nButton.setDisable(true);
        pButton.setDisable(false);
        mapTable();

    }
    public void prev()
    {
                page--;
                if (page == 1) pButton.setDisable(true);
                nButton.setDisable(false);
                mapTable();
    }

    public void back()
    {
        Stage st = (Stage) button.getScene().getWindow();
        Stage st2 = (Stage) user_con.getBrowse().getScene().getWindow();

        st2.show();
        user_con.setClient(client);
        user_con.refresh();
        st.close();
    }

    private void mapTable()
    {
        Query q = manager.createQuery("select c from BookDefinition c where c.title like ?1 AND " +
                "c.publisher like ?2 AND (c.author.name like ?3 OR c.author.surname like ?3)")
                .setParameter(1, ("%"+titleSearch.getText()+"%"))
                .setParameter(2, ("%"+ publisherSearch.getText() +"%"))
                .setParameter(3, ("%"+ authorSearch.getText() +"%"))
                .setMaxResults(10)
                .setFirstResult((page-1)*quantity);

        lista = q.getResultList();
        List<BookDefinitionAuthorIssuesDTO> lista_dummy = new ArrayList<>();

        for (BookDefinition tab : lista)
        {
            q = manager.createQuery("select c from BookIssue c where c.definition.idBookDef = ?1 AND c.away = ?2")
                    .setParameter(1, tab.getId_Book_Definitions())
                    .setParameter(2, false);

            BookDefinitionAuthorIssuesDTO bookA = new BookDefinitionAuthorIssuesDTO(tab);
            bookA.setAuthor(tab.getAuthor().getName() + " " + tab.getAuthor().getSurname());
            bookA.setIssues(q.getResultList().size());


            lista_dummy.add(bookA);
        }

        tableView.getItems().clear();

        tableView.getItems().addAll(lista_dummy);
    }

    @FXML
    public void refresh()
    {
        mapTable();
    }
}
