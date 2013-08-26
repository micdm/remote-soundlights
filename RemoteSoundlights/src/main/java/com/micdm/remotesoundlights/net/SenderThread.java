package com.micdm.remotesoundlights.net;

import com.micdm.remotesoundlights.utils.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;

public class SenderThread extends Thread {

    private static final int QUEUE_SIZE = 100;

    private boolean isActive = true;
    private DatagramSocket socket;
    private ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(QUEUE_SIZE);
    private InetAddress address;

    public SenderThread(InetAddress address) {
        this.address = address;
    }

    private DatagramSocket getSocket() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            return socket;
        } catch (IOException e) {
            return null;
        }
    }

    private void sendNext() {
        byte[] data = queue.poll();
        if (data == null) {
            return;
        }
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, address, NetParams.PORT);
            socket.send(packet);
        } catch (IOException e) {
            Logger.warning("Exception occurred during packet send", e);
        }
    }

    @Override
    public void run() {
        socket = getSocket();
        while (isActive) {
            sendNext();
            try {
                sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void send(byte[] data) {
        queue.offer(data);
    }

    public void cancel() {
        isActive = false;
        socket.close();
    }
}
