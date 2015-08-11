package com.goeuro.pinke;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.goeuro.pinke.Data.Position;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class GoEuroMainActivityFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, FilterRequestNetworkHandler.LocationFilterResultListener {

    private ListView listView;
    private EditText filterview;
    private TextView pickToDate;
    private TextView pickFromDate;
    private View listHeader;
    private View searchButton;

    private Calendar fromDateSet;
    private Calendar toDateSet;

    /**
     * Remember last location for set list item selection and search enable button
     * default value -1 no location selected
     */
    private int lastLocationId = -1;
    private String searchDisabled;
    private FilterRequestNetworkHandler networkHandler;

    public GoEuroMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_go_euro_main, container, false);

        listView = (ListView) mainView.findViewById(R.id.listView);
        TextView v = new TextView(getActivity());
        v.setHeight(100);
        v.setBackgroundResource(R.color.itemUnselected);
        listView.addFooterView(v);
        listView.setDivider(null);
        listHeader = mainView.findViewById(R.id.listHeader);
        listHeader.setVisibility(View.INVISIBLE);
        searchButton = mainView.findViewById(R.id.search);

        searchButton.setOnClickListener(this);


        searchButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                searchButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int widthToTranslate = searchButton.getWidth();

                TranslateAnimation translation;
                translation = new TranslateAnimation(-widthToTranslate, 0, 0, 0);
                translation.setStartOffset(500);
                translation.setDuration(2000);
                translation.setFillAfter(true);
                translation.setInterpolator(new BounceInterpolator());
                filterview.startAnimation(translation);


            }
        });
        filterview = (EditText) mainView.findViewById(R.id.searchFilter);
        pickFromDate = (TextView) mainView.findViewById(R.id.pickFromDate);
        pickFromDate.setOnClickListener(this);
        pickToDate = (TextView) mainView.findViewById(R.id.pickToDate);
        pickToDate.setOnClickListener(this);


        searchDisabled = getActivity().getString(R.string.search_disabled);

        filterview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastLocationId = -1;
                networkHandler.setFilterText(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        networkHandler = new FilterRequestNetworkHandler(getActivity(), new Handler()).setLocationFilterResultListener(this);
        return mainView;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onClick(View v) {

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        int id = v.getId();
        switch (id) {
            case R.id.search:
                Toast.makeText(getActivity(), searchDisabled, Toast.LENGTH_LONG).show();
                break;
            case R.id.pickFromDate:
                pickADate(true);
                break;
            case R.id.pickToDate:
                pickADate(false);
                break;
        }
    }

    @Override
    public void onNewFilterResult(Position[] newPositions) {
        lastLocationId = -1;
        final Activity activity = getActivity();
        if (activity != null) {
            try {
                listHeader.setVisibility(newPositions.length == 0 ? View.INVISIBLE : View.VISIBLE);
                listView.setVisibility(newPositions.length == 0 ? View.INVISIBLE : View.VISIBLE);
                listView.setAdapter(new LocationAdapter(activity, 0, newPositions));
                checkSetSearchVisib();
            } catch (Throwable throwable) {

            }


        }

    }

    class LocationAdapter extends ArrayAdapter<Position> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.location_list_item, null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        if (lastLocationId == viewHolder.locationId) {
                            lastLocationId = -1;
                        } else {
                            lastLocationId = viewHolder.locationId;
                        }
                        checkSetSearchVisib();
                        notifyDataSetChanged();
                    }
                });
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.distance = (TextView) convertView.findViewById(R.id.listItemDistance);
                viewHolder.country = (TextView) convertView.findViewById(R.id.listItemCountry);
                viewHolder.city = (TextView) convertView.findViewById(R.id.listItemCity);
                convertView.setTag(viewHolder);
            }


            Position currentPosition = getItem(position);
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            String[] fullname = currentPosition.getFullName().split(", ");
            viewHolder.city.setText(fullname[0]);
            viewHolder.country.setText(fullname[1]);
            if (networkHandler.lastBestLocation != null) {
                viewHolder.distance.setText((int) (networkHandler.lastBestLocation.distanceTo(currentPosition.getLocation()) / 1000) + "\nKM");
            } //TODO else...
            viewHolder.locationId = currentPosition.get_id();

            convertView.setBackgroundResource(lastLocationId == viewHolder.locationId ? R.color.itemSelected : (R.color.itemUnselected));

            return convertView;
        }

        public LocationAdapter(Context context, int resource, Position[] objects) {
            super(context, resource, objects);
        }


    }


    private void pickADate(final boolean isFrom) {
        // Show date picker dialog.
        CalendarDatePickerDialog dialog = new CalendarDatePickerDialog();
        dialog.setYearRange(2015, 2016);
        dialog.setOnDateSetListener(new CalendarDatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                // Set date from user input.
                Calendar date = Calendar.getInstance();
                date.set(Calendar.HOUR_OF_DAY, 9);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthOfYear);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                boolean reset = false;
                if (fromDateSet != null && toDateSet != null) {
                    if (isFrom) {
                        reset = date.compareTo(toDateSet) > 0;
                    } else {
                        reset = date.compareTo(fromDateSet) < 0;
                    }
                }

                if (reset) {
                    pickFromDate.setText("");
                    pickToDate.setText("");

                    Toast.makeText(getActivity(), "Dates was reset", Toast.LENGTH_LONG).show();
                }

                DateFormat dateFormat = new SimpleDateFormat("dd\\MMM\\yyyy");

                if (isFrom) {
                    fromDateSet = date;
                    pickFromDate.setText(dateFormat.format(date.getTime()));
                } else {
                    toDateSet = date;
                    pickToDate.setText(dateFormat.format(date.getTime()));
                }

                checkSetSearchVisib();
            }
        });

        dialog.show(getActivity().getSupportFragmentManager(), "");
    }

    boolean isSearchOut = true;

    private void checkSetSearchVisib() {
        TranslateAnimation translation;
        if (fromDateSet != null && toDateSet != null && lastLocationId != -1) {
            if (!isSearchOut) {
                return;
            }
            isSearchOut = false;
            translation = new TranslateAnimation(-searchButton.getWidth(), 0, 0, 0);
        } else {
            if (isSearchOut) {
                return;
            }
            isSearchOut = true;
            translation = new TranslateAnimation(0, -searchButton.getWidth(), 0, 0);
        }

        translation.setStartOffset(150);
        translation.setDuration(2000);
        translation.setFillAfter(true);
        translation.setInterpolator(new BounceInterpolator());
        searchButton.startAnimation(translation);


    }

    private class ViewHolder {
        TextView city;
        TextView distance;
        TextView country;
        int locationId;
    }
}



