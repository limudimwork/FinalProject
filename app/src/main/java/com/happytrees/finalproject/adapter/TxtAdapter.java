package com.happytrees.finalproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happytrees.finalproject.R;
import com.happytrees.finalproject.activity.MainActivity;
import com.happytrees.finalproject.database.LastSearch;
import com.happytrees.finalproject.database.ResultDB;
import com.happytrees.finalproject.fragments.FragmentA;
import com.happytrees.finalproject.fragments.FragmentChanger;
import com.happytrees.finalproject.model_txt_search.TxtResult;

import java.util.ArrayList;
import java.util.List;

//create a class that extends RecyclerView.Adapter .put inside the < >  ==> Yourclass.YourInnerClassViewHolder

public class TxtAdapter extends RecyclerView.Adapter<TxtAdapter.TxtHolder> {

    //urlPartstart + urlPartfinal + photo_reference ==> URL LINK TO PHOTO
    //LINK TO SPECIFIC PLACE ->https://www.google.com/maps/search/?api=1&query=Google&query_place_id=SomeID --> ACCORDING TO PLACE ID

    //VARIABLES
    public String urlPartstart = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    public String urlPartfinal = "&key=AIzaSyC2BVTP-eAHnax9wg1sqAbyfMLgUSuE-PM";
    public ArrayList<TxtResult> txtResults;//list of places results
    public Context context;
    public float [] txtDistanceResults = new float[10];//10 random number.you need any number higher than 3
    public String placeId;
    public String linkId = "https://www.google.com/maps/search/?api=1&query=Google&query_place_id=";
    public String linkWithId;
    public String preference = "kilometre";//default value of distance measurement units
    public double roundedDis;//rounded value of distance (less numbers after dot)




    //constructor
    public TxtAdapter(ArrayList<TxtResult> txtResults, Context context) {
        this.txtResults = txtResults;
        this.context = context;
    }

    @Override
    public TxtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // generate view and viewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.txt_result_item_layout, null);//getContext refers to get value of context variable
        TxtHolder txtHolder = new TxtHolder(view);
        return txtHolder;//In the onCreateViewHolder() method we inflate the row layout as a View and return as ViewHolder object.
    }

    @Override
    public void onBindViewHolder(TxtHolder holder, int position) {
        //bind data to view holder
        TxtResult txtResultobject = txtResults.get(position);
        holder.bindDataFromArrayToView(txtResultobject);
    }

    @Override
    public int getItemCount() {
        return txtResults.size();

    }

    //create inner class  YourInnerClassViewHolder extends RecyclerView.ViewHolder => implement constructor
    class TxtHolder extends RecyclerView.ViewHolder {
        View myView;

        //override default constructor
        public TxtHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void bindDataFromArrayToView(final TxtResult txtResultCurrent) {


            TextView resultName = (TextView) myView.findViewById(R.id.resultName);//NAME
            resultName.setText(txtResultCurrent.name);



            if(txtResultCurrent.formatted_address==null) {
                Log.i("ADDRESS","EMPTY");
            }else {
                TextView resultAddress = (TextView) myView.findViewById(R.id.resultAddress);//ADDRESS
                resultAddress.setText(txtResultCurrent.formatted_address);
            }

            //latitude comes before longitude
            final double temporaryLatitude = txtResultCurrent.geometry.location.lat;
            final double temporaryLongitude = txtResultCurrent.geometry.location.lng;

            TextView resultTxtDistance = (TextView) myView.findViewById(R.id.txtDistance);//DISTANCE
            //method calculates distances between two points according to their latitude and longitude
            Location.distanceBetween(MainActivity.upLatitude,MainActivity.upLongitude,temporaryLatitude,temporaryLongitude,txtDistanceResults);//DEFAULT IN KILOMETERS


            //FETCH SETTINGS RESULTS FROM SharedPreferences
            //set Shared Preferences (there you save settings values )
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //get value from SharedPrefs
            preference = sharedPreferences.getString("list_preference_units", "kilometre");//list_preference_units is key(id) of preference item in preferences.xml

            //check settings results
            if(preference.equals("kilometre") ) {
                roundedDis =  (double)Math.round( (txtDistanceResults[0]/1000 ) * 100d) / 100d;//number of zeros must be same in and outside parenthesis.number of zeroes equals to number of numbers after dot that will remain after rounding up
                  resultTxtDistance.setText(roundedDis + " km ");//km
            }else{//mile
                roundedDis =  (double)Math.round( (((txtDistanceResults[0]*0.621371)/1000 ) ) * 100d) / 100d;//number of zeros must be same in and outside parenthesis.number of zeroes equals to number of numbers after dot that will remain after rounding up
                   resultTxtDistance.setText(roundedDis + "  miles");//miles
            }

               //fetch place id
               placeId =  txtResultCurrent.place_id;  //PLACE ID
               linkWithId = linkId + placeId;//complete url


            ImageView resultImage = (ImageView) myView.findViewById(R.id.resultImage);//IMAGE


            final ProgressBar progressBar = (ProgressBar) myView.findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);////make progress bar visible

           //we check photos in case some of json objects have no photo_reference       //PHOTOS
            if (txtResultCurrent.photos == null)
                progressBar.setVisibility(View.GONE);//means there no String photo_reference
            else if (txtResultCurrent.photos.isEmpty())
                progressBar.setVisibility(View.GONE);//there is string but its empty " "
            else {
                String photo_reference = txtResultCurrent.photos.get(0).photo_reference;////we fetch first image (i = 0) from array of photos
                String urlLinktoPhoto = urlPartstart + photo_reference + urlPartfinal;


                /**GLIDE*/
                Glide.with(context).load(urlLinktoPhoto).override(500, 500)
                        .centerCrop().listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);//removes progress bar if there was exception
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);////removes progress bar if picture finished loading
                        return false;

                    }
                }).into(resultImage);//SET IMAGE THROUGH GLIDE
            }

            //MAKE RECYCLER TEXT CLICKABLE
            //CLICK
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentChanger fragmentChanger = (FragmentChanger)context;//CASTING OF CONTEXT TO INTERFACE
                    fragmentChanger.changeFragments(temporaryLatitude,temporaryLongitude,txtResultCurrent.name);
                }
            });
            //LONG CLICK
            myView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("which one suits you best?");//dialog message
                    builder.setPositiveButton("Save to Favourites", new DialogInterface.OnClickListener() {//SAVE OPTION
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (txtResultCurrent.photos == null || txtResultCurrent.photos.isEmpty()) {//there no photo reference
                                ResultDB altResultDB = new ResultDB(txtResultCurrent.name,txtResultCurrent.formatted_address,txtResultCurrent.geometry.location.lat,txtResultCurrent.geometry.location.lng,"no photo");
                                altResultDB.save();
                            }else {//there is photo reference
                                ResultDB resultDB = new ResultDB(txtResultCurrent.name,txtResultCurrent.formatted_address,txtResultCurrent.geometry.location.lat,txtResultCurrent.geometry.location.lng,txtResultCurrent.photos.get(0).photo_reference);
                                resultDB.save();
                            }

                            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();
                        }
                    });


                    //SHARE BUTTON
                    builder.setNegativeButton("Share", new DialogInterface.OnClickListener() {//SHARE OPTION
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //share
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT,"visit this cool place :) " + " " + linkWithId);
                            intent.putExtra(Intent.EXTRA_SUBJECT,"check out this cool place !" );
                            context.startActivity(Intent.createChooser(intent,"SHARE USING"));

                            Toast.makeText(context,"shared",Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }
    }
}


