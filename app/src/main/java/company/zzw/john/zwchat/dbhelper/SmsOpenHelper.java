package company.zzw.john.zwchat.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by john on 2016/5/19.
 */
public class SmsOpenHelper extends SQLiteOpenHelper {

    public static final String T_SMS = "t_sms"; //表的名称

    public class SmsTable implements BaseColumns {
        /**
         * 聊天记录应该保存的字段
         * from_Account(发送者),to_Account(接受者),body(发送内容),
         * type(发送类型),time(发送时间),status(发送状态),session_account(会话列表)
         */
        public static final String FROM_ACCOUNT = "from_Account";
        public static final String TO_ACCOUNT = "to_Account";
        public static final String BODY = "body";
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String STATUS = "status";
        public static final String SESSION_ACCOUNT = "session_Account";


    }

    public SmsOpenHelper(Context context) {
        super(context, "sms.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + T_SMS +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +//表的结构
                SmsTable.FROM_ACCOUNT + " TEXT, " +
                SmsTable.TO_ACCOUNT + " TEXT, " +
                SmsTable.BODY + " TEXT, " +
                SmsTable.TYPE + " TEXT, " +
                SmsTable.TIME + " TEXT, " +
                SmsTable.SESSION_ACCOUNT + " TEXT, " +
                SmsTable.STATUS + " TEXT);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
