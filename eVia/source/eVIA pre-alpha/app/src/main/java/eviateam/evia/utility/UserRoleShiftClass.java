package eviateam.evia.utility;

import android.content.Context;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Class supporting shifting user role, saving it as a file on phone memory, so that next
 * time, app will start with previously used role
 */
public class UserRoleShiftClass
{
    /**
     * Method setting last used role as passenger
     * @param context current context
     * @throws IOException thrown if error during saving file
     */
    public void userRoleShiftToPassenger(Context context) throws IOException
    {

        try (OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("evia_role.txt", Context.MODE_PRIVATE)))
            {
                writer.write(1);
            }
    }

    /**
     * Method setting last used role as driver
     * @param context current context
     * @throws IOException thrown if error during saving file
     */
    public void userRoleShiftToDriver(Context context) throws IOException
    {

        try (OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("evia_role.txt", Context.MODE_PRIVATE)))
            {
                writer.write(0);
            }

    }

}
