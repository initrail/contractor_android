package com.integrail.networkers.user_interface.signin;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonSyntaxException;
import com.integrail.networkers.AndroidDatabaseManager;
import com.integrail.networkers.data_representations.account_representations.LoginCredentials;
import com.integrail.networkers.MainActivity;
import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.message_representations.LatestConversationData;
import com.integrail.networkers.data_representations.project_representations.Project;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.messaging.StartMessagingService;
import com.integrail.networkers.primary_operations.messaging.ToggleAlarm;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.primary_operations.storage.ProjectDataBase;
import com.integrail.networkers.primary_operations.storage.RecipientsDataBase;
import com.integrail.networkers.user_interface.AfterTask;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.user_interface.createaccount.CreateAccount;
import com.integrail.networkers.user_interface.home.HomeDrawer;
import com.integrail.networkers.data_representations.validate.InValid;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Integrail on 7/20/2016.
 */

public class SignInFragment extends Fragment implements View.OnClickListener, AfterTask {
    private View view;
    private EditText emailText;
    private EditText passwordText;
    private TextView topError;
    private TextView emailError;
    private TextView passwordError;
    private String email;
    private String password;
    private int index;
    private Long[] users;
    private String session;
    private long userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sign_in, container, false);

        emailText = (EditText) this.view.findViewById(R.id.signInEmailText);
        index = 0;
        passwordText = (EditText) this.view.findViewById(R.id.signInPasswordText);

        TextView createAccount = (TextView) view.findViewById(R.id.signInCreateAccountButton);
        createAccount.setOnClickListener(this);

        TextView forgotPassword = (TextView) view.findViewById(R.id.signInForgotPassword);
        forgotPassword.setOnClickListener(this);

        Button signIn = (Button) view.findViewById(R.id.signInButton);
        signIn.setOnClickListener(this);

        topError = (TextView) view.findViewById(R.id.topError);

        emailError = (TextView) view.findViewById(R.id.emailError);

        passwordError = (TextView) view.findViewById(R.id.passwordError);

        ImageView tv = (ImageView) view.findViewById(R.id.imageView);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent dbmanager = new Intent(getActivity(),AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        NetworkConnection connection = null;
        eraseErrors();
        switch (view.getId()) {
            case R.id.signInForgotPassword:
                break;
            case R.id.signInCreateAccountButton:
                getFragmentManager().beginTransaction().replace(R.id.fragment, new CreateAccount(), "").addToBackStack("").setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                break;
            case R.id.signInButton:
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                LoginCredentials login = new LoginCredentials(email, password);
                String jsonLogin = new Gson().toJson(login, LoginCredentials.class);
                connection = new NetworkConnection(NetworkConstants.SIGNING_IN, jsonLogin, true, getActivity(), this, 0, 0);
                try {
                    connection.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void update(NetworkConnection connection, int index) {
        switch (index) {
            case 0:
                eraseErrors();
                InValid error = null;
                String idUser = connection.getReturnData();
                session = connection.getSession();
                try {
                    error = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
                } catch (JsonSyntaxException e){
                    e.printStackTrace();
                }
                if (error != null) {
                    inputErrorMessage5(error);
                } else {
                    userId = Long.valueOf(idUser);
                    new Preferences(getActivity()).setOtherCredentials(true, userId);
                    new Preferences(getActivity()).setSession(session);
                    connection = new NetworkConnection(NetworkConstants.LOAD_CONVERSATIONS, null, true, getActivity(), this, 1, 0);
                    try {
                        connection.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                String result = connection.getReturnData();
                if(result!=null) {
                    LatestConversationData[] threads = new GsonBuilder().create().fromJson(connection.getReturnData(), LatestConversationData[].class);
                    if(threads != null)
                        new RecipientsDataBase(getActivity().getApplicationContext()).insertConversations(threads);
                }
                connection = new NetworkConnection(NetworkConstants.LOAD_MESSAGES, result, true, getActivity(), this, 2, 0);
                try{
                    connection.execute();
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case 2:
                if(connection.getReturnData()!=null) {
                    Message[] messages = new GsonBuilder().create().fromJson(connection.getReturnData(), Message[].class);
                    if(messages!=null) {
                        MessageDataBase db = new MessageDataBase(getActivity().getApplicationContext());
                        db.runningOnUI(true);
                        db.insertMessages(messages, false);
                    }
                }
                connection = new NetworkConnection(NetworkConstants.GET_EXISTING_PROJECTS, null, true, getActivity(), this, 3, 0);
                try{
                    connection.execute();
                } catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case 3:
                if(connection.getReturnData()!=null) {
                    Project[] projects = new GsonBuilder().create().fromJson(connection.getReturnData(), Project[].class);
                    if(projects!=null)
                        new ProjectDataBase(getActivity().getApplicationContext()).insertProjects(projects);
                }
                users = new RecipientsDataBase(getActivity()).getUsers();
                connection = new NetworkConnection(NetworkConstants.GET_USER_IMAGE , String.valueOf(userId), true, getActivity(), this, 5, 0);
                connection.setExpectingImage(true);
                connection.setDirectory("UserImages");
                try{
                    connection.execute();
                } catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case 5:
                new Preferences(getActivity()).setServiceShouldBeRunning(true);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeDrawer()).commit();
                ((MainActivity) getActivity()).bindService();
                break;
        }
    }
    public void eraseErrors() {
        topError.getLayoutParams().height = 0;
        topError.setLayoutParams(topError.getLayoutParams());

        passwordError.getLayoutParams().height = 0;
        passwordError.setLayoutParams(passwordError.getLayoutParams());

        emailError.getLayoutParams().height = 0;
        emailError.setLayoutParams(emailError.getLayoutParams());
    }

    public void inputErrorMessage5(InValid inValid) {
        String badEmail = inValid.getInValidEmail();
        String badPassword = inValid.getInValidPassword();
        if (!badEmail.equals("")) {
            if (badEmail.equals("Either the password or email address are incorrect.")) {
                topError.setText(badEmail);
                topError.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                topError.setLayoutParams(topError.getLayoutParams());
            } else {
                emailError.setText(badEmail);
                emailError.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                emailError.setLayoutParams(emailError.getLayoutParams());
            }
        }
        if (!badPassword.equals("")) {
            passwordError.setText(badPassword);
            passwordError.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            passwordError.setLayoutParams(passwordError.getLayoutParams());
        }
    }
}
