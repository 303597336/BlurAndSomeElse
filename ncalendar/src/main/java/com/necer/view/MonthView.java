package com.necer.view;

import android.content.Context;
import android.view.ViewGroup;

import com.necer.utils.CalendarUtil;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2018/9/11.
 * qq群：127278900
 */
public class MonthView extends CalendarView {

    public MonthView(Context context, ViewGroup container) {
        super(context, container);
    }

    @Override
    protected void dealClickDate(LocalDate localDate) {
        /*if (CalendarUtil.isLastMonth(localDate, mInitialDate)) {
            mCalendar.onClickLastMonthDate(localDate);
        } else if (CalendarUtil.isNextMonth(localDate, mInitialDate)) {
            mCalendar.onClickNextMonthDate(localDate);
        } else {*/
        mCalendar.onClickCurrentMonthOrWeekDate(localDate);//点击上或下个月的日期不切换月份
//        }
    }

    @Override
    public boolean isEqualsMonthOrWeek(LocalDate date, LocalDate initialDate) {
        return CalendarUtil.isEqualsMonth(date, initialDate);
    }

    @Override
    public LocalDate getFirstDate() {
        return new LocalDate(mInitialDate.getYear(), mInitialDate.getMonthOfYear(), 1);
    }
}
