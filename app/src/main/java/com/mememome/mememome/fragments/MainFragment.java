package com.mememome.mememome.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.mememome.mememome.R;
import com.mememome.mememome.activities.MemoActivity;
import com.mememome.mememome.adapters.MemoArrayAdapter;
import com.mememome.mememome.model.dao.Memo;
import com.mememome.mememome.networking.server.dropbox_api.SyncDropBoxFiles;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String EXTRA_MEMO = "extra_memo";

    private MemoArrayAdapter adapter;


    //Buttons
    Button btnAdd;

    //Views
    View rootView;
    EditText memoNameEditText;
    ListView memosList;

    // LIST
    ArrayList<Memo> memos;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SyncDropBoxFiles lf = new SyncDropBoxFiles(getActivity(),"");
        lf.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        memosList = (ListView)rootView.findViewById(R.id.listMemos);

        memoNameEditText = (EditText)rootView.findViewById(R.id.memoNameEditText);
        btnAdd = (Button)rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(v);
            }
        });

        List memosFromDB = Memo.listAll(Memo.class);

        memos = new ArrayList<Memo>(memosFromDB);

        memosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MemoActivity.class);
                intent.putExtra(EXTRA_MEMO, memos.get(position).getId());
                startActivity(intent);
            }
        });

        memosList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeMemo(position);
                return false;
            }
        });

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        adapter = new MemoArrayAdapter(getActivity(), memos);
        memosList.setAdapter(adapter);



        return rootView;
    }

    private void removeMemo(int position) {
        Memo memo = memos.get(position);
        adapter.remove(memo);
        memo.delete();
        memos.remove(position);
        adapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

        // Will be called via the onClick attribute
        // of the buttons in main.xml
    public void onClickButton(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Memo> adapter = (ArrayAdapter<Memo>) memosList.getAdapter();
        Memo memo = null;
        switch (view.getId()) {
            case R.id.btnAdd:
                if(!memoNameEditText.getText().toString().isEmpty()) {
                    // save the new comment to the database
                    memo = new Memo(memoNameEditText.getText().toString(), //name
                            "", // text
                            System.currentTimeMillis(), // created
                            System.currentTimeMillis(), // updated
                            "0", // rev
                            "", // hash
                            1L, // memoGroupId
                            "", // localFilePath
                            ""); // dropboxFilePath
                    memo.save();
                    adapter.add(memo);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {

        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
