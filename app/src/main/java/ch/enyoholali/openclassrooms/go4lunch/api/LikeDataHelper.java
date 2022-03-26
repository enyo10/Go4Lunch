package ch.enyoholali.openclassrooms.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ch.enyoholali.openclassrooms.go4lunch.models.firebase.LikeData;


/**
 * This class help make the CRUD of likes.
 */
public class LikeDataHelper {
    private static final String COLLECTION_NAME = "likes";



    // --- COLLECTION REFERENCE ---

    public static CollectionReference getLikeDataCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createLikeData(String placeId) {
        // 1 - Create LikeData object
        LikeData likeDataToCreate = new LikeData(placeId);
        // 2 - Add a new like data Document to Firestore
        return LikeDataHelper.getLikeDataCollection()
                .document(placeId) // Setting uID for Document
                .set(likeDataToCreate); // Setting object for Document
    }
    // --- GET ---

    public static Task<DocumentSnapshot> getLikeData(String uid){
        return LikeDataHelper.getLikeDataCollection().document(uid).get();
    }

    public static Query getAllLikeData() {
        return LikeDataHelper.getLikeDataCollection().orderBy("likeSum").limit(20);
    }

    // --- UPDATE ---

    public static Task<Void> updateLikeData(String uid) {
        return LikeDataHelper.getLikeDataCollection().document(uid).update("likeSum", FieldValue.increment(1));
    }



    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return LikeDataHelper.getLikeDataCollection().document(uid).delete();
    }

}
