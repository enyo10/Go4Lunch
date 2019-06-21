package ch.enyo.openclassrooms.go4lunch.api;

import com.google.firebase.firestore.Query;

public class MessageHelper {
    private static final String COLLECTION_NAME="messages";

    //GET
    private static Query getGetAllMessagesForChat(String chat){
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }
}
