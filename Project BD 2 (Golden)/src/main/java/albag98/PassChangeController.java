package albag98;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import albag98.Entities.Client;
import albag98.OtherClasses.HashClass;

import javax.persistence.EntityManager;

public class PassChangeController extends HashClass
{

    private MainScreenUserController con;
    private Client client;

    public void setCon(MainScreenUserController con) {
        this.con = con;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private EntityManager manager = Main.factory.createEntityManager();

    @FXML
    private PasswordField t1;
    @FXML
    private PasswordField t2;
    @FXML
    private PasswordField t3;

    @FXML
    private Label label;


    /**
     * will return true if given password equals current password
     * @return
     */
    private boolean test()
    {
        return hash(t1.getText()).equals(client.getPassword());
    }

    /**
     * method will return true if two new passwords are equal
     * @return
     */
    private boolean test2()
    {
        return t2.getText().equals(t3.getText());
    }

    @FXML
    private void confirm()
    {
        if (!test()) label.setText("Given password is incorrect!");
        else if(!test2()) label.setText("New passwords are not the same!");
        else
        {
            manager.getTransaction().begin();

            try
            {
                client.setPassword(hash(t3.getText()));

                manager.merge(client);
                manager.getTransaction().commit();

            }

            catch (Exception e)
            {
                System.out.println("Error! Failed to update passwords!\n");
                manager.getTransaction().rollback();
            }
            back();
        }



    }

    @FXML
    private void back()
    {
        Stage st = (Stage) con.getBrowse().getScene().getWindow();
        Stage st2 = (Stage) label.getScene().getWindow();

        st.show();
        st2.close();
    }

}
