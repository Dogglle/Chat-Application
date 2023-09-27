package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView userRecyclerView;
    private ArrayList<User> userList;
    private UserAdapter adapter; // Assuming UserAdapter is a custom adapter you have defined.
    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDbRef = FirebaseDatabase.getInstance().getReference();
        userRecyclerView = findViewById(R.id.userRecyclerView); // Replace with your RecyclerView ID.
        userList = new ArrayList<>();
        adapter = new UserAdapter(this, userList); // Assuming UserAdapter is a custom adapter.

        // Set the adapter to the RecyclerView
        userRecyclerView.setAdapter(adapter);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(adapter);

        mDbRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User currentUser = postSnapshot.getValue(User.class);
                    if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().equals(currentUser.getUid())) {
                        userList.add(currentUser);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); // Use getMenuInflater() instead of menuInflater
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) { // Use itemId instead of itemid, and compare with the correct resource ID
            mAuth.signOut();
            Intent intent = new Intent(this, Login.class);
            finish(); // Close the MainActivity
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
