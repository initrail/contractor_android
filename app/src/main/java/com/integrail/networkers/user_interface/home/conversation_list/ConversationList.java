package com.integrail.networkers.user_interface.home.conversation_list;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.integrail.networkers.MainActivity;
import com.integrail.networkers.R;
import com.integrail.networkers.primary_operations.messaging.MessagingService;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.data_representations.message_representations.ConversationItem;
import com.integrail.networkers.user_interface.home.conversation_list.messagethread.MessageList;

/**
 * Created by Integrail on 7/25/2016.
 */

public class ConversationList extends Fragment implements View.OnClickListener{
    private View view;
    private TextView textView;
    private TextView create;
    private List<ConversationItem> list;
    private RecyclerView mRecyclerView;
    private ConversationListAdapter adapter;
    private MessagingService messagingService;
    public void setMessagingService(MessagingService m){
        messagingService = m;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.conversation_list, container, false );
        ConversationDataBase db = new ConversationDataBase(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = db.buildConversationPage();
        adapter = new ConversationListAdapter(list, this);
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        mRecyclerView.setAdapter(adapter);
        create = (TextView) view.findViewById(R.id.create_conversation);
        create.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view){
        MessageList newFragment = new MessageList();
        newFragment.setCreateConv(true);
        switch(view.getId()){
            case R.id.create_conversation:
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment, newFragment, "").
                        addToBackStack("").
                        setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                        commit();
        }
    }
    public void showMessageThread(int index){
        Bundle args = new Bundle();
        args.putLong(MainActivity.CONVERSATION_ID,list.get(index).getConvId());
        args.putLong(MainActivity.MESSAGE_TO, list.get(index).getOther());
        args.putString("otherName", list.get(index).getName());
        MessageList newFragment = new MessageList();
        newFragment.setArguments(args);
        getFragmentManager().beginTransaction().
                replace(R.id.fragment, newFragment, "").
                addToBackStack("").
                setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                commit();
    }
    public void updateConversationPage(){
        list = new ConversationDataBase(getActivity()).buildConversationPage();
        adapter = new ConversationListAdapter(list, this);
        mRecyclerView.setAdapter(adapter);
    }
}