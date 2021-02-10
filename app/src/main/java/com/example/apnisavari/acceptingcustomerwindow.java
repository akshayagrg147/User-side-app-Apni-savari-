package com.example.apnisavari;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Helper.CustoInfoWindow;
import com.example.apnisavari.Model.DataMessage;
import com.example.apnisavari.Model.FCMResponse;
import com.example.apnisavari.Model.Rider;
import com.example.apnisavari.Model.Token;
import com.example.apnisavari.Remote.IFCMService;
import com.example.apnisavari.Remote.IGoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class acceptingcustomerwindow extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, ValueEventListener {
    ProgressDialog mdialog;

    double lati, lngi;
    private static final long START_TIME_IN_MILLIS = 330000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    AlertDialog.Builder alertDialog;
    private Polyline direction;
    //file store in storage
    SharedPreferences sharedPreferences;
    public static final String MyPreference = "DriverInfo";
    public static final String driverId = "driverId";
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    boolean IsTruck = true;
    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private LocationRequest mLocationRequest;
    private static Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int Displacement = 10;
    Marker mUserMarker, mdriverMaarker;
    private TextView txtCountDown;
    IGoogleApi mService;
    Button btnPickUpRequest;
    private static final int Limit = 3;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    IFCMService mServicee;
    DatabaseReference driverAvailables;
    Button cancelRequest;
    CircleImageView avtarimage;
    TextView txt_name, txt_vechile_num, txt_rate;
    ImageButton btn_call_driver;
    TextView otpcall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptingcustomerwindow);
        mdialog=new ProgressDialog(acceptingcustomerwindow.this,R.style.SpotsDialogDefault);
        mdialog.setTitle("   ApniSavari");
        mdialog.setMessage("Data loading...");
        mdialog.setCancelable(false);
        mdialog.show();
        sharedPreferences = getSharedPreferences(MyPreference, Context.MODE_PRIVATE);
        mService = Common.getGoogleApiService();
        avtarimage = (CircleImageView) findViewById(R.id.avatar_image);
        txt_name = findViewById(R.id.txtName);
        txt_vechile_num = findViewById(R.id.txt_vechile_num);
        otpcall=findViewById(R.id.otpnumber);
        txt_rate = findViewById(R.id.txt_rate);
        btn_call_driver = findViewById(R.id.btn_call_driver1);
        txtCountDown = (TextView) findViewById(R.id.circular_view_with_timer);
        cancelRequest = findViewById(R.id.CancelRequest);
        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBooking();
            }
        });
        final Toolbar toolbar = findViewById(R.id.toolbar);
        alertDialog = new AlertDialog.Builder(acceptingcustomerwindow.this);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final Double[] lat = new Double[1];
        final Double[] lng = new Double[1];
        final DatabaseReference Destination = FirebaseDatabase.getInstance().getReference(Common.destination_request_tb1);
        Destination.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot childsnap : dataSnapshot.getChildren()) {

                    lat[0] = childsnap.child("desLat").getValue(Double.class);
                    lng[0] = childsnap.child("desLng").getValue(Double.class);

                }
                lati = lat[0];

                lngi = lng[0];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.d("acceptingwindow map fragment", "map fragment is null");
        }
        setSupportActionBar(toolbar);
        mServicee = Common.getFCMsService();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeadeView = navigationView.getHeaderView(0);
        TextView txtName = navigationHeadeView.findViewById(R.id.txtDriverName);
        final CircleImageView imageUpdate = navigationHeadeView.findViewById(R.id.profileAks);
        try {
            if (!Common.currentUser.getName().isEmpty())
                txtName.setText(Common.currentUser.getName());
            else
                Log.d("acceptingwin", "error not finding nm");
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
        } catch (Exception e) {
            e.printStackTrace();
        }


        displayLocation();


        btnPickUpRequest = findViewById(R.id.btnPickupRequest);
        final long time=System.currentTimeMillis();
        String tim=String.valueOf(time).substring(String.valueOf(time).length()-4);

        otpcall.setText(tim);
        FirebaseDatabase.getInstance().getReference("otp").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(tim).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        });


        FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String driverid = dataSnapshot.child("switchingSystem").getValue(String.class);

                try {
                    if (!driverid.isEmpty()) {

                        FirebaseDatabase.getInstance().getReference(Common.user_driver_tb1).child(driverid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final Rider driverUser = dataSnapshot.getValue(Rider.class);
                                Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                                Picasso.get()
                                        .load(data.get("profileAks"))
                                        .into(avtarimage);
                                txt_name.setText(driverUser.getName());
                                txt_vechile_num.setText(driverUser.getVechilenumber());
                                txt_rate.setText(driverUser.getRates());
                                btn_call_driver.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        String phone = driverUser.getPhone();

                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));

                                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    Activity#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for Activity#requestPermissions for more details.
                                            return;
                                        }
                                        startActivity(intent);

                                    }
                                });
                            }

                                                                                                                                                                              @Override
                                                                                                                                                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                                                                              }
                                                                                                                                                                          }
                        );




                    } else {
                        Log.d("switch","switching value is null");
                       }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        setUpLocaton();
        updateFirebaseToken();
    }

    private void cancelBooking() {

Intent ab=new Intent(acceptingcustomerwindow.this,CancellationReason.class);
startActivity(ab);
overridePendingTransition(R.anim.fade_in,R.anim.fadeout);





    }

    public void getDirection(final double lat, final double lng) {

        FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("switchingSystem").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       String switchid=dataSnapshot.getValue().toString();

                        if ((!switchid.isEmpty())||(switchid!=null)) {


                            FirebaseDatabase.getInstance().getReference(Common.OnDutyDrivers)
                                    .child( switchid)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            Rider rider = dataSnapshot.getValue(Rider.class);



                                            //              LatLng   currentPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
                                            String requestApi = null;
                                            try {

                                                if (rider != null) {
                                                    requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                                                            "mode=drivering&" +
                                                            "transit_routing_preference=less_driving&" +
                                                            "origin=" + lat + "," + lng + "&" +
                                                            "destination=" + rider.getDriverlat1() + "," + rider.getDriverlng1() + "&" +
                                                            "key=" + getResources().getString(R.string.google_direction_Api);
                                                }
                                                else
                                                {
                                                    Log.d("accepting window ","Rider is null");

                                                    requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                                                            "mode=drivering&" +
                                                            "transit_routing_preference=less_driving&" +
                                                            "origin=" + 12.9657324 + "," + 79.1553823 + "&" +
                                                            "destination=" + 12.9657324 + "," + 79.1553823 + "&" +
                                                            "key=" + getResources().getString(R.string.google_direction_Api);
                                                }
                                                mMap.clear();
                                                mUserMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(lat, lng))
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.star)). title("you"));



                                                mdriverMaarker=mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(Double.parseDouble(rider.getDriverlat1()),Double.parseDouble(rider.getDriverlng1())))
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle)). title("you"));



                                                mService.getPath(requestApi)
                                                        .enqueue(new Callback<String>() {
                                                            @Override
                                                            public void onResponse(Call<String> call, Response<String> response) {
                                                                try {

                                                                    new ParserTrack().execute(response.body().toString());

                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<String> call, Throwable t) {
                                                                ;
                                                            }
                                                        });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                        }
                        else
                        {
                           Log.d("cs","called switch");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

       Intent intent = getIntent();
        String str = intent.getStringExtra("driverid");


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
    private  class ParserTrack extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {

        //  ProgressDialog mDialog = new ProgressDialog(DriverTracking.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jobject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jobject = new JSONObject(strings[0]);

                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jobject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

            ArrayList points=null;
            PolylineOptions polylineOptions=null;
            for(int i=0;i<lists.size();i++)
            {
                points=new ArrayList();
                polylineOptions =new PolylineOptions();
                List<HashMap<String,String>> path=lists.get(i);
                for(int j=0;j<path.size();j++)
                {
                    HashMap<String,String>point=path.get(j);
                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));

                    LatLng positions=new LatLng(lat,lng);

                    points.add(positions);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }
            direction=mMap.addPolyline(polylineOptions);


        }
    }


    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_Tb1);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());

        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);
    }


    //CTRL O


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                buildLocationCallback();
                createLocationRequest();
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


        } else {



            buildLocationCallback();

            createLocationRequest();

            displayLocation();

        }

    }

    private void buildLocationCallback() {

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation=locationResult.getLastLocation();
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


                    final double latitude = Common.mLastLocation.getLatitude();
                    final double longitude = Common.mLastLocation.getLongitude();
                    if (mUserMarker != null)
                        mUserMarker.remove();
                        mUserMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude)).title("you"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                    FirebaseDatabase.getInstance().getReference("StartingPoint").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Rider rider = dataSnapshot.getValue(Rider.class);
                            mMap.clear();
                            mUserMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(rider.getDesLat(), rider.getDesLng())).title("you"));
                            getDirection(rider.getDesLat(),rider.getDesLng());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
mdialog.dismiss();



                    Log.d("endd", String.format("Yor location was changed: %f/%f", latitude, longitude));


                } else
                    Log.d("ERROR", "can not get your location");


            }
        });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // mLocationRequest=LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(Displacement);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.acceptingcustomerwindow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trip_history) {
            trst();
            // Handle the camera action
        } else if (id == R.id.nav_way_bill) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.share) {
            shareTextUrl();

        } else if (id == R.id.nav_change_password) {

        } else if (id == R.id.nav_sign_out1) {
            signout();

        } else if (id == R.id.nav_update_info) {
            showDialogUpdateInformation();


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void trst() {
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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(acceptingcustomerwindow.this);
        alertDialog.setTitle("Update Information");
        alertDialog.setMessage("Please fill all Informations");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout_infor = inflater.inflate(R.layout.layout_update_information, null);
        final MaterialEditText edtName = layout_infor.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = layout_infor.findViewById(R.id.edtPhone);
        final ImageView image_upload = layout_infor.findViewById(R.id.image_upload);
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

                final android.app.AlertDialog waitingDialog = new SpotsDialog(acceptingcustomerwindow.this);
                waitingDialog.show();
                String name = edtName.getText().toString();
                String phone = edtPhone.getText().toString();
                Map<String, Object> updateInfo = new HashMap<>();
                if (!TextUtils.isEmpty(name))
                    updateInfo.put("name", name);
                if (!TextUtils.isEmpty(phone))
                    updateInfo.put("phone", phone);
                DatabaseReference driverinformation = FirebaseDatabase.getInstance().getReference(Common.user_driver_tb1).child(IsTruck?"Bike":"Truck");
                driverinformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(acceptingcustomerwindow.this, "Information Updated", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(acceptingcustomerwindow.this, "Upload failed !", Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();

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
            Uri saveUri = data.getData();
            if (saveUri != null) {
                final ProgressDialog mDialog = new ProgressDialog(acceptingcustomerwindow.this);
                mDialog.setMessage("Uploading....");
                mDialog.show();
                String imageNmae = UUID.randomUUID().toString();
                final StorageReference imagefolder = storageReference.child("image/" + imageNmae);
                imagefolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mDialog.dismiss();
                        Toast.makeText(acceptingcustomerwindow.this, "Uploaded", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(acceptingcustomerwindow.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(acceptingcustomerwindow.this, "Upload error !", Toast.LENGTH_SHORT).show();

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
        Toast.makeText(acceptingcustomerwindow.this, "hello", Toast.LENGTH_SHORT).show();
        Paper.init(this);
        Paper.book().destroy();
        FirebaseAuth.getInstance().signOut();
        SharedPreferences preferences=getSharedPreferences("DriverInfo",Context.MODE_PRIVATE);
        preferences.edit().remove("driverId").commit();


        Intent intent = new Intent(acceptingcustomerwindow.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustoInfoWindow(this));

        mMap.setOnInfoWindowClickListener(this);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissioaans
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;

        }
       // getDirection(12.9665599,79.1412713);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }




    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
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

}
