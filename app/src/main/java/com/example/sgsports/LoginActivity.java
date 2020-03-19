package com.example.sgsports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private GoogleSignInClient mGoogleSignInClient;

    private final int RC_SIGN_IN_GOOGLE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        //Sign in button
        findViewById(R.id.signinB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                TextView emailText = findViewById(R.id.userEmail);
                TextView password = findViewById(R.id.userPW);

                String email = emailText.getText().toString();
                String pw = password.getText().toString();

                mAuth.signInWithEmailAndPassword(email, pw)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){ //sign in successful
                                    Toast.makeText(LoginActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{    //sign in failed: show error message
                                    Toast.makeText(LoginActivity.this, "Please check your email and password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        //Sign up button
        findViewById(R.id.signupB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        //Sign in with Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Sign in with Google button
        findViewById(R.id.googleB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);

            }
        });

        //Sign in with Facebook
        findViewById(R.id.facebookB).setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

            }
        });


    }

    /** check if user is already signed in => if user signed in, start MainActivity **/
    @Override
    public void onStart(){
        super.onStart();

        //get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){  //if user is already signed in
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN_GOOGLE){ //sign in with google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch(ApiException e){

            }
        }
    }

    /** authentication process with Google account **/
    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //sign in successful
                            final FirebaseUser user = mAuth.getCurrentUser();

                            //get database instance
                            database  = FirebaseFirestore.getInstance();
                            //if new user, save user data to database
                            database.collection("users")
                                    .whereEqualTo("name", user.getDisplayName())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful() && task.getResult().isEmpty()){
                                                UserData newUser = new UserData(user.getDisplayName(), user.getEmail(),null, null, null);

                                                database.collection("users").document(user.getUid()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(LoginActivity.this, "welcome!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });


                            Toast.makeText(LoginActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
