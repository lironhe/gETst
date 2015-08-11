package com.goeuro.pinke;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.goeuro.pinke.Data.Position;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.Comparator;


/**
 * Created by admin on 10-Aug-15.
 */
class FilterRequestNetworkHandler implements Runnable , LocationListener {
    private final android.content.Context context;
    private final Handler handler;
    LocationFilterResultListener locationFilterResultListener;
    private String reqFilter = null;
    private int lastRequestId = 0;
    /**
     * Remeber the user last location (in debug mode - London place holder)
     */
    Location lastBestLocation = null;
    private LocationManager locationManager;


    public FilterRequestNetworkHandler(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        //No need to SingleTone here.

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        lastBestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastBestLocation == null) {
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, (LocationListener) FilterRequestNetworkHandler.this);
        }
        new Thread(this).start();
    }

    public void setFilterText(String filterText) {
        synchronized (this) {
            this.reqFilter = filterText;
            this.notify();
        }
    }

    @Override
    public void run() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String filterText = null;
        while (true) {
            while (reqFilter == null) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            synchronized (this) {
                filterText = reqFilter;
                reqFilter = null;
            }

            final int currentRequestId = ++lastRequestId;

            String url = "http://api.goeuro.com/api/v2/position/suggest/en/" + filterText;
            JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    //Check in case new request had sent - so no need to update the data with old one
                    if (currentRequestId != lastRequestId) {
                        if (GoEuroMainActivity.IS_DEBUG) {
                            Log.e("###", "Request ID response ignored " + currentRequestId);
                        }
                        return;
                    }

                    String responseStr = response.toString();
                    Gson gson = new Gson();

                    Position[] positionsArray = null;
                    try {
                        positionsArray = gson.fromJson(responseStr, Position[].class);
                    } catch (Throwable t) {
                    }

                    if (positionsArray != null) {
                        Location location = lastBestLocation;


                        if (GoEuroMainActivity.IS_DEBUG) {
                            if (location == null) {
                                location = new Location("");
                                //London
                                location.setLongitude(-0.132983);
                                location.setLatitude(51.529739);
//                                    location.setLongitude(37.922947);
//                                    location.setAltitude(15.787399);
                            }
                        }
                        lastBestLocation = location;

                        if (location != null) {
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();

                            final Location myLocation = new Location("");
                            myLocation.setLongitude(longitude);
                            myLocation.setLatitude(latitude);

                            if (lastBestLocation != null) {

                                Arrays.sort(positionsArray, new Comparator<Position>() {
                                    @Override
                                    public int compare(Position lhs, Position rhs) {
                                        Location lhsLocation = lhs.getLocation();
                                        Location rhsLocation = rhs.getLocation();

                                        float lhsDist = myLocation.distanceTo(lhsLocation);
                                        float rhsDist = myLocation.distanceTo(rhsLocation);
                                        //Return the distance compare - NOT between to comparable locations -- But between both to MY_LOCATION distance
                                        return (int) (lhsDist - rhsDist);// == 0 ? 0 : (lhsDist - rhsDist) > 0 ? 1 : -1;
                                    }
                                });
                            }

                            if (GoEuroMainActivity.IS_DEBUG) {
                                Log.e("", "\n\n");
                            }
                            for (Position ps : positionsArray) {
                                if (GoEuroMainActivity.IS_DEBUG) {
                                    Log.e("", "\t\t\n" + myLocation.distanceTo(ps.getLocation()) + " > " + ps.toString() + ", " + ps.getLocation().toString());
                                }
                            }



                        }

                        //Recheck in case new request had sent - so no need to update the data with old one
                        if (currentRequestId != lastRequestId) {
                            return;
                        }

                        if (locationFilterResultListener != null) {

                            final Position[] newFilterResultArray = positionsArray;
                            if (handler != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (locationFilterResultListener != null) {
                                            locationFilterResultListener.onNewFilterResult(newFilterResultArray);
                                        }
                                    }
                                });
                            }
                        }


                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (GoEuroMainActivity.IS_DEBUG) {
                        Log.e("err: ", error.toString());
                    }
                }
            });

            requestQueue.add(jsObjRequest);
        }
    }

    public FilterRequestNetworkHandler setLocationFilterResultListener(LocationFilterResultListener locationFilterResultListener) {
        this.locationFilterResultListener = locationFilterResultListener;
        return this;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            lastBestLocation = location;
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    interface LocationFilterResultListener {
        void onNewFilterResult(Position[] newPositions);
    }
}
