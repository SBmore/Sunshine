package app.com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.com.example.android.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    public String mDay;
    public String mMonthAndDay;
    private String mForecastStr;
    private int mHigh;
    private int mLow;
    private String mDescription;
    private int mHumidity;
    private int mPressure;
    private String mWind;
    private ShareActionProvider mShareActionProvider;
    private final static int DETAIL_LOADER_ID = 1;
    private Intent mShareIntent = new Intent(Intent.ACTION_SEND);
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };
    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getActivity().getIntent() != null) {
            mForecastStr = getActivity().getIntent().getDataString();
        }
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            String concatCast = mDay + " " + mMonthAndDay + " - " + mDescription + " - " + mHigh + "  / " + mLow;
            String forecast = concatCast + " #SunshineApp";
            mShareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            mShareIntent.setType("text/plain");
            mShareIntent.putExtra(Intent.EXTRA_TEXT, forecast);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu,inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
//        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        setShareIntent(mShareIntent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

//        TextView textDayView = (TextView) rootView.findViewById(R.id.whatDay);
//        TextView textMonthAndDayView = (TextView) rootView.findViewById(R.id.monthAndDay);
//        TextView textHighView = (TextView) rootView.findViewById(R.id.maxTemp);
//        TextView textLowView = (TextView) rootView.findViewById(R.id.minTemp);
//        TextView textDescriptionView = (TextView) rootView.findViewById(R.id.forecast);
//        TextView textHumidityView = (TextView) rootView.findViewById(R.id.humidity);
//        TextView textPressureView = (TextView) rootView.findViewById(R.id.pressure);
//        TextView textWindView = (TextView) rootView.findViewById(R.id.wind);
//        textDayView.setText(mForecastStr);
//        textMonthAndDayView.setText(mMonthAndDay);
//        textHighView.setText(mHigh + "");
//        textLowView.setText(mLow + "°");
//        textDescriptionView.setText(mDescription);
//        textHumidityView.setText(mHumidity + " %");
//        textPressureView.setText(mPressure + " hPa");
//        textWindView.setText(mWind);

//        ImageView weatherImage = (ImageView) rootView.findViewById(R.id.forecastImg);
//        Context context = weatherImage.getContext();
//        int id = context.getResources().getIdentifier("art_" + mDescription.toLowerCase(), "drawable", context.getPackageName());
//        weatherImage.setImageResource(id);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.

        Uri detailUri = Uri.parse(mForecastStr);
        return new CursorLoader(getActivity(), detailUri,
                FORECAST_COLUMNS, null, null,
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        TextView tv = (TextView) getActivity().findViewById(R.id.whatDay);

        if (data.moveToFirst()) {
            String weatherDescription = data.getString(ForecastFragment.COL_WEATHER_DESC);
            tv.setText(weatherDescription);
        }


//        mForecastAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
//        mForecastAdapter.swapCursor(null);
    }
}
