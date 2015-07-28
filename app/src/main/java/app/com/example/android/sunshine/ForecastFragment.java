package app.com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


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
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        postcode = preference.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        mUnits = preference.getString(getString(R.string.pref_unit_key),getString(R.string.pref_unit_default));
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }
}