package in.androidhut.autocompletewithcitystate;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class GecoderUtil
{
    private FusedLocationProviderClient mFusedLocationClient;
    private AddressResultReceiver mResultReceiver;
    private Activity activity;
    private GeocoderCallback geocoderCallback;

    public  GecoderUtil(Activity activity){
        this.activity=activity;
        mResultReceiver = new AddressResultReceiver(new Handler());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }
    public void getLocation(final GeocoderCallback geocoderCallback){
        this.geocoderCallback=geocoderCallback;

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            geocoderCallback.geoCoderNotAvailable("Location not avialble");
                            return;
                        }
                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            geocoderCallback.geoCoderNotAvailable("Geo Coder Not avialble");
                            return;
                        }
                        startIntentService(location);
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      geocoderCallback.geoCoderNotAvailable(e.getMessage());
                    }
                });
    }
    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService(Location mLastLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(activity, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        activity.startService(intent);
    }
    public interface GeocoderCallback{
        void  geocodeResult(Address address,Location location,String fullAddress);
        void geoCoderNotAvailable(String errorMessage);
    }
    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    public class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
         if(resultCode==Constants.SUCCESS_RESULT){
             Address address= resultData.getParcelable(Constants.ADDRESS);
             Location location= resultData.getParcelable(Constants.LOCATION_DATA_EXTRA);
             String addressTe=resultData.getString(Constants.RESULT_DATA_KEY);
             geocoderCallback.geocodeResult(address,location,addressTe);

         }
         else {
             geocoderCallback.geoCoderNotAvailable("Error");
         }

        }
    }
}
