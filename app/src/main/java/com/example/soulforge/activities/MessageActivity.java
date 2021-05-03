package com.example.soulforge.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soulforge.R;
import com.example.soulforge.adapter.MessageAdapter;
import com.example.soulforge.interfaces.OnServerResCallBack;
import com.example.soulforge.model.Chats;
import com.example.soulforge.model.CommentModel;
import com.example.soulforge.model.NotificationData;
import com.example.soulforge.model.NotificationModel;
import com.example.soulforge.model.Users;
import com.example.soulforge.utils.SingletonClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class MessageActivity extends AppCompatActivity {
    private TextView username;
    private ImageView imageView;
    private ImageView sendBtn;
    private EditText msg_edtText;
    private RecyclerView recyclerViewM;

    private MessageAdapter messageAdapter;

    private Users receiver;
    private FirebaseUser fUser;
    private DatabaseReference reference;
    public SingletonClass singleton;
    private List<Chats> mChat;
    private String userID;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        singleton = SingletonClass.getInstance();
        init();
    }

    private void init() {
        userID = getIntent().getStringExtra("userid");

        imageView = findViewById(R.id.imageView_profile);
        username = findViewById(R.id.username);
        sendBtn = findViewById(R.id.sendBTn);
        msg_edtText = findViewById(R.id.text_send);
        recyclerViewM = findViewById(R.id.recycler_view);

        recyclerViewM.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewM.setLayoutManager(linearLayoutManager);

        //Firebase data get specific user chat
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
        usersRef.document(userID).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Error: ", error.getMessage());
                return;
            }
            if (value == null)
                return;
            receiver = value.toObject(Users.class);
            username.setText(receiver.getName());
            if (receiver.getProfileImage().equals("default")) {
                imageView.setImageResource(R.mipmap.ic_launcher);
            } else {
                Glide.with(MessageActivity.this).load(receiver.getProfileImage()).into(imageView);
            }

            readMessage(fUser.getUid(), userID, receiver.getProfileImage());
        });

//        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                receiver = snapshot.getValue(Users.class);
//                username.setText(receiver.getName());
//                if (receiver.getProfileImage().equals("default")) {
//                    imageView.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(MessageActivity.this).load(receiver.getProfileImage()).into(imageView);
//                }
//
//                readMessage(fUser.getUid(), userID, receiver.getProfileImage());
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        //Here we are sending message
        sendBtn.setOnClickListener(v -> {
            String msg = msg_edtText.getText().toString();
            if (!msg.equals("")) {
                sendMessage(fUser.getUid(), userID, msg);
            } else {
                Toast.makeText(MessageActivity.this, "Please send non empty message", Toast.LENGTH_SHORT).show();
            }
            msg_edtText.setText("");
        });
    }

    // Its send message to other user method
    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeStartUpCall(fUser.getDisplayName(),message);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //Adding user to chat fragment:Latest chat with contacts
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList").child(fUser.getUid()).child(userID);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Its read message of current selected user method
    private void readMessage(String myID, String userID, String imageURL) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chats chats = snapshot1.getValue(Chats.class);
                    if (chats.getReceiver().equals(myID) && chats.getSender().equals(userID) ||
                            chats.getSender().equals(myID) && chats.getReceiver().equals(userID)) {
                        mChat.add(chats);
                    }

                    //Its adapter showing message in recycle view
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageURL);
                    recyclerViewM.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeStartUpCall(String senderName, String text) {

        NotificationData notificationData = new NotificationData();
        notificationData.setTitle(senderName);
        notificationData.setMessage(text);
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTo(receiver.getDeviceToken());
        notificationModel.setData(notificationData);
        Log.e("none", "makeStartUpCall: "+notificationModel.getTo()+"//"+notificationModel.getData().getTitle() );
        Call<Object> call = singleton.getApiInterface().postCall("send",notificationModel);
        OnServerResCallBack callBack = (status, message, responseStr) -> {
            if (status == singleton.SUCCESS) {
                String tokenStr = singleton.getValueStrFromJSON(responseStr, "return_token");
                Log.e("none", "makeStartUpCall: "+tokenStr );
            } else {
                Log.e("none", "makeStartUpCall: error" );
            }
        };

        singleton.makePostCall(call, callBack);
    }
}