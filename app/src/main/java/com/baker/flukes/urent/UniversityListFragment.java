package com.baker.flukes.urent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UniversityListFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private static final String TAG = "UniversityListFragment";

    private DatabaseReference mDatabase;
    List<University> mUniversities;

    private UniversityAdapter mAdapter;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
        mUniversities = new ArrayList<>();
        mDatabase = DatabaseManager.getInstance().GetUniversityListReference();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new property has been added, add it to the displayed list
                University university = dataSnapshot.getValue(University.class);
                university.setId(dataSnapshot.getKey());
                mUniversities.add(university);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A property has changed, use the key to determine if we are displaying this
                // property and if so displayed the changed property.
                University newUniversity = dataSnapshot.getValue(University.class);
                String universityKey = dataSnapshot.getKey();
                newUniversity.setId(universityKey);

                for(int i = 0; i < mUniversities.size(); i++){
                    if(mUniversities.get(i).getId().equals(universityKey)){
                        mUniversities.set(i, newUniversity);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A property has changed, use the key to determine if we are displaying this
                // property and if so remove it.
                String universityKey = dataSnapshot.getKey();
                int toRemove = -1;
                for(int i = 0; i < mUniversities.size(); i++){
                    if(mUniversities.get(i).getId().equals(universityKey)){
                        toRemove = i;
                        break;
                    }
                }
                if(toRemove >= 0){
                    mUniversities.remove(toRemove);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A property has changed position, use the key to determine if we are
                // displaying this property and if so move it.

                // don't care about this now
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postProperties:onCancelled", databaseError.toException());
                //Toast.makeText(context, "Failed to load properties.", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addChildEventListener(childEventListener);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "done loading initial data");
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "error loading initial data");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_university_list, container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        TextView emptyTextView = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyTextView);
        return view;
    }

    private void updateUI(){
        mAdapter = new UniversityAdapter(mContext, R.layout.list_item_university, mUniversities);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        University university = (University) listView.getAdapter().getItem(position);
        Log.d(TAG, university.getName() + " clicked!");
        Intent intent = PropertyListActivity.newIntentForUniversity(getActivity(), university.getId());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search Universities");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Check if there is anything in search box
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        // Filter universities by search text
        List<University> filteredUniversities = new ArrayList<>(mUniversities);
        for (University university : mUniversities) {
            if (!university.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredUniversities.remove(university);
            }
        }

        // Display filtered universities
        mAdapter = new UniversityAdapter(mContext, R.layout.list_item_university, filteredUniversities);
        setListAdapter(mAdapter);

        return false;
    }

    public void resetSearch() {
        mAdapter = new UniversityAdapter(mContext, R.layout.list_item_university, mUniversities);
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    public class UniversityAdapter extends ArrayAdapter<University>{
        private List<University> mUniversities;

        public UniversityAdapter(Context context, int textViewResourceId, List<University> universities){
            super(context, textViewResourceId, universities);
            mUniversities = universities;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_university, null);
            }

            University i = mUniversities.get(position);

            if (i != null) {
                TextView universityNameTextView = (TextView) v.findViewById(R.id.university_name);

                // check to see if each individual textview is null.
                // if not, assign some text!
                if (universityNameTextView != null){
                    universityNameTextView.setText(i.getName());
                }
            }

            // the view must be returned to our activity
            return v;
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