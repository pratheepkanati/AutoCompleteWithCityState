

package in.androidhut.autocompletewithcitystate;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LocationPicker extends AppCompatActivity {
    GecoderUtil gecoderUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        gecoderUtil=new GecoderUtil(this);

        final TextInputEditText textInputLayout=findViewById(R.id.currentLoaction);
        textInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Progress if you want
                gecoderUtil.getLocation(new GecoderUtil.GeocoderCallback() {
                    @Override
                    public void geocodeResult(Address address, Location location, String fullAddress) {
                        //dismiss progress
                        textInputLayout.setText(fullAddress);
                    }

                    @Override
                    public void geoCoderNotAvailable(String errorMessage) {
// do somting on error
                    }
                });
            }
        });


    }






}


