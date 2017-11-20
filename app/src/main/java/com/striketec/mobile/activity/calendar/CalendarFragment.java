package com.striketec.mobile.activity.calendar;

import com.calendarlibarary.caldroid.CaldroidFragment;
import com.striketec.mobile.adapters.CalendarCustomAdapter;

public class CalendarFragment extends CaldroidFragment {

    public CalendarCustomAdapter customAdapter;

    @Override
    public CalendarCustomAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        customAdapter =  new CalendarCustomAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);

        return customAdapter;
    }
}
