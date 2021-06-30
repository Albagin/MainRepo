package entities;

import enums.RequestStatus;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

public class RequestsEntity implements Entity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private Double startPointX;
    private Double startPointY;
    private Date time;
    private Double destPointX;
    private Double destPointY;
    private Integer userId;
    private RequestStatus status;
    private Double bearing;

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Double getDestPointX() {
        return destPointX;
    }

    public void setDestPointX(Double destPointX) {
        this.destPointX = destPointX;
    }

    public Double getDestPointY() {
        return destPointY;
    }

    public void setDestPointY(Double destPointY) {
        this.destPointY = destPointY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestsEntity)) return false;
        RequestsEntity entity = (RequestsEntity) o;
        return id.equals(entity.id) &&
                startPointX.equals(entity.startPointX) &&
                startPointY.equals(entity.startPointY) &&
                time.equals(entity.time) &&
                destPointX.equals(entity.destPointX) &&
                destPointY.equals(entity.destPointY) &&
                userId.equals(entity.userId) &&
                status == entity.status &&
                bearing.equals(entity.bearing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startPointX, startPointY, time, destPointX, destPointY, userId, status, bearing);
    }

    @Override
    public String toString() {
        return "RequestsEntity{" +
                "id=" + id +
                ", startPointX=" + startPointX +
                ", startPointY=" + startPointY +
                ", time=" + time +
                ", destPointX=" + destPointX +
                ", destPointY=" + destPointY +
                ", userId=" + userId +
                ", status=" + status +
                ", Bearing=" + bearing +
                '}';
    }
}
