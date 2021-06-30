package albag98.Entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "BookIssue")
@Table(name = "Book_Issues")
public class BookIssue
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name="generator", sequenceName="Book_Issue_Sequence", allocationSize=1)
    @Column(name = "Id_Book_Issue")
    private long idBookIssue;

    @Column(name = "Date_of_Acquisition")
    private Date dateOfAcquisition;

    @Column(name = "Away")
    private boolean away;


    @ManyToOne
    @JoinColumn(name = "Id_Book_Def")
    private BookDefinition definition;



    public long getIdBookIssue() {
        return idBookIssue;
    }

    public void setIdBookIssue(long id_Book_Issues) {
        idBookIssue = id_Book_Issues;
    }

    public Date getDateOfAcquisition() {
        return dateOfAcquisition;
    }

    public void setDateOfAcquisition(Date dateOfAcquisition) {
        this.dateOfAcquisition = dateOfAcquisition;
    }

    public boolean isAway() {
        return away;
    }

    public void setAway(boolean away) {
        this.away = away;
    }

    public BookDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(BookDefinition definition) {
        definition = definition;
    }

    public BookIssue(Date dateOfAcquisition, boolean away, BookDefinition definition) {
        this.dateOfAcquisition = dateOfAcquisition;
        this.away = away;
        this.definition = definition;
    }

    public BookIssue() {
    }

    @Override
    public String toString() {
        return "BookIssue{" +
                "idBookIssue=" + idBookIssue +
                ", dateOfAcquisition=" + dateOfAcquisition +
                ", away=" + away +
                ", definition=" + definition +
                '}';
    }
}
