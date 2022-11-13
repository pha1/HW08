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
import android.widget.AdapterView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyChatsFragment extends Fragment {

    FragmentMyChatsBinding binding;
    final String TAG = "test";

    public MyChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        getChats();

        adapter = new ChatAdapter(getActivity(), R.layout.my_chats_list_item, mChats);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.chat(mChats.get(i));
            }
        });

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
    String currentUid = mAuth.getCurrentUser().getUid();

    private void getChats() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    mChats.clear();
                    for(QueryDocumentSnapshot document : value) {
                        if(document.getString("user1").equals(currentUid)
                         || document.getString("user2").equals(currentUid)) {
                            Chat chat = document.toObject(Chat.class);
                            mChats.add(chat);
                        }
                    }
                    Collections.sort(mChats, new Comparator<Chat>() {
                        @Override
                        public int compare(Chat chat, Chat t1) {
                            return -1 * chat.lastMessageCreatedAt.compareTo(t1.lastMessageCreatedAt);
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            });
    }

    ArrayList<Chat> mChats = new ArrayList<>();
    ChatAdapter adapter;

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
        void chat(Chat chat);
    }
}