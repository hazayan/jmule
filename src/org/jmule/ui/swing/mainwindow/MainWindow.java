/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.ui.swing.mainwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jmule.core.JMConstants;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.ui.swing.UISwingImageRepository;
import org.jmule.ui.swing.maintabs.SearchTab;
import org.jmule.ui.swing.maintabs.ServerListTab;
import org.jmule.ui.swing.maintabs.SharedTab;
import org.jmule.ui.swing.maintabs.StatisticsTabs;
import org.jmule.ui.swing.maintabs.TransfersTab;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:20 $$
 */
public class MainWindow extends JFrame  {

	private JMenuBar main_menu_bar;
	
	private JMenu file,
	              view,
	              tools,
	              help;
	
	private JMenuItem exit,
	                  servers,
	                  transfers,
	                  search,
	                  shared,
	                  stats,
	                  ip_filter,
	                  config_wizard,
	                  options,
	                  about;
	
	private BorderLayout border_layout0;
	
	private JToolBar main_buttons_bar;
	private StatusBar status_bar;
	
	// main tabs 
	private ServerListTab server_list_tab = new ServerListTab();
	private SearchTab search_tab = new SearchTab();
	private SharedTab shared_tab = new SharedTab();
	private TransfersTab transfers_tab = new TransfersTab(); 
	private StatisticsTabs statistic_tab = new StatisticsTabs();
	
	JPanel the_current_view = server_list_tab;
	
	private JPanel center_panel = new JPanel();
	
	private JMuleCore _core;
	private ServerManager _server_manager;
	
	public MainWindow() {
		getCoreComponents();
		initUIComponents();
        setMainMenu();
        setMainButtonsBar();    
        setView(server_list_tab);
        setStatusBar();
	}
	

	public void getCoreComponents() {
		
		try {
			
			_core = JMuleCoreFactory.getSingleton();
			
			_server_manager = _core.getServersManager();
			
		}catch(Throwable t) {}
		
	}
	
	// --------------------------------------------------------------
	public void initUIComponents() {
		
		String title = JMConstants.JMULE_NAME + " " + JMConstants.CURRENT_JMULE_VERSION + " " + ( JMConstants.IS_BETA?"Beta":"" );
		this.setTitle(title);
		//TODO get this from CfgManager
		this.setSize(800, 470);
		this.setPreferredSize(new Dimension(800, 470));
		//this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("resources/jmule.png")).getImage());
		//place the main windows in the middle of display
		this.setLocation(getToolkit().getScreenSize().width/2 - getWidth()/2,
                         getToolkit().getScreenSize().height/2 - getHeight()/2);
		//set the main layout
		border_layout0 = new BorderLayout();
        this.setLayout( border_layout0 );
        
        center_panel.setLayout(new GridLayout(1,1));
        this.add(center_panel, BorderLayout.CENTER);
		
	}
	//----------------------------------------------------------------------
	private void setMainMenu() {
		main_menu_bar = new JMenuBar();
		file = new JMenu();
		view = new JMenu();
		tools = new JMenu();
		help = new JMenu();
		
		exit = new JMenuItem();
		servers = new JMenuItem();
		transfers = new JMenuItem();
		search = new JMenuItem();
		shared = new JMenuItem();
		stats = new JMenuItem();
		ip_filter = new JMenuItem();
		config_wizard = new JMenuItem();
		options = new JMenuItem();
		about = new JMenuItem();
		
		//TODO get the strings from LocaleManager
		file.setText("File");
		view.setText("View");
		tools.setText("Tools");
		help.setText("Help");
		
		//TODO get the strings from LocaleManager
		exit.setText("Exit");
		servers.setText("Servers");
		transfers.setText("Transfers");
		search.setText("Search");
		shared.setText("Shared");
		stats.setText("Stats");
		ip_filter.setText("IP Filter");
		config_wizard.setText("Configuration wizard");
		options.setText("Options");
		about.setText("About");
		
		main_menu_bar.add( file );
		main_menu_bar.add( view );
		main_menu_bar.add( tools );
		main_menu_bar.add( help );
		
		file.add( exit );
		
		view.add( servers );
		view.add( transfers );
		view.add( search );
		view.add( shared );
		view.add( stats );
		
		tools.add( ip_filter );
		tools.add( config_wizard );
		tools.add( new JSeparator() );
		tools.add( options );
		
		help.add( about );
		
		this.setJMenuBar( main_menu_bar );
	}
	
	//TODO extract this for a new class -> MainButtonsBar.java
	private void setMainButtonsBar() {
        main_buttons_bar = new JToolBar();
		this.getContentPane().add( main_buttons_bar, BorderLayout.NORTH );
		//main_buttons_bar.setPreferredSize( new java.awt.Dimension(425, 50) );
		main_buttons_bar.setFloatable( false );
		
		ButtonGroup button_group = new ButtonGroup();
		
		class JMToggleButton extends JToggleButton {
			
			 public JMToggleButton() {
				 
				 this.setSize(new Dimension(50,50));
				 
				 this.setPreferredSize(new Dimension(50,50));
				 
			 }
		}
		
		JMToggleButton connect_button = new JMToggleButton();
		connect_button.setIcon( UISwingImageRepository.getIcon("connect_do.png") );
		button_group.add( connect_button );
		main_buttons_bar.add( connect_button );
		
		JToggleButton server_list_button = new JToggleButton(); 
		
		server_list_button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			
				 setView( server_list_tab );
				 
			}
			
		});
		
		server_list_button.setIcon( UISwingImageRepository.getIcon("servers.png") );
		button_group.add( server_list_button );
		main_buttons_bar.add( server_list_button );
		
		JMToggleButton transfers_button = new JMToggleButton();
		
		transfers_button.addActionListener( new ActionListener() {
			
			 public void actionPerformed(ActionEvent e) {
				 
				 setView( transfers_tab );
				 
			 }
			
		});
		
		transfers_button.setIcon( UISwingImageRepository.getIcon("transfer.png") );
		button_group.add( transfers_button );
		main_buttons_bar.add( transfers_button );
		
		JMToggleButton shared_files_button = new JMToggleButton();
		
		shared_files_button.addActionListener(new ActionListener() {
			
			 public void actionPerformed(ActionEvent e) {
				 
				 setView( shared_tab );
				 
			 }
			
		});
		
		shared_files_button.setIcon( UISwingImageRepository.getIcon("shared_files.png") );
		button_group.add( shared_files_button );
		main_buttons_bar.add( shared_files_button );
		
		JMToggleButton search_button = new JMToggleButton();
		
		search_button.addActionListener(new ActionListener() {
			
			 public void actionPerformed(ActionEvent e) {
				 
				 setView( search_tab );
				 
			 }
		});
		
		search_button.setIcon( UISwingImageRepository.getIcon("search.png") );
		button_group.add( search_button );
		main_buttons_bar.add( search_button );
		
		JMToggleButton statistics_button = new JMToggleButton();
		
		statistics_button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				setView( statistic_tab );
				
			}
			
		}); 
		
		statistics_button.setIcon( UISwingImageRepository.getIcon("statistics.png") );
		button_group.add( statistics_button );
		main_buttons_bar.add( statistics_button );
	}
	
	private void setView(JPanel new_view) {
		// JSplitPane the_current_view = new JSplitPane();
		// the_current_view.setOrientation( JSplitPane.VERTICAL_SPLIT );
		center_panel.remove( the_current_view );
		center_panel.add( new_view );
		center_panel.updateUI();
		the_current_view = new_view;
		//center_panel.repaint();
		// JPanel server_list_panel = new ServerListTab();
		// the_current_view.add( server_list_panel, JSplitPane.LEFT );
		// the_current_view.add( new_view, JSplitPane.LEFT );
		
		//JEditorPane editor_panel = new JEditorPane();
		//JScrollPane server_log_panel = new JScrollPane();
		//server_log_panel.setViewportView( editor_panel );
		
		// server_log_panel.add(  new JLabel("Server log panel") );
		// the_current_view.add( server_log_panel, JSplitPane.RIGHT );
	}
	
	private void setStatusBar() {
		status_bar = new StatusBar();
		this.getContentPane().add(status_bar, BorderLayout.SOUTH);
	}
	
	public static void main(String args[]) {
		
		MainWindow mw = new MainWindow();
		mw.pack();
		mw.setVisible( true );
		//Toolkit.getDefaultToolkit().beep();
		
	}


	public void initialize() {
		// TODO Auto-generated method stub
		
	}


	public void shutdown() {
		// TODO Auto-generated method stub
		
	}


	public void start() {
		// TODO Auto-generated method stub
		
	}

}
