package com.integrail.networkers.primary_operations.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by integrailwork on 7/1/17.
 */

public class LocalFileManager {
    private Context context;
    public final static String MAIN_URI_DIRECTORY = "file:///data/user/0/com.integrail.networkers/app_";
    public final static String USER_IMAGE_DIRECTORY = "UserImages";
    public final static String MMS_DIRECTORY = "MultiMediaMessages";
    public LocalFileManager(Context context){
        this.context = context;
    }
    public FileInputStream openFile(String fileLocation){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(fileLocation));
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return fileInputStream;
    }
    public void writeFile(String directory, InputStream inputStream, int contentLength){
        File file = createDirectoryAndGetWritableFile(directory);
        byte[] buffer = new byte[contentLength];
        int bytesRead = -1;
        try {
            FileOutputStream stream = new FileOutputStream(file);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                stream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void deleteAllFiles(){
        File dir = context.getDir(USER_IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        if(dir.isDirectory()){
            String[] children = dir.list();
            for(int i = 0; i<children.length; i++){
                new File(dir, children[i]).delete();
            }
            dir.delete();
        }
        dir = context.getDir(MMS_DIRECTORY, Context.MODE_PRIVATE);
        if(dir.isDirectory()){
            String[] children = dir.list();
            for(int i = 0; i<children.length; i++){
                File subDir = new File(dir, children[i]);
                if(subDir.isDirectory()){
                    String[] subChildren = subDir.list();
                    for(int j = 0; j<subChildren.length; j++){
                        new File(subDir, subChildren[j]).delete();
                    }
                }
                subDir.delete();
            }
        }
        dir.delete();
    }
    public File createDirectoryAndGetWritableFile(String directory){
        String[] folders = directory.split("/");
        int length = folders.length;
        File[] finalDestination = new File[length];
        finalDestination[0] = context.getDir(folders[0], Context.MODE_PRIVATE);
        if(!finalDestination[0].exists())
            finalDestination[0].mkdir();
        for(int i = 1; i<length-1; i++){
            finalDestination[i] = new File(finalDestination[i-1], folders[i]);
            if(!finalDestination[i].exists())
                finalDestination[i].mkdir();
        }
        return new File(finalDestination[length-2], folders[length-1]);
    }
    public String getFilePath(Uri uri){
        if(uri == null)
            return null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor res = null;
        try {
            res = context.getContentResolver().query(uri, projection, null, null, null);
            if (res != null && res.moveToFirst()) {
                String path = res.getString(res.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                res.close();
                return path;
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            if(res!=null&&!res.isClosed())
                res.close();
        }
        return uri.getPath();
    }
}
