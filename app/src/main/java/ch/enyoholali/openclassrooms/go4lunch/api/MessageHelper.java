package ch.enyoholali.openclassrooms.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import ch.enyoholali.openclassrooms.go4lunch.models.chat.Message;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;

public class MessageHelper {
    private static final String COLLECTION_NAME="messages";

    //GET
    public static Query getGetAllMessagesForChat(String chat){
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    public static Task<DocumentReference> createMessageForChat(String textMessage, String chat, User userSender){

        // 1 - Create the Message object
        Message message = new Message(textMessage, userSender);

        // 2 - Store Message to Firestore
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }
}
