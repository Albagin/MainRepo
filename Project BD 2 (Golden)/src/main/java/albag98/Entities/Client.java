package albag98.Entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "Client")
@Table(name = "Clients")
public class Client
{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name="generator", sequenceName="Client_Sequence", allocationSize=1)
    @Column(name = "Id_Client")
    private long idClient;

    @Column(name = "Name")
    private String name;

    @Column(name = "Surname")
    private String surname;

    @Column(name = "EMail")
    private String eMail;

    @Column(name = "Phone_Number")
    private String phoneNumber;

    @Column(name = "Voivodeship")
    private String voivodeship;

    @Column(name = "City_of_Residence")
    private String cityOfResidence;

    @Column(name = "Street")
    private String street;

    @Column(name = "Login")
    private String login;

    @Column(name = "Password")
    private String password;

    @Column(name = "Date_of_Creation")
    private Date dateOfCreation;

    @Column(name = "Admin")
    private boolean admin;

    @Column(name = "Active")
    private boolean active;

    @OneToMany(targetEntity = Order.class)
    @JoinColumn(name = "Id_Client")
    private List<Order> order;

    public Client() {
    }

    public Client( String name, String surname, String EMail, String phone_Number, String voivodeship, String City, String street, String login, String password, boolean Admin, boolean Active) {
        this.name = name;
        this.surname = surname;
        this.eMail = EMail;
        phoneNumber = phone_Number;
        this.voivodeship = voivodeship;
        cityOfResidence = City;
        this.street = street;
        this.login = login;
        this.password = password;
        this.admin = Admin;
        this.active = Active;


        this.dateOfCreation = new Date();

    }

    public long getIdClient() {
        return idClient;
    }

    public void setIdClient(long idClient) {
        this.idClient = idClient;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getCityOfResidence() {
        return cityOfResidence;
    }

    public void setCityOfResidence(String cityOfResidence) {
        this.cityOfResidence = cityOfResidence;
    }

    public List<Order> getOrder() {
        return order;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Clients{" +
                "idClient=" + idClient +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", eMail='" + eMail + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", voivodeship='" + voivodeship + '\'' +
                ", street='" + street + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", admin=" + admin +
                ", active=" + active +
                '}';
    }
}
