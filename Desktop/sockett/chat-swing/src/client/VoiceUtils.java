package client;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Base64;

public class VoiceUtils {

    private static TargetDataLine microphone;
    private static ByteArrayOutputStream audioStream;
    private static SourceDataLine playbackLine;


    public static void startRecording() throws Exception {

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);

        microphone = AudioSystem.getTargetDataLine(format);
        microphone.open();
        microphone.start();

        audioStream = new ByteArrayOutputStream();

        new Thread(() -> {
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (microphone.isOpen()) {
                    bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) audioStream.write(buffer, 0, bytesRead);
                }
            } catch (Exception ignored) {}
        }).start();
    }

    public static String stopRecording() throws Exception {
        microphone.stop();
        microphone.close();
        byte[] audioBytes = audioStream.toByteArray();
        return Base64.getEncoder().encodeToString(audioBytes);
    }



    public static void playAudioBase64(String base64) throws Exception {

        byte[] data = Base64.getDecoder().decode(base64);

        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        playbackLine = AudioSystem.getSourceDataLine(format);
        playbackLine.open(format);
        playbackLine.start();

        playbackLine.write(data, 0, data.length);

        playbackLine.drain();
        playbackLine.stop();
        playbackLine.close();
    }



    public static void forceStopPlayback() {
        try {
            if (playbackLine != null) {
                playbackLine.stop();
                playbackLine.flush();
                playbackLine.close();
            }
        } catch (Exception ignored) {}
    }
}
