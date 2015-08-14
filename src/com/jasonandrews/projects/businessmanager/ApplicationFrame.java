/*
 * TO DO:
 *	1) Change the whole "CUSTOMERS", "EMPLOYEES", "USERS" switch statements to take an ENUM so like Enum EntityTypes = {"CUSTOMERS", "EMPLOYEES", "USERS"}.
 * 	2) A proper implementation of the loading screen bugging out when hovering over certain GUI elements (mouse hovering over them shows them).
 */

package com.jasonandrews.projects.businessmanager;

import java.awt.MouseInfo;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

import java.awt.Font;

import javax.swing.JTextPane;

import java.awt.CardLayout;

import javax.swing.JCheckBox;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Jason Andrews
 * @version 0.30
 * @dependencies AppManager.java, PopupDialog.java
 * 
 * This is the core class behind the application's GUI. It internally handles almost all of the GUI elements (exceptions being the popup dialogs that display information or errors.)
 * Variables / Object Reference Variables have been prefixed so that it is more clear as to what panel they belong too.
 * For example, a JButton on the Login Form will be prefixed with 'login_' so it may appear as follows: login_loginBtn. 
 * Prefixes:
 * - Main Menu = mm_
 * - Configuration - config_
 * - Login - login_
 * - Home - home_
 * - Customer - cust_
 * - Employee - emp_
 * - User - user_
 */
public class ApplicationFrame extends JFrame {

	private static final String CHECK_MARK_CHARACTER = "\u2713";
	private static final String X_MARK_CHARACTER = "\u2716";
	private static final Color COLOR_RED = new Color(255, 51, 51);
	private static final Color COLOR_GREEN = new Color(0, 118, 0);
	//private static final int guiLocationSubtractor = 35; 
	
	private ApplicationFrame appFrame; //Self reference object variable - used in particular instances.
	private AppManager appManager;

	private String fileDirectory;
	private Properties properties;
	
	private PopupDialog popupFrame;	
	private JPanel currentPanel;
		
	private JPanel mm_contentPanel;
	private JPanel config_contentPanel;
	private JPanel login_contentPanel;
	private JPanel home_contentPanel;
	
	private JPanel cust_contentPanel;
	
	private JTable cust_customersTable;
	private JTextField config_urlTextField;
	private JTextField config_userTextField;
	private JPasswordField config_passwordTextField;
	private JCheckBox config_saveDetailsChckbx;
	private JCheckBox login_saveDetailsChckbx;
	
	//private JLabel mainMenuConnectionLb;
	//private JLabel mainMenuConnectionLbStatus;
	private JLabel config_statusResultLbl;
	//private JLabel mm_errorLbl;
	private JLabel login_errorLbl;
	
	private String[] cust_tableColumnNames;
	
	private static final Color BUTTON_BACKGROUND_COLOR = Color.BLACK;
	private static final Color BUTTON_FOREGROUND_COLOR = Color.WHITE;
	private JTextField login_usernameTextField;
	private JPasswordField login_passwordField;
	private JTextField cust_searchTextField;
	
	private JCheckBox cust_customerNoChckbx;
	private JCheckBox cust_customerNameChckbx;
	
	private JMenuBar menu_menuBar;	
	
	private ArrayList<Entity> customerList; //A list of customer objects represented in the current customer table.
	
	private JButton cust_refreshTableBtn;
	
	//private JButton cust_viewCustomerBtn;
	//private JButton cust_editCustomerBtn;
	//private JButton cust_deleteCustomerBtn;
	
	JLabel cust_viewTableLbl;
	JLabel cust_editTableLbl;
	JLabel cust_deleteTableLbl;
	
	/**
	 * @wbp.nonvisual location=82,359
	 */
	private final JPopupMenu popup_optionsPopup = new JPopupMenu(); //The popup that appears when a user selects a row within a table. 

	/**
	 * 
	 * @param appManager - The AppManager object.
	 */
	public ApplicationFrame(AppManager appManager) {	
		this.appFrame = this; 
		this.appManager = appManager;
				
		setTitle("Business Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		WindowAdapter exitListener = new WindowAdapter() {

	            @Override
	            public void windowClosing(WindowEvent e) {
	            	//The application has been shut down.
	            	//Store the properties (such as the database information.)
	            	
	            	storeProperties();
	            }
		};
		addWindowListener(exitListener);
		setBounds(100, 100, 750, 500);
		setResizable(false);		
		getContentPane().setLayout(new CardLayout(0, 0));
		
		popupFrame = new PopupDialog(this, appManager);
		
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				//System.out.println("FRAME - click.");
				int mouseX = MouseInfo.getPointerInfo().getLocation().x; 
				int mouseY = MouseInfo.getPointerInfo().getLocation().y;
				//System.out.println("X: " + mouseX + " | Y: " + mouseY);
				//customerOptionsPopup.setVisible(false);
				
				//Hide the 'View', 'Edit' and 'Delete' buttons when the user deselects a row.
				if((cust_customersTable != null && cust_customersTable.getSelectedRow() < 0)/* || ((employeesTable != null && employeesTable.getSelectedRow() > -1)) || ((usersTable != null && usersTable.getSelectedRow() > -1))*/) {
					cust_customersTable.clearSelection();
					//cust_viewCustomerBtn.setVisible(false);
					//cust_editCustomerBtn.setVisible(false);
					//cust_deleteCustomerBtn.setVisible(false);
				}				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) { }

			@Override
			public void mouseExited(MouseEvent arg0) { }

			@Override
			public void mousePressed(MouseEvent arg0) { }

			@Override
			public void mouseReleased(MouseEvent arg0) { }
			
		});
		
		createContentPanels(); //Create the different panels.
		//createInternalFrames();	
				
		//super.setContentPane(customersPanel);
		setContentPane(mm_contentPanel);
	}
	
	/**
	 * Create the content panels that will be used within the application. 
	 * These are essentially the different screens of the application, such as the main menu and login screens.
	 */
	void createContentPanels() {			
		
		//Loading screen panel.
		ImageIcon loadingScreenImageIcon = new ImageIcon("lib/images/please_wait_loading_default_02.gif");
		loadingScreenImage = new JLabel();
		loadingScreenImage.setBounds(0, 0, getWidth(), (getHeight() - 100));
		loadingScreenImage.setHorizontalAlignment(SwingConstants.CENTER);
		loadingScreenImage.setIcon(loadingScreenImageIcon);
		
		//Main Menu Panel.
		mm_contentPanel = new JPanel();
		mm_contentPanel.setBackground(Color.WHITE);
		mm_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mm_contentPanel.setLayout(null);		
		mm_contentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("CLICKED MAIN MENU PANEL");
			}
		});
		
		mm_contentPanel.add(loadingScreenImage);
		loadingScreenImage.setVisible(false);
		
		/*
		//A label to show whether or not the application is connected to the database (not useful any more.)
		mainMenuConnectionLb = new JLabel("Connected: ");
		mainMenuConnectionLb.setHorizontalAlignment(SwingConstants.RIGHT);
		mainMenuConnectionLb.setBounds(500, 15, 70, 10);
		mainMenuPanel.add(mainMenuConnectionLb);		
		
		mainMenuConnectionLbStatus = new JLabel(X_MARK_CHARACTER);
		mainMenuConnectionLbStatus.setForeground(COLOR_RED);
		mainMenuConnectionLbStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		mainMenuConnectionLbStatus.setBounds(515, 15, 70, 10);
		mainMenuPanel.add(mainMenuConnectionLbStatus);
		*/
		
		JButton mm_configurationBtn = new JButton("Configuration");		
		mm_configurationBtn.setToolTipText("Configure the database settings.");
		mm_configurationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				display("CONFIGURATION");
				//When the Configure button is clicked/pressed.
			}
		});
		mm_configurationBtn.setBackground(BUTTON_BACKGROUND_COLOR);
		mm_configurationBtn.setForeground(BUTTON_FOREGROUND_COLOR);
		mm_configurationBtn.setBounds(268, 169, 122, 33);
		mm_contentPanel.add(mm_configurationBtn);
		
		JButton mm_loginBtn = new JButton("Login");
		mm_loginBtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {	
				
				display("LOGIN");
			}				
		});
		mm_loginBtn.setBackground(BUTTON_BACKGROUND_COLOR);
		mm_loginBtn.setForeground(BUTTON_FOREGROUND_COLOR);
		mm_loginBtn.setBounds(268, 128, 122, 33);		
		mm_contentPanel.add(mm_loginBtn);
		
		JButton mm_exitBtn = new JButton("Exit");
		mm_exitBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispatchEvent(new WindowEvent(appFrame, WindowEvent.WINDOW_CLOSING)); //Exit the application.				
			}
			
		});
		mm_exitBtn.setBackground(BUTTON_BACKGROUND_COLOR);
		mm_exitBtn.setForeground(BUTTON_FOREGROUND_COLOR);
		mm_exitBtn.setBounds(268, 213, 122, 33);
		mm_contentPanel.add(mm_exitBtn);
		
		/*
		JButton mm_testConnectionBtn = new JButton("Test Connection");
		mm_testConnectionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String url = urlTextField.getText();
				String user = userTextField.getText();
				String password = passwordTextField.getText();
				
				if(!appManager.testConnectionToDatabase(url, user, password)) { //If the connection failed, then give them the error on the main menu.
					errorLbl.setText("Could not connect, please enter the correct credentials on the Configuration form.");		
					statusResultLbl.setText("Could not connect.");
					statusResultLbl.setForeground(COLOR_RED);			
					
					mainMenuConnectionLbStatus.setText(X_MARK_CHARACTER);
					mainMenuConnectionLbStatus.setForeground(COLOR_RED);
					
				} else { 
					errorLbl.setText("");
					mainMenuConnectionLbStatus.setText(CHECK_MARK_CHARACTER);
					mainMenuConnectionLbStatus.setForeground(COLOR_GREEN);
					statusResultLbl.setForeground(COLOR_GREEN);
					statusResultLbl.setText("Connected.");
				}
			}
		});
		mm_testConnectionBtn.setBackground(BUTTON_BACKGROUND_COLOR);
		mm_testConnectionBtn.setForeground(BUTTON_FOREGROUND_COLOR);		
		mm_testConnectionBtn.setBounds(268, 257, 122, 33);
		mainMenuPanel.add(mm_testConnectionBtn);
		
		
		mm_errorLbl = new JLabel("");
		mm_errorLbl.setForeground(COLOR_RED);
		mm_errorLbl.setBounds(100, 301, 540, 14);
		mainMenuPanel.add(mm_errorLbl);
		*/		
		//Configure Panel
		
		
		//Creating the elements of the Configuration content pane.
		config_contentPanel = new JPanel();
		config_contentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("CLICKED CONFIGURE PANEL");
			}
		});
		config_contentPanel.setBackground(Color.WHITE);		
		config_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		config_contentPanel.setLayout(null);
		config_contentPanel.setBounds(0,0,0,0);
		
		JLabel config_statusTextLbl = new JLabel("Status:");
		config_statusTextLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		config_statusTextLbl.setBounds(253, 111, 61, 14);
		config_contentPanel.add(config_statusTextLbl);
		
		config_statusResultLbl = new JLabel("Not connected.");
		config_statusResultLbl.setForeground(COLOR_RED);
		config_statusResultLbl.setBounds(324, 111, 124, 14);
		config_contentPanel.add(config_statusResultLbl);
		
		config_urlTextField = new JTextField("");
		config_urlTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				properties.setProperty("dbUrl", config_urlTextField.getText());
			}
		});
		config_urlTextField.setBounds(242, 142, 226, 20);
		config_urlTextField.setColumns(10);
		config_contentPanel.add(config_urlTextField);
		
		JLabel config_urlLbl = new JLabel("URL:");
		config_urlLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		config_urlLbl.setBounds(173, 145, 61, 14);
		config_contentPanel.add(config_urlLbl);
		
		config_userTextField = new JTextField("");
		config_userTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				properties.setProperty("dbUser", config_userTextField.getText());
			}
		});
		config_userTextField.setColumns(10);
		config_userTextField.setBounds(242, 173, 226, 20);
		config_contentPanel.add(config_userTextField);
		
		JLabel config_userLbl = new JLabel("User:");
		config_userLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		config_userLbl.setBounds(173, 176, 61, 14);
		config_contentPanel.add(config_userLbl);
		
		JLabel config_pwLbl = new JLabel("Password:");
		config_pwLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		config_pwLbl.setBounds(151, 207, 83, 14);
		config_contentPanel.add(config_pwLbl);
		
		config_passwordTextField = new JPasswordField();
		config_passwordTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				properties.setProperty("dbPassword", new String(config_passwordTextField.getPassword()));
			}
		});
		config_passwordTextField.setBounds(242, 204, 226, 20);
		config_contentPanel.add(config_passwordTextField);
		
		config_saveDetailsChckbx = new JCheckBox("Save details");
		config_saveDetailsChckbx.setBackground(Color.WHITE);
		config_saveDetailsChckbx.setBounds(375, 225, 97, 23);
		config_saveDetailsChckbx.setSelected(true);
		config_contentPanel.add(config_saveDetailsChckbx);
		
		JButton config_testConnectionBtn = new JButton("Test");
		config_testConnectionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				showLoadingScreen();
				
				SwingWorker<Integer, Integer> sw = null;
				
				sw = new SwingWorker<Integer, Integer>() {

					@Override
					protected Integer doInBackground() throws Exception {
						String url = config_urlTextField.getText();
						String user = config_userTextField.getText();
						String password = new String(config_passwordTextField.getPassword());
						
						//Test the connection to the database using the specified credentials.
						if(appManager.testConnectionToDatabase(url, user, password)) {
							//The connection succeeded.
							config_statusResultLbl.setForeground(COLOR_GREEN);
							config_statusResultLbl.setText("Connected.");
							//mainMenuConnectionLbStatus.setText(CHECK_MARK_CHARACTER);
							//mainMenuConnectionLbStatus.setForeground(COLOR_GREEN);
							
							hideLoadingScreen();
						} else {
							//The connection failed.
							config_statusResultLbl.setText("Connection failed.");
							config_statusResultLbl.setForeground(COLOR_RED);	
							//mainMenuConnectionLbStatus.setText(X_MARK_CHARACTER);
							//mainMenuConnectionLbStatus.setForeground(COLOR_RED);
							hideLoadingScreen();
						}
						
						return null;
					}
					
				};
				sw.execute();
				
				//try connect to the database specified.				
				
				//String url = "jdbc:mysql://localhost:3306/business_manager";
				//String user = "root";
				//String pw = "";
				
				
			}
		});
		config_testConnectionBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		config_testConnectionBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		config_testConnectionBtn.setBounds(272, 250, 83, 23);
		config_contentPanel.add(config_testConnectionBtn);
		
		JButton config_resetFormBtn = new JButton("Reset");
		config_resetFormBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				config_urlTextField.setText("");
				config_userTextField.setText("");
				config_passwordTextField.setText("");
			}
		});
		config_resetFormBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		config_resetFormBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		config_resetFormBtn.setBounds(365, 250, 83, 23);
		config_contentPanel.add(config_resetFormBtn);
		
		JButton config_backBtn = new JButton("Back");
		config_backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				display("MAIN MENU");
			}
		});
		config_backBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		config_backBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		config_backBtn.setBounds(312, 297, 89, 23);
		config_contentPanel.add(config_backBtn);
		
		JSeparator config_upperSeparator = new JSeparator();
		config_upperSeparator.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		config_upperSeparator.setBounds(242, 129, 226, 2);
		config_contentPanel.add(config_upperSeparator);
		
		JSeparator config_lowerSeparator = new JSeparator();
		config_lowerSeparator.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		config_lowerSeparator.setBounds(242, 284, 226, 2);
		config_contentPanel.add(config_lowerSeparator);
		
		
		//Setting up the Login content pane.
		login_contentPanel = new JPanel();
		login_contentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("CLICKED LOGIN PANEL");
			}
		});
		login_contentPanel.setBackground(Color.WHITE);
		login_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		login_contentPanel.setLayout(null);
		login_contentPanel.setBounds(0,0,0,0);
		
		
		
		login_errorLbl = new JLabel("");
		login_errorLbl.setForeground(COLOR_RED);
		login_errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
		login_errorLbl.setBounds(125, 136, 420, 14);
		login_contentPanel.add(login_errorLbl);
		
		JLabel login_usernameLbl = new JLabel("Username:");
		login_usernameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		login_usernameLbl.setBounds(169, 160, 81, 22);
		login_contentPanel.add(login_usernameLbl);
		
		login_usernameTextField = new JTextField();
		login_usernameTextField.setBounds(260, 161, 207, 20);
		login_contentPanel.add(login_usernameTextField);
		login_usernameTextField.setColumns(10);
		
		JLabel login_passwordLbl = new JLabel("Password:");
		login_passwordLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		login_passwordLbl.setBounds(169, 193, 81, 22);
		login_contentPanel.add(login_passwordLbl);
		
		login_passwordField = new JPasswordField();
		login_passwordField.setBounds(260, 194, 207, 21);
		login_contentPanel.add(login_passwordField);
		
		JButton login_loginBtn = new JButton("Login");
		login_loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {						
				showLoadingScreen();
				
				SwingWorker<Integer, Integer> sw = null;
				
				sw = new SwingWorker<Integer, Integer>() {

					@Override
					protected Integer doInBackground() {
						
						login_errorLbl.setText(""); //Resetting the error message.
						
						//Update the database properties as the user may have altered the credentials for the database.
						properties.setProperty("dbUrl", config_urlTextField.getText());
						properties.setProperty("dbUser", config_userTextField.getText());
						properties.setProperty("dbPassword", new String(config_passwordTextField.getPassword()));
						
						//Test the connection to the database.
						if(appManager.testConnectionToDatabase(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPassword"))) {							
							//There connection attempt was successful.							
							
							//Get the username and password specified by the user.
							char[] passwordArray = login_passwordField.getPassword();
							String password = new String(passwordArray);
							String username = login_usernameTextField.getText();
																		
							//Attempt to log the user in (check if the username and password are correct).
							if(appManager.loginUser(username, password)) {
								//The user successfully logged in.
								//hideLoadingScreen(); //Was causing a bug where once the user successfully logged in, a blank gray panel would only be shown.
								display("HOME");
							} else {
								//The user failed to log in.
								hideLoadingScreen();
								triggerError(ApplicationFrame.ERROR_LOGIN_FAILED, "Login failed, you have entered an incorrect username and/or password.");								
							}
							
						} else {
							//The connection failed.
							hideLoadingScreen();
							appFrame.triggerError(ApplicationFrame.ERROR_CONNECTION_FAILED, "The connection to the database failed!\n\nPlease ensure your configuration credentials are correct!");
							
						}
												
						return 1;
					}
					
				};				
				sw.execute();
			}
		});
		login_loginBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		login_loginBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		login_loginBtn.setBounds(270, 240, 89, 23);		
		login_contentPanel.add(login_loginBtn);
		
		JButton login_clearBtn = new JButton("Clear");
		login_clearBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				login_usernameTextField.setText("");
				login_passwordField.setText("");
			}
			
		});
		login_clearBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		login_clearBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		login_clearBtn.setBounds(369, 240, 89, 23);
		login_contentPanel.add(login_clearBtn);
		
		login_saveDetailsChckbx = new JCheckBox("Save details");
		login_saveDetailsChckbx.setBackground(Color.WHITE);
		login_saveDetailsChckbx.setBounds(375, 215, 97, 23);
		login_saveDetailsChckbx.setSelected(true);
		login_contentPanel.add(login_saveDetailsChckbx);
		
		JSeparator login_separator = new JSeparator();
		login_separator.setBackground(Color.BLACK);
		login_separator.setBounds(260, 275, 207, 2);
		login_contentPanel.add(login_separator);
		
		JButton login_backBtn = new JButton("Back");
		login_backBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				display("MAIN MENU");				
			}
			
		});
		login_backBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		login_backBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		login_backBtn.setBounds(324, 288, 89, 23);		
		login_contentPanel.add(login_backBtn);
		
		
		
		
		//Home Panel
		home_contentPanel = new JPanel();
		home_contentPanel.setBackground(Color.WHITE);
		home_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		home_contentPanel.setLayout(null);
		
		menu_menuBar = new JMenuBar();
		menu_menuBar.setBounds(0, 0, 681, 21);
		home_contentPanel.add(menu_menuBar);
		
		JMenu applicationMenu = new JMenu("Application");
		menu_menuBar.add(applicationMenu);
		
		
		
		JMenuItem menu_homeMnItem = new JMenuItem("Home");
		menu_homeMnItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				display("HOME");
			}
		});
		applicationMenu.add(menu_homeMnItem);
		
		JMenuItem menu_logoutMnItem = new JMenuItem("Logout");
		menu_logoutMnItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				display("MAIN MENU");			
			}	
			
		});
		applicationMenu.add(menu_logoutMnItem);
		
		JMenuItem menu_exitMnItem = new JMenuItem("Exit");
		menu_exitMnItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispatchEvent(new WindowEvent(appFrame, WindowEvent.WINDOW_CLOSING)); //Exit the application.				
			}
			
		});
		applicationMenu.add(menu_exitMnItem);
		
		JMenu customersMenu = new JMenu("Customers");
		menu_menuBar.add(customersMenu);
		
		JMenuItem menu_viewCustomersMnItem = new JMenuItem("View");
		menu_viewCustomersMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//currentPanel.setVisible(false);
				//setContentPane(cust_contentPanel);
				display("CUSTOMER");
			}
		});
		customersMenu.add(menu_viewCustomersMnItem);
		
		JMenuItem menu_newCustomersMnItem = new JMenuItem("New");
		customersMenu.add(menu_newCustomersMnItem);
		
		JMenu menu_employeesMenu = new JMenu("Employees");
		menu_menuBar.add(menu_employeesMenu);
		
		JMenuItem menu_viewEmployeesMnItem = new JMenuItem("View");
		menu_employeesMenu.add(menu_viewEmployeesMnItem);
		
		JMenuItem menu_newEmployeesMnItem = new JMenuItem("New");
		menu_employeesMenu.add(menu_newEmployeesMnItem);
		
		JMenu menu_settingsMenu = new JMenu("Settings");
		menu_menuBar.add(menu_settingsMenu);
		
		JMenu menu_accountMenu = new JMenu("Account");
		menu_menuBar.add(menu_accountMenu);
		
		JMenuItem menu_changePasswordMnItem = new JMenuItem("Change password");
		menu_accountMenu.add(menu_changePasswordMnItem);
		
		JMenu menu_adminMenu = new JMenu("Admin");
		menu_menuBar.add(menu_adminMenu);
		
		JMenuItem menu_newUserMnItem = new JMenuItem("New User");
		menu_newUserMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Yooo");
			}
		});
		menu_adminMenu.add(menu_newUserMnItem);
		//homePanel.setLayout(null);		
		
		JLabel home_appTitleLbl = new JLabel("Business Manager Application");
		home_appTitleLbl.setBounds(5, 5, 671, 86);
		home_appTitleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		home_appTitleLbl.setFont(new Font("Tahoma", Font.PLAIN, 17));
		home_appTitleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		home_contentPanel.add(home_appTitleLbl);
		
		JLabel home_informationLbl = new JLabel("Information");
		home_informationLbl.setBounds(5, 102, 671, 75);
		home_informationLbl.setHorizontalAlignment(SwingConstants.CENTER);
		home_contentPanel.add(home_informationLbl);
		
		JTextPane home_newsTextPane = new JTextPane();
		home_newsTextPane.setBounds(5, 167, 666, 285);
		home_newsTextPane.setEditable(false);
		home_newsTextPane.setText("\u2022 This application is currently in the BETA stage of development.\n\u2022 Please provide feedback to the developer by emailing jasonandrews271192@gmail.com, thanks!");
		home_contentPanel.add(home_newsTextPane);
		
		
		//Customers Panel		
		cust_contentPanel = new JPanel();
		cust_contentPanel.setBackground(Color.WHITE);
		cust_contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		cust_contentPanel.setLayout(null);
		
		/*
		JButton cust_newCustomerBtn = new JButton("New customer");
		cust_newCustomerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				popupFrame.fillInForm("CUSTOMERS",  new Customer());
				popupFrame.setLocation((getLocation().x + (popupFrame.getWidth()/4)), ((getLocation().y + (popupFrame.getHeight()/4)))); //Set the position of the PopupFrame.
				popupFrame.setVisible(true);	
			}
		});
		cust_newCustomerBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		cust_newCustomerBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		cust_newCustomerBtn.setBounds(20, 425, 120, 30);
		cust_contentPanel.add(cust_newCustomerBtn);
		cust_newCustomerBtn.setVisible(false); //Hide this button for now.
		
		
		cust_viewCustomerBtn = new JButton("View");
		cust_viewCustomerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//View information about the customer.
				int selectedRow = cust_customersTable.getSelectedRow();
				
				if(selectedRow >= 0) { //Make sure the selected row is valid.
					popupFrame.fillInForm("CUSTOMERS",  customerList.get(selectedRow));
					popupFrame.setLocation((getLocation().x + (popupFrame.getWidth()/4)), ((getLocation().y + (popupFrame.getHeight()/4))));
					popupFrame.setVisible(true);
					popup_optionsPopup.setVisible(false);
				}	
			}
		});
		cust_viewCustomerBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		cust_viewCustomerBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		cust_viewCustomerBtn.setBounds(150, 425, 120, 30);
		cust_viewCustomerBtn.setVisible(false);
		cust_contentPanel.add(cust_viewCustomerBtn);
		
		cust_editCustomerBtn = new JButton("Edit");
		cust_editCustomerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Edit information about the customer (can also be done when viewing the customer, but this is direct.)
				
				int selectedRow = cust_customersTable.getSelectedRow();
				
				if(selectedRow >= 0) { //Make sure the selected row is valid.
					popupFrame.fillInForm("CUSTOMERS",  customerList.get(selectedRow));
					popupFrame.setLocation((getLocation().x + (popupFrame.getWidth()/4)), ((getLocation().y + (popupFrame.getHeight()/4))));
					popupFrame.setVisible(true);
					popupFrame.setEditingForm("CUSTOMERS", true);
					popupFrame.setFormEditable("CUSTOMERS", true);
					popup_optionsPopup.setVisible(false);
				}	
			}
		});
		cust_editCustomerBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		cust_editCustomerBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		cust_editCustomerBtn.setBounds(300, 425, 120, 30);
		cust_editCustomerBtn.setVisible(false);
		cust_contentPanel.add(cust_editCustomerBtn);
		
		cust_deleteCustomerBtn = new JButton("Delete");
		cust_deleteCustomerBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Delete the customer from the database.
			}
		});
		cust_deleteCustomerBtn.setBackground(ApplicationFrame.BUTTON_BACKGROUND_COLOR);
		cust_deleteCustomerBtn.setForeground(ApplicationFrame.BUTTON_FOREGROUND_COLOR);
		cust_deleteCustomerBtn.setBounds(450, 425, 120, 30);
		cust_deleteCustomerBtn.setVisible(false);
		cust_contentPanel.add(cust_deleteCustomerBtn);
		*/
		
		//The search text field the user can use to filter the customers shown.
		cust_searchTextField = new JTextField();
		cust_searchTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) { }

			@Override
			public void keyReleased(KeyEvent arg0) { }

			@Override
			public void keyTyped(KeyEvent arg0) {
				refreshTable("CUSTOMERS");
			}
			
		});
		
		//Add a focus adapter so we can set the textfields text according to what method is called.
		cust_searchTextField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusGained(FocusEvent arg0) {
				if(cust_searchTextField != null && cust_searchTextField.getText().equals("Search...")) cust_searchTextField.setText(""); //Set the textfield's text to an empty string.
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				if(cust_searchTextField != null && cust_searchTextField.getText().length() == 0) {
					cust_searchTextField.setText("Search..."); //If the user never typed anything into the textfield, set the text to the default message.
				}
			}
		});
		cust_searchTextField.setText("Search...");
		cust_searchTextField.setBounds(80, 30, 165, 20);		
		cust_contentPanel.add(cust_searchTextField);
		cust_searchTextField.setColumns(10);
		
		
		
		cust_customerNoChckbx = new JCheckBox("Customer No.");
		cust_customerNoChckbx.setBackground(Color.WHITE);
		cust_customerNoChckbx.setSelected(true);
		cust_customerNoChckbx.setBounds(306, 28, 102, 23);
		cust_contentPanel.add(cust_customerNoChckbx);
		
		cust_customerNameChckbx = new JCheckBox("Name");
		cust_customerNameChckbx.setBackground(Color.WHITE);
		cust_customerNameChckbx.setSelected(true);
		cust_customerNameChckbx.setBounds(416, 28, 102, 23);
		cust_contentPanel.add(cust_customerNameChckbx);
		
		
		cust_viewTableLbl = new JLabel("");
		cust_viewTableLbl.setBounds(55, 100, 25, 20);
		cust_viewTableLbl.setBackground(Color.WHITE);
		cust_viewTableLbl.setBorder(null);
		cust_viewTableLbl.setIcon(new ImageIcon("lib/images/view_entity_image.png"));
		cust_viewTableLbl.setVisible(false);
		cust_viewTableLbl.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				int selectedRow = cust_customersTable.getSelectedRow();
				
				if(selectedRow >= 0) { //Make sure the selected row is valid.
					popupFrame.fillInForm("CUSTOMERS",  customerList.get(selectedRow));
					popupFrame.setLocation((getLocation().x + (popupFrame.getWidth()/4)), ((getLocation().y + (popupFrame.getHeight()/4))));
					popupFrame.setVisible(true);
					popup_optionsPopup.setVisible(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) { }

			@Override
			public void mouseExited(MouseEvent arg0) { }

			@Override
			public void mousePressed(MouseEvent arg0) { }

			@Override
			public void mouseReleased(MouseEvent arg0) { }
			
		});
		cust_contentPanel.add(cust_viewTableLbl);
				
		cust_editTableLbl = new JLabel("");
		cust_editTableLbl.setBounds(30, 100, 20, 20);
		cust_editTableLbl.setBackground(Color.WHITE);
		cust_editTableLbl.setBorder(null);
		cust_editTableLbl.setIcon(new ImageIcon("lib/images/edit_entity_image.png"));
		cust_editTableLbl.setVisible(false);
		cust_editTableLbl.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				//Edit information about the customer (can also be done when viewing the customer, but this is direct.)
				
				int selectedRow = cust_customersTable.getSelectedRow();
				
				if(selectedRow >= 0) { //Make sure the selected row is valid.
					popupFrame.fillInForm("CUSTOMERS",  customerList.get(selectedRow));
					popupFrame.setLocation((getLocation().x + (popupFrame.getWidth()/4)), ((getLocation().y + (popupFrame.getHeight()/4))));
					popupFrame.setVisible(true);
					popupFrame.setEditingForm("CUSTOMERS", true);
					popupFrame.setFormEditable("CUSTOMERS", true);
					popup_optionsPopup.setVisible(false);
				}	
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) { }

			@Override
			public void mouseExited(MouseEvent arg0) { }

			@Override
			public void mousePressed(MouseEvent arg0) { }

			@Override
			public void mouseReleased(MouseEvent arg0) { }
			
		});
		cust_contentPanel.add(cust_editTableLbl);
		
		cust_deleteTableLbl = new JLabel("");
		cust_deleteTableLbl.setBounds(5, 98, 20, 25);
		cust_deleteTableLbl.setBackground(Color.WHITE);
		cust_deleteTableLbl.setBorder(null);
		cust_deleteTableLbl.setIcon(new ImageIcon("lib/images/delete_entity_image.png"));
		cust_deleteTableLbl.setVisible(false);
		cust_deleteTableLbl.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) { 
				//Delete the customer from the database.
				//Make a popup for a confirmation.
			}

			@Override
			public void mouseEntered(MouseEvent arg0) { }

			@Override
			public void mouseExited(MouseEvent arg0) { }

			@Override
			public void mousePressed(MouseEvent arg0) { }

			@Override
			public void mouseReleased(MouseEvent arg0) { }
			
		});
		cust_contentPanel.add(cust_deleteTableLbl);
		
		
		//Customer Table
		final JScrollPane cust_scrollPane = new JScrollPane();
		cust_scrollPane.setBackground(Color.WHITE);
		cust_scrollPane.addMouseListener(new MouseListener() {			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//If they left clicked.
				if(arg0.getButton() == MouseEvent.BUTTON1) {
					cust_viewTableLbl.setVisible(false);
					cust_editTableLbl.setVisible(false);
					cust_deleteTableLbl.setVisible(false);
					
					cust_customersTable.clearSelection();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) { }

			@Override
			public void mouseExited(MouseEvent arg0) { }

			@Override
			public void mousePressed(MouseEvent arg0) { }

			@Override
			public void mouseReleased(MouseEvent arg0) { }
			
		});
		cust_scrollPane.setBounds(80, 60, 661, 348);
		cust_contentPanel.add(cust_scrollPane);

		cust_customersTable = new JTable();
		cust_scrollPane.setViewportView(cust_customersTable);

		cust_customersTable.setAutoCreateRowSorter(true);
		cust_customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cust_customersTable.setColumnSelectionAllowed(true);		
		cust_customersTable.setBorder(new LineBorder(new Color(0, 0, 0)));
		cust_customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cust_customersTable.setColumnSelectionAllowed(false);
		cust_customersTable.setCellEditor(null);
		cust_tableColumnNames = new String[]{"Customer No.", "First Name", "Last Name"};
		cust_customersTable.setModel(new DefaultTableModel(null, cust_tableColumnNames));		
		cust_customersTable.addMouseListener(new MouseListener() {			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//If they left clicked.
								
				if(arg0.getButton() == MouseEvent.BUTTON1) {
				
					
					
					int selectedRow = cust_customersTable.getSelectedRow(); //Get the id of the row the user selected. The return int value will be the value used in the array to retrieve information on the customer.					
					int columnCount = cust_customersTable.getColumnCount(); //Get the total amount of columns, if it's only 1, then that means that they have no check boxes ticked and the notice was shown.
					
					if(selectedRow >= 0 && columnCount > 1) { //Only show the options if the person clicked on a valid row.

						//Get the Y position of the mouse.
						int mouseY = arg0.getPoint().y;

						//Reposition the 'View', 'Edit' & 'Delete' icons (the icons that appear to the left of the tables).
						cust_viewTableLbl.setLocation(cust_viewTableLbl.getLocation().x, (mouseY + cust_scrollPane.getLocation().y + 10));
						cust_editTableLbl.setLocation(cust_editTableLbl.getLocation().x, (mouseY + cust_scrollPane.getLocation().y + 10));
						cust_deleteTableLbl.setLocation(cust_deleteTableLbl.getLocation().x, (mouseY + cust_scrollPane.getLocation().y + 6));
						
						//Set the icons visible.
						cust_viewTableLbl.setVisible(true);
						cust_editTableLbl.setVisible(true);
						cust_deleteTableLbl.setVisible(true);
						
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) { }

			@Override
			public void mouseExited(MouseEvent arg0) { }

			@Override
			public void mousePressed(MouseEvent arg0) { }

			@Override
			public void mouseReleased(MouseEvent arg0) { }
		});
		
		
		cust_refreshTableBtn = new JButton();		
		cust_refreshTableBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshTable("CUSTOMERS");
				
			}
		});
		cust_refreshTableBtn.setBorderPainted(false);
		cust_refreshTableBtn.setBackground(Color.WHITE);
		cust_refreshTableBtn.setBounds(255, 25, 30, 30);
		//ImageIcon cust_refreshImageIcon = new ImageIcon("lib/images/refresh_image_icon");
		ImageIcon cust_refreshImageIcon = new ImageIcon("lib/images/table_search_button.png");
		cust_refreshTableBtn.setIcon(cust_refreshImageIcon);
		cust_contentPanel.add(cust_refreshTableBtn);	
		
		//Popup Menu (When a user left clicks a row, a menu will pop up with a list of different options). 
		final JMenuItem popup_closeMnItem = new JMenuItem("Close");
		final JMenuItem popup_viewMnItem = new JMenuItem("View");
		final JMenuItem popup_editMnItem = new JMenuItem("Edit");
		final JMenuItem popup_deleteMnItem = new JMenuItem("Delete");
		
		popup_optionsPopup.setPopupSize(60, 60);
		
		//The Close menu item.
		popup_closeMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("CLOSING");
				if(popup_optionsPopup != null) popup_optionsPopup.setVisible(false);
				//popupIsShown = false;
			}
		});
		popup_closeMnItem.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) { }

			@Override
			public void mouseMoved(MouseEvent arg0) {				
				popup_closeMnItem.setForeground(COLOR_RED);
				popup_viewMnItem.setForeground(Color.BLACK);
				popup_editMnItem.setForeground(Color.BLACK);
				popup_deleteMnItem.setForeground(Color.BLACK);
			}
			
		});
		popup_optionsPopup.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) { }
			
			@Override
			public void focusLost(FocusEvent arg0) {
				popup_optionsPopup.setVisible(false);
				//System.out.println("Focus Lost: customerOptionsPopup");
			}
		});
		popup_closeMnItem.setBounds(0, 0, 20, 20);
		popup_optionsPopup.add(popup_closeMnItem);
		
		//The View menu item.
		popup_viewMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("VIEWING");
				int selectedRow = cust_customersTable.getSelectedRow();
				
				if(selectedRow >= 0) { //Make sure the selected row is valid.
					popupFrame.fillInForm("CUSTOMERS",  customerList.get(selectedRow));
					popupFrame.setLocation((getLocation().x + (popupFrame.getWidth()/4)), ((getLocation().y + (popupFrame.getHeight()/4))));
					popupFrame.setVisible(true);
					popup_optionsPopup.setVisible(false);
				}				
			}
		});
		popup_viewMnItem.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) { }

			@Override
			public void mouseMoved(MouseEvent arg0) {		
				popup_closeMnItem.setForeground(Color.BLACK);
				popup_viewMnItem.setForeground(Color.RED);
				popup_editMnItem.setForeground(Color.BLACK);
				popup_deleteMnItem.setForeground(Color.BLACK);
			}
			
		});
		popup_viewMnItem.setBounds(0, 0, 20, 20);
		popup_optionsPopup.add(popup_viewMnItem);
		
		//The Edit menu item.
		popup_editMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("EDITING");
			}
		});
		popup_editMnItem.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) { }

			@Override
			public void mouseMoved(MouseEvent arg0) {			
				popup_closeMnItem.setForeground(Color.BLACK);	
				popup_viewMnItem.setForeground(Color.BLACK);	
				popup_editMnItem.setForeground(Color.RED);
				popup_deleteMnItem.setForeground(Color.BLACK);
			}
			
		});
		popup_optionsPopup.add(popup_editMnItem);
		
		//The Delete menu item.
		popup_deleteMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("DELETING");
			}
		});
		popup_deleteMnItem.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) { }

			@Override
			public void mouseMoved(MouseEvent arg0) {	
				popup_closeMnItem.setForeground(Color.BLACK);	
				popup_editMnItem.setForeground(Color.BLACK);
				popup_viewMnItem.setForeground(Color.BLACK);				
				popup_deleteMnItem.setForeground(Color.RED);
			}
			
		});
		popup_optionsPopup.add(popup_deleteMnItem);
	}	
	
	/**
	 * Sets the Login screen's username and password fields.
	 * @param username - Set the text in the usernames JTextField.
	 * @param password - Set the text in the passwords JPasswordField.
	 */
	public void login_setLoginFields(String username, String password) {	
		login_usernameTextField.setText(username);
		login_passwordField.setText(password);		
	}
	
	/**
	 * Show the loading screen on a particular panel.
	 */
	public void showLoadingScreen() {	
		//System.out.println("Attempting to show the loading screen");
		if(loadingScreenImage != null) {
			//System.out.println("Attempting to show the loading screen 02");
			if(currentPanel != null) {
				//System.out.println("Attempting to show the loading screen 03");
				Component[] comps = currentPanel.getComponents();
				if(currentPanel != cust_contentPanel/*May need to add the other table content panels here*/) { //A hacky way of dealing with an issue that allows certain GUI elements to be visible when the loading screen is on. Temporary fix.
					for(Component c : comps) {
						c.setVisible(false);
					}
				}
			}
			
			//System.out.println("Showing loading screen");
			loadingScreenImage.setVisible(true);
		}
	}
	
	/**
	 * Hide the loading screen.
	 */
	public void hideLoadingScreen() {
		//System.out.println("Attempting to hide the loading screen");
		if(loadingScreenImage != null) {
			//System.out.println("Attempting to hide the loading screen part 2");
			if(currentPanel != null) {
				//System.out.println("Attempting to hide the loading screen 3");
				Component[] comps = currentPanel.getComponents();
				if(currentPanel != cust_contentPanel) { //A hacky way of dealing with an issue that allows certain GUI elements to be visible when the loading screen is on. Temporary fix.
					for(Component c : comps) {
						c.setVisible(true);
					}
				}
			}
			
			//System.out.println("Hiding loading screen");
			loadingScreenImage.setVisible(false);
		}
	}
	
	public static final int ERROR_CONNECTION_FAILED = 1;
	public static final int ERROR_CONNECTION_DROPPED = 2;
	public static final int ERROR_LOGIN_FAILED = 3; 
	
	public static final int SUCCESS_CONNECTION_PASSED = 1;
	
	private JOptionPane errorDialog;
	
	/**
	 * A way to trigger error messages within the application. 
	 * In most instances, this will just show an error message in a label or a popup dialog.
	 * @param errorID - The Error ID for the error.
	 * @param message - The message that should be displayed to the user.
	 */
	public void triggerError(int errorID, String message) {
		switch(errorID) {
			case ERROR_CONNECTION_FAILED: {
				
				//Show them a dialog (JOptionPane) that waits until they click 'Ok' if the database connection fails.
				errorDialog = new JOptionPane();
				JOptionPane.showMessageDialog(
						this,
					    message,
					    "Connection error",
					    JOptionPane.ERROR_MESSAGE);
				errorDialog.setVisible(true);
				break; 
			}
			case ERROR_LOGIN_FAILED: { 
				login_errorLbl.setText(message);
				break; 
			}			
		}
	}
	
	/**
	 * A way to trigger a success within the application. 
	 * An example would be to change the connection status if the connection to the database was successful. 
	 * Currently unused.
	 */
	public void triggerSuccess(int successID) {
		switch(successID) {
			case SUCCESS_CONNECTION_PASSED: {
				
				break; 
			}			
		}
	}
	
	/**
	 * Display a JPanel depending on the given name.
	 * To display the main menu, display("MAIN MENU") would work.
	 * @param panelName - The name of the panel to display. Example - "HOME" would be used to display the home panel.
	 */
	void display(String panelName) {
		//System.out.println("Attempting to display.. " + panelName);
		JPanel panelToDisplay = null;
		switch(panelName) {		
			case "MAIN MENU": {
				//configurePanel.setVisible(false);
				//loginPanel.setVisible(false);
				//homePanel.setVisible(false);				
				//setContentPane(mainMenuPanel);
				appManager.clearUsers(); //Clear all the User objects currently loaded.
				appManager.setLoggedInUser(null); //Reset the 'Currently logged in user'.
				
				panelToDisplay = mm_contentPanel;
				break;
			}
			case "CONFIGURATION": {
				//mainMenuPanel.setVisible(false);
				//loginPanel.setVisible(false);
			//	homePanel.setVisible(false);
				
				//setContentPane(configurePanel);
				//configurePanel.setVisible(true);
				panelToDisplay = config_contentPanel;
				break;
			}
			case "LOGIN": {
				//mainMenuPanel.setVisible(false);
				//configurePanel.setVisible(false);
				//homePanel.setVisible(false);
				
				//setContentPane(loginPanel);
				panelToDisplay = login_contentPanel;
				break;
			}	
			case "HOME": {
				//mainMenuPanel.setVisible(false);
				//configurePanel.setVisible(false);
				//loginPanel.setVisible(false);
				
				//setContentPane(homePanel);
				//homePanel.setVisible(true);
				panelToDisplay = home_contentPanel;
				break;
			}
			case "CUSTOMER": {
				panelToDisplay = cust_contentPanel;
				break;
			}
		}

		if(panelToDisplay != null) setContentPane(panelToDisplay); 
	}
	
	/*
	//Display the content pane that contains all the elements for the main menu screen.
	void displayMainMenu() {		
		//mm_errorLbl.setText("");
		configurePanel.setVisible(false);
		loginPanel.setVisible(false);
		homePanel.setVisible(false);
		
		mainMenuPanel.setVisible(true);
		setContentPane(mainMenuPanel);
		
	}
	
	//Display the content pane that contains all the elements for the configuration screen.
	void displayConfiguration() {		
		mainMenuPanel.setVisible(false);
		loginPanel.setVisible(false);
		homePanel.setVisible(false);
		
		setContentPane(configurePanel);
		configurePanel.setVisible(true);
	}
	
	//Display the content pane that contains all the elements for the login screen.
	void displayLogin() {
		mainMenuPanel.setVisible(false);
		configurePanel.setVisible(false);
		homePanel.setVisible(false);
		
		setContentPane(loginPanel);
		loginPanel.setVisible(true);
	}
	
	void displayHome() {
		mainMenuPanel.setVisible(false);
		configurePanel.setVisible(false);
		loginPanel.setVisible(false);
		
		setContentPane(homePanel);
		homePanel.setVisible(true);
	}
	*/
	
	/**
	 * Return the currently loaded ArrayList of customers.
	 * @return The ArrayList of Customer objects. 
	 */
	public ArrayList<Entity> getCustomerList() {
		return customerList;
	}
	
	/**
	 * Get the text the user has entered into the 'Search' part of the customers area.
	 * @return The text within the table's appropriate search field. 
	 */
	public String getCustomerTableSearchQuery() {
		return (cust_searchTextField.getText());
	}
	
	/**
	 * Get the text the user has entered into the 'Search' part of the employees area.
	 * @return
	 */
	public String getEmployeeTableSearchQuery() { //#WIP
		//return (employeeSearchTextField.getText());
		return null;
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	private JLabel loadingScreenImage;
	
	/**
	 * This will internally handle the showing/hiding of different panels. It will also move the JMenuBar around so it fits on all panels where appropiate.
	 * @param panel - The JPanel to set as the JFrames content panel.
	 */
	public void setContentPane(JPanel panel) {
		if(currentPanel != null) currentPanel.setVisible(false);
		super.setContentPane(panel);		
		
		if(currentPanel != null && currentPanel != mm_contentPanel && currentPanel != config_contentPanel && currentPanel != login_contentPanel) {
			currentPanel.remove(menu_menuBar);			
		}
		
		if(currentPanel != null) {
			Component[] comps = currentPanel.getComponents();
			for(Component c : comps) {
				if(c == loadingScreenImage) { //If the loading screen label is on the current (will be the previous after reassignment) panel.
					
					currentPanel.remove(c); //Remove the component (loading screen label)
				}
			}
		}
		
		this.currentPanel = panel;
		if(currentPanel != null) {
			this.currentPanel.setVisible(true);
			currentPanel.add(loadingScreenImage, 1);
			//loadingScreenImage.setVisible(false);
			hideLoadingScreen();
		}
		
		if(currentPanel != null && currentPanel != mm_contentPanel && currentPanel != config_contentPanel && currentPanel != login_contentPanel) {
			currentPanel.add(menu_menuBar);
		}
		
	}
	
	/**
	 * Resets the elements of a JPanel. Some elements are meant to be hidden by default, this method does just that. 
	 * @param String panelName - The name of the panel to reset the GUI elements of.
	 */
	/*
	private void resetPanelElements(String panelName) {
		switch(panelName) {
			case "CUSTOMERS": {
				cust_viewCustomerBtn.setVisible(false);
				cust_deleteCustomerBtn.setVisible(false);
				cust_editCustomerBtn.setVisible(false);
			}
		}
	}
	*/
	
	/**
	 * Get a prepared MySQL query for the appropriate table, specified by the parameter tableName.
	 * @param String tableName - The name of the table the query is for. For example - "CUSTOMERS" for the customers table. 
	 * @return String - Returns the finished query for the appropriate table.
	 */
	public String prepareTableQuery(String tableName) {
		String query = null;
		
		switch(tableName) {
			case "CUSTOMERS": {
				
				if(cust_customerNoChckbx.isSelected() || cust_customerNameChckbx.isSelected()) {
					
					query = "SELECT * FROM `customers` ";
					
					query += "WHERE ";
					
					//Concatenate the query depending on if the search by employee number check box is ticked.
					if(cust_customerNoChckbx.isSelected()) { 
						query += "`customer_number` LIKE '"+cust_searchTextField.getText()+"%' ";
					}
					
					//Concatenate the query depending on if the search by customer name check box is ticked.
					if(cust_customerNameChckbx.isSelected()) {
						
						if(cust_customerNoChckbx.isSelected()) query += "OR "; //Easy way to alter the query so the query will work properly. "OR " is needed for the query to actually work if both checkboxes are ticked.
						
						query += "`first_name` LIKE '"+cust_searchTextField.getText()+"%' OR `last_name` LIKE '"+cust_searchTextField.getText()+"%'";	
					}
					
					//If the search field is just left as "Search..." or there's nothing in it, then select everything from the table.
					if(cust_searchTextField.getText().equals("Search...") || cust_searchTextField.getText().length() == 0) query = "SELECT * FROM `customers`";
					
				} else {
					query = null;
				}
				
				break;
			}
			case "EMPLOYEES": {
				break;
			}
			case "USERS": {
				break;
			}
		}
				
		return query; 
	}
	
	/**
	 * Refresh the appropriate table. The table to be refreshed is decided by the parameter tableName.
	 * Get the list of Customer, Employee or User objects, depending on the query (prepared in the prepareTableQuery method) sent.
	 * From the list of objects, prepare the tables rows and then populate the table with the data.
	 * @param tableName - The name of the table to refresh. "CUSTOMERS", "EMPLOYEES", "USERS".
	 */
	public void refreshTable(String tableName) {
		String[] columnNames = null;
		Object[][] rowData = null;
		JTable table = null;
		String query = null;
		
		//Set the 'View', 'Edit' & 'Delete' icons to be invisible.
		if(null != cust_viewTableLbl) cust_viewTableLbl.setVisible(false);
		if(null != cust_editTableLbl) cust_editTableLbl.setVisible(false);
		if(null != cust_deleteTableLbl) cust_deleteTableLbl.setVisible(false);
		
		
		switch(tableName) {
		
			case "CUSTOMERS": {
				
				table = cust_customersTable;
				
				query = this.prepareTableQuery(tableName);
				
				//If query is not null at this point, then atleast one of the checkboxes are ticked so get the data.
				if(null != query) {
					
					//Get the list of Customer objects generated from the query. Then get an array containing all the data from the objects so it can be displayed in the table.
					customerList = appManager.getTableRowData("CUSTOMERS", query);  
					
					columnNames = cust_tableColumnNames;
					
					rowData = appManager.getRowData("CUSTOMER", customerList);
				} 
				
				//If there is no results, then create a notice to the user that nothing was returned so that they are notified.	
				if(rowData == null || rowData.length < 1) {
					rowData = new Object[1][];
					rowData[0] = new Object[]{"No results matched the search criteria"};
					
					columnNames = new String[]{""};
				}
				
				break;
			} 
			case "EMPLOYEES": {
				break;
			}
			case "USERS": {
				break;
			}
		}		 
		
		//Display the new data.
		if(table != null) {
			//Create a new table model for the table.
			DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames) {
				//Overriding the method so no cell is editable.
				@Override
			    public boolean isCellEditable(int row, int column) {
			       //set all cells false
			       return false;
			    }
			};
			table.setModel(tableModel); //Set the tables model.
		}
		
		table = null;
	}
	
	/**
	 * Get the instanced Properties object.
	 * @return Properties - Returns the instanced Properties object.
	 */
	public Properties getProperties() {		
		return this.properties;
	}
	
	/**
	 * Loads the properties file, if it exists.
	 * Checks if the configuration file exists. If it does, load the properties from the file. If it doesn't, create the directory (if needed) and file.
	 */
	public void loadProperties() {	
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File dirCheck = null;
		
		try {
			
			properties = new Properties();
			
			fileDirectory = (System.getProperty("user.home")+"\\Business Manager\\config.ini");			
			dirCheck = new File(fileDirectory); //Create a new file (to check if it exists.)
			
			if(dirCheck.exists()) { //The file exists.
				
				fis = new FileInputStream(fileDirectory); //Create a new FileInputStream for the configuration file.
				
				properties.load(fis); //Load the properties from the file.
				
				//Set the details on the configuration form to the details that were saved.
				config_urlTextField.setText(properties.getProperty("dbUrl"));
				config_userTextField.setText(properties.getProperty("dbUser"));
				config_passwordTextField.setText(properties.getProperty("dbPassword"));
				
				//Set the details on the login form to the details that were saved.
				login_usernameTextField.setText(properties.getProperty("loginUser"));
				login_passwordField.setText(properties.getProperty("loginPassword"));	
				
			} else { //The file doesn't exist.
				
				dirCheck = new File(System.getProperty("user.home")+"\\Business Manager"); //Create a new file to see if the directory exists.
				if(!dirCheck.exists()) dirCheck.mkdirs(); //Create the directory if it doesn't exist (the previous check is if the file exists.)
				
				fos = new FileOutputStream(fileDirectory); //Create a new file so we can store the properties in it.				
				
				properties.setProperty("dbUrl", "");
				properties.setProperty("dbUser", "");
				properties.setProperty("dbPassword", "");
				
				properties.setProperty("loginUser", "");
				properties.setProperty("loginPassword", "");
				
				properties.store(fos, "");
			}			
			
		} catch (Exception e) {

			e.printStackTrace();
			
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}		
	}
	
	/**
	 * Save all appropriate data into the config.ini file.
	 * Saved properties depend on what the user wants saved, for example, ticking the checkbox for the database configuration will save the credentials.
	 */
	public void storeProperties() {
		FileOutputStream fos = null;
		
		try {
			
			fos = new FileOutputStream(fileDirectory);
			
			//Check if the 'Save details' button is selected under the Configuration panel.
			if(config_saveDetailsChckbx.isSelected()) {
				//if it is selected, save the details.	
				properties.setProperty("dbUrl", config_urlTextField.getText());
				properties.setProperty("dbUser", config_userTextField.getText());
				properties.setProperty("dbPassword", new String(config_passwordTextField.getPassword()));
			} else {
				//if it's not selected, do not save the details.
				properties.setProperty("dbUrl", "");
				properties.setProperty("dbUser", "");
				properties.setProperty("dbPassword", "");
			}
			
			if(login_saveDetailsChckbx.isSelected()) {
				//if it is selected, save the details.
				properties.setProperty("loginUser", login_usernameTextField.getText());
				properties.setProperty("loginPassword", new String(login_passwordField.getPassword()));	
			} else {
				//if it's not selected, do not save the details.
				properties.setProperty("loginUser", "");
				properties.setProperty("loginPassword", "");
			}
			
			properties.store(fos, null);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if(fos != null) {
				try { 
					fos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}		
	}
	
	
	
}