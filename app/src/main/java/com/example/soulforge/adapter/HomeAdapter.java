package com.example.soulforge.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soulforge.R;
import com.example.soulforge.model.HomeModel;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    private Context context;
    private List<HomeModel> list;
    private PostSelected postSelected;
    private String userId;

    public HomeAdapter(List<HomeModel> list, Context context, PostSelected postSelected, String userId) {
        this.list = list;
        this.context = context;
        this.postSelected = postSelected;
        this.userId = userId;
    }

    public interface PostSelected {
        void onPostSelected(HomeModel post, int position);

        void onCommentSelected(HomeModel post, int position);

        void shareSelectedPost(HomeModel post, int position);

        void getSelectedUserId(String id);
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new HomeAdapter.HomeHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        holder.model = list.get(position);
        holder.nameTv.setText(list.get(position).getName());
        holder.timeTv.setText("" + list.get(position).getTimestamp());

        List<String> users = list.get(position).getLikedBy();
        if (users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).equals(userId)) {
                    holder.likeBtn.setImageResource(R.drawable.ic_heart_red);
                }
            }
        }

        int count = list.get(position).getLikeCount();
        if (count == 0) {
            holder.likeCountTv.setVisibility(View.VISIBLE);
        } else if (count == 1) {
            holder.likeCountTv.setText(count + " like");
        } else {
            holder.likeCountTv.setText(count + " likes");
        }

        holder.descriptionTv.setText(list.get(position).getDescription());
        Random random = new Random();

        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.profileImage);

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private HomeModel model;
        private CircleImageView profileImage;
        public TextView nameTv, timeTv, likeCountTv, descriptionTv;
        private ImageView imageView;
        private ImageButton likeBtn, commentBtn, shareBtn;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            imageView = itemView.findViewById(R.id.imageView);
            nameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            descriptionTv = itemView.findViewById(R.id.descTv);
            likeBtn.setOnClickListener(this);
            commentBtn.setOnClickListener(this);
            shareBtn.setOnClickListener(this);
            profileImage.setOnClickListener(this);
            nameTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == likeBtn) {
                postSelected.onPostSelected(list.get(getAdapterPosition()), getAdapterPosition());
            } else if (v == commentBtn) {
                postSelected.onCommentSelected(list.get(getAdapterPosition()), getAdapterPosition());
            } else if (v == shareBtn) {
                postSelected.shareSelectedPost(list.get(getAdapterPosition()), getAdapterPosition());
            } else if (v == profileImage) {
                postSelected.getSelectedUserId(list.get(getAdapterPosition()).getUid());
            } else if (v == nameTv) {
                postSelected.getSelectedUserId(list.get(getAdapterPosition()).getUid());
            }
        }
    }
}