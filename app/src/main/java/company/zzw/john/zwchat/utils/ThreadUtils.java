package company.zzw.john.zwchat.utils;

import android.os.Handler;

/**
 * Created by john on 2016/5/14.
 */
public class ThreadUtils {

    /**
     * 子线程执行的Task
     */

    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    /**
     * 主线程里边的handler
     */
    public static Handler mhandler = new Handler();

    /**
     * UI线程执行的Task
     */
    public static void runInUIThread(Runnable task) {
        mhandler.post(task);
    }
}
