package com.integrail.networkers.user_interface.createaccount;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.integrail.networkers.AfterTaskImplementation;
import com.integrail.networkers.MainActivity;
import com.integrail.networkers.data_representations.account_representations.Account;
import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.validate.ErrorMessageManager;
import com.integrail.networkers.data_representations.validate.InValid;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Integrail on 7/18/2016.
 */

public class CreateAccountCustomer extends Fragment implements View.OnClickListener, AfterTaskImplementation{
    private TextView[] error;
    private LinearLayout linearLayout;
    private View view;
    private EditText eText;
    private ErrorMessageManager manage;
    private ImageView profilePic;
    private String customImagePath;
    private String user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.create_client_account, container, false);
        error = new TextView[7];
        manage = new ErrorMessageManager();
        manage.initializeErrorMessages(error, getActivity());
        linearLayout = (LinearLayout) view.findViewById(R.id.dynamicCreateCustomerAccountLayout);
        Button finish = (Button) view.findViewById(R.id.createCustomerAccountFinish);
        Button image = (Button) view.findViewById(R.id.pictureButton1);
        image.setOnClickListener(this);
        finish.setOnClickListener(this);
        profilePic = (ImageView) view.findViewById(R.id.imageView2);
        return view;
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.createCustomerAccountFinish:
                Account cuAccount = parseCustomerFields();
                NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_ACCOUNT, new Gson().toJson(cuAccount), true, getActivity(), this, 0, 0);
                try {
                    connection.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pictureButton1:
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
                customImagePath = getFilePath(selectedImageUri);
                profilePic.setImageBitmap(BitmapFactory.decodeFile(customImagePath));
            }
        }
    }
    public String getFilePath(Uri uri){
        if(uri == null)
            return null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor res = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if(res!=null&&res.moveToFirst()){
            String path = res.getString(res.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            res.close();
            return path;
        }
        return uri.getPath();
    }
    @Override
    public void update(NetworkConnection connection, int index){
        manage.removeErrorMessages(linearLayout, error);
        InValid message = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
        if (message != null) {
            inputErrorMessage(message);
        } else {
            if(customImagePath!=null){
                connection = new NetworkConnection(NetworkConstants.SET_USER_IMAGE, null, false, getActivity(), null, 0, 0);
                connection.openFile(new LocalFileManager(getActivity()).openFile(customImagePath));
                connection.setExistingFileName(user);
                try{
                    connection.execute();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void inputErrorMessage(InValid valid) {
        boolean stopScroll = true;
        String badPassword1 = valid.getInValidPassword();
        String badPhoneNumber = valid.getInValidPhone();
        String badZip = valid.getInValidZip();
        String badFirstName = valid.getInValidFirstName();
        String badEmail = valid.getInValidEmail();
        String badLastName = valid.getInValidLastName();
        final ScrollView scroll = (ScrollView) view.findViewById(R.id.createCustomerAccountScroll);
        if (!badFirstName.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.cuCr0));
            error[0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[0].setTextColor(Color.parseColor("#cc0000"));
            error[0].setGravity(Gravity.CENTER);
            error[0].setText(badFirstName);
            linearLayout.addView(error[0], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView9).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badLastName.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.cuCr1));
            error[1].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[1].setTextColor(Color.parseColor("#cc0000"));
            error[1].setGravity(Gravity.CENTER);
            error[1].setText(badLastName);
            linearLayout.addView(error[1], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView10).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badEmail.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.cuCr2));
            error[2].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[2].setTextColor(Color.parseColor("#cc0000"));
            error[2].setGravity(Gravity.CENTER);
            error[2].setText(badEmail);
            linearLayout.addView(error[2], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView11).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badPhoneNumber.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.cuCr3));
            error[3].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            error[3].setTextColor(Color.parseColor("#cc0000"));
            error[3].setGravity(Gravity.CENTER);
            error[3].setText(badPhoneNumber);
            linearLayout.addView(error[3], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView12).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badZip.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.cuCr4));
            error[4].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[4].setTextColor(Color.parseColor("#cc0000"));
            error[4].setGravity(Gravity.CENTER);
            error[4].setText(badZip);
            linearLayout.addView(error[4], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView13).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badPassword1.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.cuCr5));
            error[5].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[5].setTextColor(Color.parseColor("#cc0000"));
            error[5].setGravity(Gravity.CENTER);
            error[5].setText(badPassword1);
            linearLayout.addView(error[5], find + 1);
            eText = (EditText) view.findViewById(R.id.cuCr5);
            eText.setText("");
            eText = (EditText) view.findViewById(R.id.cuCr6);
            eText.setText("");
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView14).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (badPassword1.equals("Required field.")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.cuCr6));
            error[6].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            error[6].setTextColor(Color.parseColor("#cc0000"));
            error[6].setGravity(Gravity.CENTER);
            error[6].setText(badPassword1);
            linearLayout.addView(error[6], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView15).getTop());
                    }
                });
                stopScroll = false;
            }
        }

    }
    @Override
    public void openImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    public Account parseCustomerFields() {
        String[] c = new String[7];
        EditText cuEtext = (EditText) view.findViewById(R.id.cuCr0);
        EditText cuEtext1 = (EditText) view.findViewById(R.id.cuCr1);
        EditText cuEtext2 = (EditText) view.findViewById(R.id.cuCr2);
        user = cuEtext2.getText().toString();
        EditText cuEtext3 = (EditText) view.findViewById(R.id.cuCr3);
        EditText cuEtext4 = (EditText) view.findViewById(R.id.cuCr4);
        EditText cuEtext5 = (EditText) view.findViewById(R.id.cuCr5);
        EditText cuEtext6 = (EditText) view.findViewById(R.id.cuCr6);
        EditText[] cu = {cuEtext, cuEtext1, cuEtext2, cuEtext3, cuEtext4, cuEtext5, cuEtext6};
        for (int i = 0; i < cu.length; i++) {
            c[i] = cu[i].getText().toString();
        }
        return new Account(c[0], c[1], c[2], c[3], c[4], c[5], c[6]);
    }
}
