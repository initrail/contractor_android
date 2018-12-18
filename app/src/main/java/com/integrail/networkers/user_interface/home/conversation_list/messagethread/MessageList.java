package com.integrail.networkers.user_interface.home.conversation_list.messagethread;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integrail.networkers.AfterTaskImplementation;
import com.integrail.networkers.AndroidDatabaseManager;
import com.integrail.networkers.constants.NetworkConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.integrail.networkers.MainActivity;
import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.message_representations.LatestConversationData;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.primary_operations.messaging.DownloadImage;
import com.integrail.networkers.primary_operations.messaging.ToggleAlarm;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.user_interface.AfterTask;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Integrail on 7/27/2016.
 */

public class MessageList extends Fragment implements View.OnClickListener, View.OnLayoutChangeListener, AfterTask {
    private View view;
    private Message sms;
    private List<Message> list;
    private List<Message> notSent;
    private long conversationId;
    private MessageDataBase db;
    private RecyclerView mRecyclerView;
    private MessageListAdapter adapter;
    private EditText parseMsg;
    private long messageFrom;
    private long messageTo;
    private Preferences preferences;
    private ConversationDataBase cDB;
    private String otherName;
    private MessageDataBase mDB;
    private RecyclerView images;
    private ImageListAdapter imageListAdapter;
    private String customImagePath;
    private LocalFileManager fileManager;
    private String fileName;
    private long latest;
    private boolean createConv;
    private EditText create;
    private TextView tv;
    private ImageButton camera;
    private LinearLayoutManager layoutManager;
    private Listener listener;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_FILE_SYSTEM = 2;
    public void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_FILE_SYSTEM);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(getActivity().getApplicationContext());
        notSent = new ArrayList<>();
        fileManager = new LocalFileManager(getActivity());
        db = new MessageDataBase(getActivity().getApplicationContext());
        mDB = new MessageDataBase(getActivity());
        messageFrom = preferences.userId();
        if(!createConv) {
            Bundle b = getArguments();
            conversationId = b.getLong(MainActivity.CONVERSATION_ID);
            messageTo = b.getLong(MainActivity.MESSAGE_TO);
            otherName = b.getString("otherName");
            list = db.getMessageThread(conversationId);
        } else {
            list = new ArrayList<>();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_thread, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_message);
        String userImage = messageFrom + ".jpg";
        String otherImage = messageTo + ".jpg";
        adapter = new MessageListAdapter(list, this, userImage, otherImage, getActivity(), conversationId);
        mRecyclerView.setAdapter(adapter);
        ImageButton mms = (ImageButton) view.findViewById(R.id.sendAttachment);
        mms.setOnClickListener(this);
        Button button = (Button) view.findViewById(R.id.sendMessage);
        button.setOnClickListener(this);
        cDB = new ConversationDataBase(getActivity());
        parseMsg = (EditText) view.findViewById(R.id.message);
        tv = (TextView) view.findViewById(R.id.messageThread);
        tv.setText(otherName);
        tv.setOnClickListener(this);
        camera = (ImageButton) view.findViewById(R.id.capturePhoto);
        camera.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        listener = new Listener(this, layoutManager, conversationId, list, getActivity().getApplicationContext(), getActivity());
        mRecyclerView.addOnScrollListener(listener);
        if (Build.VERSION.SDK_INT >= 11) {
            mRecyclerView.addOnLayoutChangeListener(this);
        }
        if(!createConv) {
            setMessagesRead();
            mRecyclerView.scrollToPosition(list.size() - 1);
        } else {
            create = (EditText) view.findViewById(R.id.addRecipient);
            create.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            create.setLayoutParams(create.getLayoutParams());
            tv.getLayoutParams().height = 0;
            tv.setLayoutParams(tv.getLayoutParams());
        }
        stopAlarm();
        return view;
    }
    public void stopAlarm(){
        Thread stop = new Thread(){
            @Override
            public void run(){
                try{
                    Thread.sleep(2500);
                    if(getActivity() != null)
                        getActivity().sendBroadcast(new Intent(ToggleAlarm.STOP_ALARM));
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        stop.start();
    }
    @Override
    public void onLayoutChange(View v,
                               int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom < oldBottom) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(
                                    mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            });
        }
    }

    public void setMessagesRead() {
        String[] smsIds = db.setReadMessages(conversationId);
        if (smsIds.length > 0) {
            NetworkConnection connection = new NetworkConnection(NetworkConstants.SET_MESSAGE_READ, new Gson().toJson(smsIds, String[].class), false, getActivity(), this, 2, 0);
            connection.setRunningOnUIThread(false);
            connection.setExpectingAfterTaskUpdate(true);
            connection.execute();
        }
    }
    public void setCreateConv(boolean createConv){
        this.createConv = createConv;
    }
    @Override
    public void onClick(View view) {
            if(view.getId() == R.id.sendMessage) {
                String msg = parseMsg.getText().toString();
                Matcher m = Pattern.compile("^\\s+$").matcher(msg);
                if (m.find()) {
                    msg = "";
                }
                if (!msg.equals("")) {
                    if(!createConv) {
                        parseMsg.setText("");
                        sms = new Message("" + conversationId, 0, messageFrom, msg, 0, System.currentTimeMillis(), 0);
                        NetworkConnection connection = new NetworkConnection(NetworkConstants.RECEIVE_MESSAGE, new Gson().toJson(sms, Message.class), false, getActivity(), this, 1, 0);
                        connection.setRunningOnUIThread(false);
                        connection.setExpectingAfterTaskUpdate(true);
                        connection.execute();
                    } else {
                        String convName = create.getText().toString();
                        String[] users = convName.split(" ");
                        NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_CONVERSATION, new Gson().toJson(users, String[].class), true, getActivity(), this, 4, 0);
                        connection.setRunningOnUIThread(false);
                        connection.setExpectingAfterTaskUpdate(true);
                        create.getLayoutParams().height = 0;
                        create.setLayoutParams(create.getLayoutParams());
                        tv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(convName);
                        createConv = false;
                        connection.execute();
                    }
                }
            }
            else if(view.getId() == R.id.sendAttachment) {
                if (((MainActivity) getActivity()).hasStoragePermissions()) {
                    openImage();
                } else {
                    ((MainActivity) getActivity()).requestStoragePermissions();
                }
            }
            else if(view.getId() == R.id.messageThread) {
                Intent dbmanager = new Intent(getActivity(), AndroidDatabaseManager.class);
                startActivity(dbmanager);
            } else if(view.getId() == R.id.capturePhoto){
                if (((MainActivity) getActivity()).hasCameraPermissions()) {
                    capturePhoto();
                } else {
                    ((MainActivity) getActivity()).requestCameraPermissions();
                }
            }
    }
    public void capturePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    public void update(NetworkConnection connection, int index) {
        switch (index) {
            case 1:
                if (connection.getError() != null) {
                    long latest = cDB.getLatestId(conversationId) + 1;
                    cDB.updateCount(latest, conversationId);
                    sms.setMessageId(conversationId + "-" + latest);
                    sms.setId(latest);
                    sms.setMessageFrom(messageFrom);
                    sms.setMessageRead(1);
                    sms.setTimeSent(ConversationDataBase.INFINITE_TIME);
                    mDB.insertMessage(sms, false);
                }
                updateMessageThread(sms);
                break;
            case 2:
                if (connection.getError() != null) {
                    db.updateMsgRead(conversationId, 2);
                } else {
                    db.updateMsgRead(conversationId, 1);
                }
                break;
            case 3:
                try {
                    FileInputStream fileInputStream = new FileInputStream(new File(customImagePath));
                    sms = new Message("", 0, messageFrom, "", 0, 0, 0);
                    if (connection.getError() == null) {
                        fileManager.writeFile(LocalFileManager.MMS_DIRECTORY + "/" + conversationId + "/" + connection.getReturnData(), fileInputStream, fileInputStream.available());
                        sms.setMessage(connection.getReturnData());
                        sms.setTimeSent(System.currentTimeMillis());
                        sms.setMessageId(""+conversationId+"-0");
                        sms.setMms(true);
                        updateMessageThread(sms);
                    } else {
                        long latest = cDB.getLatestId(conversationId) + 1;
                        cDB.updateCount(latest, conversationId);
                        sms.setMessageId("" + conversationId + "-" + latest);
                        sms.setId(latest);
                        sms.setMessage("" + conversationId + "-" + latest + "-" + fileName);
                        sms.setMessageRead(1);
                        sms.setTimeSent(ConversationDataBase.INFINITE_TIME);
                        fileManager.writeFile(LocalFileManager.MMS_DIRECTORY + "/" + conversationId + "/" + sms.getMessage(), fileInputStream, fileInputStream.available());
                        sms.setMms(true);
                        mDB.insertMessage(sms, false);
                        updateMessageThread(sms);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                conversationId = Long.valueOf(connection.getReturnData());
                String msg = parseMsg.getText().toString();
                parseMsg.setText("");
                sms = new Message("" + conversationId, 0, messageFrom, msg, 0, System.currentTimeMillis(), 0);
                connection = new NetworkConnection(NetworkConstants.RECEIVE_MESSAGE, new Gson().toJson(sms, Message.class), false, getActivity(), this, 1, 0);
                connection.setRunningOnUIThread(false);
                connection.setExpectingAfterTaskUpdate(true);
                connection.execute();
                break;
            case 5:
                try {
                    fileName = customImagePath.substring(customImagePath.lastIndexOf('/') + 1);
                    FileInputStream fileInputStream = new FileInputStream(new File(customImagePath));
                    conversationId = Long.valueOf(connection.getReturnData());
                    adapter.setConvId(conversationId);
                    connection = new NetworkConnection(NetworkConstants.SEND_MMS, null, true, getActivity(), this, 3, 0);
                    connection.openFile(fileInputStream);
                    connection.setRunningOnUIThread(false);
                    connection.setExpectingAfterTaskUpdate(true);
                    connection.setExistingFileName(conversationId + "-" + fileName);
                    try {
                        connection.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch(FileNotFoundException e){
                    e.printStackTrace();
                }
            case 6:
                if(connection.getError()==null){
                    Message[] loading = new GsonBuilder().create().fromJson(connection.getReturnData(), Message[].class);
                    if(loading == null)
                        Toast.makeText(getActivity().getApplicationContext(), "No more messages", Toast.LENGTH_SHORT).show();
                    else
                        insertMessages(loading);
                }
        }
    }
    public void insertMessages(Message[] loading){
        mRecyclerView.removeOnScrollListener(listener);
        for(int i = loading.length - 1; i >= 0; i--){
            if(loading[i].isMms())
                downloadImage(loading[i].getMessageId(), loading[i].getMessage());
            list.add(0, loading[i]);
            adapter.notifyItemInserted(0);
        }
        mRecyclerView.addOnScrollListener(listener);
    }
    public void downloadImage(String conversationId, String message){
        Matcher m = Pattern.compile("^\\d+-").matcher(conversationId);
        if(m.find()){
            conversationId = m.group().replaceAll("-", "");
        }
        Intent intent = new Intent(getActivity().getApplicationContext(), DownloadImage.class);
        intent.putExtra(ConversationDataBase.COLUMN_1, Long.valueOf(conversationId));
        intent.putExtra(MessageDataBase.COLUMN_5, message);
        getActivity().startService(intent);
    }
    public void updateMessageThread(Message m) {
        if(m.isMms())
            m.setMms(true);
        list.add(list.size(), m);
        mRecyclerView.scrollToPosition(list.size() - 1);
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        customImagePath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_FILE_SYSTEM) {
                latest = cDB.getLatestId(conversationId) + 1;
                Uri selectedImageUri = data.getData();
                customImagePath = fileManager.getFilePath(selectedImageUri);
                if (customImagePath != null) {
                    try {
                        if(!createConv) {
                            fileName = customImagePath.substring(customImagePath.lastIndexOf('/') + 1);
                            FileInputStream fileInputStream = new FileInputStream(new File(customImagePath));
                            NetworkConnection connection = new NetworkConnection(NetworkConstants.SEND_MMS, null, true, getActivity(), this, 3, 0);
                            connection.openFile(fileInputStream);
                            connection.setRunningOnUIThread(false);
                            connection.setExpectingAfterTaskUpdate(true);
                            connection.setExistingFileName(conversationId + "-" + fileName);
                            try {
                                connection.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            String convName = create.getText().toString();
                            String[] users = convName.split(" ");
                            NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_CONVERSATION, new Gson().toJson(users, String[].class), true, getActivity(), this, 5, 0);
                            connection.setRunningOnUIThread(false);
                            connection.setExpectingAfterTaskUpdate(true);
                            create.getLayoutParams().height = 0;
                            create.setLayoutParams(create.getLayoutParams());
                            tv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            tv.setLayoutParams(tv.getLayoutParams());
                            tv.setText(convName);
                            createConv = false;
                            connection.execute();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if(requestCode == REQUEST_IMAGE_CAPTURE){
                latest = cDB.getLatestId(conversationId) + 1;
                Uri selectedImageUri = data.getData();
                customImagePath = fileManager.getFilePath(selectedImageUri);
                if (customImagePath != null) {
                    try {
                        if(!createConv) {
                            File f = new File(customImagePath);
                            galleryAddPic(f);
                            fileName = customImagePath.substring(customImagePath.lastIndexOf('/') + 1);
                            FileInputStream fileInputStream = new FileInputStream(new File(customImagePath));
                            NetworkConnection connection = new NetworkConnection(NetworkConstants.SEND_MMS, null, true, getActivity(), this, 3, 0);
                            connection.openFile(fileInputStream);
                            connection.setRunningOnUIThread(false);
                            connection.setExpectingAfterTaskUpdate(true);
                            connection.setExistingFileName(conversationId + "-" + fileName);
                            try {
                                connection.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            String convName = create.getText().toString();
                            String[] users = convName.split(" ");
                            NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_CONVERSATION, new Gson().toJson(users, String[].class), true, getActivity(), this, 5, 0);
                            connection.setRunningOnUIThread(false);
                            connection.setExpectingAfterTaskUpdate(true);
                            create.getLayoutParams().height = 0;
                            create.setLayoutParams(create.getLayoutParams());
                            tv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            tv.setLayoutParams(tv.getLayoutParams());
                            tv.setText(convName);
                            createConv = false;
                            connection.execute();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public long getConvId() {
        return conversationId;
    }
}
class Listener extends RecyclerView.OnScrollListener {
    private AfterTask task;
    private LinearLayoutManager layoutManager;
    private long conversationId;
    private List<Message> list;
    private Context context;
    private Activity activity;
    public Listener(AfterTask task, LinearLayoutManager layoutManager, long conversationId, List<Message> list, Context context, Activity activity){
        this.task = task;
        this.layoutManager = layoutManager;
        this.conversationId = conversationId;
        this.list = list;
        this.context = context;
        this.activity = activity;
    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(layoutManager.findFirstCompletelyVisibleItemPosition()==0){
            if(list.get(0).getId() == 13)
                System.out.println("Stalling...");
            if(list.get(0).getId() - 1 > 0) {
                Toast.makeText(context, "Loading messages...", Toast.LENGTH_SHORT).show();
                LatestConversationData[] conv = new LatestConversationData[]{new LatestConversationData(conversationId, list.get(0).getId() - 1)};
                NetworkConnection connection = new NetworkConnection(NetworkConstants.LOAD_MESSAGES, new Gson().toJson(conv, LatestConversationData[].class), true, activity, task, 6, 0);
                connection.setRunningOnUIThread(false);
                connection.setExpectingAfterTaskUpdate(true);
                connection.execute();
            }
        }
    }
}