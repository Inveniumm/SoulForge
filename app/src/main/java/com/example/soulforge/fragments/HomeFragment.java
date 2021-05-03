package com.example.soulforge.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soulforge.R;
import com.example.soulforge.activities.ChatActivity;
import com.example.soulforge.adapter.HomeAdapter;
import com.example.soulforge.model.HomeModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeAdapter.PostSelected {
    private RecyclerView recyclerView;
    private ImageButton sendBtn;

    private  HomeAdapter adapter;

    private DataFromHome dataFromHome;
    private FirebaseUser user;
    private  CollectionReference favReference;

    private List<HomeModel> list;
    private List<String> userList;

    private  boolean firstClick = true;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendBtn=view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        });
        init(view);

        list = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new HomeAdapter(list, getContext(),this,user.getUid());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataFromHome=(DataFromHome) context;
    }

    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        favReference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .collection("Favourite Images");
    }

    // First we were getting the post data from user but now directly getting from posts. In best way to get all users post
    public void loadDataFromFirestore(){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Posts");
        reference.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            Log.e("none", "onEvent: firstClick:"+firstClick );
            if(firstClick) {
                if (error != null) {
                    Log.e("Error: ", error.getMessage());
                    return;
                }
                if (value == null)
                    return;

                for (QueryDocumentSnapshot snapshot : value) {
                    if (!snapshot.exists())
                        return;

                    Log.e("none", "onEvent:getPosts " + snapshot.getId());
                    HomeModel model = snapshot.toObject(HomeModel.class);
                    System.out.println(model.getName());
                    list.add(new HomeModel(
                            model.getName(),
                            model.getProfileImage(),
                            model.getImageUrl(),
                            model.getUid(),
                            model.getComments(),
                            model.getDescription(),
                            model.getId(),
                            model.getTimestamp(),
                            model.getLikeCount(),
                            model.getLikedBy()
                    ));
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    //Handle click on post
    @Override
    public void onPostSelected(HomeModel post,int position) {
        firstClick=false;
        final int[] count = {0};
        favReference.document(post.getId()).addSnapshotListener(getActivity(), (value, error) -> {
            if (count[0] == 0) {
                if (!value.exists()) {
                    int i = post.getLikeCount() + 1;
                    List<String> likedBy=post.getLikedBy();
                    likedBy.add(user.getUid());
                    post.setLikeCount(i);
                    post.setLikedBy(likedBy);
                    updateLikeCount(post);
                    addToFavourites(post);
                    count[0] =1;
                } else {
                    if(post.getLikeCount()>0){
                        int i = post.getLikeCount() - 1;
                        List<String> likedBy=post.getLikedBy();
                        for(int j=0;j<likedBy.size();j++){
                            if(likedBy.get(j).equals(user.getUid())){
                                likedBy.remove(j);
                            }
                        }
                        post.setLikedBy(likedBy);
                        post.setLikeCount(i);
                        updateLikeCount(post);
                        removeFromFavourites(post);
                        count[0]=1;
                    }
                }
            }
            adapter.notifyItemChanged(position);
        });
    }

    private void updateLikeCount(HomeModel post) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Posts");
        reference.document(post.getId()).set(post).addOnCompleteListener(getActivity(), task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Liked Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(getActivity(), e -> Log.e("none", "onFailure: "+e ));
    }

    // Its  like post method
    private void addToFavourites(HomeModel post) {
        favReference.document(post.getId()).set(post).addOnCompleteListener(getActivity(), task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Added to favourites",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(getActivity(), e -> Log.e("none", "onFailure: "+e ));
    }

    // Its remove from like post method
    private void removeFromFavourites(HomeModel post) {
        favReference.document(post.getId()).delete().addOnCompleteListener(getActivity(), task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Unlike Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("none", "onFailure: "+e );
            }
        });
    }

    // Its open the comment bottom sheet
    @Override
    public void onCommentSelected(HomeModel post, int position) {
        firstClick=false;

        CommentBottomSheet bottomSheet=new CommentBottomSheet(getActivity(),post,user.getUid());
        bottomSheet.show(getFragmentManager(),"Comment Bottom sheet");
    }

    public void hideKeyboardFrom() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void shareSelectedPost(HomeModel post, int position) {
        firstClick=false;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,post.getDescription()+"\n"+ post.getImageUrl());
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Deal"));
    }

    @Override
    public void getSelectedUserId(String id) {
        dataFromHome.onReceiveUid(id);
    }

    public interface DataFromHome {
        void onReceiveUid(String uid);
    }
}