package com.micdm.remotesoundlights.modes.boss;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.micdm.remotesoundlights.data.GainListPacket;
import com.micdm.remotesoundlights.data.GainListPacketBuilder;
import com.micdm.remotesoundlights.modes.BaseMode;
import com.micdm.remotesoundlights.net.SenderThread;

import java.io.IOException;
import java.net.InetAddress;

public class BossMode extends BaseMode {

    private SenderThread sender;
    private AnalyzerThread analyzer;
    private VisualizerWatcher watcher;

    public BossMode(Context context, OnReceiveListener listener) {
        super(context, listener);
    }

    private InetAddress getBroadcastAddress() {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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
        sender = new SenderThread(getBroadcastAddress());
        sender.start();
    }

    private void setupAnalyzer() {
        analyzer = new AnalyzerThread(new AnalyzerThread.OnGainListener() {
            @Override
            public void onGain(Analyzer.Gain[] gains) {
                GainListPacket packet = new GainListPacket(gains);
                sender.send(GainListPacketBuilder.encode(packet));
            }
        });
        analyzer.start();
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
    public void onCreate() {
        super.onCreate();
        setupSender();
        setupAnalyzer();
        setupWatcher();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        watcher.deinit();
        analyzer.cancel();
        sender.cancel();
    }
}
