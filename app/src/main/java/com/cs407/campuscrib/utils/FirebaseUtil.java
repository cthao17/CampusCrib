package com.cs407.campuscrib.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUser() {return FirebaseAuth.getInstance().getUid();}

    public static CollectionReference allUsers() {
        return FirebaseFirestore.getInstance().collection("users");
    }
}
