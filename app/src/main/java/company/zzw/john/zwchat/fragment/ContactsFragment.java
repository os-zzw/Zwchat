package company.zzw.john.zwchat.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import company.zzw.john.zwchat.provider.ContactsProvider;
import company.zzw.john.zwchat.utils.ThreadUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {


    private ListView lv_contacts;
    private CursorAdapter adapter;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        init();
        Log.d("aaa", "onCreate");
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        Log.d("aaa", "onCreateView");
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData();
        initListener();
        Log.d("aaa", "onActivityCreate");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        Log.d("aaa", "onDestroy");
        unRegisterContentObserver();

        super.onDestroy();
    }

    private void init() {
        registerContentObserver();
    }

    private void initView(View view) {
        lv_contacts = (ListView) view.findViewById(R.id.lv_contacts);

    }

    private void initListener() {

        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                String account = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                String nickname = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
                Intent chat = new Intent(getActivity(), ChatActivity.class);
                chat.putExtra(ChatActivity.CLICKACCOUNT, account);
                chat.putExtra(ChatActivity.CLICKNICKNAME, nickname);

                startActivity(chat);
            }
        });
    }

    private void initData() {

        setOrNotifyAdapter();

    }

    /**
     * 设置或者更新adapter
     */
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
                final Cursor c = getActivity().getContentResolver().query(ContactsProvider.URI_CONTACT, null, null, null, null);

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
                                View view = View.inflate(context, R.layout.item_contact, null);
                                return view;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                TextView tv_account = (TextView) view.findViewById(R.id.account);
                                TextView tv_nickname = (TextView) view.findViewById(R.id.nickname);
                                String account = cursor.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                                String nickname = cursor.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
                                tv_account.setText(account);
                                tv_nickname.setText(nickname);
                            }
                        };
                        //设置adapter
                        lv_contacts.setAdapter(adapter);

                    }
                });

            }
        });


    }


    MyContentObserver contentObserver = new MyContentObserver(new Handler());


    /**
     * 注册监听
     */
    public void registerContentObserver() {
        getActivity().getContentResolver().registerContentObserver(ContactsProvider.URI_CONTACT, true,
                contentObserver);
    }

    /**
     * 反注册监听
     */
    public void unRegisterContentObserver() {
        getActivity().getContentResolver().unregisterContentObserver(contentObserver);
    }

    /**
     * ---监听数据库的改变 -------
     */
    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 如果数据库发生改变,会在这里发通知
         */
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            setOrNotifyAdapter();
        }
    }
}
