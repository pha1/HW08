/**
 * Group 9 HW 08
 * CreateChatFragment.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateChatFragment extends Fragment {

    FragmentCreateChatBinding binding;
    final String TAG = "test";

    public CreateChatFragment() {
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
        binding = FragmentCreateChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    String selectedUid;
    String selectedUserName;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getUserList();
        adapter = new UserAdapter(getActivity(), R.layout.users_row_item, mUsers);
        binding.listView.setAdapter(adapter);

        // Clicking on a list item will update the Selected User TextView
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = mUsers.get(i);
                binding.textViewSelectedUser.setText(user.name);
                selectedUid = user.user_id;
                selectedUserName = user.name;
            }
        });

        getActivity().setTitle(R.string.create_chat);

        // Cancel Button
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToMyChats();
            }
        });

        // Submit Button
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.editTextMessage.getText().toString();
                String selected_user = binding.textViewSelectedUser.getText().toString();
                //Log.d(TAG, "onClick: " + selected_user);

                if (message.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a chat!", Toast.LENGTH_SHORT).show();
                } else if (selected_user.equals("No User Selected !!")) {
                    Toast.makeText(getContext(), "Please select a user!", Toast.LENGTH_SHORT).show();
                } else {
                    startChat(selectedUid, message, selectedUserName);
                }
            }
        });
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUid = mAuth.getCurrentUser().getUid();

    private void startChat(String selectedUid, String message, String selectedUserName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String chat_id = db.collection("chats").document().getId();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());

        HashMap<String, Object> chat = new HashMap<>();
        chat.put("chat_id", chat_id);
        chat.put("user1", mAuth.getCurrentUser().getUid());
        chat.put("user1name", mAuth.getCurrentUser().getDisplayName());
        chat.put("user2", selectedUid);
        chat.put("user2name", selectedUserName);
        chat.put("lastMessageSent", message);
        chat.put("lastMessageCreatedAt", date);

        db.collection("chats").document(chat_id)
                .set(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        createMessage(message, chat_id, date);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createMessage(String message, String chat_id, String date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String message_id = db.collection("chats").document(chat_id).collection("messages")
                .document().getId();

        HashMap<String, Object> newMessage = new HashMap<>();
        newMessage.put("message_text", message);
        newMessage.put("created_At", date);
        newMessage.put("user_id", mAuth.getCurrentUser().getUid());
        newMessage.put("message_id", message_id);
        newMessage.put("user_name", mAuth.getCurrentUser().getDisplayName());

        db.collection("chats").document(chat_id).collection("messages")
                .document(message_id).set(newMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Go back to My Chats
                        mListener.goToMyChats();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    ArrayList<User> mUsers = new ArrayList<>();
    UserAdapter adapter;

    private void getUserList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    mUsers.clear();
                    for(QueryDocumentSnapshot document: value) {
                        if (document.getString("user_id").equals(currentUid)) {
                            continue;
                        }
                        User user = document.toObject(User.class);
                        mUsers.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof CreateChatFragmentListener) {
            mListener = (CreateChatFragmentListener) context;
        }
    }

    CreateChatFragmentListener mListener;

    public interface CreateChatFragmentListener {
        void goToMyChats();
    }
}