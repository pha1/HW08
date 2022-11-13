/**
 * Group 9 HW 08
 * MainActivity.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements MyChatsFragment.MyChatsFragmentListener, CreateChatFragment.CreateChatFragmentListener, ChatFragment.ChatFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new MyChatsFragment())
                .commit();
    }

    /**
     * This method will sign out the user and start the AuthActivity
     */
    @Override
    public void logout() {
        // Sign out
        FirebaseAuth.getInstance().signOut();

        // Go to AuthActivity
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Go to CreateChatFragment
     */
    @Override
    public void newChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateChatFragment(), "New Chat")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Go to Chat Fragment to display Chat details
     * @param chat The selected Chat Object
     */
    @Override
    public void chat(Chat chat) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ChatFragment.newInstance(chat), "Chat")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Cancel method
     */
    @Override
    public void goToMyChats() {
        getSupportFragmentManager().popBackStack();
    }
}