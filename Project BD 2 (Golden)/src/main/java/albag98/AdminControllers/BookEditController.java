package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import albag98.DTO.BookDefinitionAuthorIssuesDTO;
import albag98.Entities.Author;
import albag98.Entities.BookDefinition;
import albag98.Main;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookEditController
{

    private AdminBooksController con;
    private BookDefinitionAuthorIssuesDTO selectedBook;

    private final SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

    public void setCon(AdminBooksController con) {
        this.con = con;
    }

    public void setSelectedBook(BookDefinitionAuthorIssuesDTO selectedBook) {
        this.selectedBook = selectedBook;

        if (selectedBook == null)
        {
            con.setLabel("Error! Could not get book to edit!");
            back();
        }
        else
        {
            setUp(selectedBook);
        }

    }

    //private List<Author> lista;

    @FXML
    private TextField title;
    @FXML
    private TextField date;
    @FXML
    private TextField publisher;
    @FXML
    private TextField genre;
    @FXML
    private ChoiceBox<Author> author;
    @FXML
    private Label label;


    private EntityManager manager = Main.factory.createEntityManager();

    /**
     * method will return true if all textfields are filled
     * @return
     */
    private boolean test()//test czy wszystkie pola są zapełnione
    {
        if (title.getText().isEmpty()) {return false;}
        if (date.getText().isEmpty()) {return false;}
        if (publisher.getText().isEmpty()) {return false;}
        if (genre.getText().isEmpty()) {return false;}
        if (author.getSelectionModel().isEmpty()) {return false;}
        else return  true;
    }

    /**
     * method will return true if the same BookDefinition object does not exist in databse
     * @return
     */
    private boolean test2()//test czy już nie ma takiej książki w bazie
    {
        if (selectedBook.getTitle().equals(title.getText())) return true;


        Query q = manager.createQuery("select c from BookDefinition c where c.title = ?1")
                .setParameter(1, title.getText());

        try
        {
            BookDefinition a = (BookDefinition) q.getSingleResult();//jeśli taka książka istnieje, to w tym miejscu zostanie rzucony wyjątek
            System.out.println(a.getTitle());
            return false;
        }
        catch (NoResultException e)
        {
            return true;
        }


    }


    public void initialize()
    {
        mapTable();
    }


    /**
     * this method will change old data in selected BookDefinition object to new from filled textfields
     * @throws ParseException
     */
    public void confirm() throws ParseException
    {
        if (test() && test2())
        {
            manager.getTransaction().begin();

            Date date = dt.parse(this.date.getText());

            Author a1 = author.getValue();

            Query q = manager.createQuery("select c from BookDefinition c where c.idBookDef = ?1")
                    .setParameter(1, selectedBook.getIdBookDef());

            BookDefinition book;

            try
            {
                book = (BookDefinition) q.getSingleResult();

                book.setTitle(title.getText());
                book.setGenre(genre.getText());
                book.setPublisher(publisher.getText());
                book.setDateOfRelease(date);
                book.setAuthor(a1);

                manager.merge(book);

                manager.getTransaction().commit();
            }
            catch (NoResultException e)
            {
                System.out.println("Error! Failed to find the book! (BookEditController)\n");
                manager.getTransaction().rollback();
            }
            catch (Exception e)
            {
                System.out.println("Error! Failed to commit (BookEditController)\n");
                manager.getTransaction().rollback();
            }
        }
        else
        {
            System.out.println("testy nie pomyślne\n");
            if (!test()) System.out.println("s1\n");
            if (!test2()) System.out.println("s2\n");
        }

        back();

    }

    public void back()
    {
        Stage st = (Stage) label.getScene().getWindow();
        Stage st2 = (Stage) con.getButton().getScene().getWindow();

        st2.show();
        con.refresh();
        st.close();

    }

    /**
     * this method will fill textfields with data from parameter and run findAuthor method to set right Author object to choicebox
     * @param book
     */
    private void setUp(BookDefinitionAuthorIssuesDTO book)
    {
        title.setText(book.getTitle());
        date.setText(dt.format(book.getDateOfRelease()));
        publisher.setText(book.getPublisher());
        genre.setText(book.getGenre());

        findAuthor(book);

    }

    /**
     * this method will stop when required Author object will be found and set to choicebox
     * @param book selected BookDefinition object converted to BookDefinitionAuthorIssuesDTO object
     */
    private void findAuthor(BookDefinitionAuthorIssuesDTO book)
    {
        for (Author tab : author.getItems())
        {
            if ((tab.getName()+" "+tab.getSurname()).equals(book.getAuthor()))
            {
                author.setValue(tab);
                break;
            }
        }
    }

    public void mapTable()
    {
        List<Author> lista;

        Query q = manager.createQuery("select c from Author c");

        lista = q.getResultList();

        author.getItems().addAll(lista);
    }

}

