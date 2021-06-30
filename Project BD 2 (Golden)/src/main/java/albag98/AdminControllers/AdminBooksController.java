package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import albag98.DTO.BookDefinitionAuthorDTO;
import albag98.DTO.BookDefinitionAuthorIssuesDTO;
import albag98.Entities.*;
import albag98.Main;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AdminBooksController
{
    @FXML
    private TableView<BookDefinitionAuthorIssuesDTO> tableView;
    private MainScreenAdminController user_con;
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
    private TableColumn IssuesStockColumn;
    @FXML
    private Button pButton;
    @FXML
    private Button nButton;
    @FXML
    private TextField titleSearch;
    @FXML
    private TextField authorSearch;
    @FXML
    private TextField publisherSearch;

    private int page = 1;
    private int quantity = 18;

    public void setCon(MainScreenAdminController user_con) {
        this.user_con = user_con;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Button getButton() {
        return button;
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public Label getLabel() {
        return label;
    }

    private EntityManager manager = Main.factory.createEntityManager();



    public void initialize()
    {

        TitleColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO,String>("title"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO,String>("dateOfReleaseS"));
        PublisherColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorDTO,String>("publisher"));
        GenreColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO,String>("genre"));
        AuthorColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO, String>("author"));
        IssuesColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO, Integer>("issues"));
        IssuesStockColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorIssuesDTO, Integer>("issuesStock"));


        mapTable();

    }

    public void back()
    {
        Stage st = (Stage) button.getScene().getWindow();
        Stage st2 = (Stage) user_con.getAdd().getScene().getWindow();

        st2.show();
        st.close();
    }

    /**
     * opens new stage allowing to add new BookDefinition object
     * @throws IOException
     */
    @FXML
    public void addBook() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/BookAdd.fxml"));

        Parent root = pom.load();

        BookAddController con = pom.getController();
        con.setCon(this);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 500));

        stage2.setTitle("Add Book");

        stage2.show();

        Stage st = (Stage) button.getScene().getWindow();
        st.hide();
    }

    /**
     * allows to remove BookDefinition object from the database. If selected book has ever been assigned to an Order object
     * removal will be impossible
     */
    @FXML
    public void deleteBook()
    {
        BookDefinitionAuthorIssuesDTO dummy = tableView.getSelectionModel().getSelectedItem();

        if (dummy == null)
        {
            label.setText("Select a book to delete first!");
        }
        else
        {
            Query q = manager.createQuery("select c from BookDefinition c where c.idBookDef = ?1")
                    .setParameter(1, dummy.getIdBookDef());

            BookDefinition book = null;

            manager.getTransaction().begin();

            try
            {
                book = (BookDefinition) q.getSingleResult();

                manager.remove(book);
                manager.getTransaction().commit();
            }
            catch(NoResultException e)
            {
                System.out.println("Error! Could not get the book definition!\n");
            }
            catch (Exception e)
            {
                System.out.println("Could not delete book! Maybe it's rented?\n");
                manager.getTransaction().rollback();
            }

            refresh();
        }
    }


    /**
     * opens new stage to allow to edit selected BookDefinition object
     * @throws IOException
     */
    @FXML
    public void editBook() throws IOException {

        if (tableView.getSelectionModel().getSelectedItem() != null)
        {
            FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/BookEdit.fxml"));

            Parent root = pom.load();

            BookEditController con = pom.getController();
            con.setCon(this);
            con.setSelectedBook(tableView.getSelectionModel().getSelectedItem());

            Stage stage2 = new Stage();

            stage2.setScene(new Scene(root, 940, 500));

            stage2.setTitle("Edit");

            stage2.show();

            Stage st = (Stage) button.getScene().getWindow();
            st.hide();
        }
        else
        {
            label.setText("Select a book to edit first!");
        }


    }

    @FXML
    public void issueChange() throws IOException
    {
        if (tableView.getSelectionModel().getSelectedItem() == null)
        {
            label.setText("Choose a book first!");
        }
        else
        {
            FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/AdminBooksIssues.fxml"));

            Parent root = pom.load();

            AdminBooksIssuesController con = pom.getController();
            con.setCon(this);
            con.setBook(tableView.getSelectionModel().getSelectedItem());
            con.setUP();

            Stage stage2 = new Stage();

            stage2.setScene(new Scene(root, 940, 500));

            stage2.setTitle("Issue Change");

            stage2.show();

            Stage st = (Stage) button.getScene().getWindow();
            st.hide();
        }
    }

    /**
     * method automatically called when changing to next page
     */
    public void prev()
    {
        page--;
        if (page == 1) pButton.setDisable(true);
        nButton.setDisable(false);
        refresh();
    }

    /**
     * method automatically called when changing to previous page
     */
    public void next()
    {
        page++;
        if (lista.size() < quantity) nButton.setDisable(true);
        pButton.setDisable(false);
        refresh();
    }


    /**
     * this method will perform a query, and BookDefinition objects from result set will be converted to
     *  BookDefinitionAuthorIssuesDTO objects and inserted into tableView
     */
    private void mapTable()
    {
        Query q = manager.createQuery("select c from BookDefinition c where c.title like ?1 AND " +
                "c.publisher like ?2 AND (c.author.name like ?3 OR c.author.surname like ?3)")
                .setParameter(1, ("%"+titleSearch.getText()+"%"))
                .setParameter(2, ("%"+ publisherSearch.getText() +"%"))
                .setParameter(3, ("%"+ authorSearch.getText() +"%"))
                .setMaxResults(quantity)
                .setFirstResult((page-1)*quantity);

        lista = q.getResultList();

        List<BookDefinitionAuthorIssuesDTO> lista_dummy = new ArrayList<>();

        for (BookDefinition tab : lista)
        {
            BookDefinitionAuthorIssuesDTO bookA = new BookDefinitionAuthorIssuesDTO(tab);

            q = manager.createQuery("select c from BookIssue c where c.definition.idBookDef = ?1 AND c.away = ?2")
                    .setParameter(1,tab.getId_Book_Definitions())
            .setParameter(2, false);

            int issuesStock = q.getResultList().size();
            bookA.setIssuesStock(issuesStock);

            q = manager.createQuery("select c from BookIssue c where c.definition.idBookDef = ?1")
                .setParameter(1,tab.getId_Book_Definitions());

            int issues = q.getResultList().size();

            bookA.setAuthor(tab.getAuthor().getName() + " " + tab.getAuthor().getSurname());
            bookA.setIssues(issues);

            lista_dummy.add(bookA);
        }

        tableView.getItems().addAll(lista_dummy);
    }

    public void refresh()
    {
        tableView.getItems().clear();

        mapTable();
    }

}
