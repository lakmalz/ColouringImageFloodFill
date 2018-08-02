package lakmalz.git.colouringimagefloodfill.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utility method for Bitmap class
 *
 * Created by Dhaval Patel
 *
 * @version 1.0
 * @since 01 August 2018
 */
public class BitmapUtil {

    /**
     * @param context Application Context
     * @param filePath  Asset file path
     * @return Bitmap Object
     */
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        Bitmap bitmap = null;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream stream = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(stream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Save Bitmap as Image
     *
     * @param bitmap to save as Image file
     */
    public static File saveBitmapAsImage(Bitmap bitmap) {

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            String formattedDate = df.format(Calendar.getInstance().getTime());

            //create a file to write bitmap data
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(directory, "Image_"+formattedDate+".jpg");
            file.createNewFile();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
