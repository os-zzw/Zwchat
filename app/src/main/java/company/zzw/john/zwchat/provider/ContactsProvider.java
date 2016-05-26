package company.zzw.john.zwchat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import company.zzw.john.zwchat.dbhelper.ContactOpenHelper;

/**
 * Created by john on 2016/5/17.
 */
public class ContactsProvider extends ContentProvider {

    //主机地址的常量-->类的完整名称
    public static final String AUTHORITIES = ContactsProvider.class.getCanonicalName();//得到类的完整的路径

    //地址匹配对象
    static UriMatcher uriMatcher;

    //对应的一个联系人表的一个uri常量
    public static Uri URI_CONTACT = Uri.parse("content://" + AUTHORITIES + "/contact");

    private static final int CONTACT = 1;

    static {
        //初始化,
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //添加一个匹配规则
        uriMatcher.addURI(AUTHORITIES, "/contact", CONTACT);
        //外界访问:   ---content://company.zzw.john.zwchat.provider.ContactsProvider/contact


    }

    private ContactOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new ContactOpenHelper(getContext());
        if (mHelper != null) {
            return true;
        }
        return false;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = uriMatcher.match(uri);
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();//对数据库进行操作
                long id = db.insert(ContactOpenHelper.T_CONTACT, "", values);
                if (id != -1) {
                    Log.d("aaa", "插入成功");
                    //拼接最新的uri--content://company.zzw.john.zwchat.provider.ContactsProvider/contact/id
                    uri = ContentUris.withAppendedId(uri, id);

                    //通知ContentObserver数据进行改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT, null);

                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = uriMatcher.match(uri);
        int deleteCount = 0;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();//对数据库进行操作
                //影响的行数
                deleteCount = db.delete(ContactOpenHelper.T_CONTACT, selection, selectionArgs);
                if (deleteCount > 0) {
                    Log.d("aaa", "删除成功");

                }
                break;
            default:
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = uriMatcher.match(uri);
        int updateCount = 0;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();//对数据库进行操作
                //影响更新的记录总数
                updateCount = db.update(ContactOpenHelper.T_CONTACT, values, selection, selectionArgs);
                if (updateCount > 0) {
                    Log.d("aaa", "更新成功");
                }
            default:
                break;
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = uriMatcher.match(uri);
        Cursor query = null;
        switch (code) {
            case CONTACT:
                SQLiteDatabase db = mHelper.getWritableDatabase();//对数据库进行操作
                query = db.query(ContactOpenHelper.T_CONTACT, projection, selection, selectionArgs, null, null, sortOrder);
                Log.d("aaa", "查询成功");
                break;
            default:
                break;
        }
        return query;
    }
}
