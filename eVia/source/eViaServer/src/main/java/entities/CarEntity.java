package entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Objects;

public class CarEntity implements Entity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private String registration;
    private String brand;
    private String color;
    private Integer userId;
    private Boolean isDefault = false;
    private Integer photoId;

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

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarEntity carEntity = (CarEntity) o;
        return id.equals(carEntity.id) &&
                registration.equals(carEntity.registration) &&
                brand.equals(carEntity.brand) &&
                color.equals(carEntity.color) &&
                Objects.equals(userId, carEntity.userId) &&
                isDefault.equals(carEntity.isDefault) &&
                Objects.equals(photoId, carEntity.photoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registration, brand, color, userId, isDefault, photoId);
    }

    @Override
    public String toString() {
        return "CarEntity{" +
                "id=" + id +
                ", registration='" + registration + '\'' +
                ", brand='" + brand + '\'' +
                ", color='" + color + '\'' +
                ", userId=" + userId +
                ", isDefault=" + isDefault +
                ", photoId=" + photoId +
                '}';
    }
}
