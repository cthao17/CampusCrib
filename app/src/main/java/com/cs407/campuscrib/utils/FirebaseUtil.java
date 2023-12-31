package com.cs407.campuscrib.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

import io.grpc.Context;

public class FirebaseUtil {

    public static String currentUser() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static CollectionReference allUsers() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomRef(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessagesRef(String chatroomId) {
        return getChatroomRef(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatRef() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static CollectionReference allPersonalListingRef() {
        return FirebaseFirestore.getInstance().collection("personalListing");
    }

    public static DocumentReference getOtherUserFromChat(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUser())) {
            return allUsers().document(userIds.get(1));
        } else{
            return allUsers().document(userIds.get(0));
        }
    }

    public static String timeStamptoString (Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static StorageReference getCurrentProfilePicsRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(currentUser());
    }

    public static StorageReference getOtherProfilePicsRef(String otherUser) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUser);
    }

    public static StorageReference getListingImagesRef() {
        return FirebaseStorage.getInstance().getReference().child("listing_images");
    }

    public static StorageReference getSavedListingImagesRef() {
        return FirebaseStorage.getInstance().getReference().child("savedListing_images")
                .child(currentUser());
    }

    public static StorageReference getPersonalListingImageRef() {
        return FirebaseStorage.getInstance().getReference().child("listing_images")
                .child(currentUser());
    }

    public static StorageReference getMainListingImageRef(String Uid) {
        return FirebaseStorage.getInstance().getReference().child("listing_images")
                .child(Uid);
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUser());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static boolean isLoggedIn(){
        if(currentUser()!=null){
            return true;
        }
        return false;
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
}
