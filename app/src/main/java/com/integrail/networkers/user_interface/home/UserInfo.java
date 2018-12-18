package com.integrail.networkers.user_interface.home;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integrail.networkers.AfterTaskImplementation;
import com.integrail.networkers.MainActivity;
import com.integrail.networkers.constants.NetworkConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.integrail.networkers.R;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.data_representations.account_representations.SimpleCustomer;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.primary_operations.storage.RecipientsDataBase;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Integrail on 7/21/2016.
 */

public class UserInfo extends Fragment implements View.OnClickListener, AfterTaskImplementation{
    private View view;
    private EditText fname;
    private EditText lname;
    private EditText email;
    private EditText phone;
    private EditText zip;
    private Button updateButton;
    private ImageView userPic;
    private String customImagePath;
    private Button changeUserImage;
    private long userId ;
    private LocalFileManager fileManager;
    private String lastChanged;
    @Override
    public void openImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.my_info, container, false );
        userPic = (ImageView) view.findViewById(R.id.imageView2);
        fname = (EditText) view.findViewById(R.id.editFirstName);
        lname = (EditText) view.findViewById(R.id.editLastName);
        email = (EditText) view.findViewById(R.id.editEmail);
        phone = (EditText) view.findViewById(R.id.editPhone);
        zip = (EditText) view.findViewById(R.id.editZip);
        updateButton = (Button) view.findViewById(R.id.updateInfo);
        updateButton.setOnClickListener(this);
        changeUserImage = (Button) view.findViewById(R.id.changeUserImage);
        changeUserImage.setOnClickListener(this);
        userId = new Preferences(getActivity()).userId();
        fileManager = new LocalFileManager(getActivity());
        lastChanged = ""+fileManager.createDirectoryAndGetWritableFile(LocalFileManager.USER_IMAGE_DIRECTORY+"/"+this.userId +".jpg").lastModified();
        NetworkConnection connection = new NetworkConnection(NetworkConstants.GET_USER_INFO, null, true, getActivity(), this, 1, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    @Override
    public void update(NetworkConnection connection, int index){
        switch(index) {
            case 1:
                SimpleCustomer user = new GsonBuilder().create().fromJson(connection.getReturnData(), SimpleCustomer.class);
                if (user != null) {
                    RequestOptions req = new RequestOptions().signature(new ObjectKey(String.valueOf(lastChanged))).placeholder(R.drawable.blank).dontAnimate();
                    lname.setText(user.getLastName());
                    fname.setText(user.getFirstName());
                    email.setText(user.getEmail());
                    zip.setText(user.getZip());
                    phone.setText(user.getPhone());
                    Glide.with(getActivity())
                            .load(Uri.parse(LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.USER_IMAGE_DIRECTORY+"/"+this.userId +".jpg"))
                            .apply(req)
                            .into(userPic);
                }
                break;
            case 2:
                String users = new Gson().toJson(new RecipientsDataBase(getActivity()).getUsers(), Long[].class);
                connection = new NetworkConnection(NetworkConstants.UPDATE_PIC, users, false, getActivity(), null, 0, 0);
                try{
                    connection.execute();
                } catch(Exception e){
                    e.printStackTrace();
                }
                break;
        }

    }
    @Override
    public void onClick(View view){
        NetworkConnection connection = null;
        switch(view.getId()){
            case R.id.updateInfo:
                String fName = fname.getText().toString();
                String lName = lname.getText().toString();
                String newEmail = email.getText().toString();
                String newPhone = phone.getText().toString();
                String newZip = zip.getText().toString();
                connection = new NetworkConnection(NetworkConstants.UPDATE_DATA, new Gson().toJson(new SimpleCustomer(fName,lName,newEmail,newPhone,newZip)), false, getActivity(), null, 0, 0);
                try {
                    connection.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.changePassword:
                break;
            case R.id.changeUserImage:
                if(((MainActivity)getActivity()).hasStoragePermissions()) {
                    openImage();
                } else{
                    ((MainActivity) getActivity()).requestStoragePermissions();
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Uri selectedImageUri = data.getData();
                customImagePath = fileManager.getFilePath(selectedImageUri);
                if(customImagePath!=null){
                    try {
                        FileInputStream fileInputStream = new FileInputStream(new File(customImagePath));
                        fileManager.writeFile(LocalFileManager.USER_IMAGE_DIRECTORY+"/"+ userId +".jpg", fileInputStream, fileInputStream.available());
                        lastChanged = ""+fileManager.createDirectoryAndGetWritableFile(LocalFileManager.USER_IMAGE_DIRECTORY+"/"+ userId +".jpg").lastModified();
                        RequestOptions req = new RequestOptions().signature(new ObjectKey(String.valueOf(lastChanged))).placeholder(R.drawable.blank).dontAnimate();
                        Glide.with(getActivity())
                                .load(Uri.parse(LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.USER_IMAGE_DIRECTORY+"/"+this.userId +".jpg"))
                                .apply(req)
                                .into(userPic);
                        NetworkConnection connection = new NetworkConnection(NetworkConstants.SET_USER_IMAGE, null, false, getActivity(), this, 2, 0);
                        connection.openFile(fileManager.openFile(customImagePath));
                        connection.setExistingFileName(""+ userId);
                        try{
                            connection.execute();
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}