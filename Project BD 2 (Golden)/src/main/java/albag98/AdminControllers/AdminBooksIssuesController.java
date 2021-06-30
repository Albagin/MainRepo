package albag98.AdminControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import albag98.DTO.BookDefinitionAuthorIssuesDTO;
import albag98.Entities.BookDefinition;
import albag98.Entities.BookIssue;
import albag98.Main;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminBooksIssuesController
{

    final private SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

    private EntityManager manager = Main.factory.createEntityManager();

    private AdminBooksController con;
    private BookDefinitionAuthorIssuesDTO book;
    private BookDefinition bookDefinition;

    public void setCon(AdminBooksController con) {
        this.con = con;
    }

    public void setBook(BookDefinitionAuthorIssuesDTO book) {
        this.book = book;
    }

    private List<BookIssue> lista;

    public void setUP()
    {
        mapTable();
    }

    @FXML
    private Label label;
    @FXML
    private Label label2;
    @FXML
    private TextField field;


    /**
     * changes number of BookIssue object corresponding to selected BookDefinition objects
     */
    public void confirm()
    {
        if (field.getText().isEmpty())
        {
            label2.setText("Select a number of issues first!");
        }
        else
        {
            int liczba = Integer.parseInt(field.getText());

            if (liczba > 0) addIssues(liczba);
            else if (liczba < 0) removeIssues(liczba);
        }


    }

    /**
     * creates new BookIssue objects and adds them to database
     * @param liczba number of BookIssue objects to be created
     */
    private void addIssues(int liczba)
    {
        manager.getTransaction().begin();
        Date date = new Date();

        for (int i = 0; i<liczba; i++)
        {
            BookIssue dummy = new BookIssue(date, false, bookDefinition);

            try
            {
                manager.persist(dummy);
            }
            catch (Exception e)
            {
                System.out.println("Error with adding book issues!\n");
                manager.getTransaction().rollback();
                break;
            }
        }

        manager.getTransaction().commit();
        back();
    }

    /**
     * removes BookIssue objects from database, provided there are unassigned to Order objects. If given parameter is larger
     * than unassigned objects, only those will be removed
     * @param liczba number of BookIssue objects to be removed
     */
    private void removeIssues(int liczba)
    {
        liczba = liczba * (-1);

        manager.getTransaction().begin();


        int rozmiar = lista.size();
        int j = 0;//zmienna poboczna, wykorzystana do poprawnego obliczenia
        //usuniętych egzemplarzy, jeśli nie usunięto wszystkich
        boolean fail = false;

        for (int i = 0; i<liczba; i++)
        {

            try
            {
                BookIssue dummy = lista.get(rozmiar-i-1);

                if (dummy.isAway())
                {
                    fail = true;
                    j++;
                    continue;
                }

                manager.remove(dummy);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                System.out.println("Error! Couldn't remove the selected number" +
                        "of issues. Removed only " + (i-1));
                break;
            }
            catch (Exception e)
            {
                System.out.println("Error with removing issues!\n");
                manager.getTransaction().rollback();
                break;
            }
        }
        manager.getTransaction().commit();
        if (fail) System.out.println("Failed to remove selected issues, since " +
                "some of them were away: " + j);
        back();
    }

    public void back()
    {
        Stage st = (Stage) con.getButton().getScene().getWindow();
        Stage st2 = (Stage) label.getScene().getWindow();

        st.show();
        con.refresh();
        st2.close();
    }

    private void mapTable()
    {
        Query q = manager.createQuery("select c from BookIssue c where c.definition.idBookDef = ?1")
                .setParameter(1, book.getIdBookDef());

         lista = q.getResultList();

        label.setText(String.valueOf(lista.size()));

        q = manager.createQuery("select c from BookDefinition c where c.idBookDef = ?1")
                .setParameter(1, book.getIdBookDef());

        try
        {
            bookDefinition = (BookDefinition) q.getSingleResult();
        }
        catch (NoResultException e)
        {
            System.out.println("Error with mapping BookDefinition (AdminBookIssuesController)\n");
            back();
        }
    }
}
