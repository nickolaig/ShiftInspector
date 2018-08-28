package com.ortaib.shiftinspector.Logic;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ortaib.shiftinspector.R;
import com.ortaib.shiftinspector.Views.ShiftView;

import java.util.ArrayList;

/**
 * Created by Ortaib on 05/08/2018.
 */

public class ShiftAdapter extends BaseAdapter{
    private ArrayList<Shift> shifts;
    private Context context;

    public ShiftAdapter(Context context, ArrayList<Shift> shifts) {
        this.shifts = shifts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return shifts.size();
    }

    @Override
    public Object getItem(int position) {
        return shifts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Shift shift = (Shift)getItem(position);
        ShiftView shiftView = new ShiftView(context,shift);
        return shiftView;
    }
}
