package entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Objects;

public class PathEntity implements Entity{
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private Integer userIdDriver;
    private Integer userIdPassenger;
    private Integer courseId;

    public Integer getUserIdPassenger() {
        return userIdPassenger;
    }

    public void setUserIdPassenger(Integer userIdPassenger) {
        this.userIdPassenger = userIdPassenger;
    }

    public Integer getUserIdDriver() {
        return userIdDriver;
    }

    public void setUserIdDriver(Integer userIdDriver) {
        this.userIdDriver = userIdDriver;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathEntity)) return false;
        PathEntity that = (PathEntity) o;
        return id.equals(that.id) &&
                userIdDriver.equals(that.userIdDriver) &&
                userIdPassenger.equals(that.userIdPassenger) &&
                courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userIdDriver, userIdPassenger, courseId);
    }

    @Override
    public String toString() {
        return "PathEntity{" +
                "id=" + id +
                ", userIdKierowca=" + userIdDriver +
                ", userIdPasazer=" + userIdPassenger +
                ", courseId=" + courseId +
                '}';
    }
}
