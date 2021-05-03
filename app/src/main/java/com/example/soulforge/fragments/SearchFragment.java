package com.example.soulforge.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soulforge.R;
import com.example.soulforge.adapter.UserAdapter;
import com.example.soulforge.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView recyclerView;

    private UserAdapter adapter;
    private List<Users> list;

    private CollectionReference reference;
    private OnDataPass onDataPass;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDataPass = (OnDataPass) context;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    //Initializing the view
    private void init(View view) {
        reference = FirebaseFirestore.getInstance().collection("Users");
        searchView = view.findViewById(R.id.searchView);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new UserAdapter(list);
        recyclerView.setAdapter(adapter);

        loadUserData();
        searchUser();
        clickListener();
    }

    //Its interface observing click inside the adapter
    private void clickListener() {
        adapter.OnUserClicked(uid -> onDataPass.onChange(uid));
    }

    //Its the method who is searing the user in base of keyword
    private void searchUser() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                reference.orderBy("search").startAfter(query).endAt(query + "\uf8ff")
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                list.clear();
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    if (!snapshot.exists())
                                        return;
                                    Users users = snapshot.toObject(Users.class);
                                    list.add(users);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals(""))
                    loadUserData();

                return false;
            }
        });
    }
    //Loading users from firebase
    private void loadUserData() {
        reference.addSnapshotListener((value, error) -> {
            if (error != null)
                return;
            if (value == null)
                return;

            list.clear();
            for (QueryDocumentSnapshot snapshot : value) {
                Users users = snapshot.toObject(Users.class);
                list.add(users);
            }
            adapter.notifyDataSetChanged();
        });
    }

    public interface OnDataPass {
        void onChange(String uid);
    }
}