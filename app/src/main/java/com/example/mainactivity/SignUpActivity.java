package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mainactivity.databinding.ActivitySignUpBinding;
import com.example.mainactivity.models.Users;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth Auth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.wall2.playAnimation();

        pd = new ProgressDialog(SignUpActivity.this);
        pd.setTitle("Signing-Up");
        pd.setMessage("Please Wait");
      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.username.getText().toString().isEmpty()) {

                    binding.username.setError("Invalid Username");
                    return;
                } else if (binding.email.getText().toString().isEmpty()) {
                    binding.email.setError("Enter Valid Email");
                    return;
                } else if (binding.password.getText().toString().isEmpty()) {
                    binding.password.setError("Password cannot be empty");
                    return;
                } else if (binding.password.length() <= 6) {
                    binding.password.setError("Password Is Too Short");
                    return;
                }
                /*Query usernameQuery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(binding.username.getText().toString());
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            //binding.username.setError("Username Already Taken");
                        } else {*/
                            pd.show();
                            Auth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = Auth.getCurrentUser();
                                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpActivity.this, "Verification Link Sent to " + binding.email.getText().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this, "Errror, Try Again", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                       Users users = new Users(binding.username.getText().toString(), binding.email.getText().toString(),
                                                binding.password.getText().toString());
                                        FirebaseUser user = Auth.getCurrentUser();

                                        users.setUserid(user.getUid());
                                        database.getReference().child("Users").child(user.getUid()).setValue(users);
                                        pd.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Sign-up Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });

                        //}
                    }

                  /*  @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }*/
        });
        binding.acnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

       /* binding.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
                pd.show();
            }
        });*/
    }
   /* int RC_SIGN_IN = 65;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = Auth.getCurrentUser();
                            Users users=new Users();
                            users.setUserid(user.getUid());
                            users.setUsername(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);
                            Toast.makeText(SignUpActivity.this, "Signing in with Google", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            // updateUI(null);
                        }
                    }
                });*/

}