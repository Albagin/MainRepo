package  albag98.Entities;


import javax.persistence.*;

@Entity(name = "Author")
@Table(name = "Authors")
public class Author
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name="generator", sequenceName="Author_Sequence", allocationSize=1)
    @Column(name = "Id_Author")
    private long idAuthor;

    @Column(name = "Name")
    private String name;

    @Column(name = "Surname")
    private String surname;

    @Column(name = "Country")
    private String country;

    public long getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(long idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Author(String name, String surname, String country) {
        this.name = name;
        this.surname = surname;
        this.country = country;
    }

    public Author() {
    }

    @Override
    public String toString() {
        return name + " " +
                surname + " " +
                country;
    }
}
