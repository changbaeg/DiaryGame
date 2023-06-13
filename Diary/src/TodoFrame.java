import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.sql.*;

public class TodoFrame {
	private JFrame frame;
	private JPanel mainPanel;
	private JTextField newItemTextField;
	private JButton addButton;
	private JList<String> toDoList;
	private DefaultListModel<String> listModel;
	private JButton completeButton;
	private JLabel levelLabel, countLabel;
	public int level;
	private int completionCount=0;
	public static int count = 0;
	private final String dataFolder = "data";
	private final String dataFile = "data\\todolist.txt";
	
	private Connection connection;
	private String url = "jdbc:mysql://localhost:3306/diary?zeroDateTimeBehavior=convertToNull";
	private String username = "root";
	private String password = "4011";

	public TodoFrame() {
		frame = new JFrame("MISSION");
//      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 600);
		frame.setLocationRelativeTo(null);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		newItemTextField = new JTextField();
		newItemTextField.setPreferredSize(new Dimension(200, 30));

		addButton = new JButton("Add");
		addButton.addActionListener(new AddButtonListener());

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		inputPanel.add(newItemTextField);
		inputPanel.add(addButton);

		listModel = new DefaultListModel<>();
		toDoList = new JList<>(listModel);
		JScrollPane listScrollPane = new JScrollPane(toDoList);
		listScrollPane.setBorder(BorderFactory.createTitledBorder("To Do List"));
		listScrollPane.setPreferredSize(new Dimension(360, 300));

		completeButton = new JButton("Complete");
		completeButton.addActionListener(new CompleteButtonListener());
		completeButton.setEnabled(false);

		countLabel = new JLabel("" + count);
		levelLabel = new JLabel("Level: " + level);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(completeButton);
		buttonPanel.add(levelLabel);

		mainPanel.add(inputPanel, BorderLayout.NORTH);
		mainPanel.add(listScrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);

		// createDataFolder(); DB 연결 되면 삭제
		connectToDatabase();
		// 이전 정보 가져오기
        retrieveUserData();
	}

	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() { 
				new TodoFrame(); 
				} 
			});
		}
	
	private void connectToDatabase() {
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        connection = DriverManager.getConnection(url, username, password);
	        System.out.println("Connected to the database");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Failed to load MySQL JDBC driver");
	        e.printStackTrace();
	    } catch (SQLException e) {
	        System.out.println("Failed to connect to the database");
	        e.printStackTrace();
	    }
	}
	
	private class AddButtonListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        String newItem = newItemTextField.getText();
	        if (!newItem.isEmpty()) {
	            listModel.addElement(newItem);
	            newItemTextField.setText("");
	            completeButton.setEnabled(true);

	            // Insert the new item into the database
	            try {
	                String query = "INSERT INTO todo_items (item, user_id) VALUES (?, ?)";
	                PreparedStatement statement = connection.prepareStatement(query);
	                statement.setString(1, newItem);
	                statement.setInt(2, 1); // 예시로 사용하는 user_id 값 (1)
	                statement.executeUpdate();
	                System.out.println("New item inserted into the database");
	            } catch (SQLException ex) {
	                System.out.println("Failed to insert item into the database");
	                ex.printStackTrace();
	            }
	        }
	    }
	}

	
	public class CompleteButtonListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        int selectedIndex = toDoList.getSelectedIndex();
	        if (selectedIndex != -1) {
	            String selectedItem = listModel.getElementAt(selectedIndex);

	            // Delete all records of the selected item from the database
	            try {
	                String deleteQuery = "DELETE FROM todo_items WHERE item = ?";
	                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
	                deleteStatement.setString(1, selectedItem);
	                deleteStatement.executeUpdate();
	                System.out.println("All records of the item deleted from the database");
	                deleteStatement.close();
	            } catch (SQLException ex) {
	                System.out.println("Failed to delete records of the item from the database");
	                ex.printStackTrace();
	            }

	            listModel.remove(selectedIndex);
	            completionCount++;
	            if (completionCount % 5 == 0) {
	                level++;
	                count++;
	                levelLabel.setText("Level: " + level);

	                // Update the level in the database
	                try {
	                	String levelQuery = "UPDATE user_info SET level = ? WHERE user_id = ?";
	                	PreparedStatement levelStatement = connection.prepareStatement(levelQuery);
	                	levelStatement.setInt(1, level);
	                	levelStatement.setInt(2, 1); // 예시로 사용하는 user_id 값 (1)
	                	levelStatement.executeUpdate();
	                	System.out.println("Level updated in the database");
	                	levelStatement.close();
	                } catch (SQLException ex) {
	                    System.out.println("Failed to update level in the database");
	                    ex.printStackTrace();
	                }
	            }
	        }

	        if (listModel.isEmpty()) {
	            completeButton.setEnabled(false);
	        }
	    }
	}

	private void retrieveUserData() {
	    try {
	        String levelQuery = "SELECT * FROM user_info WHERE user_id = ?";
	        PreparedStatement levelStatement = connection.prepareStatement(levelQuery);
	        levelStatement.setInt(1, 1); // 예시로 사용하는 user_id 값 (1)
	        ResultSet levelResultSet = levelStatement.executeQuery();
	        if (levelResultSet.next()) {
	            level = levelResultSet.getInt("level");
	            count = level;
	            levelLabel.setText("Level: " + level);
	        } else {
	            // 사용자 정보가 DB에 없는 경우, 새로운 레코드를 삽입합니다.
	            String insertQuery = "INSERT INTO user_info (user_id, level) VALUES (?, ?)";
	            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
	            insertStatement.setInt(1, 1); // 예시로 사용하는 user_id 값 (1)
	            insertStatement.setInt(2, level);
	            insertStatement.executeUpdate();
	            insertStatement.close();
	            System.out.println("New user record inserted into the database");
	        }
	        levelResultSet.close();
	        levelStatement.close();

	        String todoQuery = "SELECT * FROM todo_items WHERE user_id = ?";
	        PreparedStatement todoStatement = connection.prepareStatement(todoQuery);
	        todoStatement.setInt(1, 1); // 예시로 사용하는 user_id 값 (1)
	        ResultSet todoResultSet = todoStatement.executeQuery();
	        while (todoResultSet.next()) {
	            String item = todoResultSet.getString("item");
	            listModel.addElement(item);
	        }
	        todoResultSet.close();
	        todoStatement.close();
	    } catch (SQLException e) {
	        System.out.println("Failed to retrieve user data from the database");
	        e.printStackTrace();
	    }
	}
}