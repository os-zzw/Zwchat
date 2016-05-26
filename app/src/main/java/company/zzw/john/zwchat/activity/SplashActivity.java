package company.zzw.john.zwchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import company.zzw.john.zwchat.R;
import company.zzw.john.zwchat.utils.ThreadUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //停留三秒进入主界面
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //停留三秒
                SystemClock.sleep(3000);
                //然后进入登录界面
                Intent login = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

    }
}
