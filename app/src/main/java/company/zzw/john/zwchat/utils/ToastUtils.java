package company.zzw.john.zwchat.utils;

import android.widget.Toast;

import company.zzw.john.zwchat.application.MyApplication;

/**
 * Created by john on 2016/5/16.
 */
public class ToastUtils {

    /**
     * 在子线程中进行Toast
     *
     * @param text
     */
    public static void showToast(final String text) {
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 在主线程中进行Toast,安全的Toast
     */
    public static void showSafeToast(final String text) {
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
