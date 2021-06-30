package albag98;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import albag98.Entities.Client;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class EditPersonalsController
{
    private MainScreenUserController con;

    private Client client;

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
    Label label;


    private EntityManager manager = Main.factory.createEntityManager();

    public void setCon(MainScreenUserController con) {
        this.con = con;
    }

    public void setClient(Client client) {
        this.client = client;
        setUP();
    }



    @FXML
    public void back()
    {
        Stage st = (Stage) con.getBrowse().getScene().getWindow();
        Stage st2 = (Stage) name.getScene().getWindow();

        st.show();
        st2.close();
    }

    private void setUP()
    {
        name.setText(client.getName());
        surname.setText(client.getSurname());
        phone.setText(client.getPhoneNumber());
        eMail.setText(client.geteMail());
        city.setText(client.getCityOfResidence());
        street.setText(client.getStreet());
        voivodeship.setText(client.getVoivodeship());
        label.setText("");
    }

    /**
     * method will return true if all required textfields are filled
     * @return
     */
    private boolean test1()//test czy wszystkie (potrzebne) pola sa zapełnione
    {
        if ( name.getText().isEmpty() ) return false;
        if ( surname.getText().isEmpty() ) return false;
        if ( phone.getText().isEmpty() ) return false;
        if ( eMail.getText().isEmpty() ) return false;

        return true;
    }


    /**
     * method will return true if given eMail is valid
     * @return
     */
    private boolean test3()//validacja maila
    {
        String dummy = eMail.getText();

        return dummy.contains("@");
    }

    /**
     * method will return true if given phone number is valid
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
     * method will return true if phone number has not been changed or if no other Client object has it assigned
     * @return
     */
    private boolean test5()//czy ktoś już nie ma takiego numeru
    {//sytuacja, gdy numer nie jest ruszony
        if (phone.getText().equals(client.getPhoneNumber())) return true;

        Query q = manager.createQuery("select c from Client c");

        List<Client> lista = q.getResultList();

        for (Client tab : lista)
        {
            if (tab.getPhoneNumber().equals(phone.getText())) return false;
        }
        return true;
    }

    /**
     * method will return true if eMail has not been changed or if no other Client object has it assigned
     * @return
     */
    private boolean test6()//czy ktoś nie ma już takiego maila
    {//sytuacja, gdy mail nie jest ruszony
        if (eMail.getText().equals(client.geteMail())) return true;

        Query q = manager.createQuery("select c from Client c");

        List<Client> lista = q.getResultList();

        for (Client tab : lista)
        {
            if (tab.geteMail().equals(eMail.getText())) return false;
        }
        return true;
    }


    /**
     * method will change selected Client object old data to new, given from filled textfields
     */
    public void confirm()
    {
        if (!test1()) label.setText("Some fields are left unfilled!");
        else if(!test3()) label.setText("Invalid eMail!");
        else if(!test4()) label.setText("Invalid phone number!");
        else if (!test5()) label.setText("This phone number is already taken!");
        else if (!test6()) label.setText("This eMail is already taken!");
        else
        {
            manager.getTransaction().begin();

            try
            {

                client.setName(name.getText());
                client.setSurname(surname.getText());
                client.seteMail(eMail.getText());
                client.setPhoneNumber(phone.getText());
                client.setCityOfResidence(city.getText());
                client.setVoivodeship(voivodeship.getText());
                client.setStreet(street.getText());


                manager.merge(client);
                manager.getTransaction().commit();
            }
            catch (Exception e)
            {
                System.out.println("Erorr! Could not edit personals!\n");
                manager.getTransaction().rollback();
            }

            back();
        }
    }
}
