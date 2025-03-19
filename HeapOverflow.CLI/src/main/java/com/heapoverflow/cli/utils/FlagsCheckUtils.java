package com.heapoverflow.cli.utils;

import java.util.ArrayList;
import java.util.List;

public class FlagsCheckUtils {
    public static List<String> ensureOnlyOneFlagIsSetForReplies(
        boolean list, 
        boolean get, 
        boolean post, 
        boolean edit, 
        boolean delete
    ){
        List<String> selectedFlags = new ArrayList<>();
        if (list) selectedFlags.add("list");
        else if (get) selectedFlags.add("get");
        else if (post) selectedFlags.add("post");
        else if (edit) selectedFlags.add("edit");
        else if (delete) selectedFlags.add("delete");

        return selectedFlags;
    }

    public static List<String> ensureOnlyOneFlagIsSetForComments(
        boolean list, 
        boolean get, 
        boolean post, 
        boolean edit, 
        boolean delete,
        boolean upvote,
        boolean unupvote
    ){
        List<String> selectedFlags = ensureOnlyOneFlagIsSetForReplies(list, get, post, edit, delete);
        if (upvote) selectedFlags.add("upvote");
        else if (unupvote) selectedFlags.add("unupvote");

        return selectedFlags;
    }

    public static List<String> ensureOnlyOneFlagIsSetForAuth(
        boolean login, 
        boolean logout, 
        boolean gid, 
        boolean name, 
        boolean jwt
    ){
        List<String> selectedFlags = new ArrayList<>();
        if (login) selectedFlags.add("login");
        else if (logout) selectedFlags.add("logout");
        else if (gid) selectedFlags.add("gid");
        else if (name) selectedFlags.add("name");
        else if (jwt) selectedFlags.add("jwt");

        return selectedFlags;
    }

    public static List<String> ensureOnlyOneFlagIsSetForUser(
        boolean list, 
        boolean get
    ){
        return ensureOnlyOneFlagIsSetForReplies(list, get, false, false, false);
    }
    
}
