/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of pdf-template.
 *
 * pdf-template is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * pdf-template is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pdf-template; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.pdf.template.editor;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

class PdfTemplateEditorView extends JPanel {

	private JPanel panel;
	private JButton buttonCreate;
	private static final long serialVersionUID = 1L;
	
	private JScrollPane previewScrollpane;
	private PagePanel previewPanel;
	private JTabbedPane tabbedPane;
	private JScrollPane scrollPane;
	private JButton buttonPrevious;
	private JButton buttonNext;
	private PagePanel pagePanel;
	private JButton buttonOut;
	private JButton buttonIn;
	
	PdfTemplateEditorView() {
		setLayout(new BorderLayout());

		panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		buttonCreate = new JButton();
		buttonCreate.setText("Add Field");
		panel.add(buttonCreate);

		buttonIn = new JButton();
		buttonIn.setText("In");
		panel.add(buttonIn);

		buttonOut = new JButton();
		buttonOut.setText("Out");
		panel.add(buttonOut);

		buttonPrevious = new JButton();
		buttonPrevious.setText("Previous");
		panel.add(buttonPrevious);

		buttonNext = new JButton();
		buttonNext.setText("Next");
		panel.add(buttonNext);

		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BorderLayout());
		tabbedPane.addTab("Edit", null, panel_1, null);

		final JSplitPane splitPane = new JSplitPane();
		panel_1.add(splitPane, BorderLayout.CENTER);

		final JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		splitPane.setLeftComponent(panel_2);

		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);

		pagePanel = new PagePanel();
		scrollPane.setViewportView(pagePanel);

		previewScrollpane = new JScrollPane();
		tabbedPane.addTab("Preview", null, previewScrollpane, null);

		previewPanel = new PagePanel();
		previewScrollpane.setViewportView(previewPanel);
	}
	
	JButton getButtonIn() {
		return buttonIn;
	}
	
	JButton getButtonOut() {
		return buttonOut;
	}
	
	PagePanel getPagePanel() {
		return pagePanel;
	}
	
	JButton getButtonNext() {
		return buttonNext;
	}
	
	JButton getButtonPrevious() {
		return buttonPrevious;
	}
	
	JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	
	PagePanel getPreviewPanel() {
		return previewPanel;
	}
	
	JScrollPane getPreviewScrollpane() {
		return previewScrollpane;
	}
	public JButton getButtonCreate() {
		return buttonCreate;
	}
	public JPanel buttonPanel() {
		return panel;
	}
}
