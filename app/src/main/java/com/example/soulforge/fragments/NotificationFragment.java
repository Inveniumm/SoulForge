package com.example.soulforge.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soulforge.R;
import com.example.soulforge.adapter.HomeAdapter;
import com.example.soulforge.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements HomeAdapter.PostSelected {
    private RecyclerView recyclerView;
    private HomeAdapter adapter;

    private DataFromNotification dataFromNotification;
    private FirebaseUser user;
    private CollectionReference favReference;

    private List<HomeModel> list;
    private boolean firstClick = true;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getContext(),this,user.getUid());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataFromNotification=(DataFromNotification) context;
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

    // Its loading the post data from firebase
    public void loadDataFromFirestore() {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .collection("Favourite Images");

        reference.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Error: ", error.getMessage());
                return;
            }
            if (value == null)
                return;
            list.clear();
            for (QueryDocumentSnapshot snapshot : value) {
                if (!snapshot.exists())
                    return;

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
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onPostSelected(HomeModel post,int position) {
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
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void updateLikeCount(HomeModel post) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(post.getUid())
                .collection("Post Images");
        reference.document(post.getId()).set(post).addOnCompleteListener(getActivity(), task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Liked",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),"not Liked",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(getActivity(), e -> Log.e("none", "onFailure: "+e ));
    }

    // Its method for like post
    private void addToFavourites(HomeModel post) {
        favReference.document(post.getId()).set(post).addOnCompleteListener(getActivity(), task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Added to favourites",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),"not added",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(getActivity(), e -> Log.e("none", "onFailure: "+e ));
    }
    // Its method for unlike post
    private void removeFromFavourites(HomeModel post) {
        favReference.document(post.getId()).delete().addOnCompleteListener(getActivity(), task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Unlike Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(getActivity(), e -> Log.e("none", "onFailure: "+e ));
    }

    // Its interface to open the comment bottom sheet
    @Override
    public void onCommentSelected(HomeModel post, int position) {
        CommentBottomSheet bottomSheet=new CommentBottomSheet(getActivity(),post,user.getUid());
        bottomSheet.show(getFragmentManager(),"Comment Bottom sheet");
    }

    //Its method to share post
    @Override
    public void shareSelectedPost(HomeModel post, int position) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TITLE,"send");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, post.getDescription());
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(post.getImageUrl()));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "SoulForge"));
    }

    @Override
    public void getSelectedUserId(String id) {
        dataFromNotification.UidFromNotification(id);
    }

    public interface DataFromNotification {
        void UidFromNotification(String uid);
    }
}