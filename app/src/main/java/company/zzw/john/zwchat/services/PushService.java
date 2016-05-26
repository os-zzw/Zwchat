package company.zzw.john.zwchat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import java.util.Collection;

import company.zzw.john.zwchat.utils.ToastUtils;

/**
 * Created by john on 2016/5/21.
 */
public class PushService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        IMService.conn.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                org.jivesoftware.smack.packet.Message msg = (org.jivesoftware.smack.packet.Message) packet;
                String body = ((org.jivesoftware.smack.packet.Message) packet).getBody();
                ToastUtils.showSafeToast(body);

            }
        }, null);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
