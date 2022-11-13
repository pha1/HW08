/**
 * Group 9 HW 08
 * ChatFragment.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentChatBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String TAG = "test";

    private static final String ARG_PARAM_CHAT = "chat_id";

    private Chat chat;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param chat The Chat Object selected
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance(Chat chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CHAT, chat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chat = (Chat) getArguments().getSerializable(ARG_PARAM_CHAT);
        }
    }

    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAuth.getCurrentUser().getUid().equals(chat.user1)) {
            getActivity().setTitle(getResources().getString(R.string.chat_label) + " " + chat.user2name);
        } else {
            getActivity().setTitle(getResources().getString(R.string.chat_label) + " " + chat.user1name);
        }

        getMessages();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter();
        binding.recyclerView.setAdapter(adapter);

        // Delete Chat Button
        binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteChat();
            }
        });

        // Submit Button
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message_text = binding.editTextMessage.getText().toString();

                if (message_text.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a message!", Toast.LENGTH_SHORT).show();
                } else {
                    submitMessage(message_text);
                    binding.editTextMessage.setText("");
                }
            }
        });

        // Close Button
        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToMyChats();
            }
        });
    }

    private void getMessages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference messageColl = db.collection("chats").document(chat.chat_id).collection("messages");

        messageColl.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mMessages.clear();
                for(QueryDocumentSnapshot document : value) {
                    Message message = document.toObject(Message.class);
                    mMessages.add(message);
                }
                Collections.sort(mMessages, new Comparator<Message>() {
                    @Override
                    public int compare(Message message, Message t1) {
                        return message.created_At.compareTo(t1.created_At);
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This deletes the chat from the collection
     */
    private void deleteChat() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chats").document(chat.chat_id)
                .delete()
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

    private void submitMessage(String message_text) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference messageColl = db.collection("chats").document(chat.chat_id).collection("messages");

        String message_id = messageColl.document().getId();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());

        HashMap<String, Object> message = new HashMap<>();
        message.put("created_At", date);
        message.put("message_id", message_id);
        message.put("message_text", message_text);
        message.put("user_id", mAuth.getCurrentUser().getUid());
        message.put("user_name", mAuth.getCurrentUser().getDisplayName());

        messageColl.document(message_id)
                .set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Message created");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("chats").document(chat.chat_id)
                .update("lastMessageCreatedAt", date, "lastMessageSent", message_text)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Chat Updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    ArrayList<Message> mMessages = new ArrayList<>();
    MessageAdapter adapter = new MessageAdapter();

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new MessageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Message message = mMessages.get(position);
            holder.setupUi(message);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            ChatListItemBinding mBinding;
            Message mMessage;

            public MessageViewHolder(ChatListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUi(Message message) {
                mMessage = message;
                if (message.user_id.equals(mAuth.getCurrentUser().getUid())) {
                    mBinding.textViewMsgBy.setText(getResources().getString(R.string.me));
                } else {
                    mBinding.textViewMsgBy.setText(mMessage.user_name);
                }
                mBinding.textViewMsgText.setText(mMessage.message_text);
                mBinding.textViewMsgOn.setText(mMessage.created_At);

                if(mMessage.user_id.equals(mAuth.getCurrentUser().getUid())){
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                }

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteMessage(mMessage.message_id);
                    }
                });
            }

            /**
             * This deletes the selected message.
             * @param message_id The id of the message selected
             */
            private void deleteMessage(String message_id) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("chats").document(chat.chat_id)
                        .collection("messages").document(message_id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: Delete successful");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                mMessages.remove(mMessage);

                db.collection("chats").document(chat.chat_id)
                        .update("lastMessageCreatedAt", mMessages.get(mMessages.size()-1).created_At,
                                "lastMessageSent", mMessages.get(mMessages.size()-1).message_text)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "onSuccess: Chat updated after deleting");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChatFragmentListener) {
            mListener = (ChatFragmentListener) context;
        }
    }

    ChatFragmentListener mListener;

    public interface ChatFragmentListener {
        void goToMyChats();
    }
}