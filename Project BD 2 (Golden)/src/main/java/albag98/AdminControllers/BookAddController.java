package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import albag98.Entities.Author;
import albag98.Entities.BookDefinition;
import albag98.Main;


import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookAddController
{
    final private SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");


    private AdminBooksController con;

    public void setCon(AdminBooksController con) {
        this.con = con;
    }

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

    private boolean test()
    {
        if (title.getText().isEmpty()) {return false;}
        if (date.getText().isEmpty()) {return false;}
        if (publisher.getText().isEmpty()) {return false;}
        if (genre.getText().isEmpty()) {return false;}
        if (author.getSelectionModel().isEmpty()) {return false;}
        else return  true;
    }

    private boolean test2()
    {
        List<BookDefinition> lista;

        Query q = manager.createQuery("select c from BookDefinition c");

        lista = q.getResultList();

        for (BookDefinition tab: lista)
        {
            if (title.getText().equals(tab.getTitle())) return false;
        }
        return true;
    }


    /**
     * method will return true if date in TextField has correct format
     * @return
     */
    private boolean test3()
    {
        String dummy = date.getText();
        dt.setLenient(false);

        if (dummy == null) return false;

        try
        {
            Date date = dt.parse(dummy);
        }
        catch (ParseException e)
        {
            return false;
        }

        return true;
    }

    public void initialize()
    {
        mapTable();
    }


    public void add() throws ParseException
    {
        if (test() && test2() && test3())
        {
            manager.getTransaction().begin();

            Date data = dt.parse(date.getText());

            Author a1 = author.getValue();

            BookDefinition book = new BookDefinition(title.getText(), data, publisher.getText(),genre.getText(), a1);

            manager.persist(book);

            manager.getTransaction().commit();

            back();
        }
        else if(!test3())
        {
            label.setText("Wrong date format!");
        }
        else
        {
            label.setText("Wrong data inserted!");
        }

    }


    public void back()
    {
        Stage st = (Stage) label.getScene().getWindow();
        Stage st2 = (Stage) con.getButton().getScene().getWindow();

        st2.show();
        con.refresh();
        st.close();

    }

    private void mapTable()
    {
        List<Author> lista;

        Query q = manager.createQuery("select c from Author c");

        lista = q.getResultList();

        author.getItems().addAll(lista);
    }

}
