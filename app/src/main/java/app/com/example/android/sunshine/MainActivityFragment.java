package app.com.example.android.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container);

        String[] weatherArray = new String[7];
        String[] dayArray = {"Sunday", "Monday", "Tuesday","Wednesday","Thursday","Friday","Saturday"};
        String[] forecastArray = {"Sunny", "Foggy", "Cloudy", "Windy"};
        Calendar calendar = Calendar.getInstance();
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int y = dayNum;

        for(int x = 0; x < weatherArray.length; x++) {
            int farenheight = (int) (Math.random() * 100);
            int celcius = ((farenheight - 32) / 9) * 5;
            int forecast = (int) (Math.random() * 3);
            String day;

            if (y == dayNum) {
                day = "Today";
            } else if (y == dayNum + 1) {
                day = "Tomorrow";
                dayNum = 8;
            } else if (y >= dayArray.length){
                y = 0;
                day = dayArray[y];
            } else {
                day = dayArray[y];
            }

            weatherArray[x] = day + " - " + forecastArray[forecast] + " - " + farenheight + " / " + celcius;
            y++;
        }

        List<String> weekForecast = new ArrayList<>(
                Arrays.asList(weatherArray));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

//        FrameLayout frame = (FrameLayout) this.findViewById(R.id.container);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return rootView;
    }
}
