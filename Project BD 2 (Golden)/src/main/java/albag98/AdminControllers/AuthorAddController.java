package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import albag98.Entities.Author;
import albag98.Main;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.util.List;

public class AuthorAddController
{
    private AdminAuthorsController con;

    public void setCon(AdminAuthorsController con) {
        this.con = con;
    }

    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField country;

    @FXML
    private Label label;


    private EntityManager manager = Main.factory.createEntityManager();

    /**
     * method will return true if fields name, surname and country are not empty
     * @return
     */
    private boolean test()
    {
        if (name.getText().equals(" ")) {return false;}
        if (surname.getText().equals(" ")) {return false;}
        if (country.getText().equals(" ")) {return false;}
        else return  true;
    }

    /**
     * this will return true if author with name and surname equal to those given in TextFields does not exist
     * @return
     */
    private boolean test2()
    {
        List<Author> lista;

        Query q = manager.createQuery("select c from Author c");

        lista = q.getResultList();

        for (Author tab: lista)
        {
            if (name.getText().equals(tab.getName()) && surname.getText().equals(tab.getSurname())) return false;
        }
        return true;
    }


    public void add() throws ParseException
    {
        if (test() && test2())
        {
            manager.getTransaction().begin();


            Author author = new Author(name.getText(), surname.getText(), country.getText());

            manager.persist(author);

            manager.getTransaction().commit();


            cancel();
        }
        else
        {
            label.setText("Wrong data inserted!");
        }

    }


    public void cancel()
    {
        //to do BackController a tu wywolac super.back()
        Stage st2 = (Stage) con.getButton().getScene().getWindow();
        st2.show();
        con.refresh();

        Stage st = (Stage) label.getScene().getWindow();
        st.close();

    }

}
