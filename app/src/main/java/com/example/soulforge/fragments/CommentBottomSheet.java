package com.example.soulforge.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soulforge.R;
import com.example.soulforge.adapter.CommentAdapter;
import com.example.soulforge.model.CommentModel;
import com.example.soulforge.model.HomeModel;
import com.example.soulforge.model.Users;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CommentBottomSheet extends BottomSheetDialogFragment {
    private Activity activity;
    private ImageView sendBT;
    private EditText comment_ET;
    private RecyclerView commentRV;

    private CommentAdapter cAdapter;
    private HomeModel post;
    private List<CommentModel> cList;

    private String userId;

    public CommentBottomSheet(Activity activity, HomeModel post, String userId) {
        this.activity = activity;
        this.post = post;
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        cList = new ArrayList<>();
        sendBT = view.findViewById(R.id.comment_sendBT);
        commentRV = view.findViewById(R.id.comment_recycler_view);
        comment_ET = view.findViewById(R.id.comment_ET);
        commentRV.setHasFixedSize(true);
        commentRV.setLayoutManager(new LinearLayoutManager(activity));
        cList = post.getComments();
        Log.e("none", "init: " + cList + "/////" + post.getComments());
        cAdapter = new CommentAdapter(activity, cList);
        commentRV.setAdapter(cAdapter);
//        getComments();

        sendBT.setOnClickListener(v -> {
            String msg = comment_ET.getText().toString();
            if (!msg.equals("")) {
//                    sendMessage(fUser.getUid(),userid,msg);
                getData(msg);
                comment_ET.setText("");
            } else {
                Toast.makeText(activity, "Please send non empty message", Toast.LENGTH_SHORT).show();
            }

        });
    }

    // Here getting all comment message from firebase
    private void getData(String msg) {
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
        usersRef.document(userId).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Error: ", error.getMessage());
                return;
            }
            if (value == null)
                return;
            Users user = value.toObject(Users.class);
            if (user.getUid() != null) {
                CommentModel comment = new CommentModel();
                comment.setUserId(user.getUid());
                comment.setProfileImage(user.getProfileImage());
                comment.setPostId(post.getId());
                comment.setCommentText(msg);
                comment.setUserName(user.getName());
                List<CommentModel> commentModelList = post.getComments();
                commentModelList.add(comment);
                post.setComments(commentModelList);
                sendComment(post);
            } else {
                Log.e("none", "onEvent: error in getting user");
            }
        });
    }

    // Its posting comment against post
    private void sendComment(HomeModel post) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Posts");
        reference.document(post.getId()).set(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cAdapter.notifyDataSetChanged();
            } else {
                Log.e("none", "onComplete: error in uploading data");
            }
        }).addOnFailureListener(e -> Log.e("none", "onFailure: " + e));
    }
}
