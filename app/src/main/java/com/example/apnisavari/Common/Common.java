package com.example.apnisavari.Common;


import android.location.Location;

import com.example.apnisavari.Model.Rider;
import com.example.apnisavari.Remote.FCMClient;
import com.example.apnisavari.Remote.GoogleMapApi;
import com.example.apnisavari.Remote.IFCMService;
import com.example.apnisavari.Remote.IGoogleApi;

public class Common {

  public static boolean isDriverFound = false;
  public static String driverId = "";
  public static final String user_field = "rider_user";

  public static final String pwd_field = "rider_pwd";
    public static final String OnDutyDrivers="OnDutyDrivers";
    public static String startting="";
    public static  String destuser="";
    public static  String price="";
    public static  Boolean promochk=false;
  public static Rider currentUser;
  public static Location mLastLocation;
    public static double DriverLati;
    public static double DriverLngi;
    public static double StrtingLati;
    public static double StrtingLngi;
    public static double endingLati;
    public static double endingLngi;
  public static String mPlaceLocation, MplaceDestination;
  public static String res = "";

  public static String cureeentToken = "";
  public static final String driver_tb1 = "Drivers";
  public static final String user_driver_tb1 = "DriverInformation";
  public static final String user_rider_tb1 = "RiderInformation";
  public static final String pickup_request_tb1 = "PickUpRequst";
  public static final String destination_request_tb1 = "Destination";
    public static final String starting_location = "StartingPoint";

    public static final String SwitchingSystem = "SwitchingSystem";

  public static final String token_Tb1 = "Tokens";
  public static final String rate_detail_tb1 = "RateDetails";
  private static double base_fareBike = 2.55;

  private static double Time_RateBike = 0.35;
  private static double distance_rateBike = 1.55;

  private static double base_fareCar = 10.00;
  public static double latitude;

  private static double Time_RateCar = 2.00;
  private static double distance_rateCar = 5.00;
  public static boolean vechile = false;
  public static final String broad_cast_string = "cancel_pickup";
  public static final String broad_cast_dropoff = "arrived";
    public static final String broad_cast_accept = "Accept";

  public static double getBikeprice(double km, int min){

        return(base_fareBike+(Time_RateBike*min)+distance_rateBike*km);

}
public static double  getCrPrice(double km,int min)
{

    return (base_fareCar+(Time_RateCar*min)+distance_rateCar*km);



}


    public static final String fcmURL="https://fcm.googleapis.com/";
    public static final String googleApiUrl="https://maps.googleapis.com";
    public static int Pick_Image_request=9999;

    public static IFCMService getFCMsService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
    public static IGoogleApi getGoogleApiService()
    {
        return GoogleMapApi.getClient(googleApiUrl).create(IGoogleApi.class);
    }



}
