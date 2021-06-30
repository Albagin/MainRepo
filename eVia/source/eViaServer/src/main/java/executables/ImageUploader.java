package executables;

import entities.CarEntity;
import entities.PhotoEntity;
import org.tinylog.Logger;
import utility.DBConnection;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageUploader {

    private static final DBConnection connection = new DBConnection();

    private static byte[] toByte() {
        BufferedImage bImage;
        byte [] data = new byte[0];
        try {
            bImage = ImageIO.read(new File("sample2.png"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", bos );
            data = bos.toByteArray();
        } catch (IOException e) {
            Logger.error(e);
        }

        return data;
    }

    private static void uploadToDB() {
        List<CarEntity> cars = connection.selectAllCarsByUserId(1);
        PhotoEntity photo = new PhotoEntity();
        photo.setPhoto(toByte());
        cars.get(0).setPhotoId(connection.save(photo));
        connection.update(cars.get(0));
    }

    public static void main(String[] args) {
        uploadToDB();
    }
}
