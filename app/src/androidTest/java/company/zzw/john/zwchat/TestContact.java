package company.zzw.john.zwchat;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import company.zzw.john.zwchat.dbhelper.ContactOpenHelper;
import company.zzw.john.zwchat.provider.ContactsProvider;
import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by john on 2016/5/17.
 */
public class TestContact extends AndroidTestCase {


    public void testInsert() {

        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "zzw.asd");
        values.put(ContactOpenHelper.ContactTable.NICKNAME, "哲哲");
        values.put(ContactOpenHelper.ContactTable.AVATAR, "head");
        values.put(ContactOpenHelper.ContactTable.PINYIN, "zz");
        getContext().getContentResolver().insert(ContactsProvider.URI_CONTACT, values);

    }

    public void testDelete() {
        getContext().getContentResolver().delete(ContactsProvider.URI_CONTACT, ContactOpenHelper.ContactTable.ACCOUNT
                + "=?", new String[]{"zzw"});


    }

    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, "zzw");
        values.put(ContactOpenHelper.ContactTable.NICKNAME, "哲");
        values.put(ContactOpenHelper.ContactTable.AVATAR, "ad");
        values.put(ContactOpenHelper.ContactTable.PINYIN, "z");
        getContext().getContentResolver().update(ContactsProvider.URI_CONTACT, values, ContactOpenHelper.ContactTable.ACCOUNT
                + "=?", new String[]{"zzw.asd"});

    }

    public void testQuery() {
        Cursor query = getContext().getContentResolver().query(ContactsProvider.URI_CONTACT, null, null, null, null);
        int columnCount = query.getColumnCount();
        while (query.moveToNext()) {
            //循环打印
            for (int i = 0; i < columnCount; i++) {
                Log.d("aaa", query.getString(i) + "    ");
            }
        }

    }

    public void testPinYin() {
        //内容--分隔符--格式
        String pinyin = PinyinHelper.convertToPinyinString("张哲伟", "", PinyinFormat.WITHOUT_TONE);
        Log.d("aaa", pinyin);
    }
}

