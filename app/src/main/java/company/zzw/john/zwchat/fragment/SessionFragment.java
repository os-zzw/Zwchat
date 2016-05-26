package company.zzw.john.zwchat.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import company.zzw.john.zwchat.R;
import company.zzw.john.zwchat.activity.ChatActivity;
import company.zzw.john.zwchat.dbhelper.ContactOpenHelper;
import company.zzw.john.zwchat.dbhelper.SmsOpenHelper;
import company.zzw.john.zwchat.provider.ContactsProvider;
import company.zzw.john.zwchat.provider.SmsProvider;
import company.zzw.john.zwchat.services.IMService;
import company.zzw.john.zwchat.utils.ThreadUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionFragment extends Fragment {

    private ListView lv_sessions;

    private CursorAdapter adapter;

    public SessionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sessions, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        //反注册监听
        unRegisterContentObserver();
        super.onDestroy();
    }

    private void init() {

        //注册监听
        registerContentObserver();
    }

    private void initView(View view) {
        lv_sessions = (ListView) view.findViewById(R.id.lv_sessions);

    }

    private void initData() {
        setOrNotifyAdapter();
    }


    private void initListener() {
        lv_sessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                String account = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                String nickname = getNickName(account);
                Intent chat = new Intent(getActivity(), ChatActivity.class);
                chat.putExtra(ChatActivity.CLICKACCOUNT, account);
                chat.putExtra(ChatActivity.CLICKNICKNAME, nickname);

                startActivity(chat);
            }
        });

    }

    private void setOrNotifyAdapter() {
        if (adapter != null) {
            //刷新数据,更新adapter--这是对应的CursorAdapter的更新方法
            adapter.getCursor().requery();
            return;
        }
        //开启线程,同步花名册到
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {

                //对应的查询记录--查询所有的联系人
                final Cursor c = getActivity().getContentResolver().query(SmsProvider.URI_SESSION,
                        null,
                        null,
                        new String[]{IMService.mAccount, IMService.mAccount},
                        null);

                if (c.getCount() <= 0) {
                    return;
                }

                //设置adapter并进行显示
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 设置adapter
                         */
                        adapter = new CursorAdapter(getActivity(), c) {
                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                View view = View.inflate(context, R.layout.item_session, null);
                                return view;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                TextView tv_body = (TextView) view.findViewById(R.id.body);
                                TextView tv_nickname = (TextView) view.findViewById(R.id.nickname);
                                String body = cursor.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                                String account = cursor.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                                String nickname = getNickName(account);
                                tv_body.setText(body);
                                tv_nickname.setText(nickname);
                            }
                        };
                        //设置adapter
                        lv_sessions.setAdapter(adapter);

                    }
                });

            }
        });
    }

    /**
     * 查询联系人的nickname
     */
    public String getNickName(String account) {
        String nickname = "";
        Cursor c = getActivity().getContentResolver().query(ContactsProvider.URI_CONTACT, null, ContactOpenHelper.ContactTable.ACCOUNT +
                "=?", new String[]{account}, null);
        if (c.getCount() > 0) {
            //有数据
            c.moveToFirst();
            nickname = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
            return nickname;
        } else {
            return nickname;
        }

    }


    MyContentObserver myContentObserver = new MyContentObserver(new Handler());

    /**
     * 注册监听
     */
    public void registerContentObserver() {
        getActivity().getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, myContentObserver);
    }

    /**
     * 反注册监听
     */
    public void unRegisterContentObserver() {
        getActivity().getContentResolver().unregisterContentObserver(myContentObserver);
    }

    /**
     * ContentObserver类进行 监听
     */
    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            setOrNotifyAdapter();
            super.onChange(selfChange);
        }
    }


}
