package com.happytrees.finalproject.database;

import com.orm.SugarRecord;


/*NOTE: Sugar ORM has int field ID but Retrofit's Gson has also field ID of String type .In order to prevent crashes you need create additional java class
which will extend SugarRecord,have variables with same names contained in original Json document and in time of creating objects will receive same values which its related
module class's objects received using constructor in both times.
This way we will store objects made by TxtResult class through ResultDB class in Sugar Orm database by converting json objects to java objects and only then store them*/
//NOTE: there no need in recreating module hierarchy ,the only requirement is that variables of both classes (ResultDB and TxtResult/NearbyResult will have same names and values)

public class LastSearch extends SugarRecord {

    public  String name;
    public  String formatted_address;
    public double lat;
    public double lng;


    //required empty constructor
    public LastSearch() {
    }

    public LastSearch(String name, String formatted_address, double lat, double lng) {
        this.name = name;
        this.formatted_address = formatted_address;
        this.lat = lat;
        this.lng = lng;
    }
}
