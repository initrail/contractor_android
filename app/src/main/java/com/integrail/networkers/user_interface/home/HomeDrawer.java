package com.integrail.networkers.user_interface.home;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.integrail.networkers.MainActivity;
import com.integrail.networkers.R;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.messaging.MessagingService;
import com.integrail.networkers.primary_operations.messaging.StartMessagingService;
import com.integrail.networkers.primary_operations.messaging.ToggleAlarm;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.DataBaseManager;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.user_interface.AfterTask;
import com.integrail.networkers.user_interface.home.conversation_list.ConversationList;
import com.integrail.networkers.user_interface.home.create_project.CreateProject;
import com.integrail.networkers.user_interface.home.existing_projects_list.ProjectList;
import com.integrail.networkers.user_interface.signin.SignInFragment;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Integrail on 7/21/2016.
 */

public class HomeDrawer extends Fragment implements NavigationView.OnNavigationItemSelectedListener, AfterTask {
    private View view;
    private boolean loadConversationList;
    private MessagingService messagingService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if (view == null){
            view = inflater.inflate(R.layout.my_home, container, false );
            NavigationView navigationView = (NavigationView) view.findViewById(R.id.home_view);
            navigationView.setNavigationItemSelectedListener(this);
            FragmentManager fM = getFragmentManager();
            FragmentTransaction fT = fM.beginTransaction();
            Fragment main = new Home();
            fT.add(R.id.homeFragment, main);
            fT.commit();
        } else {
            if(getActivity().getFragmentManager().findFragmentById(R.id.homeFragment) instanceof ConversationList){
                ((ConversationList) getFragmentManager().findFragmentById(R.id.homeFragment)).updateConversationPage();
            }
        }
        if(loadConversationList){
            getFragmentManager().beginTransaction().add(R.id.homeFragment, new ConversationList()).commit();
            loadConversationList = false;
        }
        return view;
    }
    public void setLoadConversationList(boolean loadConversationList){
        this.loadConversationList = loadConversationList;
    }
    public void setMessagingService(MessagingService m){
        messagingService = m;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        setView(id);
        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.my_home_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void setView(int id){
        Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.homeFragment);
        switch(id) {
            case R.id.home:
                if(!(currentFragment instanceof Home)) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.homeFragment, new Home(), "").
                            addToBackStack("").
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                            commit();
                }
                break;
            case R.id.create_project:
                if(!(currentFragment instanceof CreateProject)) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.homeFragment, new CreateProject(), "").
                            addToBackStack("").
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                            commit();
                }
                break;
            case R.id.details:
                if(!(currentFragment instanceof UserInfo)) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.homeFragment, new UserInfo(), "").
                            addToBackStack("").
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                            commit();
                }
                break;
            case R.id.messages:
                if(!(currentFragment instanceof ConversationList)) {
                    ConversationList messages = new ConversationList();
                    messages.setMessagingService(messagingService);
                    getFragmentManager().beginTransaction().
                            replace(R.id.homeFragment, messages, "").
                            addToBackStack("").
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                            commit();
                }
                break;
            case R.id.existing_projects:
                if(!(currentFragment instanceof ProjectList)) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.homeFragment, new ProjectList(), "").
                            addToBackStack("").
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                            commit();
                }
                break;
            case R.id.logout:
                NetworkConnection connection = new NetworkConnection(NetworkConstants.SIGN_OUT, null, false, getActivity(), this, 0, 0);
                try {
                    connection.execute();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.settings:
                if(!(currentFragment instanceof Settings)) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.homeFragment, new Settings(), "").
                            addToBackStack("").
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                            commit();
                }
                break;
            case R.id.help:
                break;
        }
    }
    @Override
    public void update(NetworkConnection connection, int index){
        switch(index){
            case 0:
                new LocalFileManager(getActivity()).deleteAllFiles();
                Preferences clear = new Preferences(getActivity());
                clear.setServiceShouldBeRunning(false);
                clear.setSignedIn(false);
                NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancelAll();
                clearBackStack();
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment, new SignInFragment(), "" ).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                        commit();
                new DataBaseManager(getActivity().getApplicationContext()).clearDB();
                getActivity().getApplicationContext().deleteDatabase(DataBaseManager.DATA_BASE_NAME);
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), MessagingService.class));
                ((MainActivity)getActivity()).unBind();
                break;
        }
    }
    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
