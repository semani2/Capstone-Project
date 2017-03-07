package sai.developement.travelogue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import sai.developement.travelogue.R;
import sai.developement.travelogue.adapters.TripsRecyclerAdapter;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.models.Trip;
import sai.developement.travelogue.models.User;

public class HomeActivity extends AppCompatActivity {

    private static final int AUTH_REQ_CODE = 1001;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.trips_recycler_view)
    RecyclerView tripsRecyclerView;

    @BindView(R.id.emptyRecyclerTextView)
    TextView emptyTripsTextView;

    private RecyclerView.LayoutManager mLayoutManager;
    private TripsRecyclerAdapter mTripsAdapter;
    private ArrayList<Trip> mTrips = new ArrayList<>();

    private ChildEventListener mTripsEventListener;
    private DatabaseReference mTripsReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(null != currentUser) {
                    // Add the user to Firebase if not yet added
                    User user = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail());

                    FirebaseDatabaseHelper.onLoginComplete(mDatabaseReference, user);

                    mTripsReference = FirebaseDatabaseHelper.getTripsDatabaseReference().
                            child(mFirebaseAuth.getCurrentUser().getUid());
                    fetchTrips();
                    mTripsReference.addChildEventListener(mTripsEventListener);
                    /*Toast.makeText(HomeActivity.this, "You are logged in! Welcome " + currentUser.getDisplayName(),
                            Toast.LENGTH_LONG).show();*/
                }
                else {
                    // Show the login screen
                    mTrips.clear();
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
            }
        };

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // Launch New Trip activity to create a new trip
                Intent newTripIntent = new Intent(HomeActivity.this, NewTripActivity.class);
                startActivity(newTripIntent);
            }
        });

        initRecyclerView();
    }

    private void fetchTrips() {
        mTripsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null) {
                    addDataToAdapter(dataSnapshot.getValue(Trip.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initRecyclerView() {
        tripsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        tripsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mTripsAdapter = new TripsRecyclerAdapter(mTrips, this);
        tripsRecyclerView.setAdapter(mTripsAdapter);

        if(mTrips.size() == 0) {
            tripsRecyclerView.setVisibility(View.GONE);
            emptyTripsTextView.setVisibility(View.VISIBLE);
        }
        else {
            tripsRecyclerView.setVisibility(View.VISIBLE);
            emptyTripsTextView.setVisibility(View.GONE);
        }
    }

    private void addDataToAdapter(Trip trip) {
        mTrips.add(trip);
        mTripsAdapter.notifyDataSetChanged();

        if(mTrips.size() == 0) {
            tripsRecyclerView.setVisibility(View.GONE);
            emptyTripsTextView.setVisibility(View.VISIBLE);
        }
        else {
            tripsRecyclerView.setVisibility(View.VISIBLE);
            emptyTripsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTrips.clear();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        if(mTripsEventListener != null) {
            mTripsReference.addChildEventListener(mTripsEventListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTrips.clear();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        if(mTripsEventListener != null) {
            mTripsReference.removeEventListener(mTripsEventListener);
        }

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
        AuthUI.getInstance().signOut(this);
    }
}
