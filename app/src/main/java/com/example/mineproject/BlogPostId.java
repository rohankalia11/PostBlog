package com.example.mineproject;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

public class BlogPostId {
    @Exclude
    public String BlogPostId;

    public <T extends BlogPostId> T withID(@NonNull final String id)
    {
        this.BlogPostId=id;

        return (T) this;
    }

}
