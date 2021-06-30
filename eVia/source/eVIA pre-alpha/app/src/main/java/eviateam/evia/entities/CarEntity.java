package eviateam.evia.entities;

import java.util.Objects;

/**
 * Class holding car data
 */
public class CarEntity //implements Serializable
{

    private Integer id;
    private String registration;
    private String brand;
    private String color;
    private Integer userId;
    private Boolean isDefault = false;
    private Integer photoId;

    /**
     * Constructor called when constructing carEntity object from a car that doesn't posses a photo, acquired from JSONObject.
     * @param id car id
     * @param registration registration number
     * @param brand car brand
     * @param color car color
     * @param userId id of a user registered as owner of a car
     * @param isDefault is the car set as current
     * @param photoId id of a photo associated with the car
     */
    public CarEntity(Integer id, String registration, String brand, String color, Integer userId, Boolean isDefault, Integer photoId)
    {
        this.id = id;
        this.registration = registration;
        this.brand = brand;
        this.color = color;
        this.userId = userId;
        this.isDefault = isDefault;
        this.photoId = photoId;
    }

    /**
     * Constructor called when constructing carEntity object from a car that posses a photo, acquired from JSONObject.
     * @param id car id
     * @param registration registration number
     * @param brand car brand
     * @param color car color
     * @param userId id of a user registered as owner of a car
     * @param isDefault is the car set as current
     */
    public CarEntity(Integer id, String registration, String brand, String color, Integer userId, Boolean isDefault)
    {
        this.id = id;
        this.registration = registration;
        this.brand = brand;
        this.color = color;
        this.userId = userId;
        this.isDefault = isDefault;
    }

    /**
     * Constructor called when creating new carEntity
     * @param registration registration number
     * @param brand car brand
     * @param color car color
     */
    public CarEntity(String registration, String brand, String color)
    {
        this.registration = registration;
        this.brand = brand;
        this.color = color;
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
        if (!(o instanceof CarEntity)) return false;
        CarEntity carEntity = (CarEntity) o;
        return registration.equals(carEntity.registration) &&
                brand.equals(carEntity.brand) &&
                color.equals(carEntity.color) &&
                photoId.equals(carEntity.photoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registration, brand, color, userId, isDefault);
    }

    @Override
    public String toString() {
        return "" + brand + " " + color;
    }
}
