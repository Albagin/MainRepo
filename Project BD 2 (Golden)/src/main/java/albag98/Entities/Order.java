package albag98.Entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Order")
@Table(name = "Orders")
public class Order
{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name="generator", sequenceName="Order_Sequence", allocationSize=1)
    @Column(name = "Id_Order")
    private long idOrder;

    @Column(name = "Date_of_Creation")
    private Date dateOfCreation;

    @Column(name = "Date_of_Finalizing")
    private Date dateOfFinalizing;

    @ManyToOne
    @JoinColumn(name = "Id_Client")
    private  Client client;

    @ManyToOne
    @JoinColumn(name = "Id_Book_Issue")
    private BookIssue issue;


    public Order(Date date_of_Creation,  Client client, BookIssue issue) {
        dateOfCreation = date_of_Creation;
        this.client = client;
        this.issue = issue;

    }



    public Order() {
    }

    public long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(long idOrder) {
        this.idOrder = idOrder;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Date getDateOfFinalizing() {
        return dateOfFinalizing;
    }

    public void setDateOfFinalizing(Date dateOfFinalizing) {
        this.dateOfFinalizing = dateOfFinalizing;
    }

    /*public long getIdClient() {
        return Id_Client;
    }

    public void setIdClient(long id_Client) {
        Id_Client = id_Client;
    }*/

    public BookIssue getIssue() {
        return issue;
    }

    public void setIssue(BookIssue issue) {
        issue = issue;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Order{" +
                "idOrder=" + idOrder +
                ", dateOfCreation=" + dateOfCreation +
                ", dateOfFinalizing=" + dateOfFinalizing +
                ", client=" + client +
                ", issue=" + issue +
                '}';
    }
}
