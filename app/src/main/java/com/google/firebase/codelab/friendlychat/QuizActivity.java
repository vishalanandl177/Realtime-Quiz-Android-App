package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private SharedPreferences mSharedPreferences;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;

    private static final String TAG = "Profile";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private String mPhotoUrl, mUserEmail;
    private DatabaseReference myConnectionsRef;
    private DatabaseReference questionsConnectionsRef, mScoreRef,
            refQuizScore;
    private String ans;
    int c = 0;
    int aCount = 1;
    HashMap<String, QuestionHolder> questionHolderHashMap;

    EditText questionTextView;
    Button option1Button, option2Button, option3Button, option4Button;
    int count = 0;
    int time = 30;
    TextView timeleft;
    Timer timer;
    Runnable runnable;
    String topic = "", em, en;
    int score = 0, total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        topic = intent.getStringExtra("topic");
        //  if (!intent.getStringExtra("em").equals(null)) {
        em = intent.getStringExtra("em");
        en = intent.getStringExtra("en");
        //}
        Log.e("jyfufiyyigil ", en + " " + em);
        refQuizScore = FirebaseDatabase.getInstance().getReference().child("quizUsers");
        timeleft = (TextView) findViewById(R.id.timelefttextview);
        setTitle("Time left ");
        try {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    runnable = new Runnable() {
                        @Override
                        public void run() {

                            timeleft.setText(String.valueOf(time--));
                            if (time == 1) {
                                showQuestions();
                                time = 30;
                            }
                        }
                    };
                    runOnUiThread(runnable);
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

        final Intent[] i = {getIntent()};
        String opponentEmail = i[0].getStringExtra("email");

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            mUserEmail = mFirebaseUser.getEmail();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        myConnectionsRef = FirebaseDatabase.getInstance().getReference().child("online/" + topic).child(mFirebaseUser.getUid());
        /*DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {
                    OnlineUsers ou = new OnlineUsers(mUsername, mUserEmail, mPhotoUrl, Boolean.TRUE);
                    myConnectionsRef.setValue(ou);
                    Toast.makeText(QuizActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                } else {
                    //   OnlineUsers ou = new OnlineUsers(mUsername, mUserEmail, mPhotoUrl, Boolean.FALSE);
                    // myConnectionsRef.setValue(ou);
                    myConnectionsRef.removeValue();
                    Toast.makeText(QuizActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });*/

        questionTextView = (EditText) findViewById(R.id.question_text_view);
        option1Button = (Button) findViewById(R.id.option_one);
        option2Button = (Button) findViewById(R.id.option_two);
        option3Button = (Button) findViewById(R.id.option_three);
        option4Button = (Button) findViewById(R.id.option_four);

        option1Button.setOnClickListener(this);
        option2Button.setOnClickListener(this);
        option3Button.setOnClickListener(this);
        option4Button.setOnClickListener(this);

        questionsConnectionsRef = FirebaseDatabase.getInstance().getReference().child("questions/" + topic);
        mScoreRef = FirebaseDatabase.getInstance().getReference().child("scores").child(mFirebaseUser.getUid());
        questionHolderHashMap = new HashMap<String, QuestionHolder>();
        final int[] t = {0};

        questionsConnectionsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                QuestionHolder holder = dataSnapshot.getValue(QuestionHolder.class);
                questionHolderHashMap.put(String.valueOf(count++), holder);
                // Toast.makeText(QuizActivity.this, "Added", Toast.LENGTH_SHORT).show();
                if (t[0] == 0) {
                    showQuestions();

                    t[0]++;
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
        });
        mScoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ScoreHolder scoreHolder = dataSnapshot.getValue(ScoreHolder.class);
                try {
                    total = scoreHolder.getTotal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* try {
            refQuizScore.child(em)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String score = dataSnapshot.getValue(String.class);
                            Log.e("score ", score);
                            if (mUserEmail.substring(0, mUserEmail.indexOf('@')).equals(em))
                                if (score.equals("0"))e
                                    Toast.makeText(QuizActivity.this, en + " give wrong answer!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(QuizActivity.this, en + " give right answer!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        runnable = null;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        timer.cancel();
        runnable = null;
        super.onStop();
    }

    void showQuestions() {
        if (c != count) {
            QuestionHolder holder = questionHolderHashMap.get(String.valueOf(c++));
            questionTextView.setText(holder.getQues());
            option1Button.setText(holder.getOption1());
            option2Button.setText(holder.getOption2());
            option3Button.setText(holder.getOption3());
            option4Button.setText(holder.getOption4());
            ans = holder.getAns();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizActivity.this);
            alertDialog.setTitle("Quiz Up");
            alertDialog.setMessage("End of questions");
            alertDialog.setPositiveButton("See Score?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    c = 0;
                    //showQuestions();
                    startActivity(new Intent(QuizActivity.this, Profile.class));
                    finish();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(QuizActivity.this, MainActivity.class));
                    finish();
                }
            });
            alertDialog.show();
        }
    }

    void showAlertMessage(String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton("OK", null);
        alertDialog.show();
    }

    @Override
    protected void onRestart() {
        OnlineUsers ou = new OnlineUsers(mUsername, mUserEmail, mPhotoUrl, Boolean.TRUE);
        myConnectionsRef.setValue(ou);
        super.onRestart();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {

        String a = null;

        switch (v.getId()) {
            case R.id.option_one:
                a = option1Button.getText().toString();
                //a = "option1";
                break;
            case R.id.option_two:
                a = option2Button.getText().toString();
                // a = "option2";
                break;
            case R.id.option_three:
                a = option3Button.getText().toString();
                break;
            case R.id.option_four:
                a = option4Button.getText().toString();
                break;

        }
        if (a.equals("")) {
            Toast.makeText(this, "Wait", Toast.LENGTH_SHORT).show();
        } else if (a.equals(ans)) {
            showAlertMessage("Answer", "Right answer! You get +5 score");
            try {
                refQuizScore.child(em).setValue(String.valueOf(aCount++));
            } catch (Exception e) {
                e.printStackTrace();
            }
            score += 5;
            mScoreRef.setValue(new ScoreHolder(score, total + score));
            time = 30;
            showQuestions();
        } else {
            showAlertMessage("Answer", "Wrong answer!");
            refQuizScore.child(em).setValue("0");
            showQuestions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.quiz_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.skip_menu:
                showQuestions();
                time = 30;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
