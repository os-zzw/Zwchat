package company.zzw.john.zwchat.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by john on 2016/4/8.
 */
public class SpTools {

    /**
     *
     * @param context---用来得到Sp保存数据
     * @param key----关键字
     * @param value----对应的值
     */
    public static void setBoolean(Context context,String key,boolean value){

        SharedPreferences sp=context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();//提交保存键值对

    }

    /**
     *
     * @param context
     * @param key--------关键字
     * @param defValue-------设置的默认值为false
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue){
        SharedPreferences sp=context.getSharedPreferences(MyConstants.CONFIGFILE,Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }

    /**
     *
     * @param context---用来得到Sp保存数据
     * @param key----关键字
     * @param value----对应的值
     */
    public static void setString(Context context,String key,String value){

        SharedPreferences sp=context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();//提交保存键值对

    }

    /**
     *
     * @param context
     * @param key--------关键字
     * @param defValue-------设置的默认值为false
     * @return
     */
    public static String getString(Context context, String key, String defValue){
        SharedPreferences sp=context.getSharedPreferences(MyConstants.CONFIGFILE,Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }
}
