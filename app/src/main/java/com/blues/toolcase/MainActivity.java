package com.blues.toolcase;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blues.toolcase.utils.CacheDataManager;
import com.blues.toolcase.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.blues.toolcase.MyApplication.getContext;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_send_code)
    TextView tvCode;
    @BindView(R.id.tv_cache)
    TextView tvCache;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        cleanCache();
    }

    @OnClick({R.id.tv_send_code, R.id.layout_clean_cache})
    void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send_code:
                getTelCheck();
                //手机验证码
                if (getTelCheck()) {
                    tvCode.setEnabled(false); //短信倒计时开始时禁止点击
                    etPhone.setEnabled(false);
                    countDownStart();
                    ToastUtils.show("验证码已发送，1分钟内有效，请注意查看短信");
                }
                break;
            //清理缓存
            case R.id.layout_clean_cache:
                new Thread(new clearCache()).start();
                break;
        }
    }

    /**
     * 验证手机号码是否合法
     */
    private boolean getTelCheck() {
        if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            ToastUtils.show("请输入手机号！");
            return false;
        }
        if (!TextUtils.isEmpty(etPhone.getText()) && etPhone.getText().length() != 11) {
            ToastUtils.show("请输入正确的11位手机号！");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 短信发送验证码倒计时
     */
    private int countdown = 60;
    private Handler handler = new Handler();

    private void countDownStart() {
        tvCode.setMinWidth(tvCode.getWidth());
        tvCode.setEnabled(false);
        CountDownRunnable countDownRunnable = new CountDownRunnable();
        handler.postDelayed(countDownRunnable, 1000);
    }

    /**
     * 倒计时Runnable
     */
    class CountDownRunnable implements Runnable {

        @Override
        public void run() {
            countdown--;
            tvCode.setText(String.format("%ss", countdown));
            if (countdown == 0) {
                countdown = 60;
                etPhone.setEnabled(true);
                tvCode.setEnabled(true);
                tvCode.setText("获取验证码");
                handler.removeCallbacks(this);
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    }

    /**
     * 缓存清理
     */
    private void cleanCache() {
        //展示缓存
        try {
            tvCache.setText(CacheDataManager.getTotalCacheSize(MyApplication.getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("缓存清理中…");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    private Handler cachehandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.show("缓存清理成功！");
                    try {
                        tvCache.setText(CacheDataManager.getTotalCacheSize(MyApplication.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }

    };

    class clearCache implements Runnable {
        @Override
        public void run() {
            try {
                CacheDataManager.clearAllCache(MyApplication.getContext());
                Thread.sleep(3000);
                if (CacheDataManager.getTotalCacheSize(MyApplication.getContext()).startsWith("0")) {
                    cachehandler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                return;
            }
            dialog.dismiss();
        }
    }

}
