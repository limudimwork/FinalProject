package com.happytrees.finalproject.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happytrees.finalproject.R;
import com.happytrees.finalproject.activity.MainActivity;
import com.happytrees.finalproject.database.LastSearch;

import java.util.List;


//create a class that extends RecyclerView.Adapter .put inside the < >  ==> Yourclass.YourInnerClassViewHolder


public class SearchHistoryAdapter extends RecyclerView.Adapter <SearchHistoryAdapter.SearchHistoryViewHolder>{


//VARIABLES
    public List<LastSearch> lastSearchResults;//list of places results
    public Context context;
    public String hPreference = "kilometre";//default value of distance measurement units
    public double roundedHDis;//rounded value of distance (less numbers after dot)
    public float[] HDistanceResults = new float[10];//10 random number.you need any number higher than 3

    public SearchHistoryAdapter(List<LastSearch> lastSearchResults, Context context) {
        this.lastSearchResults = lastSearchResults;
        this.context = context;
    }

    @Override
    public SearchHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // generate view and viewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.lastsearch_item_layout, null);
        SearchHistoryViewHolder searchHistoryViewHolder = new SearchHistoryViewHolder(view);
        return searchHistoryViewHolder;
    }



    @Override
    public void onBindViewHolder(SearchHistoryViewHolder holder, int position) {
        //bind data to view holder
        LastSearch lastSearch = lastSearchResults.get(position);
        holder.bindDataFromArrayToView(lastSearch);
    }

    @Override
    public int getItemCount() {
        return lastSearchResults.size();
    }



    //INNER CLASS
    //create inner class  YourInnerClassViewHolder extends RecyclerView.ViewHolder => implement constructor

    public class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        View myHView;



        //constructor
        public SearchHistoryViewHolder(View itemView) {
            super(itemView);
            myHView = itemView;
        }


        public void bindDataFromArrayToView(final LastSearch currentLastSearch) {

            TextView historyName = (TextView) myHView.findViewById(R.id.historyName);//NAME
            historyName.setText(currentLastSearch.name);

            TextView historyAddress = (TextView) myHView.findViewById(R.id.historyAddress);//ADDRESS
            historyAddress.setText(currentLastSearch.formatted_address);


            //get lat and lng of current Last Search
            double keptLat = currentLastSearch.lat;
            double keptLng = currentLastSearch.lng;


            TextView hDistance = (TextView) myHView.findViewById(R.id.historyDistance);//DISTANCE
            //method calculates distances between two points according to their latitude and longitude
            Location.distanceBetween(MainActivity.upLatitude, MainActivity.upLongitude, keptLat, keptLng, HDistanceResults);// DEFAULT  IN KILOMETERS



            //FETCH SETTINGS RESULTS FROM SharedPreferences
            //set Shared Preferences (there you save settings values )
            SharedPreferences sharedFPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //get value from SharedPrefs
            hPreference  = sharedFPreferences.getString("list_preference_units", "kilometre");//list_preference_units is key(id) of preference item in preferences.xml

            //check settings results
            if(hPreference.equals("kilometre") ) {
                roundedHDis =  (double)Math.round( (HDistanceResults[0]/1000 ) * 100d) / 100d;//number of zeros must be same in and outside parenthesis.number of zeroes equals to number of numbers after dot that will remain after rounding up
                hDistance.setText(roundedHDis + " km ");//km
            }else{
                roundedHDis =  (double)Math.round( (((HDistanceResults[0]*0.621371)/1000 ) ) * 100d) / 100d;//number of zeros must be same in and outside parenthesis.number of zeroes equals to number of numbers after dot that will remain after rounding up
               hDistance.setText(roundedHDis + "  miles");//miles
            }





        }


    }




}
