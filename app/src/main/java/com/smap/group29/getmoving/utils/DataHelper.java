package com.smap.group29.getmoving.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static com.smap.group29.getmoving.utils.GlobalConstants.FILE_NAME;


// this class helps to preserve current steps value to make the app work offline
// though SharedPreferences has taken over the purpose of this class

public class DataHelper extends Activity{

    Context mContext;

    public DataHelper(Context context) {
        this.mContext = context;
    }

    public void save( long valueToSave){
        String stepsToSave = String.valueOf(valueToSave);
        FileOutputStream fos = null;

        try {
            fos = mContext.openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(stepsToSave.getBytes());
            //Toast.makeText(this, "Saved to ",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String load() {
        View v;
        FileInputStream fis = null;
        String fileValue = "";
        try {
            fis = mContext.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            fileValue = sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileValue;
    }


}
