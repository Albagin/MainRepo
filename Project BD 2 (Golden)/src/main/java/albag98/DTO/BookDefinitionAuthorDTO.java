package albag98.DTO;


import albag98.Entities.BookDefinition;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BookDefinitionAuthorDTO
{
    private final SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

    private long idBookDef;

    private String title;

    private Date dateOfRelease;
    private String dateOfReleaseS;

    private String publisher;

    private String genre;

    private String author;


    public long getId_Book_Definitions() {
        return idBookDef;
    }

    public void setId_Book_Definitions(long id_Book_Def) {
        idBookDef = id_Book_Def;
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
        this.dateOfReleaseS = dt.format(dateOfRelease);
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

    public BookDefinitionAuthorDTO(BookDefinition entry) {
        this.idBookDef = entry.getId_Book_Definitions();
        this.genre = entry.getGenre();
        this.title = entry.getTitle();
        this.publisher = entry.getPublisher();
        this.dateOfRelease = entry.getDateOfRelease();
        this.dateOfReleaseS = dt.format(dateOfRelease);

    }

    public String getDateOfReleaseS() {
        return dateOfReleaseS;
    }

    public void setDateOfReleaseS(String date)
    {

        this.dateOfReleaseS = date;
    }
}
