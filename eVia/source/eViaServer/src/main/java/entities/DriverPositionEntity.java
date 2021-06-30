package entities;

import enums.DriverStatus;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

public class DriverPositionEntity implements Entity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private Double pointLat;
    private Double pointLng;
    private Date time;
    private DriverStatus status;
    private Integer userId;
    private Double direction;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userID) {
        this.userId = userID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPointLat() {
        return pointLat;
    }

    public void setPointLat(Double pointX) {
        this.pointLat = pointX;
    }

    public Double getPointLng() {
        return pointLng;
    }

    public void setPointLng(Double pointY) {
        this.pointLng = pointY;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Double getDirection() {
        return direction;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DriverPositionEntity)) return false;
        DriverPositionEntity entity = (DriverPositionEntity) o;
        return id.equals(entity.id) &&
                pointLat.equals(entity.pointLat) &&
                pointLng.equals(entity.pointLng) &&
                time.equals(entity.time) &&
                status == entity.status &&
                userId.equals(entity.userId) &&
                direction.equals(entity.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pointLat, pointLng, time, status, userId, direction);
    }

    @Override
    public String toString() {
        return "DriverPositionEntity{" +
                "id=" + id +
                ", pointLat=" + pointLat +
                ", pointLng=" + pointLng +
                ", time=" + time +
                ", status=" + status +
                ", userId=" + userId +
                ", direction=" + direction +
                '}';
    }
}
