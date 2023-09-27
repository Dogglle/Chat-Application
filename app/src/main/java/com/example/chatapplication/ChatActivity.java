package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Message;
import com.example.chatapplication.MessageAdapter;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageBox;
    private ImageView sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList;
    private DatabaseReference mDbRef;
    private String receiverRoom;
    private String senderRoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String receiverUid = intent.getStringExtra("uid");
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDbRef = FirebaseDatabase.getInstance().getReference();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        getSupportActionBar().setTitle(name); // Set the title for the action bar

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sentButton);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageAdapter);

        mDbRef.child("chats").child(senderRoom).child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        messageList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Message message = postSnapshot.getValue(Message.class);
                            if (message != null) {
                                messageList.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle database error if needed
                    }
                });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageBox.getText().toString();
                Message messageObject = new Message(message, senderUid);
                mDbRef.child("chats").child(senderRoom).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener(aVoid -> {
                            mDbRef.child("chats").child(receiverRoom).child("messages").push()
                                    .setValue(messageObject);
                        });
                messageBox.setText("");
            }
        });
    }
}
