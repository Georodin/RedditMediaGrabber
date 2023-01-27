package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controller.Controller;

public class Windows {
	static Font font = new Font("Verdana", Font.PLAIN, 14);
	static JFrame frame;
	static JPanel panel;

	public static void displayWindowMsg(String msg) {
		panel.removeAll();
		panel.setLayout(new BorderLayout());

		JLabel msgLabel = new JLabel(msg, SwingConstants.CENTER);
		msgLabel.setFont(font);
		panel.add(msgLabel, BorderLayout.CENTER);
	}

	public void createWindow(String type, Controller controller) {
		frame = new JFrame(type);
		List<Image> icons = new ArrayList<Image>();

		try {
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_16.png"))).getImage());
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_32.png"))).getImage());
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_48.png"))).getImage());
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_64.png"))).getImage());

		} catch (Exception e) {
//			StringWriter sw = new StringWriter();
//			PrintWriter pw = new PrintWriter(sw);
//			e.printStackTrace(pw);
//			LogUtility.newLineToLog("Critical!! :"+sw.toString());
			e.printStackTrace();
		}
		frame.setIconImages(icons);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setPreferredSize(new Dimension(320, 220));
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(controller.getMv().frame);

		frame.addWindowListener(new WindowAdapter() {// Window close event
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			};

			public void windowIconified(WindowEvent e) {// Window minimized event
				frame.dispose();
			};
		});

		panel = new JPanel();

		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;

		JLabel info = new JLabel("\"pics\" or \"username\" example format");
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(info, gbc);

		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		JTextField textField = new JTextField(16);
		panel.add(textField, gbc);

		JLabel userCheck = new JLabel("user");

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(userCheck, gbc);

		gbc.gridx = 1;
		JCheckBox checkBox_user = new JCheckBox();
		panel.add(checkBox_user, gbc);

		JLabel newCheck = new JLabel("grab new");
		gbc.gridx = 0;
		gbc.gridy = 3;
		panel.add(newCheck, gbc);

		gbc.gridx = 1;
		JCheckBox checkBox_new = new JCheckBox();
		panel.add(checkBox_new, gbc);

		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(20, 0, 0, 40);
		gbc.gridx = 0;
		gbc.gridy = 4;
		JButton addBtn = new JButton("add");
		panel.add(addBtn, gbc);

		gbc.insets = new Insets(20, 40, 0, 0);
		gbc.gridx = 1;
		JButton cancelBtn = new JButton("cancel");
		panel.add(cancelBtn, gbc);

		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addBtn.setVisible(false);
				cancelBtn.setVisible(false);
				controller.tryAddSubreddit(textField.getText(), checkBox_new.isSelected(), checkBox_user.isSelected());
			}
		});

		frame.add(panel);
	}

}
