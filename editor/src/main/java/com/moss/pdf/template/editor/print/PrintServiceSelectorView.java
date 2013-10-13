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
package com.moss.pdf.template.editor.print;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

class PrintServiceSelectorView extends JPanel {
	private JButton buttonCancel;
	private JButton buttonPrint;
	private JComboBox fieldPrinter;
	public PrintServiceSelectorView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		buttonCancel = new JButton();
		buttonCancel.setText("Cancel");
		panel.add(buttonCancel);

		buttonPrint = new JButton();
		buttonPrint.setText("Print");
		panel.add(buttonPrint);

		final JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);

		final JLabel labelPrinter = new JLabel();
		labelPrinter.setText("Use Printer:");
		panel_1.add(labelPrinter);

		fieldPrinter = new JComboBox();
		panel_1.add(fieldPrinter);
	}
	public JComboBox getFieldPrinter() {
		return fieldPrinter;
	}
	public JButton getButtonPrint() {
		return buttonPrint;
	}
	public JButton getButtonCancel() {
		return buttonCancel;
	}

}
