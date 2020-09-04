package com.testdemo.webrtc.webrtctest;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.testdemo.R;

/**
 * Created by dds on 2018/11/7.
 * android_shuai@163.com
 */
public class TestAVChatActivity extends AppCompatActivity {
    private EditText et_signal;
    private EditText et_room;
    private boolean videoEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodejs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initVar();

    }

    private void initView() {
        et_signal = findViewById(R.id.et_signal);
        et_room = findViewById(R.id.et_room);

        ((RadioGroup) findViewById(R.id.rg_media_selection)).setOnCheckedChangeListener(
                (radioGroup, id) -> videoEnable = id == R.id.rb_video
        );
    }

    private void initVar() {
//        et_signal.setText("wss://47.93.186.97/wss");
        et_signal.setText(WebrtcUtil.WSS);
        et_room.setText("232343");
    }

    /*-------------------------- nodejs版本服务器测试--------------------------------------------*/
    /*public void JoinRoomSingleVideo(View view) {
        WebrtcUtil.callSingle(this,
                et_signal.getText().toString(),
                et_room.getText().toString().trim(),
                true);
    }*/

    public void JoinRoom(View view) {
        WebrtcUtil.call(this,
                et_signal.getText().toString(),
                et_room.getText().toString().trim(),
                videoEnable);
    }

}
