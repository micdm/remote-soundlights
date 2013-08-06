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
    private int port;
    private OnDataListener listener;

    public ReceiverThread(int port, OnDataListener listener) {
        this.port = port;
        this.listener = listener;
    }

    private DatagramSocket getSocket() {
        try {
            return new DatagramSocket(port);
        } catch (IOException e) {
            return null;
        }
    }

    private void receive() {
        try {
            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            listener.onData(packet.getData());
        } catch (IOException e) {
            Logger.debug("Exception occurred during packet receive");
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
