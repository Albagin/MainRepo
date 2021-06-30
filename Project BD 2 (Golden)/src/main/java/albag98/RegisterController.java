package albag98;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import albag98.Entities.Client;
import albag98.OtherClasses.HashClass;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class RegisterController extends HashClass
{
    private LoginController con;



    @FXML
    private TextField name;

    @FXML
    private TextField surname;

    @FXML
    private TextField eMail;

    @FXML
    private TextField phone;

    @FXML
    private TextField voivodeship;

    @FXML
    private TextField city;

    @FXML
    private TextField street;

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    Label label;


    private EntityManager manager = Main.factory.createEntityManager();

    public void setCon(LoginController con) {
        this.con = con;
    }

    @FXML
    public void cancel()
    {
        Stage st = (Stage) con.getLogin().getScene().getWindow();
        Stage st2 = (Stage) login.getScene().getWindow();


        con.clean_fields();
        clean_fields();

        st.show();
        st2.close();
    }

    private void clean_fields()
    {
        login.setText("");
        password.setText("");
        name.setText("");
        surname.setText("");
        phone.setText("");
        eMail.setText("");
        city.setText("");
        street.setText("");
        voivodeship.setText("");
        label.setText("");
    }

    /**
     * method wll return true if all required fields are filled
     * @return
     */
    private boolean test1()//test czy wszystkie (potrzebne) pola sa zapełnione
    {
        if ( login.getText().isEmpty() ) return false;
        if ( password.getText().isEmpty() ) return false;
        if ( name.getText().isEmpty() ) return false;
        if ( surname.getText().isEmpty() ) return false;
        if ( phone.getText().isEmpty() ) return false;
        if ( eMail.getText().isEmpty() ) return false;

        return true;
    }

    /**
     * method will return true if no user with the same login exists in database
     * @return
     */
    private boolean test2()//test czy już nie ma takiego użytkownika
    {
        Query q = manager.createQuery("select c from Client c");

        List<Client> lista = q.getResultList();

        for (Client tab: lista)
        {
            if (tab.getLogin().equals(login.getText())) return false;
        }

        return true;
    }

    /**
     * method will return true if given eMail is valide
     * @return
     */
    private boolean test3()//validacja maila
    {
        String dummy = eMail.getText();

        return dummy.contains("@");
    }

    /**
     * method will return true if given phone number is valide
     * @return
     */
    private boolean test4()//validacja numeru
    {
        String dummy = phone.getText();

        if (dummy.startsWith("+"))
        {
            String[] tab;

            tab = dummy.split(" ");

            return numberCheck(tab[1]);

        }
        else
        {
            return numberCheck(dummy);
        }
    }

    private boolean numberCheck(String n)
    {
        if (n.length() != 9) return false;//czy ma dlugosc 9

        return n.matches("[0-9]+");//czy sklada sie tylko z cyfr

    }

    /**
     * method will return true if given phone number has not been assigned to other Client object
     * @return
     */
    private boolean test5()//czy ktoś już nie ma takiego numeru
    {
        Query q = manager.createQuery("select c from Client c");

        List<Client> lista = q.getResultList();

        for (Client tab : lista)
        {
            if (tab.getPhoneNumber().equals(phone.getText())) return false;
        }
        return true;
    }

    /**
     * method will return true if given eMail has not been assigned to other Client object
     * @return
     */
    private boolean test6()//czy ktoś nie ma już takiego maila
    {
        Query q = manager.createQuery("select c from Client c");

        List<Client> lista = q.getResultList();

        for (Client tab : lista)
        {
            if (tab.geteMail().equals(eMail.getText())) return false;
        }
        return true;
    }


    /**
     * method will create new Client object with data from filled textfields
     */
    public void registerUser()
    {
        if (!test1()) label.setText("Some fields are left unfilled!");
        else if (!test2()) label.setText("This login is already taken!");
        else if(!test3()) label.setText("Invalid eMail!");
        else if(!test4()) label.setText("Invalid phone number!");
        else if (!test5()) label.setText("This phone number is already taken!");
        else if (!test6()) label.setText("This eMail is already taken!");
        else
        {
            manager.getTransaction().begin();

            try
            {
                //KeySpec key = new PBEKeySpec(password.getText(), )


                Client c = new Client( name.getText(), surname.getText(), eMail.getText(), phone.getText(), voivodeship.getText(),
                        city.getText(), street.getText(), login.getText(), hash(password.getText()), false, true);

                manager.persist(c);
                manager.getTransaction().commit();
            }
            catch (Exception e)
            {
                System.out.println("Erorr! Could not register user!\n");
                manager.getTransaction().rollback();
            }

            Stage st1 = (Stage) login.getScene().getWindow();
            Stage st2 = (Stage) con.getLogin().getScene().getWindow();

            st2.show();
            st1.close();
        }
    }
}
