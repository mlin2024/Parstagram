package com.example.parstagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.io.Serializable;

@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_AUTHOR = "author";

    public Post() {};

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }
}
