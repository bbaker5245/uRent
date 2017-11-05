package com.baker.flukes.urent;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/**
 * Created by rflukes on 11/5/17.
 */

public class ViewMessageFragment extends Fragment{
    private static final String TAG = "ViewMessageFragment";
    private static final String ARG_MESSAGE_ID = "message_id";

    private TextView mAddress;
    private TextView mContent;
    private TextView mDate;
    private FloatingActionButton mBackButton;
    private FloatingActionButton mMessageButton;
    private Message mMessage;

    public static ViewMessageFragment newInstance(String messageId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_MESSAGE_ID, messageId);
        ViewMessageFragment fragment = new ViewMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_view_message, container, false);

        mAddress = (TextView) view.findViewById(R.id.address);
        mContent = (TextView) view.findViewById(R.id.content);
        mDate = (TextView) view.findViewById(R.id.date);

        mBackButton = (FloatingActionButton) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mMessageButton = (FloatingActionButton) view.findViewById(R.id.message_button);

        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMessage.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent intent = MessageActivity.newIntent(getActivity(), mMessage.getProperty(), mMessage.getRecipient(),  mMessage.getSender() );
                    startActivity(intent);
                }else{
                    Intent intent = MessageActivity.newIntent(getActivity(), mMessage.getProperty(), mMessage.getSender(),  mMessage.getRecipient() );
                    startActivity(intent);
                }

            }
        });

        return view;
    }

    private void updateUI(){
        Log.d(TAG, "updateUI()");
        try{
            if(mMessage != null){
                mAddress.setText(mMessage.getProperty());
                mDate.setText(mMessage.getDate());
                mContent.setText(mMessage.getContent());
            }
        }finally {
            // this means message was updated before UI objects were declared
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        final String messageId = (String) getArguments().getSerializable(ARG_MESSAGE_ID);
        Log.d(TAG, "onCreate: messageId received: " + messageId);
        DatabaseReference ref = DatabaseManager.getInstance(getContext()).GetMessageReference(messageId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading message data");
                mMessage = dataSnapshot.getValue(Message.class);
                if(mMessage != null){
                    mMessage.setId(messageId);
                    updateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });
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
