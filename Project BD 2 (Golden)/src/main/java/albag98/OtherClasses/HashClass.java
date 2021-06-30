package albag98.OtherClasses;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashClass
{
    /**
     * this method will take password as a parameter and return string containing hashed password
     * @param password password to be hashed
     * @return given password hashed
     */
    public String hash(String password)
    {
        String hashed = null;

        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes());

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i<bytes.length; i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            hashed = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        //System.out.println(hashed);
        return hashed;
    }
}
