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
package org.jmule.ui.swing.wizards;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.swing.BrowserLauncher;
import org.jmule.ui.swing.ImgRep;
import org.jmule.ui.swing.common.JMSAction;
import org.jmule.ui.swing.common.LinkLabel;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:09 $$
 */
public class UIChooser extends WizardPanel {

    private JPanel bottom_panel;
    private ButtonGroup button_group;
    private JRadioButton com_line_button;
    private JLabel com_line_desc;
    private JLabel com_line_screen;
    private JLabel eclipse_logo;
    private JLabel java_logo;
    private JPanel middle_panel;
    private JRadioButton swing_button;
    private JLabel swing_desc_label;
    private LinkLabel swing_link_label;
    private JRadioButton swt_button;
    private JLabel swt_desc_label;
    private LinkLabel swt_link_label;
    private JPanel top_panel;
	
    public UIChooser() {
        initComponents();
    }

    private void initComponents() {

        button_group = new javax.swing.ButtonGroup();
        top_panel = new javax.swing.JPanel();
        eclipse_logo = new javax.swing.JLabel();
        swt_button = new javax.swing.JRadioButton();
        swt_desc_label = new javax.swing.JLabel();
        middle_panel = new javax.swing.JPanel();
        java_logo = new javax.swing.JLabel();
        swing_button = new javax.swing.JRadioButton();
        swing_desc_label = new javax.swing.JLabel();
        bottom_panel = new javax.swing.JPanel();
        com_line_screen = new javax.swing.JLabel();
        com_line_button = new javax.swing.JRadioButton();
        com_line_desc = new javax.swing.JLabel();

        JMSAction open_swt_link = new JMSAction() {

			@Override
			public void refreshActionState() {
				
			}

			public void actionPerformed(ActionEvent e) {
				
				BrowserLauncher.openURL("http://eclipse.org/swt");
			}
        	
        };
        
        open_swt_link.setName("More info");
        open_swt_link.setToolTipText("http://eclipse.org/swt");
        
        swt_link_label = new LinkLabel(open_swt_link);
        
        JMSAction open_swing_link = new JMSAction() {

			@Override
			public void refreshActionState() {

			}

			public void actionPerformed(ActionEvent e) {

				BrowserLauncher.openURL("http://en.wikipedia.org/wiki/Swing_(Java)");
			}
        	
        };
        
        open_swing_link.setName("More info");
        open_swing_link.setToolTipText("http://en.wikipedia.org/wiki/Swing_(Java)");
        
        swing_link_label = new LinkLabel(open_swing_link);
        
        setLayout(new java.awt.GridLayout(3, 1));

        eclipse_logo.setIcon(ImgRep.getIcon("wizard/eclipse_logo.jpg"));
        
        button_group.add(swt_button);
        swt_button.setForeground(new java.awt.Color(51, 51, 255));
        swt_button.setSelected(true);
        swt_button.setText("SWT");

        swt_desc_label.setText("<html>Standard Widget Toolkit - is a native platform<br>independent user interface framework</html>");

        //swt_link_label.setText();

        // <<< actions that change the panel background >>>
        swt_button.addActionListener(new ActionListener() {
        	 public void actionPerformed(ActionEvent event) {
        		 top_panel.setBackground(skin.getHighlightedUIChooserItem());
        		 middle_panel.setBackground(new Color(238, 238, 238));
        		 bottom_panel.setBackground(new Color(238, 238, 238));
        		 
        		 swt_button.setBackground(skin.getHighlightedUIChooserItem());
        		 swing_button.setBackground(new Color(238, 238, 238));
        		 com_line_button.setBackground(new Color(238, 238, 238));
        	 }
        });
        
        swing_button.addActionListener(new ActionListener() {
        	 public void actionPerformed(ActionEvent event) {
        		 top_panel.setBackground(new Color(238, 238, 238));
        		 middle_panel.setBackground(skin.getHighlightedUIChooserItem());
        		 bottom_panel.setBackground(new Color(238, 238, 238));
        		 
        		 swt_button.setBackground(new Color(238, 238, 238));
        		 swing_button.setBackground(skin.getHighlightedUIChooserItem());
        		 com_line_button.setBackground(new Color(238, 238, 238));
        	 }
        });
        
        com_line_button.addActionListener(new ActionListener() {
        	 public void actionPerformed(ActionEvent event) {
        		 top_panel.setBackground(new Color(238, 238, 238));
        		 middle_panel.setBackground(new Color(238, 238, 238));
        		 bottom_panel.setBackground(skin.getHighlightedUIChooserItem());
        		 
        		 swt_button.setBackground(new Color(238, 238, 238));
        		 swing_button.setBackground(new Color(238, 238, 238));
        		 com_line_button.setBackground(skin.getHighlightedUIChooserItem());
        	 }
        });
        // <<< end change the panel background >>>
        
        swt_button.doClick();
        
        org.jdesktop.layout.GroupLayout top_panelLayout = new org.jdesktop.layout.GroupLayout(top_panel);
        top_panel.setLayout(top_panelLayout);
        top_panelLayout.setHorizontalGroup(
            top_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(top_panelLayout.createSequentialGroup()
                .add(eclipse_logo)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(top_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(swt_desc_label)
                    .add(swt_button)
                    .add(swt_link_label))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        top_panelLayout.setVerticalGroup(
            top_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(top_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(swt_button)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(swt_desc_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(swt_link_label)
                .addContainerGap(20, Short.MAX_VALUE))
            .add(eclipse_logo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        );

        add(top_panel);

        java_logo.setIcon(ImgRep.getIcon("wizard/java_logo.jpg")); 

        button_group.add(swing_button);
        swing_button.setForeground(new java.awt.Color(51, 51, 255));
        swing_button.setText("SWING");

        swing_desc_label.setText("Standard Java GUI provided by Sun Microsystems");

        //swing_link_label.setText("http://en.wikipedia.org/wiki/Swing_(Java)");

        org.jdesktop.layout.GroupLayout middle_panelLayout = new org.jdesktop.layout.GroupLayout(middle_panel);
        middle_panel.setLayout(middle_panelLayout);
        middle_panelLayout.setHorizontalGroup(
            middle_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(middle_panelLayout.createSequentialGroup()
                .add(java_logo)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(middle_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(swing_button)
                    .add(swing_desc_label)
                    .add(swing_link_label))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        middle_panelLayout.setVerticalGroup(
            middle_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(middle_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(swing_button)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(swing_desc_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(swing_link_label)
                .addContainerGap(35, Short.MAX_VALUE))
            .add(java_logo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        );

        add(middle_panel);

        com_line_screen.setIcon(ImgRep.getIcon("wizard/ui_console.jpg"));

        button_group.add(com_line_button);
        com_line_button.setForeground(new java.awt.Color(51, 51, 255));
        com_line_button.setText("Command line");

        com_line_desc.setText("Very fast user interface (for advanced users only)");

        org.jdesktop.layout.GroupLayout bottom_panelLayout = new org.jdesktop.layout.GroupLayout(bottom_panel);
        bottom_panel.setLayout(bottom_panelLayout);
        bottom_panelLayout.setHorizontalGroup(
            bottom_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottom_panelLayout.createSequentialGroup()
                .add(com_line_screen)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(bottom_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(com_line_button)
                    .add(com_line_desc))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        bottom_panelLayout.setVerticalGroup(
            bottom_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottom_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(com_line_button)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(com_line_desc)
                .addContainerGap(56, Short.MAX_VALUE))
            .add(com_line_screen, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        );

        add(bottom_panel);
    }

    public String getChosenUI() {
    	
    	     if( swt_button.isSelected() ) return JMuleUIManager.SWT_UI;
    	
    	else if( swing_button.isSelected() ) return JMuleUIManager.SWING_UI;
    	     
    	else if( com_line_button.isSelected() ) return JMuleUIManager.CONSOLE_UI;
    	
    	return null;
    }
    
}
