package com.example.EEEBuddy;

public class CommentModel {

    private String comment, commentedBy, date;

    public CommentModel() {
    }

    public CommentModel(String comment, String commentedBy, String date) {
        this.comment = comment;
        this.commentedBy = commentedBy;
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
