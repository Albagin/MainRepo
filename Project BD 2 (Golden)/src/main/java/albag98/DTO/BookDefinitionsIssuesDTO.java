package albag98.DTO;


import albag98.Entities.BookDefinition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookDefinitionsIssuesDTO
{
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private String title;

    private Date dateOfRelease;
    private String dateOfReleaseS;

    private String Publisher;

    private String genre;

    private Date dateOfAcquisition;
    private String dateOfAcquisitionS;






    public BookDefinitionsIssuesDTO(BookDefinition entry) {
        this.genre = entry.getGenre();
        this.title = entry.getTitle();
        this.Publisher = entry.getPublisher();
        this.dateOfRelease = entry.getDateOfRelease();

        this.dateOfReleaseS = dateFormat.format(entry.getDateOfRelease());
    }

    public Date getDate_of_Acquisition() {

        return dateOfAcquisition;
    }

    public void setDate_of_Acquisition(Date date_of_Acquisition) {
        dateOfAcquisition = date_of_Acquisition;
        dateOfAcquisitionS = dateFormat.format(dateOfAcquisition);
    }

    public String getDateAcqTable() {
        return dateOfAcquisitionS;
    }

    public void setDateAcqTable(String dateAcqTable) {
        this.dateOfAcquisitionS = dateAcqTable;
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
        this.dateOfReleaseS = dateFormat.format(dateOfRelease);
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Date getDateOfAcquisition() {
        return dateOfAcquisition;
    }

    public void setDateOfAcquisition(Date dateOfAcquisition) {
        this.dateOfAcquisition = dateOfAcquisition;
    }

    public String getDateOfAcquisitionS() {
        return dateOfAcquisitionS;
    }

    public void setDateOfAcquisitionS(String dateOfAcquisitionS) {
        this.dateOfAcquisitionS = dateOfAcquisitionS;
    }


    public String getdateOfAcquisitionS() {
        return dateOfAcquisitionS;
    }

    public void setdateOfAcquisitionS(String getDateofAcquisitionS) {
        this.dateOfAcquisitionS = getDateofAcquisitionS;
    }

    public String getDateOfReleaseS() {
        return dateOfReleaseS;
    }

    public void setDateOfReleaseS(String dateOfReleaseS) {
        this.dateOfReleaseS = dateOfReleaseS;
    }


}
