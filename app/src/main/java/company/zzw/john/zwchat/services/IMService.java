package company.zzw.john.zwchat.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import company.zzw.john.zwchat.activity.LoginActivity;
import company.zzw.john.zwchat.dbhelper.ContactOpenHelper;
import company.zzw.john.zwchat.dbhelper.SmsOpenHelper;
import company.zzw.john.zwchat.provider.ContactsProvider;
import company.zzw.john.zwchat.provider.SmsProvider;
import company.zzw.john.zwchat.utils.PinYinUtil;
import company.zzw.john.zwchat.utils.ThreadUtils;
import company.zzw.john.zwchat.utils.ToastUtils;

/**
 * Created by john on 2016/5/17.
 */
public class IMService extends Service {
    //不被GC所管理,内存回收不会管他
    public static XMPPConnection conn;
    public static String mAccount;
    private Roster roster;
    private MyRosterListener rosterListener;

    public ChatManager chatManager;
    public Chat chat;
    public MyMessageListener messageListener;

    private Map<String, Chat> chatMap = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        /**
         * 调用binder对象来返回,service的实例
         */
        public IMService getIMService() {
            return IMService.this;
        }
    }

    @Override
    public void onCreate() {
        /** 同步花名册*/
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //需要连接对象,得到花名册对象
                if (IMService.conn.getRoster() != null) {
                    roster = IMService.conn.getRoster();
                }
                //得到所有的联系人
                final Collection<RosterEntry> entries = roster.getEntries();

                //监听联系人的改变
                rosterListener = new MyRosterListener();
                roster.addRosterListener(rosterListener);

                //遍历,并保存数据
                for (RosterEntry entry : entries) {
                    saveOrUpdateEntry(entry);
                }
            }
        });

        /** 创建消息的管理者,并添加对Message的监听**/
        /**连接*/
        //1.获取消息的管理者

        chatManager = IMService.conn.getChatManager();

        //2.创建聊天对象
        if (messageListener == null) {
            messageListener = new MyMessageListener();
        }

        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createLocally) {
                String participant = chat.getParticipant();  //和我聊天的那个人,参与者
                participant = filter(participant);
                if (!chatMap.containsKey(participant)) {
                    chatMap.put(participant, chat);
                    chat.addMessageListener(messageListener);

                }
            }
        });


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //移除RoserListener
        if (roster != null && rosterListener != null) {
            roster.removeRosterListener(rosterListener);
        }

        //移除messageListener

        for (String key : chatMap.keySet()) {
            chat = chatMap.get(key);
            chat.removeMessageListener(messageListener);
        }


        super.onDestroy();
    }

    /**
     * 保存或更新联系人
     *
     * @param entry
     */
    private void saveOrUpdateEntry(RosterEntry entry) {
        String account = entry.getUser();
        account=filter(account);

        String nickname = entry.getName();
        //对nickname进行非空判定
        if (nickname == null || "".equals(nickname)) {
            nickname = account.substring(0, account.indexOf("@"));
        }
        //向数据库中添加数据
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, nickname);
        values.put(ContactOpenHelper.ContactTable.AVATAR, "head");
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinYinUtil.getPinYin(account));

        //先进行update,在进行insert
        int updateCount = getContentResolver().update(ContactsProvider.URI_CONTACT, values,
                ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account});
        if (updateCount <= 0) {//没有更新,进行插入
            getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        }
    }

    /**
     * 保存消息 -->ContentProvider
     */
    public void saveMessage(String session_Account, org.jivesoftware.smack.packet.Message msg) {
        ContentValues values = new ContentValues();
        if (msg.getBody() == "") {
            return;
        }
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

    /**
     * 发送消息
     */
    public void sendMessage(final org.jivesoftware.smack.packet.Message msg) {
        try {
            String to_Account = msg.getTo();
            if (chatMap.containsKey(to_Account)) {
                chat = chatMap.get(to_Account);
            } else {
                chat = chatManager.createChat(msg.getTo(), messageListener);
                chatMap.put(to_Account, chat);
            }

            chat.sendMessage(msg);

            //保存消息,
            //发送消息的时候,session为接收消息的一方,但还要进行处理
            String session_Account = msg.getTo();
            session_Account = filter(session_Account);
            saveMessage(session_Account, msg);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }


    /**
     * 监听联系人的变化
     */
    private class MyRosterListener implements RosterListener {
        @Override
        public void entriesAdded(Collection<String> collection) {  //联系人添加了
            //插入或者更新
            for (String coll : collection) {
                RosterEntry entry = roster.getEntry(coll);
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> collection) {  //联系人修改了
            //插入或者更新
            for (String coll : collection) {
                RosterEntry entry = roster.getEntry(coll);
                saveOrUpdateEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> collection) { //联系人删除了
            //删除
            for (String coll : collection) {  //--->此处得到的就是User(coll),直接删除就可,不必再继续得到entry
                //                String account = coll.substring(0, coll.indexOf("@"));
                getContentResolver().delete(ContactsProvider.URI_CONTACT, ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{coll});
            }
        }

        @Override
        public void presenceChanged(Presence presence) {  //联系人状态改变

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
            ToastUtils.showSafeToast(body);
            String participant = chat.getParticipant();  //当前账户收到消息,参与者为发送消息的
            participant = filter(participant);
            saveMessage(participant, msg);
        }
    }


}
