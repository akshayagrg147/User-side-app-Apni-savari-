package com.example.apnisavari;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.arsy.maps_library.MapRipple;
import com.example.apnisavari.Common.Common;
import com.example.apnisavari.Model.DataMessage;
import com.example.apnisavari.Model.FCMResponse;
import com.example.apnisavari.Model.Rider;
import com.example.apnisavari.Model.Token;
import com.example.apnisavari.Remote.IFCMService;
import com.example.apnisavari.Remote.IGoogleApi;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;


public class choosevechile extends  Fragment
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener, ValueEventListener {

    TextView txt;
    double lati,lngi,Slat,Slng;
    SweetAlertDialog pd;
    int i=1;
    IGoogleApi mServices;
    String addressuser,destintionuser,farecalculate;


    AppCompatActivity obj=new AppCompatActivity();

    private static final long START_TIME_IN_MILLIS = 330000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    AlertDialog.Builder alertDialog;
    private Polyline direction;
    ProgressDialog mdialog;
    //file store in storage
    SharedPreferences sharedPreferences;
    public  static final String MyPreference="DriverInfo";
    public static final String driverId="driverId";
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    boolean IsTruck=true;
    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private LocationRequest mLocationRequest;
    private static Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int Displacement = 10;
    Marker mUserMarker;
    private TextView txtCountDown;
    IGoogleApi mService;
    Button btnPickUpRequest;
    int radius = 1;
    int distance = 1;
    private static final int Limit = 3;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    View mapView;
    //send alert
    IFCMService mServicee;
    DatabaseReference driverAvailables;
    ImageView truck1,truck2;
    DrawerLayout drawer;
    MapRipple mapRipple;
    Toolbar toolbar;
    Button promocode,farecal;

    Boolean estimate=false;
    private BroadcastReceiver mCancelBrroadCast=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btnPickUpRequest.setText("PICKUP REQUEST");
            Common.driverId="";
            Common.isDriverFound=false;
            if(mapRipple.isAnimationRunning())
                mapRipple.stopRippleMapAnimation();
            mUserMarker.hideInfoWindow();
            circularViewWithTimer.stopTimer();
            circularViewWithTimer.setText("");
            btnPickUpRequest.setEnabled(true);

        }
    };
    String name,rates,profilePic;
    private CircularView circularViewWithTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mdialog=new ProgressDialog(getActivity(),R.style.SpotsDialogDefault);
        mdialog.setTitle("   ApniSavari");
        mdialog.setMessage("Data loading...");
        mdialog.setCancelable(false);
        mdialog.show();
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mServices= Common.getGoogleApiService();
        sharedPreferences=this.getActivity().getSharedPreferences("prices",Context.MODE_PRIVATE);
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_choosevechile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btnPickUpRequest = view.findViewById(R.id.btnPickupRequest);
        drawer = view.findViewById(R.id.drawer_layout1);

        mService = Common.getGoogleApiService();
        txtCountDown = view.findViewById(R.id.circular_view_with_timer);


        alertDialog = new AlertDialog.Builder(view.getContext());
        truck1 = view.findViewById(R.id.select_truck);
        truck2 = view.findViewById(R.id.select_truck2);
        circularViewWithTimer = view.findViewById(R.id.circular_view_with_timer) ;
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        farecal= view.findViewById(R.id.getfare);
        toolbar= view.findViewById(R.id.tootlbarcomp1);
        toolbar= view.findViewById(R.id.tootlbarcomp1);
        farecal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(getActivity());
                dialog.setTitle("        Estimate Fares");
                dialog.setMessage("Fare can be changed based on vechile crowd");

                LayoutInflater inflater=LayoutInflater.from(getActivity());
                View fare_calculate=inflater.inflate(R.layout.farecal,null);
                TextView address=fare_calculate.findViewById(R.id.txtLocation);
                TextView destination=fare_calculate.findViewById(R.id.txtDestination);
                final TextView price=fare_calculate.findViewById(R.id.txtCalculation);
                final MaterialEditText promo=fare_calculate.findViewById(R.id.edtpromo);
                final TextView sttatuscode=fare_calculate.findViewById(R.id.apply);
                final int[] count = {1};
                dialog.setView(fare_calculate);
                address.setText(Common.startting);
                destination.setText(Common.destuser);
                price.setText(Common.price);
                sttatuscode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Common.vechile) {

                            if (promo.getText().toString().equals("ApniSavari50")&&(count[0] ==1)) {
                              // String dd=sharedPreferences.getString("prices","");

                                price.setText(String.valueOf(Double.parseDouble(Common.price)-50));
                                promo.setEnabled(false);
                                sttatuscode.setText("Applied");
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("price",String.valueOf(Double.parseDouble(Common.price)-50));
                                editor.commit();
                                ++count[0];
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(" Applied Successfully").show();
                            } else {
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText("..oops..!")
                                        .setTitleText("Not Applied").show();
                            }
                        }
                        else
                        {
                            if (promo.getText().toString().equals("ApniSavari20")&&(count[0] ==1)) {
                                price.setText(String.valueOf(Double.parseDouble(Common.price)-50));
                                sttatuscode.setText("Applied");
                                ++count[0];
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(" Applied Successfully").show();
                            } else {
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText("..oops..!")
                                        .setTitleText("Not Applied").show();
                            }

                        }
                    }
                });

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                if(estimate)
                dialog.show();
                else
                {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText("..oops..!")
                            .setTitleText("vechile not Selected").show();
                }

            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("ApniSavari Vechiles");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),Map_destination_Select.class));

            }
        });

        final Double[] lat = new Double[1];
        final Double[] lng = new Double[1];
        final Double[] Slati = new Double[1];
        final Double[] Slngi = new Double[1];
        final DatabaseReference Destination = FirebaseDatabase.getInstance().getReference(Common.destination_request_tb1);
        Destination.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                    lat[0] = childsnap.child("desLat").getValue(Double.class);
                    lng[0] = childsnap.child("desLng").getValue(Double.class);
                }
                lati = lat[0];
                if (lng[0] != null)
                    lngi = lng[0];
                else {
                    Log.e("lngi value", "lngi value is zero");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        final DatabaseReference StartingPoint = FirebaseDatabase.getInstance().getReference(Common.starting_location);
        StartingPoint.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                    Slati[0] = childsnap.child("desLat").getValue(Double.class);
                    Slngi[0] = childsnap.child("desLng").getValue(Double.class);


                }
                Slat = Slati[0];
                if (Slngi[0] != null)
                    Slng = Slngi[0];
                else {
                    Log.e("Sngi value", "SLngi value is zero");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        getDirection();
        mServicee = Common.getFCMsService();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());


        //   Log.d("user name", Common.currentUser.getName());




        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mapView = mapFragment.getView();
        }
        else
        {
            Log.d("home","mAP fragment null");
        }
        truck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsTruck = true;
                estimate=true;
                Common.vechile = true;
                if (IsTruck) {
                    getDirection();
                    truck1.setImageResource(R.drawable.truck2);
                    truck2.setImageResource(R.drawable.ranger);

                    destintionuser=getStringAddress(lati, lngi);
                    addressuser=getStringAddress(Slat, Slng);
                    Common.startting=addressuser;
                    Common.destuser=destintionuser;





                   farecalculate= getPrice(destintionuser,addressuser);


                }

                mMap.clear();
                if (driverAvailables != null)
                    driverAvailables.removeEventListener(choosevechile.this);
                driverAvailables = FirebaseDatabase.getInstance().getReference(Common.driver_tb1).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("driverId");

                driverAvailables.addValueEventListener(choosevechile.this);

                loadAllDriverAvailable(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        });
        truck2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDirection();
                IsTruck = false;
                Common.vechile = false;
                estimate=true;
                if (!IsTruck) {
                    truck1.setImageResource(R.drawable.truck1);
                    truck2.setImageResource(R.drawable.ranger2);
                } else {
                    truck1.setImageResource(R.drawable.truck1);
                    truck2.setImageResource(R.drawable.ranger);
                }
                farecalculate= getPrice(destintionuser,addressuser);





                mMap.clear();
                if (driverAvailables != null)
                    driverAvailables.removeEventListener(choosevechile.this);
                driverAvailables = FirebaseDatabase.getInstance().getReference(Common.driver_tb1).child("Bike");

                driverAvailables.addValueEventListener(choosevechile.this);

                loadAllDriverAvailable(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            }
        });





        btnPickUpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Common.isDriverFound) {
                    requestPickUpHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else {

                }


            }
        });


        setUpLocaton();
        updateFirebaseToken();
        super.onViewCreated(view, savedInstanceState);
    }

    private String getPrice(String mLocation, String mdestination) {
        final String[] final_calcuate = new String[1];

        String requestUrl=null;
        try{
            requestUrl="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"
                    +"transit_routing_preference=less_driving&"
                    +"origin="+mLocation+"&"
                    +"destination="+mdestination+"&"
                    +"key="+getResources().getString(R.string.google_browser_Api);
            Log.e("Link",requestUrl);
            mServices.getPath(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try{
                        JSONObject jsonObject=new JSONObject(response.body().toString());
                        JSONArray routes=jsonObject.getJSONArray("routes");
                        JSONObject object=routes.getJSONObject(0);
                        JSONArray legs=object.getJSONArray("legs");
                        JSONObject legsObject=legs.getJSONObject(0);

                        JSONObject distance=legsObject.getJSONObject("distance");
                        String distance_text=distance.getString("text");
                        Double distance_value=Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+",""));
                        JSONObject time=legsObject.getJSONObject("duration");
                        String time_text=time.getString("text");
                        Integer time_value= Integer.parseInt(time_text.replaceAll("\\D+",""));
                         if(Common.vechile) {

                                Common.price=Common.getCrPrice(distance_value, time_value)+"";

                            final_calcuate[0] = String.format("%s + %s=%.2fRupees", distance_text, time_text
                                    , Common.getCrPrice(distance_value, time_value));

                           }

                        if(!Common.vechile) {
                            final_calcuate[0] = String.format("%s + %s=%.2fRupees", distance_text, time_text
                                    , Common.getBikeprice(distance_value, time_value));
                                Common.price=Common.getBikeprice(distance_value, time_value)+"";


                        }




                        //    Intent share=new Intent(getContext(),Home.class);
                        //    share.putExtra("dest",end_address);
                        //    startActivity(share);





                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("eroor",t.getMessage());
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return final_calcuate[0];

    }

    public void getDirection() {
        final Double[] lat = new Double[1];
        final Double[] lng = new Double[1];

        //  LatLng   currentPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
        String requestApi = null;
        try {

            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=drivering&"+
                    "transit_routing_preference=less_driving&" +
                    "origin=" +  Common.StrtingLati+ "," + Common.StrtingLngi + "&" +
                    "destination=" +   Common.endingLati+","+  Common.endingLngi  + "&" +
                    "key=" + getResources().getString(R.string.google_direction_Api);

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {

                                new ParserTrack().execute(response.body().toString());

                            }
                            catch (Exception e) {
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private class ParserTrack extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {

        //  ProgressDialog mDialog = new ProgressDialog(DriverTracking.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //     mDialog.setMessage("Please Waiting...");

            //     mDialog.show();
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
            try {
                ArrayList points = null;
                PolylineOptions polylineOptions = null;
                for (int i = 0; i < lists.size(); i++) {
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = lists.get(i);
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));

                        LatLng positions = new LatLng(lat, lng);
                        points.add(positions);
                    }
                    polylineOptions.addAll(points);
                    polylineOptions.width(10);
                    polylineOptions.color(Color.RED);
                    polylineOptions.geodesic(true);
                }
                try {
                    direction = mMap.addPolyline(polylineOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Common.StrtingLati, Common.StrtingLngi)
                        )
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.star)));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Common.endingLati, Common.endingLngi)
                        )
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dest)));


            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }


    public String getStringAddress(double latitude, double longitude) {
        String address="";
        String city="";
        Geocoder geocoder;

        List<Address> addresses;
        geocoder=new Geocoder(getActivity(), Locale.getDefault());
        try{
            addresses=geocoder.getFromLocation(latitude,longitude,1);
            address=addresses.get(0).getAddressLine(0);
            city=addresses.get(0).getLocality();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  address+" "+city;
    }

    public  void sendRequestToDriver(final String driverId, final IFCMService mServicee, final Context context, final Location currentLocation) {
        final String customerid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_Tb1);

        FirebaseDatabase.getInstance().getReference("StartingPoint").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Rider rider = dataSnapshot.getValue(Rider.class);
                tokens.orderByKey().equalTo(driverId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Token token = postSnapshot.getValue(Token.class);

                                    String riderToken = FirebaseInstanceId.getInstance().getToken();
                                    Map<String,String> content=new HashMap<>();
                                    content.put("title","requet");

                                    content.put("customer",customerid);
                                    content.put("lat",String.valueOf(rider.getDesLat()));
                                    content.put("lng",String.valueOf(rider.getDesLng()));

                                    DataMessage dataMessage = new DataMessage(token.getToken(),content );




                                    mServicee.sendMessage(dataMessage)
                                            .enqueue(new Callback<FCMResponse>() {
                                                @Override
                                                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                                                    if (response.body() != null && response.body().success == 1) {
                                                        new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE).setTitleText("..Request sent..!")
                                                                .setTitleText("Wait 30 seconds").show();
                                                    } else
                                                        new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE).setTitleText("..Oops..!")
                                                                .setTitleText("Something went wrong").show();
                                                }

                                                @Override
                                                public void onFailure(Call<FCMResponse> call, Throwable t) {
                                                    Log.e("ERROR", t.getMessage());
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void initCircularViewWithTimer() {

        CircularView.OptionsBuilder builderWithTimer = new
                CircularView.OptionsBuilder()
                .shouldDisplayText(true)
                .setCounterInSeconds(30)
                .setCircularViewCallback(new CircularViewCallback() {
                    @Override
                    public void onTimerFinish() {

                        try {
                            btnPickUpRequest.setEnabled(true);
                            btnPickUpRequest.setText("Request Again");
                            Common.isDriverFound = false;
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE).setTitleText("..Oops..!")
                                    .setTitleText("All Drivers are busy").show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onTimerCancelled() {
                        Toast.makeText(getContext(), "Request Cancelled ", Toast.LENGTH_SHORT).show();
                    }
                });
        circularViewWithTimer.setOptions(builderWithTimer);
    }




    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_Tb1);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());

        tokens.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(token);
    }



    private void requestPickUpHere(String uid) {

        DatabaseReference dbrequest = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tb1);
        GeoFire mGeoFire = new GeoFire(dbrequest);
        mGeoFire.setLocation(uid, new GeoLocation(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()));

        if (mUserMarker.isVisible()) {
            mUserMarker.remove();
        }
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .title("PickUP here")
                .snippet("")
                .position(new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude())));

        mUserMarker.showInfoWindow();
        mapRipple=new MapRipple(mMap,new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude()),getActivity());
        mapRipple.withNumberOfRipples(1);
        mapRipple.withDistance(1000);
        mapRipple.withRippleDuration(3000);
        mapRipple.withTransparency(0.5f);
        mapRipple.startRippleMapAnimation();
        btnPickUpRequest.setText("Getting your Driver....");
        findDriver();



    }

    private void findDriver() {

        DatabaseReference driverLocation;
        if(IsTruck)

            driverLocation= FirebaseDatabase.getInstance().getReference(Common.driver_tb1).child("Truck");
        else
            driverLocation= FirebaseDatabase.getInstance().getReference(Common.driver_tb1).child("Bike");



        GeoFire gf = new GeoFire(driverLocation);
        final GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()), radius);


        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!Common.isDriverFound) {
                   


                    Common.isDriverFound = true;
                    Common.driverId = key;
                    btnPickUpRequest.setEnabled(false);


                    btnPickUpRequest.setText("WAIT 30 SECONDS");
                    btnPickUpRequest.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference("CallForward").child(Common.driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String str=dataSnapshot.getValue().toString();

                            initCircularViewWithTimer();
                            circularViewWithTimer.startTimer();
                            if(str.isEmpty())
                            {   sendRequestToDriver(Common.driverId, mServicee, getActivity(), Common.mLastLocation);
                                FirebaseDatabase.getInstance().getReference("CancelRequest").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Map<String, String> ab1 = (Map<String, String>) dataSnapshot.getValue();
                                        String str1 = ab1.get(Common.driverId);

                                        try {
                                            if (str1.contains("CancelRequest")) {
                                                Common.driverId="";
                                                Common.isDriverFound = false;
                                                findDriver();
                                            }
                                            else {
                                                Log.d("true","true");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else {
                                Common.isDriverFound = false;
                                //findDriver();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference(Common.SwitchingSystem).
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String[] driver1 = new String[1];
                                    try {
                                        for (DataSnapshot childsnap : dataSnapshot.getChildren())
                                            driver1[0] = childsnap.child("switchingSystem").getValue(String.class);
                                        if (!driver1[0].isEmpty()) {
                                            geoQuery.removeAllListeners();
                                         Intent ab=new Intent(getActivity(),acceptingcustomerwindow.class);
                                         startActivity(ab);



                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    //    startTimer();


                }





            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //if still not found increase distance
                if (!Common.isDriverFound && radius < Limit) {
                    radius++;
                    findDriver();
                } if(!Common.isDriverFound && radius == Limit) {


                    new SweetAlertDialog(getActivity(),SweetAlertDialog.NORMAL_TYPE).setTitleText("..Oops..!")
                            .setTitleText("All Drivers are busy").show();  //   mMap.clear();


                    if(mapRipple.isAnimationRunning())
                        mapRipple.stopRippleMapAnimation();
                    mUserMarker.hideInfoWindow();

                    btnPickUpRequest.setText("Request PickUp");
                    btnPickUpRequest.setEnabled(true);
                    geoQuery.removeAllListeners();
                    loadAllDriverAvailable(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }
    //CTRL O


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    buildLocationCallback();
                    createLocationRequest();



                }
        }
    }


    private void setUpLocaton() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
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



        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Common.mLastLocation = location;
                if (Common.mLastLocation != null) {

                    driverAvailables = FirebaseDatabase.getInstance().getReference(Common.driver_tb1).child(IsTruck?"Truck":"Bike");

                    driverAvailables.addValueEventListener(choosevechile.this);

                    final double latitude = Common.mLastLocation.getLatitude();
                    final double longitude = Common.mLastLocation.getLongitude();
                    try {

                        if(mUserMarker!=null)
                            mUserMarker.remove();
                        else
                        {
                            Log.d("value","marker is null");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mUserMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude)).title("you"));

mdialog.dismiss();

                    double totalsistance= SphericalUtil.computeDistanceBetween(new  LatLng(Common.StrtingLati, Common.StrtingLngi),new LatLng(Common.endingLati, Common.endingLngi));

                    try {
                        if ((totalsistance / 1000) <= 4.000)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));
                        else if (((totalsistance / 1000) > 4.000)&&((totalsistance / 1000) < 10.000))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11.0f));
                        else
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7.0f));



                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    loadAllDriverAvailable(new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude()));


                    Log.d("endd", String.format("Yor location was changed: %f/%f", latitude, longitude));


                } else
                    Log.d("ERROR", "can not get your location");


            }
        });


    }

    private void loadAllDriverAvailable(final LatLng location) {

        DatabaseReference driverLocation;
        if(IsTruck)
            driverLocation= FirebaseDatabase.getInstance().getReference(Common.driver_tb1).child("Truck");
        else
            driverLocation= FirebaseDatabase.getInstance().getReference(Common.driver_tb1).child("Bike");


        GeoFire gf = new GeoFire(driverLocation);
        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(location.latitude, location.longitude), distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Common.user_driver_tb1)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Rider rider = dataSnapshot.getValue(Rider.class);
                                if(IsTruck) {

                                    if (rider.getCarType().equals("Truck")) {
                                        if (Common.DriverLati != location.latitude) {
                                            mMap.clear();
                                        }getDirection();

                                        Common.DriverLati = location.latitude;
                                        Common.DriverLngi = location.longitude;

                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(location.latitude, location.longitude)
                                                ).
                                                        title("your").flat(true)

                                                .snippet(dataSnapshot.getKey())

                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


                                    }

                                }
                                else
                                {
                                    if (rider.getCarType().equals("Bike")) {
                                        if (Common.DriverLati != location.latitude) {
                                            mMap.clear();
                                        }
                                        Common.DriverLati=location.latitude;
                                        Common.DriverLngi=location.longitude;
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(location.latitude, location.longitude)
                                                ).
                                                        title("your").flat(true)

                                                .snippet(dataSnapshot.getKey())

                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle)));

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= Limit) {
                    distance++;
                    loadAllDriverAvailable(location);
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        // mLocationRequest=LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }






    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
      //  mMap.setMinZoomPreference(12);z
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


        mMap.setOnInfoWindowClickListener(this);
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 600);
        }





        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        if(marker.getTitle().equals("your"))
        {

            Intent intent=new Intent(getActivity(),CallDriver.class);

            intent.putExtra("driverId",marker.getSnippet());
            intent.putExtra("lat",Common.mLastLocation.getLatitude());
            intent.putExtra("lng",Common.mLastLocation.getLongitude());
//startActivity(intent);
        }





    }



    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mCancelBrroadCast);
        super.onDestroy();
    }

}
