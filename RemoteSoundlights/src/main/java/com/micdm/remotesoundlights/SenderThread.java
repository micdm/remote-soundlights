package com.micdm.remotesoundlights;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;

public class SenderThread extends Thread {

    private final int QUEUE_SIZE = 100;

    private boolean isActive = true;
    private InetAddress address;
    private int port;

    private ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(QUEUE_SIZE);

    public SenderThread(InetAddress address, int port) {
        this.address = address;
        this.port = port;
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

    private void sendNext(DatagramSocket socket) {
        byte[] data = queue.poll();
        if (data == null) {
            return;
        }
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        DatagramSocket socket = getSocket();
        while (isActive) {
            sendNext(socket);
            try {
                sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void send(byte[] data) {
        queue.add(data);
    }

    public void cancel() {
        isActive = false;
    }
}