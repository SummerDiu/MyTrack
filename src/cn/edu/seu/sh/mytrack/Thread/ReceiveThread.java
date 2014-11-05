package cn.edu.seu.sh.mytrack.Thread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import cn.edu.seu.sh.mytrack.Config.CommonConfig;

public class ReceiveThread extends Thread {

    private boolean keepRunning = true;
    private DatagramSocket socket = null;
    private AudioTrack audioTrack = null;
    private int minSize = 0;
    int n = 0;

    @Override
    public void run() {
        initSocket();
        initAudio();
        while(keepRunning){
            receiveAndPlay();
        }
        release();
    }

    public void stopThread(){
        keepRunning = false;
    }

    private void initSocket(){
        if(socket==null){
            try{
                socket = new DatagramSocket(CommonConfig.CLIENT_A_PORT);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initAudio(){
        minSize = AudioTrack.getMinBufferSize(CommonConfig.freq,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,CommonConfig.freq,AudioFormat.CHANNEL_OUT_STEREO,AudioFormat.ENCODING_PCM_16BIT,minSize,AudioTrack.MODE_STREAM);
        audioTrack.play();
    }

    private void receiveAndPlay(){
        byte[] packetBuffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(packetBuffer,packetBuffer.length);

        try {
           socket.receive(packet);
//            Log.d("ReceiveThread","Receive Audio Packet!");
//           if(n++<100){
           	System.out.println("ClientReceivePacket:--->from"+packet.getAddress()+"length:"+packet.getLength()+
           			"content:"+Arrays.toString(packet.getData()));
//           }
           audioTrack.write(packet.getData(),0,packet.getData().length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release(){
        if(socket!=null){
            socket.close();
            socket = null;
        }
        if(audioTrack!=null){
            audioTrack.release();
            audioTrack = null;
        }
    }
}