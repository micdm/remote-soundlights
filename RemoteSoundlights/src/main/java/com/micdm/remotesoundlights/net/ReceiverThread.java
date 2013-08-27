package com.micdm.remotesoundlights.net;

import com.micdm.remotesoundlights.utils.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiverThread extends Thread {

    public static interface OnDataListener {
        public void onData(byte[] data);
    }

    private boolean isActive = true;
    private DatagramSocket socket;
    private OnDataListener listener;

    public ReceiverThread(OnDataListener listener) {
        this.listener = listener;
    }

    private DatagramSocket getSocket() {
        try {
            return new DatagramSocket(NetParams.PORT);
        } catch (IOException e) {
            throw new RuntimeException("Can not create socket", e);
        }
    }

    private void receive() {
        try {
            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            listener.onData(packet.getData());
        } catch (IOException e) {
            Logger.warning("Exception occurred during packet receive", e);
        }
    }

    @Override
    public void run() {
        socket = getSocket();
        while (isActive) {
            receive();
            try {
                sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void cancel() {
        isActive = false;
        socket.close();
    }
}
