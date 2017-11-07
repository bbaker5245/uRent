package com.baker.flukes.urent;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rflukes on 11/4/17.
 */

public class MessageFragment extends Fragment{
    private static final String TAG = "MessageFragment";
    private static final String ARG_PROPERTY_ADDRESS = "property_address";
    private static final String ARG_SENDER_ID = "sender_id";
    private static final String ARG_RECIPIENT_ID = "recipient_id";

    private TextView mDate;
    private TextView mAddress;
    private EditText mMessageContent;
    private FloatingActionButton mBackButton;
    private FloatingActionButton mSendMessageButton;
    private TextView mSender;
    private TextView mRecipient;

    public static MessageFragment newInstance(String address, String ownerId, String tenantId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ADDRESS, address);
        args.putSerializable(ARG_SENDER_ID, ownerId);
        args.putSerializable(ARG_RECIPIENT_ID, tenantId);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        final String date = Calendar.getInstance().getTime().toString();
        String temp1 = (String) getArguments().getSerializable(ARG_PROPERTY_ADDRESS);
        String temp2;
        if(temp1.indexOf(',') > -1){
            temp2 = temp1.substring(0,temp1.indexOf(','));
        }else{
            temp2 = temp1;
        }
        final String address = temp2;

        mMessageContent = (EditText) view.findViewById(R.id.message_content);

        mSender = (TextView) view.findViewById(R.id.sender);
        String senderUserid = (String) getArguments().getSerializable(ARG_SENDER_ID);
        DatabaseReference senderRef = DatabaseManager.getInstance(getContext()).GetUserEmailReference(senderUserid);
        senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading sender data");
                mSender.setText("From: " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });

        mRecipient = (TextView) view.findViewById(R.id.recipient);
        String recipientUserId = (String) getArguments().getSerializable(ARG_RECIPIENT_ID);
        DatabaseReference recipientRef = DatabaseManager.getInstance(getContext()).GetUserEmailReference(recipientUserId);
        recipientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading recipient data");
                mRecipient.setText("To: " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });

        mSendMessageButton = (FloatingActionButton) view.findViewById(R.id.send_message_button);
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String senderId = (String) getArguments().getSerializable(ARG_SENDER_ID);
                final String recipientId = (String) getArguments().getSerializable(ARG_RECIPIENT_ID);

                Message message = new Message(address, senderId, recipientId, date, mMessageContent.getText().toString());
                DatabaseManager.getInstance(getContext()).AddMessage(message);
                Toast.makeText(getActivity(), "Message sent to Property Owner", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });

        mDate = (TextView) view.findViewById(R.id.date);
        mDate.setText("Date: " + date);

        mBackButton = (FloatingActionButton) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        final String senderID = (String) getArguments().getSerializable(ARG_SENDER_ID);
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
