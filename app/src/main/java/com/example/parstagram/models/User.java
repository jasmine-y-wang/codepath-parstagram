package com.example.parstagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
    public static final String KEY_PROFILE_PIC = "profilePic";

    public ParseFile getPfp() {
        return getParseFile(KEY_PROFILE_PIC);
    }

    public void setPfp(ParseFile file) {
        put(KEY_PROFILE_PIC, file);
    }

}
