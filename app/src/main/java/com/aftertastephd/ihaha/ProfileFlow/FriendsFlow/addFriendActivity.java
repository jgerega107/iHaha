package com.aftertastephd.ihaha.ProfileFlow.FriendsFlow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class addFriendActivity extends AppCompatActivity {
    private EditText searchbar;
    private RecyclerView queryRecycler;
    private FirebaseFirestore firestore;
    private CollectionReference users;
    private userQueryAdapter adapter;
    private Query q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        firestore = FirebaseFirestore.getInstance();
        users = firestore.collection("users");
        searchbar = findViewById(R.id.username_search_bar_edittext);
        queryRecycler = findViewById(R.id.user_query_recycler);
        queryRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    q = firestore.collection("Users");
                    showAdapter(q);
                }
                else {
                    q = users.orderBy("username").startAt(s.toString().trim()).endAt(s.toString().trim() + "\uf8ff");
                    showAdapter(q);
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
    }

    public void showAdapter(Query q){
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> names = new ArrayList<>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        User model = document.toObject(User.class);
                        names.add(model);
                    }
                    queryRecycler = findViewById(R.id.user_query_recycler);
                    adapter = new userQueryAdapter(names, addFriendActivity.this);
                    queryRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}