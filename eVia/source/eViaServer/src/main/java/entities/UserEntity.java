package entities;

import enums.UserType;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Objects;

public class UserEntity implements Entity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String login;
    private UserType lastUserType = UserType.P;
    private Boolean admin = false;
    private Double ocenaKierowca;
    private Integer podwozkiKierowca = 0;
    private Integer podwozkiPasazer = 0;
    private String fbId;
    private Double ocenaPasazer;

    public void dodajOcene(ScoresEntity score) {
        if (score.getRole().equals(UserType.P)) { //passenger
            if (ocenaPasazer == null) {
                ocenaPasazer = Double.valueOf(score.getScore());
            }
            else {
                ocenaPasazer = (ocenaPasazer * podwozkiPasazer + score.getScore()) / (podwozkiPasazer + 1);
            }
            podwozkiPasazer++;
        }
        else if (score.getRole().equals(UserType.D)) { //driver
            if (ocenaKierowca == null) {
                ocenaKierowca = Double.valueOf(score.getScore());
            }
            else {
                ocenaKierowca = (ocenaKierowca * podwozkiKierowca + score.getScore()) / (podwozkiKierowca + 1);
            }
            podwozkiKierowca++;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserType getLastUserType() {
        return lastUserType;
    }

    public void setLastUserType(UserType userType) {
        this.lastUserType = userType;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Double getOcenaKierowca() {
        return ocenaKierowca;
    }

    public void setOcenaKierowca(Double ranking) {
        this.ocenaKierowca = ranking;
    }

    public Integer getPodwozkiKierowca() {
        return podwozkiKierowca;
    }

    public void setPodwozkiKierowca(Integer podwozkiKierowca) {
        this.podwozkiKierowca = podwozkiKierowca;
    }

    public Integer getPodwozkiPasazer() {
        return podwozkiPasazer;
    }

    public void setPodwozkiPasazer(Integer podwozkiPasazer) {
        this.podwozkiPasazer = podwozkiPasazer;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public Double getOcenaPasazer() {
        return ocenaPasazer;
    }

    public void setOcenaPasazer(Double ocenaPasazer) {
        this.ocenaPasazer = ocenaPasazer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        UserEntity that = (UserEntity) o;
        return id.equals(that.id) &&
                name.equals(that.name) &&
                surname.equals(that.surname) &&
                email.equals(that.email) &&
                login.equals(that.login) &&
                lastUserType == that.lastUserType &&
                admin.equals(that.admin) &&
                Objects.equals(ocenaKierowca, that.ocenaKierowca) &&
                podwozkiKierowca.equals(that.podwozkiKierowca) &&
                podwozkiPasazer.equals(that.podwozkiPasazer) &&
                fbId.equals(that.fbId) &&
                Objects.equals(ocenaPasazer, that.ocenaPasazer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, email, login, lastUserType, admin, ocenaKierowca, podwozkiKierowca, podwozkiPasazer, fbId, ocenaPasazer);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", lastUserType=" + lastUserType +
                ", admin=" + admin +
                ", ocenaKierowca=" + ocenaKierowca +
                ", podwozkiKierowca=" + podwozkiKierowca +
                ", podwozkiPasazer=" + podwozkiPasazer +
                ", fbId='" + fbId + '\'' +
                ", ocenaPasazer=" + ocenaPasazer +
                '}';
    }
}
