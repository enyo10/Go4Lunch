package ch.enyo.openclassrooms.go4lunch.models.firebase;



public class LikeData {

    private String likeDataId;
    private double likeSum;

    private LikeData(){

    }
    private LikeData(String likeDataId){
        this.likeDataId=likeDataId;
        this.likeSum=1;
    }

    public double getLikeSum(){
        return this.likeSum;
    }

    public  String getLikeDataId(){
        return this.likeDataId;
    }

    public void like(){
        ++this.likeSum;
    }
}
