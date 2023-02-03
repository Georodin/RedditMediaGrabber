package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.Controller;
import model.LogUtility;

public class InfoWindow extends JFrame{
	
	static Font font = new Font("Verdana", Font.PLAIN, 14);
	
	public InfoWindow(String title, String message, Controller controller) {
		super(title);
		List<Image> icons = new ArrayList<Image>();

		try {
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_16.png"))).getImage());
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_32.png"))).getImage());
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_48.png"))).getImage());
			icons.add(new ImageIcon(ImageIO.read(getClass().getResource("/icons/icon_64.png"))).getImage());
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		setIconImages(icons);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(320, 220));
		setResizable(false);
		pack();
		setVisible(true);
		setLocationRelativeTo(controller.getMv().frame);

		addWindowListener(new WindowAdapter() {// Window close event
			public void windowClosing(WindowEvent e) {
				dispose();
			};

			public void windowIconified(WindowEvent e) {// Window minimized event
				dispose();
			};
		});
		
		setLayout(new BorderLayout());

		JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
		msgLabel.setFont(font);
		add(msgLabel, BorderLayout.CENTER);
	}
}
