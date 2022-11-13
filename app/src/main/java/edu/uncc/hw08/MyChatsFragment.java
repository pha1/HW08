/**
 * Group 9 HW 08
 * MyChatsFragment.java
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyChatsFragment extends Fragment {

    FragmentMyChatsBinding binding;
    final String TAG = "test";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyChatsFragment newInstance(String param1, String param2) {
        MyChatsFragment fragment = new MyChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.chats_label);

        // TODO Create a List (Provided ListView)
        // TODO Adapter
        // TODO Click list item go to Chat Fragment

        // New Chat
        binding.buttonNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.newChat();
            }
        });

        // Logout
        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLoggedIn(mAuth.getCurrentUser().getUid());
                mListener.logout();
            }
        });
    }


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * This changes the logged_in value of the user to false
     * @param uid
     */
    private void changeLoggedIn(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        HashMap<String, Object> user = new HashMap<>();
                        user.put("logged_in", false);

                        for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                            if(document.getString("user_id").equals(uid)) {
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MyChatsFragmentListener) {
            mListener = (MyChatsFragmentListener) context;
        }
    }

    MyChatsFragmentListener mListener;

    public interface MyChatsFragmentListener {
        void logout();
        void newChat();
    }
}