package entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Objects;

public class PhotoEntity implements Entity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private byte[] photo;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoEntity)) return false;
        PhotoEntity photoEntity = (PhotoEntity) o;
        return id.equals(photoEntity.id) &&

                Arrays.equals(photo, photoEntity.photo);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }
}
