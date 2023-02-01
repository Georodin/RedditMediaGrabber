package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import controller.Controller;
import model.LogUtility;
import model.RedditPull;
import model.SQLBridge;

public class ResetWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Controller controller;

	public ResetWindow(String name, Controller controller) {
		super(name);

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
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		setIconImages(icons);
		this.setController(controller);
		FlatDraculaIJTheme.setup();

		setPreferredSize(new Dimension(300, 180));
		setResizable(false);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
		setLayout(new BorderLayout());

		JLabel infoText = new JLabel(
				"<html>Stats to reset<br>-first pull date<br>-last pull date<br>-pull count</html>");

		infoText.setFont(new Font("Verdana", Font.PLAIN, 14));

		JButton yes = new JButton("yes");
		JButton cancel = new JButton("cancel");

		infoText.setHorizontalAlignment(SwingConstants.CENTER);
		infoText.setVerticalAlignment(SwingConstants.CENTER);
		infoText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		add(infoText, BorderLayout.CENTER);

		JPanel decision = new JPanel();
		decision.setLayout(new BorderLayout());

		decision.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		decision.add(cancel, BorderLayout.EAST);
		decision.add(yes, BorderLayout.WEST);
		add(decision, BorderLayout.PAGE_END);

		yes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RedditPull rp = controller.getRp();

				rp.firstPull = null;
				rp.lastPull = null;
				rp.pulls = 0;

				LogUtility.newLineToLog("Info: Resetted stats...");

				SQLBridge.updateMetaInfoTable();

				controller.getMv().frameComponents.updateStats();

				controller.getMv().refreshView();

				controller.getMv().setResetWindow(null);
				controller.saveProfile();
				dispose();
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.getMv().setResetWindow(null);
				dispose();
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.getMv().setResetWindow(null);
				dispose();
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}
