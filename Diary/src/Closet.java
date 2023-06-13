import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Closet extends JFrame implements ActionListener {

	JPanel panel/* , topPanel */;
	Cards cards;
	JLabel label;
	static String a = "img\\cha11.png";
	ImageIcon no1;
	ImageIcon no2;
	ImageIcon no3;
	ImageIcon no4;
	ImageIcon no5;
	ImageIcon updateIcon;
	Image img, updateImg;
	int b = 1;

	public Closet() {
		setSize(350, 350);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		label = new JLabel();
		panel = new JPanel(new GridLayout(0, 5));
//		topPanel = new JPanel(new GridLayout(0, 1));

//버튼생성하고 패널에 추가
		addButton("LV.1", panel);
		addButton("LV.5", panel);
		addButton("LV.10", panel);
		addButton("LV.15", panel);
		addButton("LV.20", panel);
//패널을 추가
//		add(topPanel, "North");
		add(panel, "South");
		cards = new Cards();
		cards.add(label);
		add(cards, "Center");
		cards.setLayout(null);
		setVisible(true);
		setLocationRelativeTo(null);
	}

//버튼생성과동시에 이벤트실행하는 메소드
	void addButton(String str, Container target) {
		JButton button = new JButton(str);
		button.addActionListener(this);
		target.add(button);
	}

//이벤트 메소드
	@Override
	public void actionPerformed(ActionEvent e) {
		b = TodoFrame.count;
		if (e.getActionCommand().equals("LV.1")) {
			no1 = new ImageIcon("img\\cha11.png");
			img = no1.getImage();
			a = "img\\cha11.png";
			image();
		} else if (e.getActionCommand().equals("LV.5")) {
			if (b > 4) {
				no2 = new ImageIcon("img\\cha22.png");
				img = no2.getImage();
				a = "img\\cha22.png";
				image();
			} else {
				JOptionPane.showMessageDialog(null, "레벨이 5이상 이어야 합니다.", "잠김", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getActionCommand().equals("LV.10")) {
			if (b > 9) {
				no3 = new ImageIcon("img\\cha33.png");
				img = no3.getImage();
				a = "img\\cha33.png";
				image();
			} else {
				JOptionPane.showMessageDialog(null, "레벨이 10이상 이어야 합니다.", "잠김", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getActionCommand().equals("LV.15")) {
			if (b > 14) {
				no4 = new ImageIcon("img\\cha44.png");
				img = no4.getImage();
				a = "img\\cha44.png";
				image();
			} else {
				JOptionPane.showMessageDialog(null, "레벨이 15이상 이어야 합니다.", "잠김", JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getActionCommand().equals("LV.20")) {
			if (b > 19) {
				no5 = new ImageIcon("img\\cha55.png");
				img = no5.getImage();
				a = "img\\cha55.png";
				image();
			} else {
				JOptionPane.showMessageDialog(null, "레벨이 20이상 이어야 합니다.", "잠김", JOptionPane.ERROR_MESSAGE);
			}
		}
		revalidate();
	}
	
	public void image() {
		updateImg = img.getScaledInstance(100, 150, Image.SCALE_SMOOTH);
		updateIcon = new ImageIcon(updateImg);
		label.setIcon(updateIcon);
		label.setBounds(115, 75, 100, 150);
		label.setHorizontalAlignment(JLabel.CENTER);
	}
	
	class Cards extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		CardLayout layout;

		public Cards() {
			layout = new CardLayout();
			setLayout(layout);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Image img = new ImageIcon("img\\closet.png").getImage();
			Dimension d = getSize();
			g.drawImage(img, 0, 0, d.width, d.height, null);
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Closet d = new Closet();
	}
}