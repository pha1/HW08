package edu.uncc.hw08;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<Chat> {

    final String TAG = "test";

    public ChatAdapter(@NonNull Context context, int resource, @NonNull List<Chat> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_chats_list_item, parent, false);
        }

        Chat chat = getItem(position);

        TextView textViewMsgBy = convertView.findViewById(R.id.textViewMsgBy);
        TextView textViewMsgText = convertView.findViewById(R.id.textViewMsgText);
        TextView textViewMsgOn = convertView.findViewById(R.id.textViewMsgOn);

        FirebaseAuth mAuth =  FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser().getUid().equals(chat.user1)) {
            textViewMsgBy.setText(chat.user2name);
            Log.d(TAG, "getView: " + chat.user2name);
        } else {
            textViewMsgBy.setText(chat.user1name);
        }

        textViewMsgText.setText(chat.lastMessageSent);
        textViewMsgOn.setText(chat.lastMessageCreatedAt);

        return convertView;
    }
}
