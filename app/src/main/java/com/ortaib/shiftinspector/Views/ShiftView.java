package com.ortaib.shiftinspector.Views;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ortaib.shiftinspector.Logic.Shift;
import com.ortaib.shiftinspector.R;

public class ShiftView extends LinearLayout {
    //shift identifier
    private Shift shift;

    //shift views
    private TextView dateStarted, dateFinished;
    private TextView moneyEarned;
    private ImageView location;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ShiftView(Context context, Shift shift) {
        super(context);
        this.shift = shift;

        dateStarted = new TextView(context);
        dateFinished = new TextView(context);
        moneyEarned = new TextView(context);
        location = new ImageView(context);

        this.setOrientation(HORIZONTAL);
        this.setWeightSum(1);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.activatedBackgroundIndicator, typedValue, true);
        if (typedValue.resourceId != 0) {
            this.setBackgroundResource(typedValue.resourceId);
        } else {
            this.setBackgroundColor(typedValue.data);
        }

        //Set the status color
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.03f);

        //Set date text view
        param = new LinearLayout.LayoutParams(0

                , 120, 0.325f);
        param.setMargins(10, 0, 30
                , 20);
        //dateStarted.setTextAppearance(R.style.TextStyle);
        dateStarted.setLayoutParams(param);
        //name.setHeight(LayoutParams.MATCH_PARENT);
        dateStarted.setGravity(Gravity.CENTER_VERTICAL
        );
        dateStarted.setText(shift.getStartTime().toString());
        addView(dateStarted);

        dateFinished.setLayoutParams(param);
        //name.setHeight(LayoutParams.MATCH_PARENT);
        dateFinished.setGravity(Gravity.CENTER_VERTICAL);
        if (shift.getEndTime() != null) {
            dateFinished.setText(shift.getEndTime().toString());
        }
        addView(dateFinished);

        moneyEarned.setLayoutParams(param);
        moneyEarned.setGravity(Gravity.CENTER_VERTICAL);
        moneyEarned.setText(Double.toString(shift.getMoneyEarned()) + " NIS");
        addView(moneyEarned);

        param = new LinearLayout.LayoutParams(80, 80
                , 0.05f);
        param.setMargins(0, 15, 10, 0);
        location.setLayoutParams(param);
        if (shift.getStartLat() != null) {
            location.setImageResource(R.mipmap.location);
        }
        addView(location);

    }

    public Shift getShift() {
        return shift;
    }
}

