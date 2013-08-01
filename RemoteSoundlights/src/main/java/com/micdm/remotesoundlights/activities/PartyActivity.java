package com.micdm.remotesoundlights.activities;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.micdm.remotesoundlights.ReceiverThread;
import com.micdm.remotesoundlights.SenderThread;
import com.micdm.remotesoundlights.data.DataPacket;
import com.micdm.remotesoundlights.data.DataPacketBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public abstract class PartyActivity extends Activity {

    private final int PORT = 50000;

    protected UUID uuid;
    protected ReceiverThread receiver;
    protected SenderThread sender;

    private InetAddress getBroadcastAddress() {
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = manager.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int i = 0; i < 4; i += 1) {
            quads[i] = (byte) ((broadcast >> i * 8) & 0xFF);
        }
        try {
            return InetAddress.getByAddress(quads);
        } catch (IOException e) {
            return null;
        }
    }

    protected void onDataReceived(DataPacket packet) {}

    protected void sendData(long content) {
        DataPacket packet = new DataPacket(uuid.getLeastSignificantBits(), content);
        sender.send(DataPacketBuilder.encode(packet));
    }

    protected void startReceiverAndSender() {
        uuid = UUID.randomUUID();
        receiver = new ReceiverThread(PORT, new ReceiverThread.OnDataListener() {
            @Override
            public void onData(byte[] data) {
                final DataPacket packet = DataPacketBuilder.decode(data);
                if (packet == null) {
                    return;
                }
                if (packet.getSender() == uuid.getLeastSignificantBits()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onDataReceived(packet);
                    }
                });
            }
        });
        receiver.start();
        sender = new SenderThread(getBroadcastAddress(), PORT);
        sender.start();
    }

    protected void stopReceiverAndSender() {
        receiver.cancel();
        sender.cancel();
    }
}
