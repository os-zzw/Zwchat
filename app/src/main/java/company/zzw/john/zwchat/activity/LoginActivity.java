package company.zzw.john.zwchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import company.zzw.john.zwchat.R;
import company.zzw.john.zwchat.services.IMService;
import company.zzw.john.zwchat.services.PushService;
import company.zzw.john.zwchat.utils.SpTools;
import company.zzw.john.zwchat.utils.ThreadUtils;
import company.zzw.john.zwchat.utils.ToastUtils;

/**
 * Created by john on 2016/5/14.
 */
public class LoginActivity extends Activity {

    public static final String HOST = "1510269g0z.iask.in"; //主机ip --可以
    public static final int PORT = 29636;  //对应的端口号
    public static final String SERVICENAME = "118.202.13.1";

    private EditText et_user;
    private EditText et_pwd;
    private Button btn_login;
    private Intent iMservice;
    private Intent pushService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();


    }


    private void initListener() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = et_user.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                //判断用户名是否为空
                if (TextUtils.isEmpty(user)) {
                    et_user.setError("用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    et_pwd.setError("密码不能为空");
                    return;
                }
                SpTools.setString(LoginActivity.this, "user", user);
                SpTools.setString(LoginActivity.this, "pwd", pwd);
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (iMservice != null) {
                                stopService(iMservice);
                                IMService.conn.disconnect();
                            }


                            //创建连接配置对象
                            ConnectionConfiguration configuration = new ConnectionConfiguration(HOST, PORT);
                            //额外的配置(方便调试上线的时候改回来)
                            configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//不适用安全模式,明文传输
                            configuration.setDebuggerEnabled(true); //开启调试模式,方便查看具体的内容

                            //开始创建连接对象
                            XMPPConnection conn = new XMPPConnection(configuration);
                            //开始连接
                            conn.connect();
                            //连接成功了
                            //开始登录
                            conn.login(user, pwd);
                            //已经登陆成功了
                            ToastUtils.showToast("登陆成功");
                            //此时将conn保存起来,用来连接对象
                            IMService.conn = conn;


                            //保存当前
                            // 账户的名称
                            String mAccount = user + "@" + SERVICENAME;
                            IMService.mAccount = mAccount;

                            //启动IMService
                            iMservice = new Intent(LoginActivity.this, IMService.class);
                            startService(iMservice);
                            //启动PushService
                            pushService = new Intent(LoginActivity.this, PushService.class);
                            startService(pushService);

                            //获取消息的管理者

                            //跳转到主界面
                            finish();
                            Intent main = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(main);

                        } catch (XMPPException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("登录失败");
                        }

                    }
                });


            }
        });
    }


    private void initView() {
        et_user = (EditText) findViewById(R.id.et_user);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);

        et_user.setText(SpTools.getString(LoginActivity.this, "user", ""));
        et_pwd.setText(SpTools.getString(LoginActivity.this, "pwd", ""));
    }
}
