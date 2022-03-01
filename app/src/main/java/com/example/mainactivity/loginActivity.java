package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mainactivity.databinding.ActivityLoginBinding;
import com.example.mainactivity.models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {
        ActivityLoginBinding binding;
        ProgressDialog pd;
        FirebaseAuth auth;
        GoogleSignInClient mGoogleSignInClient;
        FirebaseDatabase database;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide();
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            pd = new ProgressDialog(loginActivity.this);
            pd.setTitle("VibeX");
            pd.setMessage("Logging In");

            binding.wall.playAnimation();

            if (auth.getCurrentUser() != null) {
                if(!auth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(this, "Please Verify Email First", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            // Configure Google Sign In
         /*   GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("")
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

            binding.login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.email.getText().toString().isEmpty()) {
                        binding.email.setError("Enter Valid Email");
                        return;
                    } else if (binding.password.getText().toString().isEmpty()) {
                        binding.password.setError("Password cannot be empty");
                        return;
                    }
                        auth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (auth.getCurrentUser().isEmailVerified()) {
                                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                            else {
                                                Toast.makeText(loginActivity.this, "Please Verify Email First", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            Toast.makeText(loginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
            });
            binding.register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(loginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });
           /* binding.google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    signIn();
                    pd.show();
                }
            });
        }*/

        /*int RC_SIGN_IN = 65;

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
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithCredential:success");
                                FirebaseUser user = auth.getCurrentUser();
                                Users users = new Users();
                                users.setUserid(user.getUid());
                                users.setUsername(user.getDisplayName());
                                users.setProfilepic(user.getPhotoUrl().toString());
                                database.getReference().child("Users").child(user.getUid()).setValue(users);

                                Toast.makeText(loginActivity.this, "Signing in with Google", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
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
    }