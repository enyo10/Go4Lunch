package ch.enyo.openclassrooms.go4lunch.models.firebase;



public class LikeData {

    private String likeDataId;
    private double likeSum;

    private LikeData(){

    }
    public LikeData(String likeDataId){
        this.likeDataId=likeDataId;

    }

    public double getLikeSum(){
        return this.likeSum;
    }

    public  String getLikeDataId(){
        return this.likeDataId;
    }

    public void likeIt(){
        ++this.likeSum;
    }
}
