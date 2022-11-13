/**
 * Group 9 HW 08
 * AuthActivity.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements LoginFragment.LoginListener, SignUpFragment.SignUpListener {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * For test purposes
         * Email: j@j.com
         * Password: 123456
         * Name: John Smith
         *
         * Email: a@a.com
         * Password: 123456
         * Name: Alice Smith
         *
         * Email: b@b.com
         * Password: 123456
         * Name: Bob Smith
         *
         */

        if(mAuth.getCurrentUser() == null){
            setContentView(R.layout.activity_auth);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Start Main Activity
     */
    @Override
    public void gotoMyChat() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void gotoLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void gotoSignUp() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new SignUpFragment())
                .commit();
    }
}