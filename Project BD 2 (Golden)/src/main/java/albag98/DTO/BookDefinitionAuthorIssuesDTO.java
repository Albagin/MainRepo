package albag98.DTO;

import albag98.Entities.BookDefinition;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BookDefinitionAuthorIssuesDTO
{
    private final SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

    private long idBookDef;

    private String title;

    private Date dateOfRelease;
    private String dateOfReleaseS;

    private String publisher;

    private String genre;

    private String author;

    private int issues;

    private int issuesStock;

    public long getIdBookDef() {
        return idBookDef;
    }

    public void setIdBookDef(long idBookDef) {
        this.idBookDef = idBookDef;
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

    public void setDateOfRelease(Date dateOfRelease) {
        this.dateOfRelease = dateOfRelease;
    }

    public String getDateOfReleaseS() {
        return dateOfReleaseS;
    }

    public void setDateOfReleaseS(String dateOfReleaseS) {
        this.dateOfReleaseS = dateOfReleaseS;
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getIssues() {
        return issues;
    }

    public void setIssues(int issues) {
        this.issues = issues;
    }

    public int getIssuesStock() {
        return issuesStock;
    }

    public void setIssuesStock(int issuesStock) {
        this.issuesStock = issuesStock;
    }

    public BookDefinitionAuthorIssuesDTO(BookDefinition entry)
    {
        this.idBookDef = entry.getId_Book_Definitions();
        this.genre = entry.getGenre();
        this.title = entry.getTitle();
        this.publisher = entry.getPublisher();
        this.dateOfRelease = entry.getDateOfRelease();
        this.dateOfReleaseS = dt.format(dateOfRelease);
    }

}
