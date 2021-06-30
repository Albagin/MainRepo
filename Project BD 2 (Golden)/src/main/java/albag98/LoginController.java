package albag98;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import albag98.AdminControllers.MainScreenAdminController;
import albag98.Entities.Client;
import albag98.OtherClasses.HashClass;

import javax.persistence.*;
import java.io.IOException;

public class LoginController extends HashClass
{

    @FXML
    private TextField field;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    private Label label;

    public TextField getLogin() {
        return login;
    }

    private Client client;

    @FXML
    public void log_in() throws IOException {


        EntityManager manager = Main.factory.createEntityManager();


        Query q = manager.createQuery("select c from Client c WHERE c.login = ?1 AND c.password = ?2")
                .setParameter(1, login.getText())
                .setParameter(2, hash(password.getText()));

        client = null;

        try {
             client  = (Client) q.getSingleResult();
            }
        catch(NoResultException e)
        {
             label.setText("Wrong username or password!");

        }

        if (client != null)
        {
            if (client.isActive())
            {
                if(!client.isAdmin())
                {
                    user_screen();
                }
                else
                {
                    admin_screen();
                }
            }
            else
            {
                label.setText("This user account has been terminated!");
            }


        }
    }


    /**
     * method will open new stage allowing user to take action
     * @throws IOException
     */
    @FXML
    private void user_screen() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/MainScreenUser.fxml"));

        Parent root = pom.load();

        MainScreenUserController con = pom.getController();
        con.setCon(this);
        con.setClient(client);
        con.mapTable();

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 530));

        stage2.setTitle("Main Screen");

        stage2.show();

        Stage st = (Stage) login.getScene().getWindow();
        st.hide();
    }

    /**
     * method will open new stage allowing admin to take action
     * @throws IOException
     */
    @FXML
    private void admin_screen() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/MainScreenAdmin.fxml"));

        Parent root = pom.load();

        MainScreenAdminController con = pom.getController();
        con.setCon(this);
        con.setClient(client);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 530));

        stage2.setTitle("Main Screen");

        stage2.show();

        Stage st = (Stage) login.getScene().getWindow();
        st.hide();
    }

    /**
     * method will open new stage allowing registering of a new user (client only)
     * @throws IOException
     */
    @FXML
    public void register() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/Register.fxml"));

        Parent root = pom.load();

        RegisterController con = pom.getController();
        con.setCon(this);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 700, 500));

        stage2.setTitle("Register");

        stage2.show();

        Stage st = (Stage) login.getScene().getWindow();
        st.hide();
    }

    public void clean_fields()
    {
        login.setText("");
        password.setText("");
        label.setText("");
    }

    public void get()
    {
        hash(field.getText());
    }
}
