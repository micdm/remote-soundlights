package com.micdm.remotesoundlights.activities.boss;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.micdm.remotesoundlights.activities.VisualizationActivity;
import com.micdm.remotesoundlights.data.GainListPacket;
import com.micdm.remotesoundlights.data.GainListPacketBuilder;
import com.micdm.remotesoundlights.net.NetParams;
import com.micdm.remotesoundlights.net.SenderThread;

import java.io.IOException;
import java.net.InetAddress;

public class BossActivity extends VisualizationActivity {

    private SenderThread sender;
    private Analyzer analyzer;
    private VisualizerWatcher watcher;

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

    private void setupSender() {
        sender = new SenderThread(getBroadcastAddress(), NetParams.PORT);
        sender.start();
    }

    private void setupAnalyzer() {
        analyzer = new Analyzer(Analyzer.TYPE.NORMAL, new Analyzer.OnGainListener() {
            @Override
            public void onGain(Analyzer.Gain[] gains) {
                GainListPacket packet = new GainListPacket(gains);
                sender.send(GainListPacketBuilder.encode(packet));
            }
        });
    }

    private void setupWatcher() {
        watcher = new VisualizerWatcher(new VisualizerWatcher.OnDataListener() {
            @Override
            public void onData(byte[] data) {
                analyzer.setFftData(data);
            }
        });
        watcher.init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSender();
        setupAnalyzer();
        setupWatcher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watcher.deinit();
        sender.cancel();
    }
}
