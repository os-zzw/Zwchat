package company.zzw.john.zwchat.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by john on 2016/5/17.
 */
public class ContactOpenHelper extends SQLiteOpenHelper {

    public static final String T_CONTACT = "t_contact"; //表的名称


    public class ContactTable implements BaseColumns { //继承这个借口是为了自动生成_id
        /**
         * 1:_id  主键
         * 2:account  账号
         * 3:nickname  昵称
         * 4: avatar 头像
         * 5 :pinyin  拼音
         */
        public static final String ACCOUNT = "account";
        public static final String NICKNAME = "nickname";
        public static final String AVATAR = "avatar";
        public static final String PINYIN = "pinyin";


    }


    public ContactOpenHelper(Context context) {
        super(context, "contact.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + T_CONTACT +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +//表的结构
                ContactTable.ACCOUNT + " TEXT, " +
                ContactTable.NICKNAME + " TEXT, " +
                ContactTable.AVATAR + " TEXT, " +
                ContactTable.PINYIN + " TEXT);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
