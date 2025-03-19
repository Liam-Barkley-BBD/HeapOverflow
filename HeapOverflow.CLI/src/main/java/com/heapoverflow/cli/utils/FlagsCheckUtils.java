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
        if (get) selectedFlags.add("get");
        if (post) selectedFlags.add("post");
        if (edit) selectedFlags.add("edit");
        if (delete) selectedFlags.add("delete");

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
        if (unupvote) selectedFlags.add("unupvote");

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
        if (logout) selectedFlags.add("logout");
        if (gid) selectedFlags.add("gid");
        if (name) selectedFlags.add("name");
        if (jwt) selectedFlags.add("jwt");

        return selectedFlags;
    }

    public static List<String> ensureOnlyOneFlagIsSetForUser(
        boolean list, 
        boolean get
    ){
        return ensureOnlyOneFlagIsSetForReplies(list, get, false, false, false);
    }
    
}
