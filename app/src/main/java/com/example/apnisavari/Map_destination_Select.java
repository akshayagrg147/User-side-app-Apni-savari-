package com.example.apnisavari;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Helper.CustoInfoWindow;
import com.example.apnisavari.Model.Rider;
import com.example.apnisavari.Model.Token;
import com.example.apnisavari.Remote.IFCMService;
import com.example.apnisavari.Remote.IGoogleApi;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class Map_destination_Select extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, ValueEventListener {
    PlacesClient placesClient;
    AlertDialog.Builder alertDialog;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    boolean IsTruck = true;
    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private LocationRequest mLocationRequest;
    private static Location mLastLocation;
    Marker mUserMarker;
    private TextView txtCountDown;
    IGoogleApi mService;
    AutocompleteSupportFragment places_lcation, places_destination;
    Button Destnation_sELECT;
    private static final int Limit = 3;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    IFCMService mServicee;
    String name, rates, profilePic;
    View mapView;
    AutocompleteFilter autocompleteFilter;
    ProgressDialog mdialog;
ImageView imageView2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        rates = intent.getStringExtra("rating");
        profilePic = intent.getStringExtra("profile");
        mdialog=new ProgressDialog(this,R.style.SpotsDialogDefault);
        mdialog.setTitle("   ApniSavari");
        mdialog.setMessage("Data loading...");
        mdialog.setCancelable(false);
        mdialog.show();


        setContentView(R.layout.activity_map_destination__select);


        LocationManager ls = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            else
            {
                Log.d("hh","h");
            }
        }
        ls.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.onLocationChanged(null);
        txtCountDown =  findViewById(R.id.circular_view_with_timer);

        final Toolbar toolbar = findViewById(R.id.toolbar1);
        alertDialog = new AlertDialog.Builder(Map_destination_Select.this);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        Destnation_sELECT = findViewById(R.id.btnPickupRequest);

        autocompleteFilter=new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setTypeFilter(3).build();
        setSupportActionBar(toolbar);
        mServicee = Common.getFCMsService();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        NavigationView navigationView = findViewById(R.id.nav_view1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeadeView = navigationView.getHeaderView(0);
        TextView txtName = navigationHeadeView.findViewById(R.id.txtDriverName1);
        final CircleImageView imageUpdate = navigationHeadeView.findViewById(R.id.profileAks1);
        try {
            if (!Common.currentUser.getName().isEmpty())
                txtName.setText(Common.currentUser.getName());
            else
                txtName.setText(name);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
try {
    FirebaseDatabase.getInstance().getReference("RiderInformation").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();

            Picasso.get()
                    .load(data.get("profileAks"))
                    .into(imageUpdate);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
catch(Exception e)
{
    e.getMessage();
}
     mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mapView = mapFragment.getView();
        } else {
            Log.e("Map Destination Select", "mapFragement Null");
        }

        setUpLocaton();
        initPlaces();
        setUpAutoComplete();
        updateFirebaseToken();

        Destnation_sELECT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] destination1 = new String[1];
                final DatabaseReference Destination = FirebaseDatabase.getInstance().getReference(Common.destination_request_tb1);
                Destination.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                                    destination1[0] = childsnap.child("destination").getValue(String.class);
                                }
                                if(destination1[0] != null) {
                                    if (!destination1[0].isEmpty()) {


                                       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                       transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                      transaction .replace(R.id.mainfra,new choosevechile()).addToBackStack(null).commit();

                                        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
                                        drawer.closeDrawer(GravityCompat.START);
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);


                                    } else {
                                        new SweetAlertDialog(Map_destination_Select.this, SweetAlertDialog.ERROR_TYPE).setTitleText("..oops..!")
                                                .setTitleText("Destination not Selected").show();
                                    }
                                } else {
                                    Log.d("Map" ,"destination is null");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
            }
        });
    }


    private void setUpAutoComplete() {
        places_lcation = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_auto_complete1);
        if (places_lcation != null) {
            places_lcation.setPlaceFields(placeFields);
        }
        else
        {
            Log.d("place locaton","place location is null");
        }
        if (places_lcation != null) {
            places_lcation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    try {
                        Common.mPlaceLocation = place.getName().toString();
                        Geocoder gc = new Geocoder(Map_destination_Select.this);
                        List<Address> list = gc.getFromLocationName(Common.mPlaceLocation, 1);
                        Address add = list.get(0);
                        String locality = add.getLocality();
                        double lat = add.getLatitude();
                        double lng = add.getLongitude();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15.0f));
                        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {
                                LatLng center1 = mMap.getCameraPosition().target;
                                if (mUserMarker != null) {
                                    mUserMarker.remove();
                                    mMap.clear();

                                    places_lcation.setText(getStringAddress(center1.latitude, center1.longitude));
                                    DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.starting_location);
                                    Rider rider = new Rider();
                                    rider.setDestination(getStringAddress(center1.latitude, center1.longitude));
                                    rider.setDesLat(center1.latitude);
                                    rider.setDesLng(center1.longitude);
                                    Common.StrtingLati = center1.latitude;
                                    Common.StrtingLngi = center1.longitude;
                                    try {
                                        dbrequest.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });








                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } }
                            }

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(Map_destination_Select.this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        places_destination = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_destination);
        if (places_destination != null) {
            places_destination.setPlaceFields(placeFields);
        }
        else
        {
            Log.d("place destination","place destination is null");
        }
        places_destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                try {

                    Common.MplaceDestination = place.getAddress().toString();
                    //  address=Common.MplaceDestination;
                    Geocoder gc = new Geocoder(Map_destination_Select.this);
                    List<Address> list = gc.getFromLocationName(Common.MplaceDestination, 1);
                    Address add = list.get(0);
                    String locality = add.getLocality();
                    double lat = add.getLatitude();
                    double lng = add.getLongitude();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15.0f));
                    mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                        @Override
                        public void onCameraIdle() {
                            LatLng center = mMap.getCameraPosition().target;
                            if (mUserMarker != null) {
                                mUserMarker.remove();
                               // mMap.clear();
                                places_destination.setText(getStringAddress(center.latitude, center.longitude));
                                DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.destination_request_tb1);
                                Rider rider = new Rider();
                                rider.setDestination(getStringAddress(center.latitude, center.longitude));
                                rider.setDesLat(center.latitude);
                                rider.setDesLng(center.longitude);
                                Common.endingLati=center.latitude;
                                Common.endingLngi=center.longitude;
                                dbrequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                                                          }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(Map_destination_Select.this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public String getStringAddress(double latitude, double longitude) {
        String address = "";
        String city = "";
        Geocoder geocoder;

        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address + " " + city;
    }

    private void initPlaces() {
        Places.initialize(Map_destination_Select.this, getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }
    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_Tb1);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
try {
    tokens.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
            .setValue(token);
} catch (Exception e) {
    e.printStackTrace();
}

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildLocationCallback();
                displayLocation();
            }
        }
    }


    private void setUpLocaton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
            displayLocation();


        } else {
            buildLocationCallback();
            displayLocation();
        }

    }


    private void buildLocationCallback() {
         locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();

                Common.mLastLocation = locationResult.getLocations().get(locationResult.getLocations().size() - 1);


                displayLocation();
            }
        };
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Common.mLastLocation = location;

                if (Common.mLastLocation != null) {

                    RectangularBounds bound=RectangularBounds.newInstance(new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude()),(new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude())));
                    places_lcation.setLocationBias(bound);
                  places_lcation.setTypeFilter(TypeFilter.ADDRESS);
                    places_destination.setLocationBias(bound);
                    places_destination.setTypeFilter(TypeFilter.ADDRESS);


                    final double latitude = Common.mLastLocation.getLatitude();
                    final double longitude = Common.mLastLocation.getLongitude();
                    if (mUserMarker != null)
                        mUserMarker.remove();
                    mUserMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude)).title("you"));
                    CameraPosition cameraPosition=new CameraPosition.Builder().target(new LatLng(latitude,longitude)).zoom(17).bearing(90).tilt(40).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    places_lcation.setText(getStringAddress(latitude, longitude));
                    DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.starting_location);
                    Rider rider = new Rider();
                    rider.setDestination(getStringAddress(latitude, longitude));
                    rider.setDesLat(latitude);
                    rider.setDesLng(longitude);
                    Common.StrtingLati = latitude;
                    Common.StrtingLngi = longitude;
                    try {
                        dbrequest.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                        @Override
                        public void onCameraIdle() {
                            LatLng center1 = mMap.getCameraPosition().target;
                            if (mUserMarker != null) {
                                mUserMarker.remove();
                                mMap.clear();

                                places_lcation.setText(getStringAddress(center1.latitude, center1.longitude));
                                DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.starting_location);
                                Rider rider = new Rider();
                                rider.setDestination(getStringAddress(center1.latitude, center1.longitude));
                                rider.setDesLat(center1.latitude);
                                rider.setDesLng(center1.longitude);
                                Common.StrtingLati = center1.latitude;
                                Common.StrtingLngi = center1.longitude;
                                try {
                                    dbrequest.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });


                                } catch (Exception e) {
                                    e.printStackTrace();
                                } }
                        }

                    });

                    mUserMarker.remove();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
                   mdialog.dismiss();
                    Log.d("endd", String.format("Yor location was changed: %f/%f", latitude, longitude));


                } else
                    Log.d("ERROR", "can not get your location");


            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_destination__select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {



        int id = item.getItemId();

        if (id == R.id.nav_trip_history) {
            final  android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(Map_destination_Select.this);
            dialog.setTitle("Trips");
            dialog.setMessage("Choose History");
            LayoutInflater inflater=LayoutInflater.from(Map_destination_Select.this);
            View history=inflater.inflate(R.layout.choosehistory,null);
            dialog.setView(history);

            final RadioButton Cancelled=history.findViewById(R.id.Cancelled);
            final RadioButton Completed=history.findViewById(R.id.Completed);
            final Button   ChooseHistory=history.findViewById(R.id.ChooseHistory);
            ChooseHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Cancelled.isChecked())
                    {
                        Intent ab=new Intent(Map_destination_Select.this,CancelledHistoryTrips.class);
                        startActivity(ab);


                    }
                    else {
                        Intent ab=new Intent(Map_destination_Select.this,HistoryCustomer.class);
                        startActivity(ab);
                    }
                }
            });
            dialog.show();






        }  else if (id == R.id.nav_help) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainfra,new contactinfo()).commit();

        } else if (id == R.id.share) {
           // shareTextUrl();
            fn();

        } else if (id == R.id.nav_change_password) {
            changePassword();

        } else if (id == R.id.nav_sign_out1) {
            signout();

        } else if (id == R.id.nav_update_info) {
            showDialogUpdateInformation();




        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fn() {
        FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Rider driverUser = dataSnapshot.getValue(Rider.class);
                String str=driverUser.getSwitchingSystem().toString();
                Toast.makeText(Map_destination_Select.this, "=="+str.toLowerCase(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void changePassword() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Map_destination_Select.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all Informations");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout_infor = inflater.inflate(R.layout.changepassword, null);
        final MaterialEditText password1 = layout_infor.findViewById(R.id.changePassword1);
        final MaterialEditText password2 = layout_infor.findViewById(R.id.changePassword2);

        alertDialog.setView(layout_infor);


        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();


                String password11 = password1.getText().toString();
                String password12 = password2.getText().toString();
                if(password11.equals(password12)) {
                    Map<String, Object> updateInfo = new HashMap<>();
                    if (!TextUtils.isEmpty(password11))
                        updateInfo.put("password", password11);

                    DatabaseReference driverinformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tb1);
                    driverinformation.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .updateChildren(updateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(Map_destination_Select.this, "Information Updated", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(Map_destination_Select.this, "Upload failed !", Toast.LENGTH_SHORT).show();


                        }
                    });
                }
                else
                {
                    new SweetAlertDialog(Map_destination_Select.this, SweetAlertDialog.ERROR_TYPE).setTitleText("..oops..!")
                            .setTitleText("Password Mismatch").show();

                }


            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }






    private void shareTextUrl() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ApniSavari");
            String shareMessage = "\nA new way of secure riding\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void showDialogUpdateInformation() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Map_destination_Select.this);
        alertDialog.setTitle("Update Information");
        alertDialog.setMessage("Please fill all Informations");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout_infor = inflater.inflate(R.layout.layout_update_information, null);
        final MaterialEditText edtName = layout_infor.findViewById(R.id.edtName);

        final Button image_upload = layout_infor.findViewById(R.id.image_upload);
        alertDialog.setView(layout_infor);
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();


                String name = edtName.getText().toString();

                Map<String, Object> updateInfo = new HashMap<>();
                if (!TextUtils.isEmpty(name))
                    updateInfo.put("name", name);

                DatabaseReference driverinformation = FirebaseDatabase.getInstance().getReference(Common.user_driver_tb1).child(IsTruck ? "Bike" : "Truck");
                driverinformation.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .updateChildren(updateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(Map_destination_Select.this, "Information Updated", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Map_destination_Select.this, "Upload failed !", Toast.LENGTH_SHORT).show();


                    }
                });


            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictuee"), Common.Pick_Image_request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Common.Pick_Image_request && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Toast.makeText(Map_destination_Select.this, "--" + data, Toast.LENGTH_SHORT).show();
            Uri saveUri = data.getData();
            if (saveUri != null) {
                final ProgressDialog mDialog = new ProgressDialog(Map_destination_Select.this);
                mDialog.setMessage("Uploading....");
                mDialog.show();
                String imageNmae = UUID.randomUUID().toString();
                final StorageReference imagefolder = storageReference.child("image/" + imageNmae);
                imagefolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mDialog.dismiss();
                        Toast.makeText(Map_destination_Select.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> profileupdate = new HashMap<>();
                                profileupdate.put("profileAks", uri.toString());
                                DatabaseReference driverinformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tb1);
                                driverinformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .updateChildren(profileupdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                            Toast.makeText(Map_destination_Select.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(Map_destination_Select.this, "Upload error !", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Uploaded" + progress + "%");
                    }
                });
            }
        }
    }

    private void signout() {
         Paper.init(this);
        Paper.book().destroy();
        FirebaseAuth.getInstance().signOut();
        SharedPreferences preferences=getSharedPreferences("DriverInfo",Context.MODE_PRIVATE);
        preferences.edit().remove("driverId").commit();


        Intent intent = new Intent(Map_destination_Select.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

       /* try {
            boolean isSuccess = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.json_map_mode)
                     );


            if (!isSuccess)
                Log.e("Error", "Map style load failed");

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        */

        mMap = googleMap;
        mMap.setMinZoomPreference(12);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustoInfoWindow(this));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 300);
        }
if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        return;
    }
}
else
{
    Log.d("hh","gg");
}
        mMap.setMyLocationEnabled(true);


        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center1=mMap.getCameraPosition().target;
                if(mUserMarker!=null)
                {
                    mUserMarker.remove();
                    mMap.clear();
                     mMap.addCircle(new CircleOptions()
                            .center(new LatLng(center1.latitude,center1.longitude))
                             .strokeColor(Color.BLACK)
                             .fillColor(0x30ff0000)
                             .strokeWidth(1)
                            .radius(250)

                            );


                    places_lcation.setText(getStringAddress(center1.latitude, center1.longitude));
                    Common.StrtingLati=center1.latitude;
                    Common.StrtingLngi=center1.longitude;
                    DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.starting_location);
                    Rider rider = new Rider();
                    rider.setDestination(getStringAddress(center1.latitude, center1.longitude));
                    rider.setDesLat(center1.latitude);
                    rider.setDesLng(center1.longitude);
                    dbrequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rider).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });




                }


            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissioaans

                return;
            }
        }
        else
        {
            Log.d("hh","hhh");
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }












    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    protected void onDestroy() {
         super.onDestroy();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    static int i=1;

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null)
        {if(i==1) {
            float mcurentspeed = location.getSpeed();
            Toast.makeText(this, "" + mcurentspeed + "kb/s", Toast.LENGTH_SHORT).show();
            if(mcurentspeed<0.1)
            {

            }
            ++i;
        }
        }
        else {
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
