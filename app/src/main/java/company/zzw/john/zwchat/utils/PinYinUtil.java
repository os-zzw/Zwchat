package company.zzw.john.zwchat.utils;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by john on 2016/5/17.
 */
public class PinYinUtil {

    public static String getPinYin(String str) {
        //返回中文的拼音
        //str-中文,  分隔符  ,   格式-没有声调
        return PinyinHelper.convertToPinyinString(str, "", PinyinFormat.WITHOUT_TONE);
    }
}
