package com.example.noteskursach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private RecyclerView mNotesList;
    private GridLayoutManager gridLayoutManager;

    private DatabaseReference fNotesDatabase;
    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotesList = findViewById(R.id.main_notes_list);

        gridLayoutManager = new GridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);

        mNotesList.setHasFixedSize(true);
        mNotesList.setLayoutManager(gridLayoutManager);
//       gridLayoutManager.setReverseLayout(true);
//       gridLayoutManager.setStackFromEnd(true);
        mNotesList.addItemDecoration(new GridSpacingItemDecoration(3,dpTpPx(10),true));

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes")
                    .child(fAuth.getCurrentUser().getUid());


            loadData();
        }

        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    private void loadData(){

        Query query = fNotesDatabase.orderByChild("timeStamp");


        FirebaseRecyclerAdapter<NoteModel, NoteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoteModel, NoteViewHolder>(
                NoteModel.class,
                R.layout.single_note_layout,
                NoteViewHolder.class,query
        ) {
            @Override
            protected void populateViewHolder(final NoteViewHolder viewHolder, NoteModel model, int position) {
                final String noteId=getRef(position).getKey();
                fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("title")&&dataSnapshot.hasChild("timeStamp")){
                            String title = dataSnapshot.child("title").getValue().toString();
                            String timeStamp = dataSnapshot.child("timeStamp").getValue().toString();


                            long time=Long.parseLong(timeStamp);

                            if (time < 1000000000000L) {
                                time *= 1000;
                            }
                            long now = System.currentTimeMillis();
                            if (time > now || time <= 0) {
                                timeStamp=null;
                            }

                            final long diff = now - time;
                            if (diff < MINUTE_MILLIS) {
                                timeStamp="just now";
                            } else if (diff < 2 * MINUTE_MILLIS) {
                                timeStamp= "a minute ago";
                            } else if (diff < 50 * MINUTE_MILLIS) {
                                timeStamp= diff / MINUTE_MILLIS + "minutes ago";
                            } else if (diff < 90 * MINUTE_MILLIS) {
                                timeStamp="an hour ago";
                            } else if (diff < 24 * HOUR_MILLIS) {
                                timeStamp= diff / HOUR_MILLIS + "hours ago";
                            } else if (diff < 48 * HOUR_MILLIS) {
                                timeStamp="yesterday";
                            } else {
                                timeStamp=diff / DAY_MILLIS + "days ago";
                            }

                            viewHolder.setNoteTitle(title);
                           viewHolder.setNoteTime(timeStamp);

//                           GetTimeAgo getTimeAgo=new GetTimeAgo();
//                          viewHolder.setNoteTime(getTimeAgo.getTimeAgo(Long.parseLong(timeStamp)));


                            viewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(MainActivity.this, NewNoteActivity.class);
                                    intent.putExtra("noteID", noteId);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        mNotesList.setAdapter(firebaseRecyclerAdapter);
    }

    private void updateUI(){
        if (fAuth.getCurrentUser() != null) {
            Log.i("MainActivity", "fAuth != null");
        } else {
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
            Log.i("MainActivity", "fAuth != null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_menu_note_btn:
                Intent newIntent = new Intent(MainActivity.this, NewNoteActivity.class);
                startActivity(newIntent);
                break;
            case R.id.main_menu_exit_btn:
                fAuth.signOut();
                Intent newIntent1 = new Intent(MainActivity.this, StartActivity.class);
                startActivity(newIntent1);
                break;
        }

        return true;
    }
    private int dpTpPx(int dp){
        Resources r=getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics()));
    }

}


