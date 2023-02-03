package view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.formdev.flatlaf.intellijthemes.*;

import controller.Controller;
import model.LogUtility;
import model.XamppUtility;

public class MainView {

	public JFrame frame;
	public FrameComponents frameComponents;
	MainView mainView;
	ActionListener actionListener;
	Windows windows;
	Controller controller;

	JMenuItem openViewer, xamppPath, reset, resetSQL, openDownload, openLog;
	private JMenuBar menu_bar;
	private JMenu menu;

	private ResetWindow resetWindow;
	private ResetSQLWindow resetSQLWindow;

	public MainView(Controller controller) {
		this.controller = controller;
		FlatDraculaIJTheme.setup();
		frame = new JFrame("RedditGrabber");

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

		frame.setIconImages(icons);

		frame.addWindowListener(new WindowAdapter() {// Window close event
			public void windowClosing(WindowEvent e) {
				controller.saveProfile();
				System.exit(0);
			};

			public void windowIconified(WindowEvent e) {// Window minimized event
				frame.setVisible(false);
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(556, 500));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		
		TrayIcon trayIcon;
		try {
			trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("/icons/icon_16.png")), "Reddit Grabber");
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.add(trayIcon);
			trayIcon.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			        frame.setVisible(true);
			        frame.setExtendedState(JFrame.NORMAL);
			    }
			});
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}

		menu_bar = new JMenuBar();
		menu = new JMenu("Options");

		openViewer = new JMenuItem("Open Viewer");
		
		openViewer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI("http://127.0.0.1/redditgrabber"));
				} catch (Exception ex) {
					StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); ex.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
				}
			}
		});
		
		xamppPath = new JMenuItem("Set XAMPP path");

		xamppPath.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new XamppUtility().dialogXamppPath(controller);

			}
		});
		
		
		resetSQL = new JMenuItem("Reset SQL-Tables");

		resetSQL.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getResetSQLWindow() == null) {
					setResetSQLWindow(new ResetSQLWindow("Do you really want to reset?", controller));
				}
			}
		});

		reset = new JMenuItem("Reset stats");

		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getResetWindow() == null) {
					setResetWindow(new ResetWindow("Do you really want to reset?", controller));
				}
			}
		});
		
		
		
		openDownload = new JMenuItem("Open download path");
		openDownload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(controller.getRp().getPath()));
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); ex.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
				}
			}
		});

		openLog = new JMenuItem("Open logfile");

		openLog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LogUtility.openLogFile();
			}
		});

		menu.add(openViewer);
		menu.add(xamppPath);
		menu.add(reset);
		menu.add(resetSQL);
		menu.add(openDownload);
		menu.add(openLog);

		menu_bar.add(menu);

		frame.setJMenuBar(menu_bar);

		frameComponents = new FrameComponents(controller);
		frame.add(frameComponents.getView(), BorderLayout.NORTH);
	}

	public void delegateWindow(String type) {
		new Windows().createWindow(type, controller);
	}

	public void refreshView() {
		frame.revalidate();
		frame.repaint();
	}

	ResetWindow getResetWindow() {
		return resetWindow;
	}

	void setResetWindow(ResetWindow resetWindow) {
		this.resetWindow = resetWindow;
	}

	public ResetSQLWindow getResetSQLWindow() {
		return resetSQLWindow;
	}

	public void setResetSQLWindow(ResetSQLWindow resetSQLWindow) {
		this.resetSQLWindow = resetSQLWindow;
	}
}