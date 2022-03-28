package ch.enyoholali.openclassrooms.go4lunch.controllers.activities;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.LikeDataHelper;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyoholali.openclassrooms.go4lunch.data.DataFormatter;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.databinding.ActivityPlaceDetailsBinding;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyoholali.openclassrooms.go4lunch.utils.NotificationAlarmReceiver;
import ch.enyoholali.openclassrooms.go4lunch.views.WorkmatesViewsAdapter;

public class PlaceDetailsActivity extends BaseActivity<ActivityPlaceDetailsBinding> implements DataFormatter {
    private static final String TAG = PlaceDetailsActivity.class.getSimpleName();
    public static final String RESTAURANT_NAME="RestaurantName";
    public static final String BUNDLE_CONTENT_URL="BUNDLE_WEB_CONTENT_URL";


    // For DATA
    RequestManager glide;
    private String placeId;
    private boolean restaurantSelected;

    private WorkmatesViewsAdapter mWorkmatesViewsAdapter;
    private List<User>mSubscriberList;
    private PlaceDetails mPlaceDetails;
   // private List<PlaceAutoComplete>mPlaceDetailsList;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private User mUser;


    @Override
    public void configureView() {
        getSubscribersFromFireBase();
        mSubscriberList=new ArrayList<>();
        mPlaceDetails = DataSingleton.getInstance().getPlaceDetails();
        placeId=mPlaceDetails.getResult().getPlaceId();

        glide = Glide.with(this);
        String  url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=";
        String apiKey = "&key=" + "AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";
       // For notification.
        createNotificationChannel();

        if(mPlaceDetails!=null)
        showPlaceDetails(mPlaceDetails,url,apiKey);
        configureRecyclerView();
        configureSwipeRefreshLayout();
        configureAlarmManager();


        binding.restaurantLikeButton.setOnClickListener(view -> liked());

        binding.floatingActionButton.setOnClickListener(view -> { });

        binding.restaurantWebsiteButton.setOnClickListener(view -> getRestaurantWebsite());

        binding.restaurantPhoneButton.setOnClickListener(view -> callRestaurant());

    }

    @Override
    public void loadData() {

    }

    @Override
    protected ActivityPlaceDetailsBinding getViewBinding() {
        return ActivityPlaceDetailsBinding.inflate(getLayoutInflater());
    }

    private void updateFloatingButton(){
        Log.d(TAG, " user "+ mUser);
        Log.d(TAG, " is selected " +isRestaurantSelected());
        if(isRestaurantSelected()){
           // mFloatingActionButton1.setAlpha(0f);
            binding.floatingActionButton1.setAlpha(0f);
           // mFloatingActionButton.setAlpha(1f);
            binding.floatingActionButton.setAlpha(1f);
        }

        else{
           // mFloatingActionButton1.setAlpha(1f);
            binding.floatingActionButton1.setAlpha(1f);
           // mFloatingActionButton.setAlpha(0f);
            binding.floatingActionButton.setAlpha(0f);
        }

    }

    private void showPlaceDetails(PlaceDetails placeDetails,String url,String apiKey){
        if ( placeDetails.getResult() != null) {
            // Retrieve the place details id.
       //     placeId=placeDetails.getResult().getPlaceId();
            //Restaurant name
            binding.restaurantName.setText(placeDetails.getResult().getName());
            //Restaurant photo
            if (placeDetails.getResult().getPhotos() != null) {
                String imageUrl=url + placeDetails.getResult().getPhotos().get(0).getPhotoReference() + apiKey;
                // String imageUrl = placeDetails.getImageUrl();
                glide.load(imageUrl)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.restaurantImage);
            }
            // Address
            binding.restaurantAddress.setText(formatAddress(placeDetails.getResult().getFormattedAddress()));
            if (placeDetails.getResult().getRating() != null) {
                binding.restaurantRatingBar.setRating(formatRating(placeDetails.getResult().getRating()));
            }
            // Distance
           /* if (lat != 0 && lng != 0) {
                this.mRestaurantDistance.setText(computeDistance(lat, placeDetails.getResult().getGeometry().getLocation().getLat(),
                        lng, placeDetails.getResult().getGeometry().getLocation().getLng()));
            }*/
            //Opening time
            /*if (placeDetails.getResult().getOpeningHours() != null) {
                this.mRestaurantOpening.setText(formatWeekDayText(placeDetails.getResult().getOpeningHours()
                        .getWeekdayText()));

            }*/

            String phoneNumber = placeDetails.getResult().getInternationalPhoneNumber();
            String webAddress = placeDetails.getResult().getWebsite();
            double rating = placeDetails.getResult().getRating();

        }

    }


    private void updateSubscriberList(List<User>subscriberList){

        binding.placeDetailRecyclerView.swipeContainer.setRefreshing(false);
        this.mSubscriberList.clear();
        this.mSubscriberList.addAll(subscriberList);
        this.mWorkmatesViewsAdapter.notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------
    //                                 ACTION
    //----------------------------------------------------------------------------------------------

    /**
     * This method to refresh the layout.
     */
    protected void configureSwipeRefreshLayout(){
        //getAllActiveUsersFromFireBase();
        binding.placeDetailRecyclerView.swipeContainer.setOnRefreshListener(this::getSubscribersFromFireBase);
    }

    public void liked(){
        Log.i(TAG, " on liked");
        String placeId=this.placeId;
        Log.i(TAG,"placeId "+placeId);
       LikeDataHelper.createLikeData(placeId).addOnFailureListener(this.onFailureListener());
       addLikedPlaceToFireBase();

    }


    /**
     * This method check if the current user has selected a restaurant.
     * @return boolean,
     */
    private boolean isRestaurantSelected(){

        //return mUser != null && mUser.getRestaurantId() != null;
        if(mUser!=null && mUser.getRestaurantId()!=null)
            return mUser.getRestaurantId().equals(placeId);
        return false;

    }

    public void selectRestaurant(){
       //restaurantSelected=isRestaurantSelected();

        if(mUser.getRestaurantId()!=null){
            addSelectedRestaurantIdToFireBase(null);
            stopAlarm();
            Log.i(TAG, "Restaurant with id : "+placeId+"  removed");

        }
        else{
            addSelectedRestaurantIdToFireBase(mPlaceDetails.getResult().getPlaceId());

            scheduleAlarm();
            Log.i(TAG, "Restaurant with id : "+mPlaceDetails.getResult().getName()+"  selected");
        }
       updateFloatingButton();

    }

    public void getRestaurantWebsite(){
        Log.d(TAG, " Website button clicked  --");
        String website =mPlaceDetails.getResult().getWebsite();
        if(website!=null){
            Intent intent=new Intent(this, WebContentActivity.class);
            intent.putExtra(BUNDLE_CONTENT_URL,website);
            startActivity(intent);
            Log.d(TAG, " website "+ website);
        } else {
            Toast.makeText(this,"Do not provide website",Toast.LENGTH_LONG).show();
        }


    }

    public void callRestaurant(){
        Log.d(TAG, " call number "+mPlaceDetails.getResult().getFormattedPhoneNumber());
        Toast.makeText(this,"phone number "+ mPlaceDetails.getResult().getFormattedPhoneNumber(),Toast.LENGTH_LONG).show();

    }


    // --------------------
    // REST REQUESTS
    // --------------------

    /**
     * This method to retrieve all user that select the given restaurant.
     */
    private void getSubscribersFromFireBase(){


        UserHelper.getAllUsers().addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Handle error
                Log.i(TAG, " Error by retrieve user from fire base-->: "+e.getMessage());
                e.printStackTrace();
                return;
            }
            // Convert query snapshot to a list of users.
            assert snapshot != null;
            List<User> userList = snapshot.toObjects(User.class);
            List<User>subscribers=new ArrayList<>();
            for (int i=0;i<userList.size();i++) {
                if(!Objects.requireNonNull(getCurrentUser()).getUid().equals(userList.get(i).getUid())) {
                    if(placeId.equals(userList.get(i).getRestaurantId()))
                    subscribers.add(userList.get(i));

                }
                else {
                   // DataSingleton.getInstance().setActuelUser(userList.get(i));
                    mUser=userList.get(i);
                    Log.d(TAG, " current user "+mUser.getUsername());
                    Log.d(TAG, "current user hss selected " +isRestaurantSelected());
                    updateFloatingButton();

                }

            }
              /* for(int k=0;k<list.size();k++){
                   if(getCurrentUser().getUid().equals(list.get(k).getUid())){
                       mUser=list.get(k);
                       Log.d(TAG, " current User "+mUser.getUsername());
                   }
                   else {
                       if(list.get(k).getRestaurantId().equals(placeId)){
                           subscribers.add(list.get(k));

                       }
                   }
               }*/
              updateSubscriberList(subscribers);

            Log.d(TAG," subscribers size  "+mSubscriberList.size());
        });

    }


    public void addLikedPlaceToFireBase(){
        if(this.isCurrentUserLogged()){
        // create the like data to the firebase if not yet create.
        LikeDataHelper.createLikeData(placeId).addOnFailureListener(this.onFailureListener());

        // Update the like data.
        LikeDataHelper.updateLikeData(placeId).addOnFailureListener(this.onFailureListener());
        Log.i(TAG, " place with id : "+placeId + "   inserted");
        }
        Toast.makeText(getApplicationContext(), getString(R.string.likeSubmit), Toast.LENGTH_LONG).show();

    }

    /**
     * This method add a selected restaurant to fire store.
     * @param placeId,
     *        the id of the restaurant to be add.
     */
    public void addSelectedRestaurantIdToFireBase(String placeId){
        if(this.getCurrentUser()!=null){
        UserHelper.updateRestaurantSelection(this.getCurrentUser().getUid(),placeId).addOnFailureListener(this.onFailureListener());
        }
    }

    public void configureRecyclerView(){
        RecyclerView recyclerView =  binding.placeDetailRecyclerView.recyclerViewId;
        this.mWorkmatesViewsAdapter = new WorkmatesViewsAdapter(mSubscriberList, Glide.with(this),this,2);

       recyclerView.setAdapter(mWorkmatesViewsAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Log.i(TAG, " Recycler view configured ");
    }

    //----------------------------------------------------------------------------------------------
    //              ALARM MANAGEMENT
    //----------------------------------------------------------------------------------------------

    /**
     * Configure the alarm manager.
     */
    private void configureAlarmManager(){
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationAlarmReceiver.class);
        String address= formatAddress(mPlaceDetails.getResult().getFormattedAddress());
        intent.putExtra(RESTAURANT_NAME,address);
        mPendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    /**
     * This method to schedule the alarm that will fire the notification.
     */
    public void scheduleAlarm() {
        // set alarm to wakeup the device at 12 o'clock.
        /* Calendar calendar =Calendar.getInstance ();
        calendar.setTimeInMillis (System.currentTimeMillis ());
        calendar.set(Calendar.HOUR_OF_DAY,12);
        AlarmManager alarmManager=(AlarmManager) getSystemService (Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent   alarm_Intent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating (AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis (),AlarmManager.INTERVAL_DAY,alarm_Intent);

       */

            // SetRepeating() lets you specify a precise custom interval--in this case, 2 minutes.
            long time = new GregorianCalendar().getTimeInMillis() + 2 * 60 * 1000;

            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time,
                    1000 * 60 * 2, mPendingIntent);
            this.callToast("Alarm started ...");

    }

    /**
     * This method create a notification channel.
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is  not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = getString(R.string.channel_name);
            //String description = getString(R.string.channel_description);
            CharSequence name = "Channel";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Chanel_id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /**
     * Cancel the alarm.
     */
    private void stopAlarm(){
       // mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(mPendingIntent);
        this.callToast("Alarm stop ...");

    }

    // This method to display a toast message.
    private void callToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT);

        toast.show();
    }


}
