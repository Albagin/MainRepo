package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import albag98.Entities.Client;
import albag98.LoginController;

import java.io.IOException;

public class MainScreenAdminController
{
    private LoginController con;

    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    public Button getAdd() {
        return addBook;
    }

    public void setCon(LoginController con) {
        this.con = con;
    }




    @FXML
    private Button addBook;


    /**
     * closes this stage and shows LoginView stage
     */
    @FXML
    public void logOut()
    {
        Stage st = (Stage) con.getLogin().getScene().getWindow();
        Stage st2 = (Stage) addBook.getScene().getWindow();


        con.clean_fields();

        st.show();
        st2.close();
    }


    /**
     * opens new stage allowing to browse BookDefinition objects currently held in database
     * @throws IOException
     */
    public void browse() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/AdminBooks.fxml"));

        Parent root = pom.load();

        AdminBooksController con = pom.getController();
        con.setCon(this);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 575));

        stage2.setTitle("Browse Stock");

        stage2.show();

        Stage st = (Stage) addBook.getScene().getWindow();
        st.hide();
    }

    /**
     * opens new stage allowing to administrate orders
     * @throws IOException
     */
    public void orders() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/AdminOrders.fxml"));

        Parent root = pom.load();

        OrderController con = pom.getController();
        con.setCon(this);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 520));

        stage2.setTitle("Orders");

        stage2.show();

        Stage st = (Stage) addBook.getScene().getWindow();
        st.hide();
    }

    /**
     * opens new stage allowing to administrate authors
     * @throws IOException
     */
    public void authors() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/AdminAuthors.fxml"));

        Parent root = pom.load();

        AdminAuthorsController con = pom.getController();
        con.setCon(this);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 940, 500));

        stage2.setTitle("Authors");

        stage2.show();

        Stage st = (Stage) addBook.getScene().getWindow();
        st.hide();
    }

    /**
     *opens new stage allowing to administrate users
     * @throws IOException
     */

    public void users() throws IOException {
        FXMLLoader pom = new FXMLLoader(getClass().getResource("/AdminViews/AdminUsers.fxml"));

        Parent root = pom.load();

        AdminUserController con = pom.getController();
        con.setCon(this);

        Stage stage2 = new Stage();

        stage2.setScene(new Scene(root, 1100, 570));

        stage2.setTitle("Users");

        stage2.show();

        Stage st = (Stage) addBook.getScene().getWindow();
        st.hide();
    }

}
