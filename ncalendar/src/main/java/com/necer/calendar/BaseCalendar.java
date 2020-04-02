package com.necer.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.necer.adapter.BaseCalendarAdapter;
import com.necer.enumeration.MultipleNumModel;
import com.necer.enumeration.SelectedModel;
import com.necer.listener.OnCalendarChangedListener;
import com.necer.listener.OnCalendarMultipleChangedListener;
import com.necer.listener.OnCalendarPageChangeListener;
import com.necer.listener.OnClickDisableDateListener;
import com.necer.listener.OnMWDateChangeListener;
import com.necer.painter.CalendarPainter;
import com.necer.painter.InnerPainter;
import com.necer.utils.Attrs;
import com.necer.utils.AttrsUtil;
import com.necer.view.CalendarView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Greyson
 */
public abstract class BaseCalendar extends FrameLayout implements ICalendar {

    private ViewPager2 mViewPager;
    private boolean mDirtyCurrentItem = false;
    private int mDirtyItemCount = 0;

    private Context mContext;
    private Attrs mAttrs;

    private SelectedModel mSelectedModel;//选中模式

    private boolean mIsJumpClick;//是否是点击上月、下月或跳转，这个只在默认选中时有用
    private boolean mIsDefaultSelectFirst;//默认选择时，翻页选中第一个日期

    protected OnClickDisableDateListener mOnClickDisableDateListener;//点击区间之外的日期回调
    private OnMWDateChangeListener mOnMWDateChangeListener;//月周切换是折叠中心的回调
    private OnCalendarChangedListener mOnCalendarChangedListener;//单选时回调，当前页面无选中返回null
    private OnCalendarPageChangeListener mOnCalendarPageChangeListener;//日历切换页面时的回调
    private OnCalendarMultipleChangedListener mOnCalendarMultipleChangedListener;//多选时回调

    protected LocalDate mStartDate, mEndDate, mInitializeDate;
    protected CalendarPainter mCalendarPainter;
    private List<LocalDate> mAllSelectDateList;

    private boolean mIsInflateFinish;//是否加载完成，
    private MultipleNumModel mMultipleNumModel;//多选数量模式
    private int mMultipleNum;//多选个数

    public BaseCalendar(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        mViewPager = new ViewPager2(context);
        if (this instanceof MonthCalendar)
            mViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        else mViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        this.mAttrs = AttrsUtil.getAttrs(context, attributeSet);
        this.mContext = context;
        mSelectedModel = SelectedModel.SINGLE_SELECTED;
        mAllSelectDateList = new ArrayList<>();
        mInitializeDate = new LocalDate();
        mStartDate = new LocalDate("1901-01-01");
        mEndDate = new LocalDate("2099-12-31");
        mViewPager.setBackgroundColor(mAttrs.bgCalendarColor);
//        mViewPager.setOffscreenPageLimit(1);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(final int position) {
                if (!drawView(position)) {
                    mDirtyCurrentItem = true;
                    mDirtyItemCount = position;
                } else {
                    if (mOnCalendarPageChangeListener != null) {
                        mOnCalendarPageChangeListener.onCalendarPageChange(BaseCalendar.this);
                    }
                }
            }
        });

        initAdapter();
        addView(mViewPager);
    }

    private void initAdapter() {

        if (mSelectedModel == SelectedModel.SINGLE_SELECTED) {
            mAllSelectDateList.clear();
            mAllSelectDateList.add(mInitializeDate);
        }

        if (mStartDate.isAfter(mEndDate)) {
            throw new RuntimeException("startDate必须在endDate之前");
        }

        if (mStartDate.isBefore(new LocalDate("1901-01-01"))) {
            throw new RuntimeException("startDate必须在1901-01-01之后");
        }

        if (mEndDate.isAfter(new LocalDate("2099-12-31"))) {
            throw new RuntimeException("endDate必须在2099-12-31之前");
        }

        if (mStartDate.isAfter(mInitializeDate) || mEndDate.isBefore(mInitializeDate)) {
            throw new RuntimeException("日期区间必须包含初始化日期");
        }

        BaseCalendarAdapter calendarAdapter = getCalendarAdapter(mContext, mStartDate, mEndDate, mInitializeDate, mAttrs, this);
        calendarAdapter.setOnDataBoundForPage(new BaseCalendarAdapter.OnDataBoundForPage() {
            @Override
            public void onPageAttachedToWindow(int position) {//todo greyson 该方式不好，待删除！
                if (mDirtyCurrentItem && position == mDirtyItemCount) {
                    mDirtyCurrentItem = false;
                    drawView(position);
                    if (mOnCalendarPageChangeListener != null) {
                        mOnCalendarPageChangeListener.onCalendarPageChange(BaseCalendar.this);
                    }
                }
            }
        });
        int currItem = calendarAdapter.getCurrItem();
        mViewPager.setAdapter(calendarAdapter);
        mViewPager.setCurrentItem(currItem);
    }

    @Override
    public void setDateInterval(String startFormatDate, String endFormatDate) {
        try {
            mStartDate = new LocalDate(startFormatDate);
            mEndDate = new LocalDate(endFormatDate);
        } catch (Exception e) {
            throw new RuntimeException("startDate、endDate需要 yyyy-MM-dd 格式的日期");
        }
        initAdapter();
    }

    @Override
    public void setInitializeDate(String formatInitializeDate) {
        try {
            mInitializeDate = new LocalDate(formatInitializeDate);
        } catch (Exception e) {
            throw new RuntimeException("setInitializeDate的参数需要 yyyy-MM-dd 格式的日期");
        }
        initAdapter();
    }

    @Override
    public void setDateInterval(String startFormatDate, String endFormatDate, String formatInitializeDate) {
        try {
            mStartDate = new LocalDate(startFormatDate);
            mEndDate = new LocalDate(endFormatDate);
            mInitializeDate = new LocalDate(formatInitializeDate);
        } catch (Exception e) {
            throw new RuntimeException("setInitializeDate的参数需要 yyyy-MM-dd 格式的日期");
        }
        initAdapter();
    }

    private boolean drawView(int position) {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(position);
        new Exception("greyson, BaseCalendar(" + getClass().getName() + ") drawView(" + position + ") CalendarView= " + currentCalendarView).printStackTrace();
        if (currentCalendarView == null) {
            return false;
        }
        if (mSelectedModel == SelectedModel.SINGLE_SELECTED) {//每页单个选中
            LocalDate initialDate = currentCalendarView.getInitialDate();//当前页面初始化的日期
            LocalDate lastDate = mAllSelectDateList.get(0);//上个页面选中的日期
            //当前面页面的初始值和上个页选中的日期，相差几月或几周，再又上个页面选中的日期得出当前页面选中的日期
            int currNum = getTwoDateCount(lastDate, initialDate, mAttrs.firstDayOfWeek);//得出两个页面相差几个
            LocalDate tempLocalDate = getIntervalDate(lastDate, currNum, mIsJumpClick);
            LocalDate currentDate; //当前页面选中的日期
            if (mIsDefaultSelectFirst && !mIsJumpClick && !tempLocalDate.equals(new LocalDate())) {
                //默认选中第一个 且 不是点击或跳转 且 不等于今天（为了第一次进来日历选中的是今天）
                currentDate = getFirstDate();
            } else {
                currentDate = tempLocalDate;
            }

            currentDate = getAvailableDate(currentDate);

            mIsJumpClick = false;

            mAllSelectDateList.clear();
            mAllSelectDateList.add(currentDate);
            currentCalendarView.invalidate();
        } else {
            //分为两种情况，1、单选不选中 2、多选  这两种情况只需要绘制页面
            currentCalendarView.invalidate();
        }

        callBack();
        return true;
    }

    public void onClickCurrentMonthOrWeekDate(LocalDate localDate) {
        //判断日期是否合法
        if (!isAvailable(localDate)) {
            clickDisableDate(localDate);
            return;
        }

        if (mSelectedModel == SelectedModel.MULTIPLE) {
            //多选  集合不包含就添加，包含就移除
            if (!mAllSelectDateList.contains(localDate)) {
                if (mAllSelectDateList.size() == mMultipleNum && mMultipleNumModel == MultipleNumModel.FULL_CLEAR) {
                    mAllSelectDateList.clear();
                } else if (mAllSelectDateList.size() == mMultipleNum && mMultipleNumModel == MultipleNumModel.FULL_REMOVE_FIRST) {
                    mAllSelectDateList.remove(0);
                }
                mAllSelectDateList.add(localDate);
            } else {
                mAllSelectDateList.remove(localDate);
            }
            notifyCalendar();
            //当前页面选中改变，需要回调
            callBack();
        } else {
            //单选  集合中有且只有一个元素 ，不包含就清空集合添加，包含该元素不做处理，因为前面已经绘制
            if (!mAllSelectDateList.contains(localDate)) {
                mAllSelectDateList.clear();
                mAllSelectDateList.add(localDate);
                notifyCalendar();
                //当前页面选中改变，需要回调
                callBack();

            }
        }
    }

    public void onClickLastMonthDate(LocalDate localDate) {
        jump(localDate, true);
    }

    public void onClickNextMonthDate(LocalDate localDate) {
        jump(localDate, true);
    }

    public void jump(LocalDate localDate, boolean isDraw) {
        //判断日期是否合法
        if (!isAvailable(localDate)) {
            clickDisableDate(localDate);
            return;
        }

        mIsJumpClick = true;
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        new Exception("greyson, BaseCalendar(" + getClass().getName() + ") jump to " + localDate
                + ", isDraw= " + isDraw + ", currentItem= " + mViewPager.getCurrentItem() + ", it's CalendarView= " + currentCalendarView).printStackTrace();

        int indexOffset = getTwoDateCount(localDate, currentCalendarView.getInitialDate(), mAttrs.firstDayOfWeek);//得出两个页面相差几个
        if (mSelectedModel == SelectedModel.MULTIPLE) {
            //多选  点击的日期不清除，只翻页，如果需要清除，等到翻页之后再次点击
            if (!mAllSelectDateList.contains(localDate) && isDraw) {
                if (mAllSelectDateList.size() == mMultipleNum && mMultipleNumModel == MultipleNumModel.FULL_CLEAR) {
                    mAllSelectDateList.clear();
                } else if (mAllSelectDateList.size() == mMultipleNum && mMultipleNumModel == MultipleNumModel.FULL_REMOVE_FIRST) {
                    mAllSelectDateList.remove(0);
                }
                mAllSelectDateList.add(localDate);
            }
        } else {
            //单选
            if (!mAllSelectDateList.contains(localDate) && isDraw) {
                mAllSelectDateList.clear();
                mAllSelectDateList.add(localDate);
            }
        }
        if (indexOffset == 0) {
            drawView(mViewPager.getCurrentItem());
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - indexOffset, Math.abs(indexOffset) == 1);
        }

    }

    private void callBack() {
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
                LocalDate middleLocalDate = currentCalendarView.getMiddleLocalDate();
                List<LocalDate> currentSelectDateList = currentCalendarView.getCurrentSelectDateList();
                LocalDate yearMonthLocalDate;

                if (BaseCalendar.this instanceof MonthCalendar) {
                    //月日历返回初始化的月份
                    yearMonthLocalDate = currentCalendarView.getInitialDate();
                } else {
                    if (currentSelectDateList.size() == 0) {
                        yearMonthLocalDate = middleLocalDate;
                    } else {
                        yearMonthLocalDate = currentSelectDateList.get(0);
                    }
                }

                if (mOnMWDateChangeListener != null) {
                    //月周折叠日历使用
                    mOnMWDateChangeListener.onMwDateChange(BaseCalendar.this, currentCalendarView.getPivotDate(), mAllSelectDateList);
                }

                if (mOnCalendarChangedListener != null && !(mSelectedModel == SelectedModel.MULTIPLE) && getVisibility() == VISIBLE) {
                    //单选
                    mOnCalendarChangedListener.onCalendarChange(BaseCalendar.this, yearMonthLocalDate.getYear(), yearMonthLocalDate.getMonthOfYear(), currentSelectDateList.size() == 0 ? null : currentSelectDateList.get(0));
                }

                if (mOnCalendarMultipleChangedListener != null && mSelectedModel == SelectedModel.MULTIPLE && getVisibility() == VISIBLE) {
                    //多选
                    mOnCalendarMultipleChangedListener.onCalendarChange(BaseCalendar.this, yearMonthLocalDate.getYear(), yearMonthLocalDate.getMonthOfYear(), currentSelectDateList, mAllSelectDateList);
                }
            }
        });
    }

    @Override
    public void notifyCalendar() {
        if (mViewPager.getChildCount() > 0 && mViewPager.getChildAt(0) instanceof ViewGroup) {
            //换ViewPager2之后，它的里面是一个RecyclerViewImpl对象
            ViewGroup recyclerViewImpl = (ViewGroup) mViewPager.getChildAt(0);
            for (int i = 0; i < recyclerViewImpl.getChildCount(); i++) {//RecyclerViewImpl的子View

                ViewGroup frameLayout = (ViewGroup) recyclerViewImpl.getChildAt(i);
                View childAt = frameLayout.getChildAt(0);
                if (childAt != null && childAt instanceof CalendarView) {
                    CalendarView calendarView = (CalendarView) childAt;
                    calendarView.invalidate();
                }
                Log.d("greyson", "BaseCalendar(" + getClass().getName() + ")notifyCalendar()中的RecyclerViewImpl's child: " + childAt);
            }
        }
    }

    //日期边界处理
    private LocalDate getAvailableDate(LocalDate localDate) {
        if (localDate.isBefore(mStartDate)) {
            return mStartDate;
        } else if (localDate.isAfter(mEndDate)) {
            return mEndDate;
        } else {
            return localDate;
        }
    }

    @Override
    public void setOnClickDisableDateListener(OnClickDisableDateListener onClickDisableDateListener) {
        this.mOnClickDisableDateListener = onClickDisableDateListener;
    }

    //点击不可用的日期处理
    private void clickDisableDate(LocalDate localDate) {
        if (getVisibility() != VISIBLE) {
            return;
        }
        if (mOnClickDisableDateListener != null) {
            mOnClickDisableDateListener.onClickDisableDate(localDate);
        } else {
            Toast.makeText(mViewPager.getContext(), TextUtils.isEmpty(mAttrs.disabledString) ? "日期超出许可范围" : mAttrs.disabledString, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void toNextPager() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

    @Override
    public void toLastPager() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
    }

    @Override
    public void toToday() {
        jump(new LocalDate(), true);
    }

    @Override
    public void jumpDate(String formatDate) {
        LocalDate jumpDate;
        try {
            jumpDate = new LocalDate(formatDate);
        } catch (Exception e) {
            throw new RuntimeException("jumpDate的参数需要 yyyy-MM-dd 格式的日期");
        }

        jump(jumpDate, true);
    }

    @Override
    public List<LocalDate> getAllSelectDateList() {
        return mAllSelectDateList;
    }

    //点击的日期是否可用
    protected boolean isAvailable(LocalDate localDate) {
        return localDate != null && !localDate.isBefore(mStartDate) && !localDate.isAfter(mEndDate);
    }

    //获取区间开始日期
    public LocalDate getStartDate() {
        return mStartDate;
    }

    //获取区间结束日期
    public LocalDate getEndDate() {
        return mEndDate;
    }

    //设置绘制类
    @Override
    public void setCalendarPainter(CalendarPainter calendarPainter) {
        this.mCalendarPainter = calendarPainter;
        notifyCalendar();
    }

    @Override
    public CalendarPainter getCalendarPainter() {
        if (mCalendarPainter == null) {
            mCalendarPainter = new InnerPainter(this);
        }
        return mCalendarPainter;
    }

    @Override
    public void updateSlideDistance(int currentDistance) {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        if (currentCalendarView != null) {
            currentCalendarView.updateSlideDistance(currentDistance);
        }
    }

    //月周切换时交换数据，保证月日历和周日历有相同的选中日期
    public void exchangeSelectDateList(List<LocalDate> dateList) {
        mAllSelectDateList.clear();
        mAllSelectDateList.addAll(dateList);
        notifyCalendar();
    }

    public void setOnMWDateChangeListener(OnMWDateChangeListener onMWDateChangeListener) {
        this.mOnMWDateChangeListener = onMWDateChangeListener;
    }

    @Override
    public void setOnCalendarChangedListener(OnCalendarChangedListener onCalendarChangedListener) {
        this.mOnCalendarChangedListener = onCalendarChangedListener;
    }

    @Override
    public void setOnCalendarPageChangeListener(OnCalendarPageChangeListener onCalendarPageChangeListener) {
        this.mOnCalendarPageChangeListener = onCalendarPageChangeListener;
    }

    @Override
    public void setOnCalendarMultipleChangedListener(OnCalendarMultipleChangedListener onCalendarMultipleChangedListener) {
        this.mOnCalendarMultipleChangedListener = onCalendarMultipleChangedListener;
    }

    //获取当前月，当前周的第一个日期
    public LocalDate getFirstDate() {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        new Exception("greyson, BaseCalendar(" + getClass().getName() + ") getFirstDate() getCurrentItem= "
                + mViewPager.getCurrentItem() + ", CalendarView= " + currentCalendarView).printStackTrace();
        if (currentCalendarView != null) {
            return currentCalendarView.getFirstDate();
        }
        return null;
    }

    //获取PivotDate
    public LocalDate getPivotDate() {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        if (currentCalendarView != null) {
            return currentCalendarView.getPivotDate();
        }
        return null;
    }

    //localDate到顶部的距离
    public int getDistanceFromTop(LocalDate localDate) {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        if (currentCalendarView != null) {
            return currentCalendarView.getDistanceFromTop(localDate);
        }
        return 0;
    }

    //PivotDate到顶部的距离
    public int getPivotDistanceFromTop() {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        if (currentCalendarView != null) {
            return currentCalendarView.getPivotDistanceFromTop();
        }
        return 0;
    }

    //回去viewpager的adapter
    protected abstract BaseCalendarAdapter getCalendarAdapter(Context context, LocalDate startDate,
                                                              LocalDate endDate, LocalDate initializeDate, Attrs attrs, BaseCalendar baseCalendar);

    //两个日期的相差数量
    protected abstract int getTwoDateCount(LocalDate startDate, LocalDate endDate, int type);

    //相差count之后的的日期
    protected abstract LocalDate getIntervalDate(LocalDate localDate, int count, boolean isJumpClick);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mIsInflateFinish) {
            drawView(mViewPager.getCurrentItem());
            mIsInflateFinish = true;
        }
    }

    @Override
    public List<LocalDate> getCurrentSelectDateList() {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        if (currentCalendarView != null) {
            return currentCalendarView.getCurrentSelectDateList();
        }
        return null;
    }

    @Override
    public List<LocalDate> getCurrentDateList() {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        if (currentCalendarView != null) {
            return currentCalendarView.getCurrentDateList();
        }
        return null;
    }

    @Override
    public Attrs getAttrs() {
        return mAttrs;
    }

    @Override
    public void setSelectedMode(SelectedModel selectedMode) {
        this.mSelectedModel = selectedMode;
        mAllSelectDateList.clear();
        if (mSelectedModel == SelectedModel.SINGLE_SELECTED) {
            mAllSelectDateList.add(mInitializeDate);
        }
    }

    @Override
    public void setDefaultSelectFitst(boolean isDefaultSelectFitst) {
        this.mIsDefaultSelectFirst = isDefaultSelectFitst;
    }

    @Override
    public void setMultipleNum(int multipleNum, MultipleNumModel multipleNumModel) {
        this.mSelectedModel = SelectedModel.MULTIPLE;
        this.mMultipleNumModel = multipleNumModel;
        this.mMultipleNum = multipleNum;
    }

    public void printCurrentCalendarView() {
        CalendarView currentCalendarView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        Log.d("greyson", "BaseCalendar(" + getClass().getName() + ") current position: " + mViewPager.getCurrentItem() + ", CalendarView: " + currentCalendarView);
    }
}
