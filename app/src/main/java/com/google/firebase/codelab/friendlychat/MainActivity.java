/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {


    public static class OnlineViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView emailTextView, statusTextView;
        public CircleImageView userImageView;
        View view;

        public OnlineViewHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            emailTextView = (TextView) itemView.findViewById(R.id.emailTextView);
            userImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            statusTextView = (TextView) itemView.findViewById(R.id.status);
            this.view = v;
        }
    }

    LinkedList<CompRequest> compList = new LinkedList<>();
    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl, mEmail;
    private SharedPreferences mSharedPreferences;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    //private FirebaseRecyclerAdapter<FriendlyMessage, OnlineViewHolder> mFirebaseAdapter;
    private FirebaseRecyclerAdapter<OnlineUsers, OnlineViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText mMessageEditText;
    private AdView mAdView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference myConnectionsRef, refToRequest = FirebaseDatabase.getInstance().getReference();
    boolean alertShown = false;
    Intent i;
    boolean sender = false;
    boolean activity = false;
    CompRequest compRequest = new CompRequest();
    String topic = "";
    String em, en;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show();

        setTitle(getResources().getString(R.string.onlineusers));
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Intent intent = getIntent();
        topic = intent.getStringExtra("topic");
        i = new Intent(MainActivity.this, QuizActivity.class);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            mEmail = mFirebaseUser.getEmail();
        }
        refToRequest.child("comp").addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final CompRequest o = dataSnapshot.getValue(CompRequest.class);
                // Toast.makeText(MainActivity.this, o.getMyEmail(), Toast.LENGTH_SHORT).show();
                if (o.getOpponentEmail().equals(mEmail)) {
                    sender = false;
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Quiz Up");
                    alertDialog.setMessage(o.getMyName() + " wants to challenge you.");
                    //refQuizScore.child(o.getMyEmail().substring(0, o.getMyEmail().indexOf('@'))).setValue("0");
                    en = o.getMyName();
                    em = o.getMyEmail().substring(0, o.getMyEmail().indexOf('@'));
                    Log.e("Main Activity em, en ", em + " " + en);
                    alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, QuizActivity.class));
                            compRequest.setAccepted(1);
                            refToRequest.child("comp").child(mEmail.substring(0, mEmail.indexOf('@'))).setValue(compRequest);
                            refToRequest.child("comp").child(mEmail.substring(0, mEmail.indexOf('@'))).removeValue();
                            alertShown = false;
                        }
                    });
                    alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            compRequest.setAccepted(2);
                            refToRequest.child("comp").child(mEmail.substring(0, mEmail.indexOf('@'))).setValue(compRequest);
                            refToRequest.child("comp").child(mEmail.substring(0, mEmail.indexOf('@'))).removeValue();
                            alertShown = false;
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                    if (!alertShown) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    alertDialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    alertShown = true;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mProgressBar.setVisibility(View.GONE);
                CompRequest o = dataSnapshot.getValue(CompRequest.class);
                if (sender) {
                    Toast.makeText(MainActivity.this, "User denied the request", Toast.LENGTH_SHORT).show();

                  /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Quiz Up");
                    alertDialog.setMessage("User denied the request");
                    alertDialog.setPositiveButton("OK",null);
                    alertDialog.create();
                    alertDialog.show();*/

                }
                if (o.isAccepted() == 1 && !activity) {
                    i.putExtra("topic", topic);
                    i.putExtra("em", em);
                    i.putExtra("en", en);
                    Log.e("Maiem, en ", em + " " + en);
                    startActivity(i);
                    activity = true;
                }
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
        });
        myConnectionsRef = FirebaseDatabase.getInstance().getReference().child("online/" + topic).child(mFirebaseUser.getUid());
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    OnlineUsers ou = new OnlineUsers(mUsername, mEmail, mPhotoUrl, Boolean.TRUE);
                    myConnectionsRef.setValue(ou);
                    // Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                } else {
                    //OnlineUsers ou = new OnlineUsers(mUsername, mEmail, mPhotoUrl, Boolean.FALSE);
                    //myConnectionsRef.setValue(ou);
                    myConnectionsRef.removeValue();
                    // Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(false);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<OnlineUsers, OnlineViewHolder>(

                OnlineUsers.class,
                R.layout.item_message,
                OnlineViewHolder.class,
                mFirebaseDatabaseReference.child("online/" + topic)) {

            @Override
            protected void populateViewHolder(final OnlineViewHolder viewHolder, final OnlineUsers model, final int position) {
                mProgressBar.setVisibility(View.GONE);
                //   if (!Objects.equals(mEmail, model.getEmail())) {
                if (model.isOnline()) {
                    viewHolder.statusTextView.setText("online");
                    viewHolder.emailTextView.setText(model.getEmail());
                    viewHolder.nameTextView.setText(model.getName());
                    if (model.getPhotoUrl() == null) {
                        viewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                                R.drawable.ic_account_circle_black_36dp));
                    } else {
                        Glide.with(MainActivity.this)
                                .load(model.getPhotoUrl())
                                .into(viewHolder.userImageView);
                    }
                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!mEmail.equals(model.getEmail())) {
                                sender = true;
                                i.putExtra("email", model.getEmail());
                                String ema = model.getEmail().substring(0, model.getEmail().indexOf('@'));
                                compRequest.setAccepted(0);
                                compRequest.setMyEmail(mEmail);
                                compRequest.setMyName(mUsername);
                                compRequest.setOpponentEmail(model.getEmail());
                                compRequest.setOpponentName(model.getName());
                                refToRequest.child("comp").child(ema).setValue(compRequest);
                                mProgressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, "Waiting for opponent to accept", Toast.LENGTH_SHORT).show();
                                activity = false;
                            } else {
                                Toast.makeText(MainActivity.this, "You cannot challenge yourself", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    //  }

                }

            }
        };


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Initialize and request AdMob ad.
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //      OnlineUsers ou = new OnlineUsers(mUsername, mEmail, mPhotoUrl, Boolean.FALSE);

//        myConnectionsRef.setValue(ou);
        myConnectionsRef.removeValue();

        super.onStop();
    }


    @Override
    protected void onRestart() {
        OnlineUsers ou = new OnlineUsers(mUsername, mEmail, mPhotoUrl, Boolean.TRUE);
        myConnectionsRef.setValue(ou);

        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_menu:
                sendInvitation();
                return true;
            case R.id.sign_out_menu:
                myConnectionsRef.removeValue();
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            case R.id.profile_menu:
                startActivity(new Intent(this, Profile.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

}
