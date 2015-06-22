package app.com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private String postcode;
    private String mUnits;
    private ArrayAdapter<String> mForecastAdapter;
    private int mNumDays = 7;
    private String[] mDayArr = new String[mNumDays];
    private String[] mMonthAndDayArr = new String[mNumDays];
    private int[] mHighArr = new int[mNumDays];
    private int[] mLowArr = new int[mNumDays];
    private String[] mDescriptionArr = new String[mNumDays];
    private int[] mHumidityArr = new int[mNumDays];
    private int[] mPressureArr = new int[mNumDays];
    private String[] mWindArr = new String[mNumDays];

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("IntentDay", mDayArr[i]);
                intent.putExtra("IntentMonthAndDay", mMonthAndDayArr[i]);
                intent.putExtra("IntentHigh", mHighArr[i]);
                intent.putExtra("IntentLow", mLowArr[i]);
                intent.putExtra("IntentDescription", mDescriptionArr[i]);
                intent.putExtra("IntentHumidity", mHumidityArr[i]);
                intent.putExtra("IntentPressure", mPressureArr[i]);
                intent.putExtra("IntentWind", mWindArr[i]);

                startActivity(intent);
            }
        });

        return rootView;
    }

    public void updateWeather() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        postcode = preference.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        mUnits = preference.getString(getString(R.string.pref_unit_key),getString(R.string.pref_unit_default));
        new FetchWeatherTask().execute(postcode);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            String[] result;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(mNumDays))
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                result = getWeatherDataFromJson(forecastJsonStr);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                List<String> weekForecast = new ArrayList<>(Arrays.asList(strings));
                mForecastAdapter.clear();
                mForecastAdapter.addAll(weekForecast);
            }
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
             * so for convenience we're breaking it out into its own method now.
             */

        private String getReadableDateString(long time, String format) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat(format);
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        private String windDirection(double wind, double degrees) {
            String direction = "";

            if (degrees >= 348.75 || degrees < 11.25)
                direction = "N";
            else if (degrees >= 11.25 && degrees < 33.75)
                direction = "NNE";
            else if (degrees >= 33.75 && degrees < 56.25)
                direction = "NE";
            else if (degrees >= 56.25 && degrees < 78.75)
                direction = "ENE";
            else if (degrees >= 78.75 && degrees < 101.25)
                direction = "E";
            else if (degrees >= 101.25 && degrees < 123.75)
                direction = "ESE";
            else if (degrees >= 123.75 && degrees < 146.25)
                direction = "SE";
            else if (degrees >= 146.25 && degrees < 168.75)
                direction = "SSE";
            else if (degrees >= 168.75 && degrees < 191.25)
                direction = "S";
            else if (degrees >= 191.25 && degrees < 213.75)
                direction = "SSW";
            else if (degrees >= 213.75 && degrees < 236.25)
                direction = "SW";
            else if (degrees >= 236.25 && degrees < 258.75)
                direction = "WSW";
            else if (degrees >= 258.75 && degrees < 281.25)
                direction = "W";
            else if (degrees >= 281.25 && degrees < 303.75)
                direction = "WNW";
            else if (degrees >= 303.75 && degrees < 326.25)
                direction = "NW";
            else if (degrees >= 326.25 && degrees < 348.75)
                direction = "NNW";

            return wind + " km/h " + direction;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_PRESSURE = "pressure";
            final String OWM_WIND = "speed";
            final String OWM_DESCRIPTION = "main";
            final String OWM_WIND_DIRECTION = "deg";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[mNumDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime, "EEE MMM dd");

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);

                Double high = new Double(Math.round(temperatureObject.getDouble(OWM_MAX)));
                Double low = new Double(Math.round(temperatureObject.getDouble(OWM_MIN)));
                Double pressure = new Double(Math.round(dayForecast.getDouble(OWM_PRESSURE)));
                Double humidity = new Double(Math.round(dayForecast.getDouble(OWM_HUMIDITY)));
                mDayArr[i] = getReadableDateString(dateTime, "EEEE");
                mMonthAndDayArr[i] = getReadableDateString(dateTime, "MMMM dd");
                if (mUnits.equals("imperial")) {
                    mHighArr[i] = convertMetricToInperial(high).intValue();
                    mLowArr[i] = convertMetricToInperial(low).intValue();
                } else {
                    mHighArr[i] = high.intValue();
                    mLowArr[i] = low.intValue();
                }
                mDescriptionArr[i] = weatherObject.getString(OWM_DESCRIPTION);
                mHumidityArr[i] = humidity.intValue();
                mPressureArr[i] = pressure.intValue();
                mWindArr[i] = windDirection(Math.round(dayForecast.getDouble(OWM_WIND)),
                        dayForecast.getDouble(OWM_WIND_DIRECTION));
                highAndLow = formatHighLows(mHighArr[i], mLowArr[i]);
                resultStrs[i] = day + " - " + mDescriptionArr[i] + " - " + highAndLow;
            }

            return resultStrs;
        }

        private Double convertMetricToInperial(Double unit) {
            Double converted = ((unit / 5) * 9) + 32;
            return converted;
        }
    }
}