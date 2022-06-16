package com.example.parstagram;

import android.app.Application;

import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.example.parstagram.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // register parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("hh8msnYLa5wmwyVCkFUKSPjwrEF4oEzLnrBRAm9R")
                .clientKey("w8oGnWgZWtCNWDRpmBE6hY6sXgSKLJ5L8xIKMwYQ")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
