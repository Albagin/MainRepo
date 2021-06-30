package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import albag98.DTO.BookDefinitionAuthorDTO;
import albag98.Entities.Author;
import albag98.Entities.BookDefinition;
import albag98.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public class AdminAuthorsController
{
    @FXML
    private TableView<Author> tableView;
    private MainScreenAdminController user_con;

    private List<Author> lista;


    @FXML
    private Label label;
    @FXML
    private Button button;
    @FXML
    private TableColumn IdColumn;
    @FXML
    private TableColumn NameColumn;
    @FXML
    private TableColumn SurnameColumn;
    @FXML
    private TableColumn CountryColumn;

    @FXML
    private TextField nameSearch;
    @FXML
    private TextField surnameSearch;
    @FXML
    private TextField countrySearch;
    @FXML
    private Button pButton;
    @FXML
    private Button nButton;

    private int page = 1;
    private int quantity = 15;


    public void setCon(MainScreenAdminController user_con) {
        this.user_con = user_con;
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


        //TableColumn Id_def_Column = new TableColumn("Id");
        //Id_def_Column.setCellValueFactory(new PropertyValueFactory<>("Id_Book_Def"));


        IdColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorDTO,String>("idAuthor"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorDTO,Date>("name"));
        SurnameColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorDTO,String>("surname"));
        CountryColumn.setCellValueFactory(new PropertyValueFactory<BookDefinitionAuthorDTO,String>("country"));

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
     * opens new stage allowing to add new Author object
     * @throws IOException
     */
    @FXML
    public void addAuthor() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/AuthorAdd.fxml"));

        Parent root = pom.load();

        AuthorAddController con = pom.getController();
        con.setCon(this);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 500));

        stage2.setTitle("Add New Author");

        stage2.show();

        Stage st = (Stage) button.getScene().getWindow();
        st.hide();
    }

    /**
     * allows to remove an Author object from database. If there is a BookDefinition object in the database corresponding to
     * the selected Author object, this method will end
     * @throws IOException
     */
    @FXML
    public  void deleteAuthor() throws IOException {

        Author author = tableView.getSelectionModel().getSelectedItem();

        if (author != null )
        {
            if (test())
            {
                manager.getTransaction().begin();

                try
                {
                    manager.remove(author);

                    manager.getTransaction().commit();

                    refresh();
                }
                catch (Exception e)
                {
                    System.out.println("Error! Could not remove author! (AdminAuthorController)\n");
                    manager.getTransaction().rollback();
                }
            }
            else
            {
                label.setText("This author has written a book we have!");
            }

        }
        else label.setText("Select an author to remove first!");
    }


    /**
     * opens new stage allowing to edit existing Author object
     * @throws IOException
     */
    @FXML
    public void editAuthor() throws IOException {

        if (tableView.getSelectionModel().getSelectedItem() != null)
        {
            FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/AuthorEdit.fxml"));

            Parent root = pom.load();

            AuthorEditController con = pom.getController();
            con.setCon(this);
            con.setSelectedAuthor(tableView.getSelectionModel().getSelectedItem());
            con.setUP();

            Stage stage2 = new Stage();

            stage2.setScene(new Scene(root, 940, 500));

            stage2.setTitle("Edit");

            stage2.show();

            Stage st = (Stage) button.getScene().getWindow();
            st.hide();
        }
        else
        {
            label.setText("Select an author to edit first!");
        }


    }

    /**
     * method will return false if any BookDefinition object in the database has an item selected in method deleteAuthor as
     * an author. Otherwise, will return true
     * @return
     */
    private boolean test()
    {
        List<BookDefinition> lista;

        Query q = manager.createQuery("select c from BookDefinition c");

        lista = q.getResultList();


        for (BookDefinition tab: lista)
        {
            if (tab.getAuthor() == tableView.getSelectionModel().getSelectedItem()) return false;
        }
        return true;
    }

    /**
     * method automatically called when changing to next page
     */
    @FXML
    private void next()
    {
        page++;
        if (lista.size() < quantity) nButton.setDisable(true);
        pButton.setDisable(false);
        refresh();
    }

    /**
     * method automatically called when changing to previous page
     */
    @FXML
    private void prev()
    {
        page--;
        if (page == 1) pButton.setDisable(true);
        nButton.setDisable(false);
        refresh();
    }

    /**
     * this method removes all items from current tableView and maps it again to display changes
     */
    public void refresh()
    {
        tableView.getItems().clear();

        mapTable();
    }

    /**
     * method performs a query with parameters, then inserts results to tableView
     */
    public void mapTable()
    {
        Query q = manager.createQuery("select c from Author c where c.name like ?1 AND " +
                "c.surname like ?2 AND c.country like ?3")
                .setParameter(1, ("%"+nameSearch.getText()+"%"))
                .setParameter(2, ("%"+ surnameSearch.getText() +"%"))
                .setParameter(3, ("%"+ countrySearch.getText() +"%"))
                .setMaxResults(quantity)
                .setFirstResult((page-1)*quantity);

        lista = q.getResultList();

        tableView.getItems().addAll(lista);
    }

}
