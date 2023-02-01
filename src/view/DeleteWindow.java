package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import controller.Controller;
import model.LogUtility;
import model.RedditPull;
import model.SQLBridge;
import model.SubReddit;

public class DeleteWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Controller controller;

	public DeleteWindow(RedditPull rp, SubReddit sr, Controller controller, int index) {
		super("Delete " + sr.getSubReddit() + "?");

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

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(320, 220));
		setResizable(false);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
		setLayout(new BorderLayout());

		JLabel infoText = new JLabel(
				"<html>Do you really want to delete the " + sr.getSubReddit() + " subreddit?</html>");

		JCheckBox deleteDB = new JCheckBox("Also delete files and database?");

		JLabel warningInfo = new JLabel("IRREVERSIBEL!");

		infoText.setFont(new Font("Verdana", Font.PLAIN, 14));

		JButton yes = new JButton("yes");
		JButton cancel = new JButton("cancel");

		infoText.setHorizontalAlignment(SwingConstants.CENTER);
		infoText.setVerticalAlignment(SwingConstants.CENTER);
		deleteDB.setHorizontalAlignment(SwingConstants.CENTER);
		deleteDB.setVerticalAlignment(SwingConstants.CENTER);
		deleteDB.setFont(new Font("Verdana", Font.PLAIN, 14));
		deleteDB.setSelected(true);
		warningInfo.setFont(new Font("Verdana", Font.PLAIN, 14));
		warningInfo.setForeground(Color.red);
		warningInfo.setHorizontalAlignment(SwingConstants.CENTER);
		warningInfo.setVerticalAlignment(SwingConstants.CENTER);

		infoText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel center = new JPanel(new BorderLayout());
		center.add(infoText, BorderLayout.PAGE_START);
		center.add(deleteDB, BorderLayout.CENTER);
		center.add(warningInfo, BorderLayout.PAGE_END);

		add(center, BorderLayout.CENTER);

		JPanel decision = new JPanel();
		decision.setLayout(new BorderLayout());

		decision.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		decision.add(cancel, BorderLayout.EAST);
		decision.add(yes, BorderLayout.WEST);
		add(decision, BorderLayout.PAGE_END);

		yes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (deleteDB.isSelected()) {
					if (SQLBridge.isDatabaseOnline()) {
//						//System.out.println("DEL: "+rp.getPath()+"\\"+sr.getSubReddit());
						deleteDir(new File(rp.getPath() + "\\" + sr.getSubReddit()));

						LogUtility.newLineToLog("Info: Deleted " + sr.getSubReddit() + " folder from " + rp.getPath()
								+ "\\" + sr.getSubReddit() + "");

						SQLBridge.deleteTable(sr.getSubReddit());

						rp.reddits.remove(index);
						controller.selectedIndex = -1;
						controller.getMv().frameComponents.subViewList();
						FrameComponents.markSelected(index);
						controller.saveProfile();
						if (rp.reddits.size() == 0) {
							controller.getMv().frameComponents.startStop.setText("Add a Subreddit to start!");
							controller.stopRoutine();
						}

						LogUtility.newLineToLog("Info: Deleted " + sr.getSubReddit() + " from download list");
					} else {
						LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Cannot delete table for "
								+ sr.getSubReddit() + "...");
					}

				} else {
					rp.reddits.remove(index);
					controller.selectedIndex = -1;
					controller.getMv().frameComponents.subViewList();
					FrameComponents.markSelected(index);
					controller.saveProfile();
					if (rp.reddits.size() == 0) {
						controller.getMv().frameComponents.startStop.setText("Add a Subreddit to start!");
						controller.stopRoutine();
					}

					LogUtility.newLineToLog("Info: Deleted " + sr.getSubReddit() + " from download list");
				}
				dispose();
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

	}

	void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}
