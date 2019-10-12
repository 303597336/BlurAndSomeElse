package com.testdemo.testDatePicker;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.testdemo.R;
import com.testdemo.testDatePicker.datepicker.bizs.decors.DPDecor;
import com.testdemo.testDatePicker.datepicker.bizs.languages.DPLManager;
import com.testdemo.testDatePicker.datepicker.cons.DPMode;
import com.testdemo.testDatePicker.datepicker.views.DatePicker;

import java.util.Date;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CreateInfoAct extends Activity {

    private RadioGroup rgSwitchDateTime;
    private RadioButton cbDateBtn;
    private RadioButton cbTimeBtn;
    private TextView mTvConfirm;
    private LinearLayout mLlWeek;
    private View vInflater;
    private MyCalendarPicker myCalendarPicker;
    private MyTimePicker myTimePicker;

    private String selectedDate = "2019-06-02";
    private String selectedTime = "17:15";

    private TextView mTvDate;

    DPLManager mDPLManager = DPLManager.getInstance();
    private DatePickDialog datePickDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_create_info);

        ConstraintLayout clDatePicker = findViewById(R.id.cl_select_date);
        rgSwitchDateTime = findViewById(R.id.rg_switch_date_time);
        cbDateBtn = findViewById(R.id.cb_date_btn);
        cbTimeBtn = findViewById(R.id.cb_time_btn);
        mTvConfirm = findViewById(R.id.tv_confirm);

        vInflater = findViewById(R.id.v_inflater);
        myCalendarPicker = findViewById(R.id.myCalendarPicker);
        myTimePicker = findViewById(R.id.myTimePicker);
        mTvDate = findViewById(R.id.tv_date);

        mLlWeek = findViewById(R.id.ll_week);

        LinearLayout.LayoutParams lpWeek = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpWeek.weight = 1;
        for (int i = 0; i < 7; i++) {
            TextView tvWeek = new TextView(this);
            tvWeek.setText(mDPLManager.titleWeek()[i]);
            tvWeek.setGravity(Gravity.CENTER);
            tvWeek.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tvWeek.setTextColor(Color.parseColor("#283851"));
            mLlWeek.addView(tvWeek, lpWeek);
        }

        rgSwitchDateTime.setOnCheckedChangeListener((group, checkedId) -> {
            if (cbDateBtn.getId() == checkedId) {
                mLlWeek.setVisibility(View.VISIBLE);
                clDatePicker.setBackgroundColor(getResources().getColor(R.color.grey_date_picker));
                checkCbDateBtn(true);

                vInflater.getLayoutParams().height = 0;

                myCalendarPicker.setVisibility(View.VISIBLE);
                myTimePicker.setVisibility(View.GONE);

            } else if (cbTimeBtn.getId() == checkedId) {
                mLlWeek.setVisibility(View.GONE);
                clDatePicker.setBackgroundColor(getResources().getColor(R.color.white));
                checkCbDateBtn(false);

                vInflater.getLayoutParams().height = myCalendarPicker.getHeight();

                myCalendarPicker.setVisibility(View.GONE);
                myTimePicker.setVisibility(View.VISIBLE);
            }
        });

        mTvConfirm.setOnClickListener(clickView -> {
            Toast.makeText(this, selectedDate + " " + selectedTime, Toast.LENGTH_SHORT).show();
            mTvDate.setText(selectedDate + " " + selectedTime);
        });

        myCalendarPicker.setOnDayClickListener(((clickDay, selectedDays) -> {
            if (TextUtils.isEmpty(clickDay)) {
                return;
            }

            selectedDate = clickDay;
            String[] dateDatas = clickDay.split("-");
            cbDateBtn.setText(getString(R.string.text_calendar_cn, dateDatas[0], dateDatas[1], dateDatas[2]));
        }));

        myTimePicker.setOnWheelListener(new MyTimePicker.OnWheelListener() {
            @Override
            public void onHourWheeled(int index, String hour) {
                String timeStr = cbTimeBtn.getText().toString();
                selectedTime = timeStr.replaceFirst("^\\w{2}(?=:)", hour);
                cbTimeBtn.setText(selectedTime);
            }

            @Override
            public void onMinuteWheeled(int index, String minute) {
                String timeStr = cbTimeBtn.getText().toString();
                selectedTime = timeStr.replaceFirst("(?<=:)\\w{2}$", minute);
                cbTimeBtn.setText(selectedTime);
            }
        });
        myCalendarPicker.setShowMonth(2019, 7);

        /***********************************************************/

        datePickDialog = new DatePickDialog(this, DatePickDialog.MODE_DATE_AND_TIME);
        datePickDialog.setOnDatePickListener((dateStr, timeStr) -> {
            selectedDate = dateStr;
            selectedTime = timeStr;
            mTvDate.setText(dateStr + " " + timeStr);
        });

        /*********************************************************/

        DatePicker picker = (DatePicker) findViewById(R.id.main_dp);
        picker.setDate(2019, 7);
        picker.setFestivalDisplay(false);
        picker.setTodayDisplay(false);
        picker.setHolidayDisplay(false);
        picker.setDeferredDisplay(false);
        picker.setMode(DPMode.MULTIPLE);
        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorTL(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorTL(canvas, rect, paint, data);
                switch (data) {
                    case "2019-07-5":
                    case "2019-07-7":
                    case "2019-07-9":
                    case "2019-07-11":
                        paint.setColor(Color.GREEN);
                        canvas.drawRect(rect, paint);
                        break;
                    default:
                        paint.setColor(Color.RED);
                        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
                        break;
                }
            }

            @Override
            public void drawDecorTR(Canvas canvas, Rect rect, Paint paint, String data) {
                super.drawDecorTR(canvas, rect, paint, data);
                switch (data) {
                    case "2019-07-10":
                    case "2019-07-11":
                    case "2019-07-12":
                        paint.setColor(Color.BLUE);
                        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, paint);
                        break;
                    default:
                        paint.setColor(Color.YELLOW);
                        canvas.drawRect(rect, paint);
                        break;
                }
            }
        });

        /********************************************************/
        System.out.println("greyson final locale: " + Locale.getDefault());
        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList locales = Resources.getSystem().getConfiguration().getLocales();
            for (int i = 0; i < locales.size(); i++) {
                System.out.println("greyson locale: " + locales.get(i));
            }
        } else {
            System.out.println(Locale.getDefault());
        }
    }

    private void checkCbDateBtn(boolean isChecked) {
        if (isChecked) {
            cbTimeBtn.setTextColor(getResources().getColor(R.color.blue_date_picker));
            cbDateBtn.setTextColor(Color.WHITE);
        } else {
            cbTimeBtn.setTextColor(Color.WHITE);
            cbDateBtn.setTextColor(getResources().getColor(R.color.blue_date_picker));
        }
    }

    public void onClick(View view) {
        datePickDialog.show();
//        datePickDialog.setSelectedDate(selectedDate, selectedTime);
//        datePickDialog.setSelectedDate(new Date());
    }
}
