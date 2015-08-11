package com.goeuro.pinke.Data;

import android.location.Location;

import com.goeuro.pinke.GoEuroMainActivity;

/**
 * Created by admin on 05-Aug-15.
 */
public class Position {


    /**
     * coreCountry : false
     * geo_position : {"longitude":-46.34833,"latitude":-23.48611}
     * distance : null
     * _id : 370912
     * inEurope : false
     * name : Itaquaquecetuba
     * countryCode : BR
     * locationId : 3072
     * iata_airport_code : null
     * fullName : Itaquaquecetuba, Brazil
     * type : location
     * key : null
     * country : Brazil
     */
    private boolean coreCountry;
    private GeoPositionEntity geo_position;
    private String distance;
    private int _id;
    private boolean inEurope;
    private String name;
    private String countryCode;
    private int locationId;
    private String iata_airport_code;
    private String fullName;
    private String type;
    private String key;
    private String country;

    @Override
    public String toString() {
        if (GoEuroMainActivity.IS_DEBUG) {

            return fullName;
//            "Position{" +
//                    "coreCountry=" + coreCountry +
//                    ", geo_position=" + geo_position +
//                    ", distance='" + distance + '\'' +
//                    ", _id=" + _id +
//                    ", inEurope=" + inEurope +
//                    ", name='" + name + '\'' +
//                    ", countryCode='" + countryCode + '\'' +
//                    ", locationId=" + locationId +
//                    ", iata_airport_code='" + iata_airport_code + '\'' +
//                    ", fullName='" + fullName + '\'' +
//                    ", type='" + type + '\'' +
//                    ", key='" + key + '\'' +
//                    ", country='" + country + '\'' +
//                    '}';
        }
        return super.toString();
    }

    public boolean isCoreCountry() {
        return coreCountry;
    }

    public void setCoreCountry(boolean coreCountry) {
        this.coreCountry = coreCountry;
    }

    public GeoPositionEntity getGeo_position() {
        return geo_position;
    }

    public Location getLocation() {
        return geo_position.getLocation();
    }

    public void setGeo_position(GeoPositionEntity geo_position) {
        this.geo_position = geo_position;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public boolean isInEurope() {
        return inEurope;
    }

    public void setInEurope(boolean inEurope) {
        this.inEurope = inEurope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getIata_airport_code() {
        return iata_airport_code;
    }

    public void setIata_airport_code(String iata_airport_code) {
        this.iata_airport_code = iata_airport_code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static class GeoPositionEntity {
        /**
         * longitude : -46.34833
         * latitude : -23.48611
         */
        private double longitude;
        private double latitude;

        private Location location;

        @Override
        public String toString() {
            if (GoEuroMainActivity.IS_DEBUG) {
                return "GeoPositionEntity{" +
                        "longitude=" + longitude +
                        ", latitude=" + latitude +
                        '}';
            }
            return super.toString();
        }

        public Location getLocation() {
            if (location == null) {
                location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }

            location = location == null ? location = new Location("") : location;
            return location;

        }
    }



}
