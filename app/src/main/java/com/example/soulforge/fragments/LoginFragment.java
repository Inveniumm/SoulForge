package com.example.soulforge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.soulforge.activities.MainActivity;
import com.example.soulforge.R;
import com.example.soulforge.activities.ReplacerActivity;
import com.example.soulforge.model.CommentModel;
import com.example.soulforge.model.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.soulforge.fragments.CreateAccountFragment.EMAIL_REGEX;

public class LoginFragment extends Fragment {
    private EditText emailEt, passwordET;
    private TextView signUpTv, forgotPasswordTv;
    private Button loginBtn, googleSignInBtn;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        clickListener();
    }

    public void clickListener() {
        forgotPasswordTv.setOnClickListener(v -> ((ReplacerActivity) getActivity()).setFragment(new ForgotPassword()));
        loginBtn.setOnClickListener((v) -> {
            String email = emailEt.getText().toString();
            String password = passwordET.getText().toString();

            if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                emailEt.setError("Input Valid email address");
                return;
            }
            if (password.isEmpty() || password.length() < 6) {
                passwordET.setError("Input Valid Password");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
                            usersRef.document(user.getUid()).addSnapshotListener((value, error) -> {
                                if (error != null) {
                                    Log.e("Error: ", error.getMessage());
                                    return;
                                }
                                if (value == null)
                                    return;
                                Users currentUser = value.toObject(Users.class);
                                if (currentUser.getUid() != null) {
                                    currentUser.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
                                    usersRef.document(currentUser.getUid()).set(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            Log.e(TAG, "onComplete: success" +currentUser.getDeviceToken());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: "+e );
                                        }
                                    });
                                } else {
                                    Log.e("none", "onEvent: error in getting user");
                                }
                            });
                            if (!user.isEmailVerified()) {
                                Toast.makeText(getContext(), "Please Verify your email address", Toast.LENGTH_SHORT).show();
                            }

                            sendUserToMainActivity();
                        } else {
                            String exception = "Error: " + task.getException().getMessage();
                            Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        });


        googleSignInBtn.setOnClickListener(v -> {
            signIn();
        });

        signUpTv.setOnClickListener((v) -> {
            ((ReplacerActivity) getActivity()).setFragment(new CreateAccountFragment());
        });
    }


    private void init(View view) {
        emailEt = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.passwordET);
        loginBtn = view.findViewById(R.id.loginBtn);
        googleSignInBtn = view.findViewById(R.id.googleSignInBtn);
        signUpTv = view.findViewById(R.id.signUpTv);
        forgotPasswordTv = view.findViewById(R.id.forgotTv);
        progressBar = view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }


    private void sendUserToMainActivity() {
        if (getActivity() == null)
            return;

        progressBar.setVisibility(View.GONE);
        startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));
        getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        getActivity().finish();
    }

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
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        updateUi(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void updateUi(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        Map<String, Object> map = new HashMap<>();
        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profileImage", String.valueOf(account.getPhotoUrl()));
        map.put("uid", user.getUid());
        map.put("following", 0);
        map.put("followers", 0);
//        map.put("status", " ");

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assert getActivity() != null;
                        progressBar.setVisibility(View.GONE);
                        sendUserToMainActivity();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}