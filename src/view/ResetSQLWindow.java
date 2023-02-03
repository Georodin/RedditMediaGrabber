package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import controller.Controller;
import model.LogUtility;
import model.RedditPull;
import model.SQLBridge;

public class ResetSQLWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Controller controller;
	
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public class SelectAllTableModel extends DefaultTableModel {
    	
    	boolean flag = false;

    	public SelectAllTableModel() {
            // Add a first row with a "Select All" header and a Boolean value

            // Add a table model listener to listen for changes to the table data
            addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                	if(e.getType() == TableModelEvent.UPDATE&&e.getFirstRow()!=-1) {
                    	if(e.getFirstRow()==0) {
                			if((boolean) getValueAt(0, 1)) {
                				flag = false;
                     		   for (int i = 1; i < model.getRowCount(); i++) {
                     			   model.setValueAt(true, i, 1);
                     		   }
                			}else {
                				if(!flag) {
                          		   for (int i = 1; i < model.getRowCount(); i++) {
                         			   model.setValueAt(false, i, 1);
                         		   }
                				}

                			}
                    	}else {
                    		if((boolean) getValueAt(0, 1)&&!(boolean) getValueAt(e.getFirstRow(), 1)) {
                    			flag = true;
                    			model.setValueAt(false, 0, 1);
                    		}
                    	}
                	}

                }
            });
            
            
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                case 1:
                    return Boolean.class;
                default:
                    return super.getColumnClass(column);
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1;
        }
    }

	public ResetSQLWindow(String windowTitle, Controller controller) {
		super(windowTitle);

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

		setPreferredSize(new Dimension(500, 400));
		setResizable(false);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
		setLayout(new BorderLayout());

		JLabel infoText;
		
		if(SQLBridge.getTableList().size()==0) {
			infoText = new JLabel("<html>There are no SQL tables at the moment...</html>");
		}else {
			infoText = new JLabel("<html>Remove obsolete SQL tables from database.<br>This is irreversible.<br>Leftover media files may need manual deletion after reset.</html>");
		}

		infoText.setFont(new Font("Verdana", Font.PLAIN, 14));
		
		
        table = new JTable();
        model = new SelectAllTableModel();
        model.addColumn("Name");
        model.addColumn("Delete");
        model.addRow(new Object[] {"Select All", false});
        table.setModel(model);

        // Add the list entries to the table
        for (String name : SQLBridge.getTableList()) {
            model.addRow(new Object[]{name, false});
        }
        
        scrollPane = new JScrollPane(table);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        JButton resetButton = new JButton("Reset");
        
        resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String msg = "You deleted "+getArrayList().size()+" SQL tables.";
				String title = "Success!";
				
				if(!SQLBridge.removeTables(getArrayList())) {
					msg = "Failed to delete "+getArrayList().size()+" SQL tables.";
					title = "Failed!";
				};
				
				new InfoWindow(title, msg, controller);
				
				closeWindow();
			}
		});
        

        JButton cancelButton = new JButton("Cancel");
        
        cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
        
        
        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        
        panel.add(resetButton);
        panel.add(cancelButton);
        
        infoText.setBorder(border);
        scrollPane.setBorder(border);
        panel.setBorder(border);
        
        add(infoText, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.PAGE_END);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
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
	
	public ArrayList<String> getArrayList(){
		ArrayList<String> output = new ArrayList<>();
		
		
	   for (int i = 1; i < model.getRowCount(); i++) {
		   if((boolean) model.getValueAt(i, 1)) {
			   output.add((String) model.getValueAt(i, 0));
		   }
		   
	   }
		return output;
	}
	
	public void closeWindow() {
		controller.getMv().setResetSQLWindow(null);
		dispose();
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}
