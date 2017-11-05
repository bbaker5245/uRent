package com.baker.flukes.urent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rflukes on 11/5/17.
 */

public class MessageListFragment extends Fragment{
    private static final String TAG = "MessageListFragment";
    private static final String ARG_USER_ID = "user_id";
    private DatabaseReference mMessagesDatabase;
    private DatabaseReference mUserDatabase;
    private List<String> messageIds;
    private List<Message> mMessages;

    private RecyclerView mMessageRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private MessageListFragment.MessageAdapter mAdapter;

    public static MessageListFragment newInstance(String userId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);
        MessageListFragment fragment = new MessageListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        mMessages = new ArrayList<>();
        mMessagesDatabase = DatabaseManager.getInstance(getContext()).GetMessageListReference();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new Message has been added, add it to the displayed list
                Message message = dataSnapshot.getValue(Message.class);
                message.setId(dataSnapshot.getKey());
                mMessages.add(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                Message newMessage = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();
                newMessage.setId(messageKey);

                for(int i = 0; i < mMessages.size(); i++){
                    if(mMessages.get(i).getId().equals(messageKey)){
                        mMessages.set(i, newMessage);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                String messageKey = dataSnapshot.getKey();
                int toRemove = -1;
                for(int i = 0; i < mMessages.size(); i++){
                    if(mMessages.get(i).getId().equals(messageKey)){
                        toRemove = i;
                        break;
                    }
                }
                if(toRemove >= 0){
                    mMessages.remove(toRemove);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A message has changed position, use the key to determine if we are
                // displaying this message and if so move it.

                // don't care about this now
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
            }
        };
        mMessagesDatabase.addChildEventListener(childEventListener);
        mMessagesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading initial data for all properties");
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });

        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh called");
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                updateUI();
            }
        });
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.message_recycler_view);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");
        if(messageIds == null){
            Log.d(TAG, "full message list being displayed");
            mAdapter = new MessageListFragment.MessageAdapter(mMessages);
        }else{
            Log.d(TAG, "filtered message list being displayed");
            List<Message> filteredMessages = new ArrayList<>();
            for(Message message : mMessages){
                if(messageIds.contains(message.getId())){
                    filteredMessages.add(message);
                }
            }
            mAdapter = new MessageListFragment.MessageAdapter(filteredMessages);
        }
        mMessageRecyclerView.setAdapter(mAdapter);
        mSwipeContainer.setRefreshing(false);
    }

    private class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Message mMessage;
        private TextView mPropertyTextView;
        private TextView mDateTextView;
        private TextView mStatusTextView;
        private TextView mToTextView;
        private TextView mFromTextView;


        public MessageHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_message, parent, false));
            itemView.setOnClickListener(this);

            mPropertyTextView = (TextView) itemView.findViewById(R.id.property);
            mDateTextView = (TextView) itemView.findViewById(R.id.date);
            mStatusTextView = (TextView) itemView.findViewById(R.id.status);
            mToTextView = (TextView) itemView.findViewById(R.id.to);
            mFromTextView = (TextView) itemView.findViewById(R.id.from);
        }

        public void bind(Message message){
            mMessage = message;
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mMessage.getSender())){
                mStatusTextView.setText("SENT: ");
            }else{
                mStatusTextView.setText("RECEIVED: ");
            }
            mDateTextView.setText(mMessage.getDate());
            mPropertyTextView.setText(mMessage.getProperty());
            DatabaseReference senderRef = DatabaseManager.getInstance(getContext()).GetUserEmailReference(mMessage.getSender());
            senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "done loading sender data");
                    mFromTextView.setText("From: " + dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "error loading initial data");
                }
            });
            DatabaseReference recipientRef = DatabaseManager.getInstance(getContext()).GetUserEmailReference(mMessage.getRecipient());
            recipientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "done loading recipient data");
                    mToTextView.setText("To: " + dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "error loading initial data");
                }
            });
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, mMessage.getProperty() + " clicked!");
            Intent intent = ViewMessageActivity.newIntent(getActivity(), mMessage.getId());
            startActivity(intent);
        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageListFragment.MessageHolder>{
        private List<Message> mMessages;

        public MessageAdapter(List<Message> messages){
            mMessages = messages;
        }

        @Override
        public MessageListFragment.MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new MessageListFragment.MessageHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MessageListFragment.MessageHolder holder, int position) {
            Message message = mMessages.get(position);
            Log.d(TAG,message.getProperty());
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey(ARG_USER_ID)){
            messageIds = new ArrayList<>();
            final String userId = (String) getArguments().getSerializable(ARG_USER_ID);
            Log.d(TAG, "onCreate: userId received: " + userId);
            mUserDatabase = DatabaseManager.getInstance(getContext()).GetUserMessageListReference(userId);
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    messageIds.add(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A Message has changed, use the key to determine if we are displaying this
                    // Message and if so remove it.
                    String messageKey = dataSnapshot.getKey();
                    int toRemove = -1;
                    for(int i = 0; i < messageIds.size(); i++){
                        if(messageIds.get(i).equals(messageKey)){
                            toRemove = i;
                            break;
                        }
                    }
                    if(toRemove >= 0){
                        messageIds.remove(toRemove);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                    // don't care about this
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                    //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
                }
            };
            mUserDatabase.addChildEventListener(childEventListener);
            mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "done loading initial data for user messages");
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "error loading initial data");
                }
            });
        }else{
            messageIds = null;
            Log.d(TAG, "onCreate: no userId received ");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        Log.d(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
