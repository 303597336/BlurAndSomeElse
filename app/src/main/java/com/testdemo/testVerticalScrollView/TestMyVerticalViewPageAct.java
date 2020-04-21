package com.testdemo.testVerticalScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.R;
import com.testdemo.testVerticalScrollView.viewPager2.TestViewPager2Act;
import com.testdemo.testView.ColorfulDrawable;

import java.util.ArrayList;

/**
 * Created by Greyson on 2018/1/25.
 */
public class TestMyVerticalViewPageAct extends Activity implements ThreeScrollView.OnScrollChangeListener, DialogViewPresenter.OnViewClickListener {
    private final static String TAG = "Test3-greyson";

    IndexPointLayout indexPointLayout;

    private View blurBgIV;
    private FrameLayout containLayout;
    private DialogViewPresenter dialogViewPresenter;

    private boolean showCallerServiceInfo;
    private View managerServiceInfoLayout;
    private View callerServiceInfoLayout;
    private TextView mediaChatTV;

    private Handler handler = new Handler();
    private Runnable myRunnable = new Runnable() {
        public void run() {
            TranslateAnimation translateOutAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            translateOutAnimation.setDuration(1000);

            TranslateAnimation translateInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            translateInAnimation.setDuration(1000);

//            Log.i(TAG, "handler run --- ");
            if (showCallerServiceInfo) {
                callerServiceInfoLayout.setAnimation(translateOutAnimation);
                managerServiceInfoLayout.setAnimation(translateInAnimation);
                callerServiceInfoLayout.setVisibility(View.GONE);
                managerServiceInfoLayout.setVisibility(View.VISIBLE);
                showCallerServiceInfo = false;
            } else {
                callerServiceInfoLayout.setAnimation(translateInAnimation);
                managerServiceInfoLayout.setAnimation(translateOutAnimation);
                callerServiceInfoLayout.setVisibility(View.VISIBLE);
                managerServiceInfoLayout.setVisibility(View.GONE);
                showCallerServiceInfo = true;
            }
            handler.postDelayed(this, 2500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test_myvertical_viewpage);

        ThreeScrollView threeScrollView = findViewById(R.id.threeScrollView);

        threeScrollView.addChildView(findViewById(R.id.firstView));
        threeScrollView.addChildView(findViewById(R.id.holdUpView));
        threeScrollView.addChildView(findViewById(R.id.thirdView));
        threeScrollView.addChildView(findViewById(R.id.forthView));

        threeScrollView.setOnScrollChangeListener(this);

        blurBgIV = findViewById(R.id.blurBgIV);
        containLayout = findViewById(R.id.containLayout);

        managerServiceInfoLayout = findViewById(R.id.managerServiceInfoLayout);
        callerServiceInfoLayout = findViewById(R.id.callerServiceInfoLayout);
        mediaChatTV = findViewById(R.id.mediaChatTV);
        mediaChatTV.setOnClickListener(v -> {
            /*if (containLayout == null || dialogViewPresenter == null || dialogViewPresenter.getDialogView() == null) {
                return;
            }
            containLayout.addView(dialogViewPresenter.getDialogView());*/
            dialogViewPresenter.showDialog();
        });

        handler.postDelayed(myRunnable, 2500);


        //多页面切换时的小点点
        indexPointLayout = findViewById(R.id.indexPointLayout);
        indexPointLayout.setPointCount(4, 1);

        ProgressBar ratingPB = findViewById(R.id.ratingPB);
        ratingPB.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progressbar));
        ratingPB.setMax(10);
        ratingPB.setProgress(8);

        ProgressBar ratingPB2 = findViewById(R.id.ratingPB2);
        ratingPB2.setProgressDrawable(getResources().getDrawable(R.drawable.layer_progress_bar));
        ratingPB2.setMax(10);
        ratingPB2.setProgress(9);

        ProgressBar ratingPB3 = findViewById(R.id.ratingPB3);
        ratingPB3.setProgressDrawable(getResources().getDrawable(R.drawable.layer_progress_bar));
        ratingPB3.setMax(10);
        ratingPB3.setProgress(4);

        ProgressBar ratingPB4 = findViewById(R.id.ratingPB4);
        ratingPB4.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progressbar));
        ratingPB4.setMax(10);
        ratingPB4.setProgress(1);


        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.ic_launcher);
        list.add(R.mipmap.ic_launcher);
        list.add(R.drawable.ic_camera);
        OverlayListLayout overlayListLayout = findViewById(R.id.overlayListLayout);
        overlayListLayout.setAdapter(new AvatarListAdapter(this, list));
        /*ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_launcher);
        overlayListLayout.addView(imageView);
        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.mipmap.ic_launcher);
        imageView2.setBackgroundResource(R.drawable.bg_corner18_grad_fe820f);
        overlayListLayout.addView(imageView2);

        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(R.drawable.ic_camera);
        imageView3.setBackgroundResource(R.drawable.bg_popup);
        overlayListLayout.addView(imageView3);*/

        final PhoneEditText etPhone = findViewById(R.id.et_phone);
        findViewById(R.id.btn_print_phone).setOnClickListener(v -> Toast.makeText(TestMyVerticalViewPageAct.this, etPhone.getPhone(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialogViewPresenter = new DialogViewPresenter(this);
        dialogViewPresenter.setOnViewClickListener(this);
        initDialogView();
    }

    @Override
    public void onVideoClick() {

    }

    @Override
    public void onAudioClick() {

    }

    @Override
    public void onHireClick() {

    }

    @Override
    public void onBackGroundClick() {

    }

    private void initDialogView() {
        mediaChatTV.post(() -> {
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
            int windowWidth = metrics.widthPixels;

            int[] locations = new int[2];
            mediaChatTV.getLocationInWindow(locations);
            System.out.println("width = " + windowWidth + " - " + mediaChatTV.getWidth() + " - " + locations[0]);
            dialogViewPresenter.setItemsLayoutMarginRight(windowWidth - locations[0] - mediaChatTV.getWidth());
            containLayout.addView(dialogViewPresenter.getDialogView());
        });
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
    }

    @Override
    public void onFirstScrollToSecondChange(float nowY, float startY, float endY) {
        float value = (nowY - startY) / (endY - startY);
        blurBgIV.setAlpha(value);

        if (nowY < endY) {
            indexPointLayout.setSelection(1);
        } else {
            indexPointLayout.setSelection(3);
        }
    }

    public void onClick(View view) {
        Log.i(TAG, "onClick-" + view.getId());
        startActivity(new Intent(this, TestViewPager2Act.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && myRunnable != null) {
            handler.removeCallbacks(myRunnable);
        }
    }
}
