package company.zzw.john.zwchat.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import company.zzw.john.zwchat.R;
import company.zzw.john.zwchat.dbhelper.SmsOpenHelper;
import company.zzw.john.zwchat.provider.SmsProvider;
import company.zzw.john.zwchat.services.IMService;
import company.zzw.john.zwchat.utils.ThreadUtils;
import company.zzw.john.zwchat.utils.ToastUtils;

public class ChatActivity extends Activity {

    public static final String CLICKACCOUNT = "clickaccount";
    public static final String CLICKNICKNAME = "clicknickname";

    private String clickaccount;
    private String clicknickname;


    @InjectView(R.id.tv_title)
    TextView tv_title;

    @InjectView(R.id.lv_message)
    ListView lv_message;

    @InjectView(R.id.et_body)
    EditText et_body;

    @InjectView(R.id.btn_send)
    Button btn_send;

    private CursorAdapter adapter;

    private IMService imService;

    private MyServiceConnection myServiceConnection = new MyServiceConnection();
    private ChatManager chatManager;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        init();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        //反注册监听
        unregisterContentObserver();

        //解绑服务
        if (myServiceConnection != null) {
            unbindService(myServiceConnection);
        }
        super.onDestroy();
    }

    private void init() {
        //注册监听
        registerContentObserver();


        //绑定服务
        Intent service = new Intent(ChatActivity.this, IMService.class);
        bindService(service, myServiceConnection, BIND_AUTO_CREATE);

        clickaccount = getIntent().getStringExtra(CLICKACCOUNT);
        clicknickname = getIntent().getStringExtra(CLICKNICKNAME);


    }

    private void initView() {
        tv_title.setText(clicknickname);
    }

    private void initData() {
        setOrNotifyAdapter();


    }

    private void setOrNotifyAdapter() {

        //首先判断是否存在Adapter
        if (adapter != null) {
            //刷新adapter
            adapter.getCursor().requery();//刷新CursorAdapter
            lv_message.setSelection(adapter.getCount() - 1);
            return;//----> 如果存在adapter直接进行刷新,并返回此方法,不再执行下面的代码
        }

        /**子线程查询数据,并设置adapter*/
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {

                final Cursor c = getContentResolver().query
                        (SmsProvider.URI_SMS,
                                null,
                                "( from_account = ? and to_account = ? ) or (from_account = ? and to_account = ? )",//where条件
                                new String[]{IMService.mAccount, clickaccount, clickaccount, IMService.mAccount},
                                SmsOpenHelper.SmsTable.TIME + " ASC ");//aes升序,desc,降序


                //如果没有数据也直接返回
                if (c.getCount() < 1) {
                    return;
                }

                //主线程,设置adapter
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CursorAdapter(ChatActivity.this, c) {
                            private static final int RECEIVE = 0;
                            private static final int SEND = 1;


                            @Override
                            public int getItemViewType(int position) {
                                c.moveToPosition(position);
                                //取出消息的创建者
                                String fromAccount = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
                                if (!IMService.mAccount.equals(fromAccount)) {
                                    return RECEIVE;
                                } else {
                                    return SEND;
                                }
                            }

                            @Override
                            public int getViewTypeCount() {
                                return super.getViewTypeCount() + 1; //2
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                ViewHolder holder = null;
                                if (getItemViewType(position) == RECEIVE) {
                                    if (convertView == null) {
                                        convertView = View.inflate(ChatActivity.this, R.layout.item_chat_receive, null);
                                        holder = new ViewHolder();
                                        holder.time = (TextView) convertView.findViewById(R.id.time);
                                        holder.body = (TextView) convertView.findViewById(R.id.content);
                                        holder.head = (ImageView) convertView.findViewById(R.id.head);
                                        convertView.setTag(holder);
                                    } else {
                                        holder = (ViewHolder) convertView.getTag();
                                    }
                                    //得到数据并展示
                                } else {
                                    if (convertView == null) {
                                        convertView = View.inflate(ChatActivity.this, R.layout.item_chat_send, null);
                                        holder = new ViewHolder();
                                        holder.time = (TextView) convertView.findViewById(R.id.time);
                                        holder.body = (TextView) convertView.findViewById(R.id.content);
                                        holder.head = (ImageView) convertView.findViewById(R.id.head);
                                        convertView.setTag(holder);
                                    } else {
                                        holder = (ViewHolder) convertView.getTag();
                                    }
                                    //得到数据并展示
                                }
                                //得到数据,并展示
                                c.moveToPosition(position);
                                String time = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String time_format = dateFormat.format(new Date(Long.parseLong(time)));
                                String body = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                                holder.body.setText(body);
                                holder.time.setText(time_format);

                                return convertView;
                            }

                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                return null;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {

                            }

                            /**封装ViewHolder类*/
                            class ViewHolder {
                                TextView body;
                                TextView time;
                                ImageView head;
                            }
                        };

                        lv_message.setAdapter(adapter);
                        lv_message.setSelection(adapter.getCount() - 1);
                    }
                });


            }
        });
    }


    private void initListener() {
        /** 发送按钮*/

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String body = et_body.getText().toString();


                //3.发送消息
                org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message();
                msg.setFrom(IMService.mAccount);//设置消息的来源是当前的用户
                msg.setTo(clickaccount);  //消息的发送对象
                msg.setBody(body); //输入框里的内容
                msg.setType(org.jivesoftware.smack.packet.Message.Type.chat);//设置消息的类型,类型就是chat
                msg.setProperty("key", "value");//设置一些额外的属性,这里用不到

                // TODO 调用service的sendMessage方法来发送消息,并保存
                //                imService.sendMessage(msg);

                chatManager = IMService.conn.getChatManager();
                chat = chatManager.createChat(msg.getTo(), new MyMessageListener());

                try {
                    chat.sendMessage(msg);
                    //保存消息,
                    //发送消息的时候,session为接收消息的一方,但还要进行处理
                    String session_Account = msg.getTo();
                    session_Account = filter(session_Account);
                    saveMessage(session_Account, msg);

                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                //清空输入框
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        et_body.setText("");
                    }
                });
            }
        });
    }

    /**
     * 保存消息 -->ContentProvider
     */
    public void saveMessage(String session_Account, org.jivesoftware.smack.packet.Message msg) {
        if (msg.getBody() == "") {
            return;
        }
        ContentValues values = new ContentValues();
        /**进行过滤*/
        String from_account = msg.getFrom();
        from_account = filter(from_account);
        String to_account = msg.getTo();
        to_account = filter(to_account);
        session_Account = filter(session_Account);

        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, from_account);
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, to_account);
        values.put(SmsOpenHelper.SmsTable.BODY, msg.getBody());
        values.put(SmsOpenHelper.SmsTable.TYPE, msg.getType().name());
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.STATUS, "offline");
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, session_Account);
        getContentResolver().insert(SmsProvider.URI_SMS, values);
    }

    @NonNull
    private String filter(String to_account) {
        return to_account.substring(0, to_account.indexOf("@")) + "@" + LoginActivity.SERVICENAME;
    }

    MyContentObserver contentObserver = new MyContentObserver(new Handler());

    /**
     * 注册监听
     */
    public void registerContentObserver() {
        getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, contentObserver);
    }

    /**
     * 反注册监听
     */
    public void unregisterContentObserver() {
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    /**
     * 监听数据库的改变
     */
    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 接收到记录的改变
         */
        @Override
        public void onChange(boolean selfChange) {
            //设置或者刷新adapter
            setOrNotifyAdapter();
            super.onChange(selfChange);

        }
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMService.MyBinder myBinder = (IMService.MyBinder) service;
            imService = myBinder.getIMService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * 消息的监听
     */
    private class MyMessageListener implements MessageListener {

        private String body;

        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message msg) {
            body = msg.getBody();
            if (body == null) {
                return;
            }
            ToastUtils.showSafeToast(body);
            String participant = chat.getParticipant();  //当前账户收到消息,参与者为发送消息的
            participant = filter(participant);
            saveMessage(participant, msg);
        }
    }

}
