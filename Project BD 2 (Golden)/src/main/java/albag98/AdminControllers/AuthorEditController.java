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

public class AuthorEditController
{
    private AdminAuthorsController con;
    private Author author;

    public void setSelectedAuthor(Author author) {
        this.author = author;
    }

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

        if (name.getText().equals(author.getName()) && surname.getText().equals(author.getSurname())) return true;

        for (Author tab: lista)
        {
            if (name.getText().equals(tab.getName()) && surname.getText().equals(tab.getSurname())) return false;
        }
        return true;
    }


    /**
     * method will change Author object old data with the one input
     * @throws ParseException
     */
    public void confirm() throws ParseException
    {
        if (test() && test2())
        {
            manager.getTransaction().begin();

            try
            {
                    author.setName(name.getText());
                    author.setSurname(surname.getText());
                    author.setCountry(country.getText());

                    manager.merge(author);

                    manager.getTransaction().commit();
            }
            catch(Exception e)
            {
                System.out.println("Error with merging client!");
                manager.getTransaction().rollback();
            }

            back();
        }
        else
        {
            label.setText("Wrong data inserted!");
        }

    }

    /**
     * this method will fill TextFields with data from selected Author object
     */
    public void setUP()
    {
        name.setText(author.getName());
        surname.setText(author.getSurname());
        country.setText(author.getCountry());
    }



    public void back()
    {
        Stage st = (Stage) label.getScene().getWindow();
        Stage st2 = (Stage) con.getButton().getScene().getWindow();

        st2.show();
        con.refresh();
        st.close();

    }

}
