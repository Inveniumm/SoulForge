package com.example.soulforge.model;

public class CommentModel {
    private String userId,userName,postId,profileImage,commentText;

    public CommentModel(){}

    public CommentModel(String userId, String userName, String postId, String profileImage,String commentText) {
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
        this.profileImage = profileImage;
        this.commentText = commentText;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
