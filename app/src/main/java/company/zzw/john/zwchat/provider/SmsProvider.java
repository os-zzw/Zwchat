package company.zzw.john.zwchat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import company.zzw.john.zwchat.dbhelper.SmsOpenHelper;

/**
 * Created by john on 2016/5/19.
 */
public class SmsProvider extends ContentProvider {

    private static final String AUTHORITIES = SmsProvider.class.getCanonicalName();
    //地址匹配对象
    static UriMatcher uriMatcher;

    //对应的聊天的uri常量
    public static Uri URI_SMS = Uri.parse("content://" + AUTHORITIES + "/sms");

    //对应的会话的uri常量
    public static final Uri URI_SESSION = Uri.parse("content://" + AUTHORITIES + "/session");

    private static final int SMS = 1;
    private static final int SESSION = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //添加匹配规则
        uriMatcher.addURI(AUTHORITIES, "/sms", SMS);
        //添加匹配规则
        uriMatcher.addURI(AUTHORITIES, "/session", SESSION);
    }

    private SmsOpenHelper smsOpenHelper;

    @Override
    public boolean onCreate() {
        //创建表,创建数据库
        smsOpenHelper = new SmsOpenHelper(getContext());
        if (smsOpenHelper != null) {
            return true;
        }
        return false;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }


    /**
     * 增删改查
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = 0;
        switch (uriMatcher.match(uri)) {
            case SMS:
                id = smsOpenHelper.getWritableDatabase().insert(SmsOpenHelper.T_SMS, "", values);
                if (id > 0) {
                    Log.d("aaa", "insert: 成功");
                    uri = ContentUris.withAppendedId(uri, id);

                    //发送数据改变的信号,给ContentProvider
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = 0;
        switch (uriMatcher.match(uri)) {
            case SMS:
                deleteCount = smsOpenHelper.getWritableDatabase().delete(SmsOpenHelper.T_SMS, selection, selectionArgs);
                if (deleteCount > 0) {
                    Log.d("aaa", "delete: 删除成功");
                    //发送数据改变的信号,给ContentProvider
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updateCount = 0;
        switch (uriMatcher.match(uri)) {
            case SMS:
                updateCount = smsOpenHelper.getWritableDatabase().update(SmsOpenHelper.T_SMS, values, selection, selectionArgs);
                if (updateCount > 0) {
                    Log.d("aaa", "update: 更新成功");
                    //发送数据改变的信号,给ContentProvider
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS, null);
                }

                break;
            default:
                break;
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        switch (uriMatcher.match(uri)) {
            case SMS:
                c = smsOpenHelper.getWritableDatabase().query(SmsOpenHelper.T_SMS, projection,
                        selection, selectionArgs, null, null, sortOrder);
                Log.d("aaa", "query: 查询成功");
                break;
            case SESSION:
                c = smsOpenHelper.getWritableDatabase().rawQuery("SELECT * FROM " +
                        "(SELECT * FROM " + SmsOpenHelper.T_SMS + " WHERE " + SmsOpenHelper.SmsTable.FROM_ACCOUNT +
                        " =? or " + SmsOpenHelper.SmsTable.TO_ACCOUNT + " =? ORDER BY " + SmsOpenHelper.SmsTable.TIME +
                        " ASC) " + "GROUP BY " + SmsOpenHelper.SmsTable.SESSION_ACCOUNT, selectionArgs);
                break;
            default:
                break;
        }
        return c;
    }

}
