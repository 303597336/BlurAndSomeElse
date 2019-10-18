package com.testdemo.testNCalendar

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.necer.calendar.Miui10Calendar
import com.necer.enumeration.CalendarState
import com.testdemo.R
import org.joda.time.LocalDate

class TestNCalendarAct : Activity() {

    private lateinit var miui10Calendar: Miui10Calendar
    private lateinit var mTvCalendarDate: TextView
    private lateinit var mTvCalendarYear: TextView
    private lateinit var mIvCalendarYearPre: ImageView
    private lateinit var mIvCalendarYearNext: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_ncalendar)

        miui10Calendar = findViewById(R.id.miui10Calendar)
        mTvCalendarDate = findViewById(R.id.tv_calendar_date)
        mTvCalendarYear = findViewById(R.id.tv_calendar_year)
        mIvCalendarYearPre = findViewById(R.id.iv_calendar_year_pre)
        mIvCalendarYearNext = findViewById(R.id.iv_calendar_year_next)

        val madTalkPainter = MadTalkPainter(miui10Calendar)

        val pointList = arrayListOf("2019-10-01", "2019-11-01", "2019-10-17"
                , "2019-09-23", "2019-11-03", "2019-10-16")
        madTalkPainter.setPointList(pointList)

        val holidayList = arrayListOf("2019-09-01", "2019-11-01", "2019-12-02")
        val workdayList = arrayListOf("2019-10-01", "2019-11-02", "2019-12-01")
        madTalkPainter.setLegalHolidayList(holidayList, workdayList)


        miui10Calendar.setOnCalendarChangedListener { baseCalendar, year, month, localDate ->
//            mTvCalendarDate.text = "${year}年${month}月 当前页面选中 $localDate"
            mTvCalendarDate.text = localDate.toString("今天：yyyy年MM月dd日,E")
            mTvCalendarYear.text = year.toString()
        }

        findViewById<ImageView>(R.id.iv_icon).setOnClickListener {
            if (miui10Calendar.calendarState == CalendarState.MONTH) {
                miui10Calendar.toWeek()
            } else {
                miui10Calendar.toMonth()
            }
        }


        miui10Calendar.calendarPainter = madTalkPainter

        val jumpYear = { jumpMode: Int ->
            miui10Calendar.currectSelectDateList?.let {
                if (it.size > 0) {
                    it[0]?.let { selectedLocalDate ->
                        val localDateToJump = when (jumpMode) {
                            1 -> selectedLocalDate.minusYears(1)
                            2 -> selectedLocalDate.plusYears(1)
                            else -> {
                                selectedLocalDate.withYear(LocalDate().year)
                            }
                        }
                        Log.d("greyson", localDateToJump.toString("yyyy-MM-dd"))
//                        miui10Calendar.jumpDate("${localDateToJump.year}-${localDateToJump.monthOfYear}-${localDateToJump.dayOfMonth}")
                        miui10Calendar.jumpDate(localDateToJump.toString("yyyy-MM-dd"))
                    }
                }
            }
        }

        mIvCalendarYearPre.setOnClickListener {
            jumpYear(1)
        }

        mIvCalendarYearNext.setOnClickListener {
            jumpYear(2)
        }

        mTvCalendarYear.setOnClickListener {
            jumpYear(0)
        }

        mTvCalendarDate.setOnClickListener {
            miui10Calendar.jumpDate(LocalDate().toString("yyyy-MM-dd"))
        }


        /**   **********************/
        miui10Calendar.testKoMeth { i, f ->
            print("invoke my own method!")
            i == f.toInt()
        }
    }

    public fun <T> T.testKoMeth(funtion: (Int, Float) -> Boolean) {
        println("testKoMeth's result: ${funtion(1, 1.1f)}")
    }
}