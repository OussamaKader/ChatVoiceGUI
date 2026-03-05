package client;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatVoiceGUI {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ChatVoiceGUI(String host, int port) {

        JFrame frame = new JFrame("WhatsApp Chat");
        frame.setSize(420, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.getContentPane().setBackground(new Color(229, 221, 213));

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(new Color(229, 221, 213));

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(240, 242, 245));
        bottom.setBorder(new EmptyBorder(8, 10, 8, 10));

        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        inputField.setBackground(Color.WHITE);

        JButton sendBtn = new JButton("➤");
        sendBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        sendBtn.setBackground(new Color(0, 184, 148));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton recordBtn = new JButton("🎤");
        recordBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        recordBtn.setBackground(Color.WHITE);
        recordBtn.setFocusPainted(false);
        recordBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        recordBtn.setPreferredSize(new Dimension(45, 45));
        recordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottom.add(recordBtn, BorderLayout.WEST);
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setVisible(true);

        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        if (msg.startsWith("AUDIO:")) {
                            addAudioMessage(false, msg.substring(6));
                        } else {
                            addTextMessage(false, msg);
                        }
                    }
                } catch (Exception ignored) {}
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        sendBtn.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                writer.println(text);
                addTextMessage(true, text);
                inputField.setText("");
            }
        });

        inputField.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                writer.println(text);
                addTextMessage(true, text);
                inputField.setText("");
            }
        });

        recordBtn.addActionListener(e -> {
            try {
                if (recordBtn.getText().equals("🎤")) {
                    recordBtn.setText("⏹");
                    recordBtn.setBackground(new Color(255, 200, 200));
                    VoiceUtils.startRecording();
                } else {
                    recordBtn.setText("🎤");
                    recordBtn.setBackground(Color.WHITE);
                    String b64 = VoiceUtils.stopRecording();
                    writer.println("AUDIO:" + b64);
                    addAudioMessage(true, b64);
                }
            } catch (Exception ignored) {}
        });
    }

    private void addTextMessage(boolean isSender, String text) {
        SwingUtilities.invokeLater(() -> {
        
            JPanel mainPanel = new JPanel();
            mainPanel.setOpaque(false);
            
            if (isSender) {
              
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
                mainPanel.add(Box.createHorizontalGlue()); 
            } else {
              
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
            }
            
            mainPanel.setMaximumSize(new Dimension(400, 50));

            JPanel bubble = new JPanel();
            bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
            bubble.setOpaque(true);
            
            if (isSender) {
                bubble.setBackground(new Color(220, 248, 198));
            } else {
                bubble.setBackground(Color.WHITE);
            }
            
            bubble.setBorder(new CompoundBorder(
                new RoundedBorder(7, isSender),
                new EmptyBorder(4, 10, 4, 10)
            ));
            
            bubble.setMaximumSize(new Dimension(250, 40));

            JTextArea area = new JTextArea(text);
            area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setOpaque(false);
            
            area.setMaximumSize(new Dimension(200, 20));
            area.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            timePanel.setOpaque(false);
            timePanel.setMaximumSize(new Dimension(250, 15));
            
            JLabel timeLabel = new JLabel(timeFormat.format(new Date()));
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            timeLabel.setForeground(new Color(150, 150, 150));
            
            if (isSender) {
                JLabel checkLabel = new JLabel("✓✓");
                checkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                checkLabel.setForeground(new Color(0, 150, 255));
                timePanel.add(checkLabel);
            }
            
            timePanel.add(timeLabel);

            bubble.add(area);
            bubble.add(Box.createVerticalStrut(2));
            bubble.add(timePanel);

            mainPanel.add(bubble);
            
            if (!isSender) {
                
                mainPanel.add(Box.createHorizontalGlue());
            }
            
            messagesPanel.add(mainPanel);
            messagesPanel.add(Box.createVerticalStrut(2));
            refreshScroll();
        });
    }

    private void addAudioMessage(boolean isSender, String b64) {
        SwingUtilities.invokeLater(() -> {
            
            JPanel mainPanel = new JPanel();
            mainPanel.setOpaque(false);
            
            if (isSender) {
              
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
                mainPanel.add(Box.createHorizontalGlue()); 
            } else {
                
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
            }
            
            mainPanel.setMaximumSize(new Dimension(400, 50));

            JPanel bubble = new JPanel(new BorderLayout(8, 0));
            bubble.setOpaque(true);
            
            if (isSender) {
                bubble.setBackground(new Color(220, 248, 198));
            } else {
                bubble.setBackground(Color.WHITE);
            }
            
            bubble.setBorder(new CompoundBorder(
                new RoundedBorder(7, isSender),
                new EmptyBorder(8, 10, 8, 10)
            ));
            
            bubble.setPreferredSize(new Dimension(180, 35));
            bubble.setMaximumSize(new Dimension(180, 35));

          
            JButton play = new JButton("▶");
            play.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            play.setBackground(new Color(0, 184, 148));
            play.setForeground(Color.WHITE);
            play.setFocusPainted(false);
            play.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            play.setCursor(new Cursor(Cursor.HAND_CURSOR));
            play.setPreferredSize(new Dimension(30, 30));
          
            JPanel timePanel = new JPanel();
            timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
            timePanel.setOpaque(false);
            
      
            int audioDuration = calculateAudioDuration(b64);
            String durationText = formatDuration(audioDuration);
            JLabel durationLabel = new JLabel(durationText);
            durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            durationLabel.setForeground(new Color(80, 80, 80));
            durationLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
          
            JLabel timeLabel = new JLabel(timeFormat.format(new Date()));
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            timeLabel.setForeground(new Color(150, 150, 150));
            timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            timePanel.add(durationLabel);
            timePanel.add(Box.createVerticalStrut(2));
            timePanel.add(timeLabel);

            bubble.add(play, BorderLayout.WEST);
            bubble.add(timePanel, BorderLayout.CENTER);

            mainPanel.add(bubble);
            
            if (!isSender) {
                
                mainPanel.add(Box.createHorizontalGlue());
            }
            
            messagesPanel.add(mainPanel);
            messagesPanel.add(Box.createVerticalStrut(2));
            refreshScroll();

            play.addActionListener(e -> {
                new Thread(() -> {
                    try {
                        play.setEnabled(false);
                        play.setText("⏸");
                        VoiceUtils.playAudioBase64(b64);
                        play.setText("▶");
                        play.setEnabled(true);
                    } catch (Exception ignored) {
                        play.setText("▶");
                        play.setEnabled(true);
                    }
                }).start();
            });
        });
    }
    
    private int calculateAudioDuration(String base64) {
        try {
            int base64Length = base64.length();
            int originalSize = (base64Length * 3) / 4;
            int bytesPerSecond = 16000;
            int durationSeconds = originalSize / bytesPerSecond;
            return Math.max(1, Math.min(60, durationSeconds));
        } catch (Exception e) {
            return 5;
        }
    }
    
    private String formatDuration(int seconds) {
        if (seconds < 60) {
            return String.format("0:%02d", seconds);
        } else {
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            return String.format("%d:%02d", minutes, remainingSeconds);
        }
    }

    private void refreshScroll() {
        messagesPanel.revalidate();
        messagesPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatVoiceGUI("localhost", 6000);
        });
    }
}

class RoundedBorder implements javax.swing.border.Border {

    private int radius;
    @SuppressWarnings("unused")
    private boolean isSender;

    RoundedBorder(int radius, boolean isSender) {
        this.radius = radius;
        this.isSender = isSender;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(3, 3, 3, 3);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(200, 200, 200, 50));
        g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
    }
}