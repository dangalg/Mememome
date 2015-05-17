package com.mememome.mememome.model.file_manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by dangal on 5/8/15.
 */
public class FileManager {

    public final static String TAG = FileManager.class.getSimpleName();

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File saveMemo(Context context, String memoName, String text) {
        // Get the directory for the app's private pictures directory.

        File file = null;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //handle case of no SDCARD present
        } else {
            String dir = Environment.getExternalStorageDirectory()+File.separator+"mememome";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();

            //create file
            file = new File(dir, memoName + ".txt");


            writeToFile(memoName, text);
        }

        return file;
    }

    public static File loadMemo(Context context, String memoName){
        return new File(context.getExternalFilesDir("mememome"), memoName +".txt");
    }

    public static boolean deleteFile(Context context, String memoName){
        File file = new File(context.getExternalFilesDir("mememome"), memoName +".txt");
        return file.delete();
    }

    public static File writeToFile(String fileName, String data) {

        String fpath = getExternalDir() +fileName+".txt";

        File file = new File(fpath);
        try {

            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();

            Log.d("Suceess","Sucess");

        } catch (IOException e) {
            e.printStackTrace();
            file = null;
        }
        return file;

    }


    public static String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            FileInputStream inputStream = new FileInputStream (new File(fileName));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


    public static String getExternalDir(){
        File maindir = new File(Environment.getExternalStorageDirectory()+File.separator+"mememome");
        if(!maindir.exists()){
            maindir.mkdirs();
        }
        return Environment.getExternalStorageDirectory()+File.separator+"mememome"
                +File.separator;
    }

}
