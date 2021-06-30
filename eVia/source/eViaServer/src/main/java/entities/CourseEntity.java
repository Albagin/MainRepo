package entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

public class CourseEntity implements Entity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private Double startPointX;
    private Double startPointY;
    private Double lastPointX;
    private Double lastPointY;
    private Date startTime;
    private Date lastTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getStartPointX() {
        return startPointX;
    }

    public void setStartPointX(Double startPointX) {
        this.startPointX = startPointX;
    }

    public Double getStartPointY() {
        return startPointY;
    }

    public void setStartPointY(Double startPointY) {
        this.startPointY = startPointY;
    }

    public Double getLastPointX() {
        return lastPointX;
    }

    public void setLastPointX(Double lastPointX) {
        this.lastPointX = lastPointX;
    }

    public Double getLastPointY() {
        return lastPointY;
    }

    public void setLastPointY(Double lastPointY) {
        this.lastPointY = lastPointY;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseEntity)) return false;
        CourseEntity that = (CourseEntity) o;
        return id.equals(that.id) &&
                startPointX.equals(that.startPointX) &&
                startPointY.equals(that.startPointY) &&
                Objects.equals(lastPointX, that.lastPointX) &&
                Objects.equals(lastPointY, that.lastPointY) &&
                startTime.equals(that.startTime) &&
                Objects.equals(lastTime, that.lastTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startPointX, startPointY, lastPointX, lastPointY, startTime, lastTime);
    }

    @Override
    public String toString() {
        return "CourseEntity{" +
                "id=" + id +
                ", startPointX=" + startPointX +
                ", startPointY=" + startPointY +
                ", lastPointX=" + lastPointX +
                ", lastPointY=" + lastPointY +
                ", startTime=" + startTime +
                ", lastTime=" + lastTime +
                '}';
    }
}
