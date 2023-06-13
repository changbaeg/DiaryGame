import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Setting {
    private JFrame mainFrame;
    private boolean isBgmOn = true; // BGM 상태 변수
    private Player bgmPlayer; // BGM 재생용 Player 객체
    private static boolean isBgmPlaying = false; // BGM 재생 상태 변수

    public Setting() {
    	playBgm();
        initializeUI();
    }

    private void initializeUI() {
        mainFrame = new JFrame("Option");
//        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel settingsPanel = new JPanel(new BorderLayout());

        // BGM On/Off 토글 버튼 구현
        JToggleButton bgmToggleButton = new JToggleButton();
        ImageIcon bgmOnIcon = new ImageIcon("img\\bgm_icon.png"); // BGM On 이미지 파일 경로에 맞게 수정
        ImageIcon bgmOffIcon = new ImageIcon("img\\bgm_off_icon.png"); // BGM Off 이미지 파일 경로에 맞게 수정
        bgmToggleButton.setIcon(new ImageIcon(bgmOnIcon.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT))); // 아이콘 크기 조정
        bgmToggleButton.setSelectedIcon(new ImageIcon(bgmOffIcon.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT))); // 아이콘 크기 조정
        bgmToggleButton.setSelected(isBgmOn); // 초기 상태 설정
        bgmToggleButton.setPreferredSize(new Dimension(70, 70)); // 버튼의 크기 설정
        bgmToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBgmOn = bgmToggleButton.isSelected(); // BGM 상태 변경
                if (isBgmOn) {
                    playBgm(); // BGM 재생
                    JOptionPane.showMessageDialog(mainFrame, "BGM이 켜졌습니다.");
                } else {
                    stopBgm(); // BGM 정지
                    JOptionPane.showMessageDialog(mainFrame, "BGM이 꺼졌습니다.");
                }
            }
        });
        settingsPanel.add(bgmToggleButton, BorderLayout.NORTH);

        // 로그아웃 버튼 구현
        JButton logoutButton = new JButton();
        ImageIcon logoutIcon = new ImageIcon("img\\logout_icon.png"); // 로그아웃 버튼 이미지 파일 경로에 맞게 수정
        logoutButton.setIcon(new ImageIcon(logoutIcon.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT))); // 아이콘 크기 조정
        logoutButton.setPreferredSize(new Dimension(70, 70)); // 버튼의 크기 설정
        logoutButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(mainFrame, "로그아웃 되었습니다.");
            	// 버튼을 누르면 로그아웃
            	System.exit(0);
            }
        });
        settingsPanel.add(logoutButton, BorderLayout.SOUTH);
        
        mainFrame.getContentPane().add(settingsPanel);
        mainFrame.setSize(200, 200);
        mainFrame.setLocationRelativeTo(null);
    }

    private void playBgm() {
        try {
            bgmPlayer = new Player(new FileInputStream("music\\bgm.mp3"));
            Thread bgmThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bgmPlayer.play();
                        isBgmPlaying = true; // BGM 재생 상태 업데이트
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                }
            });
            bgmThread.start();
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    private void stopBgm() {
    	if (bgmPlayer != null) {
            bgmPlayer.close();
            isBgmPlaying = false; // BGM 재생 상태 업데이트
        }
    }

    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mainFrame.setVisible(true);
            }
        });
    }
    
    public static boolean isBgmPlaying() {
        return isBgmPlaying;
    }

    public static void main(String[] args) {
        Setting app = new Setting();
        app.start();
    }
}