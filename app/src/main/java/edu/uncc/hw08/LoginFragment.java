/**
 * Group 9 HW 08
 * LoginFragment.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    final String TAG = "test";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Login Button
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                if(email.isEmpty()){
                    Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
                } else if(password.isEmpty()){
                    Toast.makeText(getContext(), "Password is required", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
                        if(task.isSuccessful()){
                            updateLoggedIn();
                            mListener.gotoMyChat();
                        } else {
                            Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Create Account Button
        binding.buttonCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoSignUp();
            }
        });

        getActivity().setTitle("Login");
    }

    /**
     * When the user logs in, find their user file and change it to logged in.
     */
    private void updateLoggedIn() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // Create a map to change logged_in to true
                        HashMap<String, Object> user = new HashMap<>();
                        user.put("logged_in", true);

                        // Update the user's field of logged_in to true
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots) {
                            if(document.getString("user_id").equals(mAuth.getCurrentUser().getUid())) {
                                db.collection("users").document(document.getId())
                                        .update(user);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    LoginListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginListener) context;
    }

    interface LoginListener {
        void gotoMyChat();
        void gotoSignUp();
    }
}