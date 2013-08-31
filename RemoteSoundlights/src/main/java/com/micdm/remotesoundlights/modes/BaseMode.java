package com.micdm.remotesoundlights.modes;

import android.content.Context;

import com.micdm.remotesoundlights.data.GainListPacket;
import com.micdm.remotesoundlights.data.GainListPacketBuilder;
import com.micdm.remotesoundlights.net.ReceiverThread;

public class BaseMode {

    public static interface OnReceiveListener {
        public void onReceive(GainListPacket packet);
    }

    protected Context context;
    private ReceiverThread receiver;
    private OnReceiveListener listener;

    public BaseMode(Context context, OnReceiveListener listener) {
        this.context = context;
        this.listener = listener;
    }

    private void setupReceiver() {
        receiver = new ReceiverThread(new ReceiverThread.OnDataListener() {
            @Override
            public void onData(byte[] data) {
                GainListPacket packet = GainListPacketBuilder.decode(data);
                listener.onReceive(packet);
            }
        });
        receiver.start();
    }

    public void onCreate() {
        setupReceiver();
    }

    public void onStart() {
        setupReceiver();
    }

    public void onStop() {
        receiver.cancel();
    }

    public void onDestroy() {}
}