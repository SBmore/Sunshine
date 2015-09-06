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
import android.widget.ImageView;
import android.widget.TextView;

import app.com.example.android.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mForecastStr;
    private View mRootView;
    private Uri mUri;
    static final String DETAIL_URI = "URI";

    private ShareActionProvider mShareActionProvider;
    private final static int DETAIL_LOADER_ID = 0;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_HUMIDITY = 5;
    static final int COL_PRESSURE = 6;
    static final int COL_WIND_SPEED = 7;
    static final int COL_DEGREES = 8;
    static final int COL_WEATHER_ID = 9;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
//        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mForecastStr != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ViewHolder viewHolder = new ViewHolder(mRootView);
        mRootView.setTag(viewHolder);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        if (!data.moveToFirst()) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) mRootView.getTag();
        int weatherID = data.getInt(COL_WEATHER_ID);

        int icon = Utility.getArtResourceForWeatherCondition(weatherID);
        viewHolder.iconView.setImageResource(icon);

        String dayName = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));
        viewHolder.dayView.setText(dayName);

        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        viewHolder.dateView.setText(dateString);

        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP));
        viewHolder.highTempView.setText(high);

        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP));
        viewHolder.lowTempView.setText(low);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(weatherDescription);
        viewHolder.iconView.setContentDescription(weatherDescription);

        String humidity = "Humidity: " + data.getString(COL_HUMIDITY);
        viewHolder.humidityView.setText(humidity);

        String pressure = "Pressure: " + data.getString(COL_PRESSURE);
        viewHolder.pressureView.setText(pressure);

        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WIND_SPEED), data.getFloat(COL_DEGREES));
        viewHolder.windView.setText(wind);

        // We still need this for the share intent
        mForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
//        mForecastAdapter.swapCursor(null);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dayView;
        public final TextView dateView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView descriptionView;
        public final TextView humidityView;
        public final TextView pressureView;
        public final TextView windView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.detail_item_icon);
            dayView = (TextView) view.findViewById(R.id.detail_day_textview);
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
            descriptionView = (TextView) view.findViewById(R.id.detail_description_textview);
            humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
            pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
        }
    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
        }
    }
}
