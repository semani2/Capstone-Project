package sai.developement.travelogue.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

import sai.developement.travelogue.R;
import sai.developement.travelogue.eventhandlers.activities.IEventHandler;
import sai.developement.travelogue.events.LogoutEvent;

/**
 * Created by sai on 3/9/17.
 */

public abstract class TravelogueActivity extends AppCompatActivity {

    private static final int AUTH_REQ_CODE = 1001;

    protected FirebaseAuth.AuthStateListener mAuthStateListener;

    protected FirebaseAuth mFirebaseAuth;

    protected IEventHandler mEventHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    onUserLoggedIn(firebaseAuth);
                }
                else {
                    onUserLoggedOut(firebaseAuth);
                }
            }
        };

        mEventHandler = createEventHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventHandler.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventHandler.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    protected void showLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.AppTheme_LoginActivity)
                        .build(),
                AUTH_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AUTH_REQ_CODE) {
            if(resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        EventBus.getDefault().post(new LogoutEvent());
    }

    protected void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public abstract void onUserLoggedIn(@NonNull FirebaseAuth firebaseAuth);

    public void onUserLoggedOut(@NonNull FirebaseAuth firebaseAuth) {
        showLogin();
    }

    protected abstract IEventHandler createEventHandler();

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
