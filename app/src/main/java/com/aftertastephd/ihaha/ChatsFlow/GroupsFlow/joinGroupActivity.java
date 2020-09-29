package com.aftertastephd.ihaha.ChatsFlow.GroupsFlow;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aftertastephd.ihaha.DataSources.Group;
import com.aftertastephd.ihaha.DataSources.User;
import com.aftertastephd.ihaha.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class joinGroupActivity extends AppCompatActivity {

    private EditText inviteCodeEditText;
    private Button joinGroupButton;
    private String inviteCodeEntered;
    private FirebaseUser currentUserFirebase;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        inviteCodeEditText = findViewById(R.id.enterinvitecode_joingroupactivity);
        joinGroupButton = findViewById(R.id.joingroupbutton_joingroupactivity);
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        inviteCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inviteCodeEntered = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inviteCodeEntered != null){
                    firestore.collection("groups").document(inviteCodeEntered).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Group g = documentSnapshot.toObject(Group.class);
                                g.addUser(currentUserFirebase.getUid());
                                firestore.collection("groups").document(inviteCodeEntered).set(g);
                                Toast.makeText(getApplicationContext(), "Successfully joined group!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Group doesn't exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    firestore.collection("users").document(currentUserFirebase.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User u = documentSnapshot.toObject(User.class);
                            u.addGroup(inviteCodeEntered);
                            firestore.collection("users").document(currentUserFirebase.getUid()).set(u);
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "You didn't enter a code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
