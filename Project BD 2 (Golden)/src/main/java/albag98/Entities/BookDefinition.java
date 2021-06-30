package albag98.Entities;


import javax.persistence.*;
import java.util.Date;

@Entity(name = "BookDefinition")
@Table(name = "Book_Definitions")
public class BookDefinition
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name="generator", sequenceName="Book_Definition_Sequence", allocationSize=1)
    @Column(name = "Id_Book_Def")
    private long idBookDef;

    @Column(name = "Title")
    private String title;

    @Column(name = "Date_of_Release")
    private Date dateOfRelease;

    @Column(name = "Publisher")
    private String publisher;

    @Column(name = "Genre")
    private String genre;


    @ManyToOne
    @JoinColumn(name = "Id_Author")
    private Author author;

    public BookDefinition() {
    }

    public long getId_Book_Definitions() {
        return idBookDef;
    }

    public void setIdBookDef(long id_Book_Definitions) {
        idBookDef = id_Book_Definitions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateOfRelease() {
        return dateOfRelease;
    }

    public void setDateOfRelease(Date dateOfRelease) { this.dateOfRelease = dateOfRelease; }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public albag98.Entities.Author getAuthor() {
        return author;
    }

    public void setAuthor(albag98.Entities.Author author) {
        author = author;
    }

    public BookDefinition(String title, Date date_of_Release, String publisher, String genre, Author author) {
        this.title = title;
        dateOfRelease = date_of_Release;
        this.publisher = publisher;
        this.genre = genre;
        this.author = author;
    }
}
