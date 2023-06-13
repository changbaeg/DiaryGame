import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.util.Random;

@SuppressWarnings("serial")
public class Home extends JFrame implements KeyListener, MouseListener {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int CHARACTER_WIDTH = 80;
    private static final int CHARACTER_HEIGHT = 120;
    private static final int FRAME_DELAY = 500; // 0.5초 (밀리초 단위)
    private static final int BLOCK_SIZE = 50; // 블록의 크기

    private BufferedImage background; // 배경 이미지
    private BufferedImage characterSheet; // 캐릭터 시트 이미지
    private BufferedImage[][] characterFrames; // 캐릭터 프레임들
    private int currentRow = 3; // 현재 캐릭터 방향 (앞쪽)
    private int currentCol = 1; // 현재 캐릭터 프레임
    private double characterX; // 캐릭터 X 좌표
    private double characterY; // 캐릭터 Y 좌표
    private double characterSpeed = 1; // 캐릭터 이동 속도
    private Timer frameTimer; // 프레임 갱신을 위한 타이머
    private int leftBlockX; // 왼쪽 블록의 X 좌표
    private int leftBlockY; // 왼쪽 블록의 Y 좌표
    private int rightBlockX; // 오른쪽 블록의 X 좌표
    private int rightBlockY; // 오른쪽 블록의 Y 좌표
    private int bottomBlockX; // 아래쪽 왼쪽 블록의 X 좌표
    private int bottomBlockY; // 아래쪽 왼쪽 블록의 Y 좌표
    private boolean missionCompleted = false; // 캐릭터가 블록에 다가갔는지 여부를 나타내는 플래그
    private int blueBlockX; // 파란색 블록의 X 좌표
    private int blueBlockY; // 파란색 블록의 Y 좌표
    private boolean blueBlockVisible; // 파란색 블록의 표시 여부
    private Random random; // 랜덤한 위치 생성을 위한 Random 객체
    private Timer blueBlockTimer; // 파란색 블록 생성 타이머
    private int orangeBlockX; // 주황색 블록의 X 좌표
    private int orangeBlockY; // 주황색 블록의 Y 좌표
    private boolean orangeBlockVisible; // 주황색 블록의 표시 여부
    private BufferedImage blockA;
    private BufferedImage blockB;
    private BufferedImage blockC;
    private BufferedImage blockD;
    private BufferedImage TrashImage;

    public Home() {
        GameView();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        addKeyListener(this); // KeyListener 등록
        addMouseListener(this); // MouseListener 등록

        // 초기 캐릭터 위치 설정
        characterX = (WIDTH - CHARACTER_WIDTH) / 2;
        characterY = (HEIGHT - CHARACTER_HEIGHT) / 2;
        
        // 프레임 갱신 타이머 설정
        frameTimer = new Timer(FRAME_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCharacterFrame();
                checkMissionCompletion(); // 다가갔는지 여부 확인
                repaint();
            }
        });

     // 초기 파란색 블록 위치 설정
        random = new Random();
        generateBlueBlockPosition();
        blueBlockVisible = true;

        // 파란색 블록 생성 타이머 설정
        blueBlockTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBlueBlockPosition();
                blueBlockVisible = true;
                repaint();
            }
        });
        blueBlockTimer.setRepeats(false); // 한 번만 실행하도록 설정

        setVisible(true);

        // 타이머 시작 (0.5초마다 프레임 갱신)
        frameTimer.start();
        // 파란색 블록 생성 타이머 시작
        blueBlockTimer.start();

        setVisible(true);
        
     // 초기 블록 위치 설정
        leftBlockX = (WIDTH - CHARACTER_WIDTH) / 2 - BLOCK_SIZE - 200;
        leftBlockY = (HEIGHT - BLOCK_SIZE) / 2;
        rightBlockX = (WIDTH + CHARACTER_WIDTH) / 2 + 200;
        rightBlockY = (HEIGHT - BLOCK_SIZE) / 2;
        bottomBlockX = (WIDTH - BLOCK_SIZE) / 2;
        bottomBlockY = (HEIGHT + CHARACTER_HEIGHT) / 2 + 180;
        
     // 초기 주황색 블록 위치 설정
        orangeBlockX = getWidth() - 63; // 우측 상단에 위치
        orangeBlockY = 35; // 상단에서 10픽셀 아래에 위치
        orangeBlockVisible = true;

    }

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
            Home home = new Home();
            home.setVisible(true);
        });
    	
//    	new Setting(); //---> 이거 주석 지우면 시작 화면에서 노래 나오긴 함, 설정 열면 노래가 두개가 됨
    }
    
    private void checkBlueBlockCollision() {
        // 파란 블록과 캐릭터의 충돌 검사
        if (characterX + CHARACTER_WIDTH >= blueBlockX &&
                characterX <= blueBlockX + BLOCK_SIZE &&
                characterY + CHARACTER_HEIGHT >= blueBlockY &&
                characterY <= blueBlockY + BLOCK_SIZE) {
            if (blueBlockVisible && !missionCompleted) {
                // 빨간 블록과의 충돌 검사
                if (!checkRedBlockCollision()) {
                    missionCompleted = true;
                    incrementExperiencePoints();
                    blueBlockVisible = false;
                    blueBlockTimer.restart(); // 10초 후에 새로운 파란색 블록 생성
                }
            }
        }
    }
    
    private void checkOrangeBlockCollision() {
        // 주황색 블록과 캐릭터의 충돌 검사
        if (characterX + CHARACTER_WIDTH > orangeBlockX &&
                characterX < orangeBlockX + BLOCK_SIZE &&
                characterY + CHARACTER_HEIGHT > orangeBlockY &&
                characterY < orangeBlockY + BLOCK_SIZE) {
            if (orangeBlockVisible && !missionCompleted) {
                // 주황색 블록 클릭 시 set() 메소드 실행
                if (SwingUtilities.isLeftMouseButton(null)) {
                    new Setting().start();
                }
            }
        }
    }
    
    private boolean checkRedBlockCollision() {
        // 빨간 블록과 파란 블록의 충돌 검사
        boolean collision = false;

        if (blueBlockX + BLOCK_SIZE >= leftBlockX &&
                blueBlockX <= leftBlockX + BLOCK_SIZE &&
                blueBlockY + BLOCK_SIZE >= leftBlockY &&
                blueBlockY <= leftBlockY + BLOCK_SIZE) {
            collision = true;
        } else if (blueBlockX + BLOCK_SIZE >= rightBlockX &&
                blueBlockX <= rightBlockX + BLOCK_SIZE &&
                blueBlockY + BLOCK_SIZE >= rightBlockY &&
                blueBlockY <= rightBlockY + BLOCK_SIZE) {
            collision = true;
        } else if (blueBlockX + BLOCK_SIZE >= bottomBlockX &&
                blueBlockX <= bottomBlockX + BLOCK_SIZE &&
                blueBlockY + BLOCK_SIZE >= bottomBlockY &&
                blueBlockY <= bottomBlockY + BLOCK_SIZE) {
            collision = true;
        }

        return collision;
    }

	private void checkMissionCompletion() {
        // 캐릭터와 블록의 충돌 검사
        if (characterX + CHARACTER_WIDTH >= leftBlockX &&
                characterX <= leftBlockX + BLOCK_SIZE &&
                characterY + CHARACTER_HEIGHT >= leftBlockY &&
                characterY <= leftBlockY + BLOCK_SIZE) {
            if (!missionCompleted) {
                missionCompleted = true;
                new Memo(); // Memo 클래스 실행
            }
        } else if (characterX + CHARACTER_WIDTH >= rightBlockX &&
                characterX <= rightBlockX + BLOCK_SIZE &&
                characterY + CHARACTER_HEIGHT >= rightBlockY &&
                characterY <= rightBlockY + BLOCK_SIZE) {
            if (!missionCompleted) {
                missionCompleted = true;
                new TodoFrame(); // Memo 클래스 실행
            }
        } else if (characterX + CHARACTER_WIDTH >= bottomBlockX &&
                characterX <= bottomBlockX + BLOCK_SIZE &&
                characterY + CHARACTER_HEIGHT >= bottomBlockY &&
                characterY <= bottomBlockY + BLOCK_SIZE) {
            if (!missionCompleted) {
                missionCompleted = true;
                new Closet(); // Memo 클래스 실행
            }
        } else {
            missionCompleted = false; // 다가갔던 블록에서 멀어지면 재실행을 위해 플래그 초기화
        }
    }
	
	public void mouseClicked(MouseEvent e) {
	    if (e.getButton() == MouseEvent.BUTTON1 && !missionCompleted) {
	    	int clickedX = e.getX();  // 클릭한 X 좌표
	        int clickedY = e.getY();  // 클릭한 Y 좌표

	        // 클릭한 좌표가 orangeBlock 영역 내에 있는지 확인
	        if (clickedX >= orangeBlockX && clickedX <= orangeBlockX + BLOCK_SIZE &&
	            clickedY >= orangeBlockY && clickedY <= orangeBlockY + BLOCK_SIZE) {
	            new Setting().start();
	        }
	    }
	}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            double newCharacterY = characterY - 5;
            if (newCharacterY < 0)
                newCharacterY = 0;
            characterY = newCharacterY;
            currentRow = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            double newCharacterY = characterY + 5;
            if (newCharacterY + CHARACTER_HEIGHT > HEIGHT)
                newCharacterY = HEIGHT - CHARACTER_HEIGHT;
            characterY = newCharacterY;
            currentRow = 3;
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            double newCharacterX = characterX - 5;
            if (newCharacterX < 0)
                newCharacterX = 0;
            characterX = newCharacterX;
            currentRow = 4;
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            double newCharacterX = characterX + 5;
            if (newCharacterX + CHARACTER_WIDTH > WIDTH)
                newCharacterX = WIDTH - CHARACTER_WIDTH;
            characterX = newCharacterX;
            currentRow = 2;
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        checkBlueBlockCollision();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void GameView() {
        try {
            background = ImageIO.read(new File("img\\background.png"));
            characterSheet = ImageIO.read(new File("img\\cha1.png"));
            characterFrames = extractFrames(characterSheet, 4, 3);
            blockA = ImageIO.read(new File("img\\Diary.png"));
            blockB = ImageIO.read(new File("img\\todo.png"));
            blockC = ImageIO.read(new File("img\\closet_icon.png"));
            TrashImage = ImageIO.read(new File("img\\trash.png"));
            blockD = ImageIO.read(new File("img\\setting.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage[][] extractFrames(BufferedImage sheet, int rows, int cols) {
        int frameWidth = sheet.getWidth() / cols;
        int frameHeight = sheet.getHeight() / rows;
        BufferedImage[][] frames = new BufferedImage[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * frameWidth;
                int y = row * frameHeight;
                frames[row][col] = sheet.getSubimage(x, y, frameWidth, frameHeight);
            }
        }

        return frames;
    }
    
    private void generateBlueBlockPosition() {
        int maxX = WIDTH - 30;
        int maxY = HEIGHT - 30;
        blueBlockX = random.nextInt(maxX);
        blueBlockY = random.nextInt(maxY);
    }
    
    private void incrementExperiencePoints() {
        // 경험치 증가
        // 경험치가 10 이상이면 레벨 업데이트
    }

    private void updateCharacterFrame() {
        currentCol += 1;
        if (currentCol >= characterFrames[currentRow - 1].length)
            currentCol = 0;
    }

 // paint 메서드 오버라이드
    @Override
    public void paint(Graphics g) {
        // 더블 버퍼링을 위해 백그라운드 버퍼 생성
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dBuffer = (Graphics2D) buffer.getGraphics();

        // 백그라운드 이미지 그리기
        g2dBuffer.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // 캐릭터 그리기
        g2dBuffer.drawImage(characterFrames[currentRow - 1][currentCol], (int) characterX, (int) characterY, CHARACTER_WIDTH, CHARACTER_HEIGHT, this);

        // 블록 그리기
        g2dBuffer.drawImage(blockA, leftBlockX, leftBlockY, BLOCK_SIZE, BLOCK_SIZE, this);
        g2dBuffer.drawImage(blockB, rightBlockX, rightBlockY, BLOCK_SIZE, BLOCK_SIZE, this);
        g2dBuffer.drawImage(blockC, bottomBlockX, bottomBlockY, BLOCK_SIZE, BLOCK_SIZE, this);
        g2dBuffer.drawImage(blockD, orangeBlockX, orangeBlockY, BLOCK_SIZE, BLOCK_SIZE, this);

        if (blueBlockVisible) {
            g2dBuffer.drawImage(TrashImage, blueBlockX, blueBlockY, 30, 30, this);
        }

        // 백그라운드 버퍼를 실제 그래픽 객체에 그리기
        g.drawImage(buffer, 0, 0, null);
    }

    
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
}