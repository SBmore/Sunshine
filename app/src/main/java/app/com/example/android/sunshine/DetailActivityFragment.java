package app.com.example.android.sunshine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String mDay;
    private String mMonthAndDay;
    private int mHigh;
    private int mLow;
    private String mDescription;
    private int mHumidity;
    private int mPressure;
    private String mWind;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDay = getActivity().getIntent().getStringExtra("IntentDay");
        mMonthAndDay = getActivity().getIntent().getStringExtra("IntentMonthAndDay");
        mHigh = getActivity().getIntent().getIntExtra("IntentHigh", 0);
        mLow = getActivity().getIntent().getIntExtra("IntentLow", 0);
        mDescription = getActivity().getIntent().getStringExtra("IntentDescription");
        mHumidity = getActivity().getIntent().getIntExtra("IntentHumidity", 0);
        mPressure = getActivity().getIntent().getIntExtra("IntentPressure", 0);
        mWind = getActivity().getIntent().getStringExtra("IntentWind");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView textDayView = (TextView) rootView.findViewById(R.id.whatDay);
        TextView textMonthAndDayView = (TextView) rootView.findViewById(R.id.monthAndDay);
        TextView textHighView = (TextView) rootView.findViewById(R.id.maxTemp);
        TextView textLowView = (TextView) rootView.findViewById(R.id.minTemp);
        TextView textDescriptionView = (TextView) rootView.findViewById(R.id.forecast);
        TextView textHumidityView = (TextView) rootView.findViewById(R.id.humidity);
        TextView textPressureView = (TextView) rootView.findViewById(R.id.pressure);
        TextView textWindView = (TextView) rootView.findViewById(R.id.wind);
        textDayView.setText(mDay);
        textMonthAndDayView.setText(mMonthAndDay);
        textHighView.setText(mHigh + "");
        textLowView.setText(mLow + "°");
        textDescriptionView.setText(mDescription);
        textHumidityView.setText(mHumidity + " %");
        textPressureView.setText(mPressure + " hPa");
        textWindView.setText(mWind);

        ImageView weatherImage = (ImageView) rootView.findViewById(R.id.forecastImg);
        Context context = weatherImage.getContext();
        int id = context.getResources().getIdentifier("art_" + mDescription.toLowerCase(), "drawable", context.getPackageName());
        weatherImage.setImageResource(id);

        return rootView;
    }
}
