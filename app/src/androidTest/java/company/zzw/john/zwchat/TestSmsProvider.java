package company.zzw.john.zwchat;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import company.zzw.john.zwchat.dbhelper.SmsOpenHelper;
import company.zzw.john.zwchat.provider.SmsProvider;

/**
 * Created by john on 2016/5/19.
 */
public class TestSmsProvider extends AndroidTestCase {

    public void testInsert() {
        /**
         * public static final String FROM_ACCOUNT = "from_Account";
         public static final String TO_ACCOUNT = "to_Account";
         public static final String BODY = "body";
         public static final String TYPE = "type";
         public static final String TIME = "time";
         public static final String STATUS = "status";
         public static final String SESSION_ACCOUNT = "session_Account";
         */
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, "12345");
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, "admin");
        values.put(SmsOpenHelper.SmsTable.BODY, "今晚约吗?");
        values.put(SmsOpenHelper.SmsTable.TYPE, "chat");
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.STATUS, "offline");
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, "admin");

        getContext().getContentResolver().insert(SmsProvider.URI_SMS, values);
    }

    public void testDelete() {
        getContext().getContentResolver().delete(SmsProvider.URI_SMS, SmsOpenHelper.SmsTable.FROM_ACCOUNT + "=?"
                , new String[]{"123"});

    }

    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT, "123");
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT, "adm");
        values.put(SmsOpenHelper.SmsTable.BODY, "今晚约?");
        values.put(SmsOpenHelper.SmsTable.TYPE, "cha");
        values.put(SmsOpenHelper.SmsTable.TIME, System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.STATUS, "offlne");
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT, "admn");

        getContext().getContentResolver().update(SmsProvider.URI_SMS, values, SmsOpenHelper.SmsTable.FROM_ACCOUNT +
                "=?", new String[]{"12345"});

    }

    public void testQuery() {
        Cursor c = getContext().getContentResolver().query(SmsProvider.URI_SMS, null, null, null, null);
        //得到所有的列
        int columnCount = c.getColumnCount();
        while (c.moveToNext()) {
            for (int i = 0; i < columnCount; i++) {
                Log.d("aaa", "testQuery: " + c.getString(i));
            }
        }

    }
}
