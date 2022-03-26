package ch.enyoholali.openclassrooms.go4lunch.api;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class ChatHelper {
    private static final String COLLECTION_NAME ="chats";
    // Collection reference
    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }


}
