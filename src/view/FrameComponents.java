package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import model.Stats;
import model.SubReddit;

public class FrameComponents {

	// singleton
	public static FrameComponents frameComponents;

	// list header
	JLabel headerList_sub;
	JLabel headerList_new;

	// list action
	JButton btnList_plus;
	JButton btnList_minus;

	// list
	JPanel listWrapper;
	JScrollPane scrollPane;
	public static ArrayList<JPanel> listEntry;

	// path
	JPanel pathWrapper;
	JButton pathButton;
	JLabel path;
	JFileChooser fc;

	// time
	JSlider timeSlider;
	JLabel timeDisplay;
	JPanel timeWrapper;

	// stats
	JPanel statWrapper;
	JLabel statsLabel;

	// start stop
	public JButton startStop;

	// progressbar
	JProgressBar progressBar;

	// controller and parent
	Controller controller;
	Stats stats;

	public FrameComponents(Controller controller) {
		this.controller = controller;
		headerList_sub = new JLabel("subreddit", SwingConstants.RIGHT);
		headerList_new = new JLabel("grab new", SwingConstants.RIGHT);
		btnList_plus = new JButton("+");
		btnList_minus = new JButton("-");
		listWrapper = new JPanel();
		scrollPane = new JScrollPane(listWrapper, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listEntry = new ArrayList<>();
		pathWrapper = new JPanel();
		pathButton = new JButton("change Path");
		path = new JLabel(controller.getRp().getPath(), SwingConstants.LEFT);
		timeSlider = new JSlider(0, 6);
		timeDisplay = new JLabel("time interval: " + controller.getRp().minutesInterval + "min", SwingConstants.RIGHT);
		timeWrapper = new JPanel();
		statWrapper = new JPanel();
		statsLabel = new JLabel();

		listWrapper.setToolTipText("List of subreddits to download");

		if (controller.getRp().started) {
			startStop = new JButton("Stop");
		} else {
			startStop = new JButton("Start");
		}

		progressBar = new JProgressBar(0, 100);
		progressBar.setToolTipText("Progress till next dowload");

		timeSlider.setValue(controller.getTimeIndex());
		timeSlider.setToolTipText("Move the slider to change the download interval");
		timeDisplay.setToolTipText("Time interval to restart the download process");
		path.setToolTipText(controller.getRp().getPath());

		stats = new Stats(controller);

		statsLabel.setText(stats.getStats());
		statsLabel.setToolTipText("Miscellaneous stats");
		statsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		setBackground();
		setFont();
		setPadding();
		setDimension();

		btnList_minus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.removeSelected();
			}
		});
		btnList_plus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Windows().createWindow("add subreddit", controller);
			}
		});

		startStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.toggleStartStop();
			}
		});
	}

	private void setDimension() {
		int width = 500;
		int widthSmall = 460;
		int heightSingle = 30;
		int heightDouble = 60;
		int heightFith = 120;

		scrollPane.setPreferredSize(new Dimension(widthSmall, heightFith));
		timeWrapper.setPreferredSize(new Dimension(width, heightDouble));
		pathWrapper.setPreferredSize(new Dimension(width, heightSingle));
		timeSlider.setPreferredSize(new Dimension(width - 40, 20));
		statWrapper.setPreferredSize(new Dimension(width, heightFith));
		startStop.setPreferredSize(new Dimension(width, heightSingle));
		progressBar.setPreferredSize(new Dimension(width, 20));

		path.setPreferredSize(new Dimension(widthSmall - 60, heightSingle));
		path.setMaximumSize(new Dimension(widthSmall - 60, heightSingle));

	}

	public JPanel getView() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		subViewList();
		subViewTime();
		subViewStats();
		subViewPath();

		gbc.insets = new Insets(-5, 0, -5, 0);
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;

		panel.add(headerList_sub, gbc);

		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridwidth = 1;
		gbc.gridx = 4;
		panel.add(headerList_new, gbc);

		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridwidth = 5;
		gbc.gridheight = 4;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(scrollPane, gbc);

		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.insets = new Insets(1, 0, 1, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 5;
		panel.add(btnList_plus, gbc);

		gbc.gridy = 2;
		panel.add(btnList_minus, gbc);

		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridwidth = 6;
		gbc.gridx = 0;
		gbc.gridy = 5;
		panel.add(pathWrapper, gbc);

		gbc.gridy = 6;
		panel.add(timeWrapper, gbc);

		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridy = 7;
		panel.add(statWrapper, gbc);

		gbc.gridy = 8;
		panel.add(startStop, gbc);

		gbc.gridy = 9;
		panel.add(progressBar, gbc);

		return panel;
	}

	public void subViewList() {
		Font font = new Font("Verdana", Font.PLAIN, 14);
		listWrapper.removeAll();
		listWrapper.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;

		GridBagConstraints gbcw = new GridBagConstraints();
		gbcw.gridx = 0;
		gbcw.gridy = 0;
		gbcw.gridwidth = 1;
		gbcw.weightx = 0;
		gbcw.weighty = 0;

		listEntry.clear();

//		if(controller.getRp().reddits!=null) {
		Iterator<SubReddit> iterator = controller.getRp().reddits.iterator();
		int index = 0;
		while (iterator.hasNext()) {

			JPanel entry = new JPanel(new GridBagLayout());

			entry.setBackground(new Color(51, 54, 63));

			SubReddit subreddit = iterator.next();

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.insets = new Insets(0, 4, 0, 0);
			JLabel label = new JLabel(subreddit.getSubReddit(), SwingConstants.LEFT);
			label.setFont(font);
			label.setMinimumSize(new Dimension(400, 20));
			label.setPreferredSize(new Dimension(400, 20));
			entry.add(label, gbc);

			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.gridx = 1;
			JCheckBox check = new JCheckBox();

			check.setSelected(subreddit.isGrabNew());

			check.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					subreddit.setGrabNew(check.isSelected());
					controller.saveProfile();
				}
			});

			entry.add(check, gbc);
			gbc.gridy++;

			gbc.gridx = 0;
			gbc.gridwidth = 2;
			JPanel seperator = new JPanel();
			seperator.setBackground(new Color(208, 208, 202));
			seperator.setMinimumSize(new Dimension(440, 1));
			seperator.setPreferredSize(new Dimension(440, 1));
			entry.add(seperator, gbc);
			gbc.gridy++;

			if (!iterator.hasNext()) {
				seperator.setBackground(new Color(51, 54, 63));
			}

			listWrapper.add(entry, gbcw);
			gbcw.gridy++;

			entry.addMouseListener(new ClickListiner(index));

			index++;
			listEntry.add(entry);
		}
//		}

		gbcw.fill = GridBagConstraints.BOTH;
		gbcw.anchor = GridBagConstraints.LINE_END;
		gbcw.weighty = 100.0;
		gbcw.gridheight = GridBagConstraints.REMAINDER;

		listWrapper.add(Box.createGlue(), gbcw);

		if (controller.getMv() != null) {
			controller.getMv().refreshView();
		}

	}

	private void subViewPath() {
		pathWrapper.setLayout(new BorderLayout());

		pathWrapper.add(path, BorderLayout.WEST);
		path.setToolTipText(controller.getRp().getPath());
		pathWrapper.add(pathButton, BorderLayout.EAST);

		pathButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				fc = new JFileChooser();
				fc.setCurrentDirectory(new File(controller.getRp().getPath()));
				fc.setDialogTitle("Select path");
				
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//
				// disable the "All files" option.
				//
				fc.setAcceptAllFileFilterUsed(false);
				//
				if (fc.showOpenDialog(pathButton) == JFileChooser.APPROVE_OPTION) {
					controller.changePath(fc.getSelectedFile().toString());
					path.setText(controller.getRp().getPath());
					controller.saveProfile();
					path.setToolTipText(controller.getRp().getPath());
				} else {
//			      //System.out.println("No Selection ");
				}
			}
		});

	}

	private void setPadding() {
		Insets insets = new Insets(5, 5, 5, 5);
		headerList_sub.setBorder(new EmptyBorder(insets));
		headerList_new.setBorder(new EmptyBorder(insets));
		path.setBorder(new EmptyBorder(insets));
		timeDisplay.setBorder(new EmptyBorder(insets));
		statsLabel.setBorder(new EmptyBorder(insets));
	}

	private void subViewStats() {
		statWrapper.setLayout(new BorderLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		statWrapper.add(statsLabel, BorderLayout.LINE_START);
	}

	private void subViewTime() {
		timeWrapper.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		timeWrapper.add(timeSlider, gbc);
		gbc.gridy = 1;
		timeWrapper.add(timeDisplay, gbc);

		timeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				controller.changeInterval(timeSlider.getValue());
				timeDisplay.setText("time interval: " + controller.getRp().minutesInterval + "min");
			}
		});
	}

	private void setBackground() {
		Color bgColor = new Color(51, 54, 63);
		listWrapper.setBackground(bgColor);
		timeWrapper.setBackground(bgColor);
		statWrapper.setBackground(bgColor);
		pathWrapper.setBackground(bgColor);
	}

	private void setFont() {
		Font font = new Font("Verdana", Font.PLAIN, 14);
		Font fontsmall = new Font("Verdana", Font.PLAIN, 10);
		Font fontsmallbold = new Font("Verdana", Font.BOLD, 14);
		headerList_sub.setFont(fontsmall);
		headerList_new.setFont(fontsmall);
		btnList_plus.setFont(fontsmallbold);
		btnList_minus.setFont(fontsmallbold);
		listWrapper.setFont(font);
		path.setFont(font);
		timeDisplay.setFont(font);
		statsLabel.setFont(font);
		startStop.setFont(font);
	}

	class ClickListiner implements MouseListener {

		int index;

		public ClickListiner(int index) {
			super();
			this.index = index;
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			controller.selectedIndex = index;
			markSelected(index);

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

	public static void markSelected(int index) {
		int aIndex = 0;
		for (JPanel panel : FrameComponents.listEntry) {
			if (aIndex == index) {
				panel.setBackground(new Color(71, 74, 83));
			} else {
				panel.setBackground(new Color(51, 54, 63));
			}
			aIndex++;
		}

	}

	public void updateStats() {
		statsLabel.setText(stats.getStats());
		controller.getMv().refreshView();
	}

	public void updateProgressBar(int value) {
		progressBar.setValue(value);
		controller.getMv().refreshView();
	}

}
