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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

public class TextComponentEditorView extends JPanel {

	private JComboBox fieldExpression;
	private JButton buttonCancel;
	private JButton buttonCreate;
	private JSpinner fieldFontSize;
	private JComboBox fieldFont;
	private static final long serialVersionUID = 1L;
	public TextComponentEditorView() {
		super();
		setLayout(new BorderLayout());

		final JPanel panel_3 = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,0,0,0,0,0,0,0,0,0,7,0,7,0,0,0,0,0,7,0,0,0,0,0,0,7};
		gridBagLayout.columnWidths = new int[] {7,7,7};
		panel_3.setLayout(gridBagLayout);
		add(panel_3, BorderLayout.CENTER);

		final JLabel fontLabel = new JLabel();
		fontLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		fontLabel.setText("Font:");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_4.gridy = 11;
		gridBagConstraints_4.gridx = 1;
		panel_3.add(fontLabel, gridBagConstraints_4);

		fieldFont = new JComboBox();
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 11;
		gridBagConstraints_5.gridx = 4;
		panel_3.add(fieldFont, gridBagConstraints_5);

		final JLabel fontSizeLabel = new JLabel();
		fontSizeLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		fontSizeLabel.setText("Font Size:");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.gridy = 17;
		gridBagConstraints_6.gridx = 1;
		panel_3.add(fontSizeLabel, gridBagConstraints_6);

		fieldFontSize = new JSpinner();
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.ipadx = 25;
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 17;
		gridBagConstraints_7.gridx = 4;
		panel_3.add(fieldFontSize, gridBagConstraints_7);

		final JLabel expressionLabel = new JLabel();
		expressionLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		expressionLabel.setText("Expression:");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.gridy = 24;
		gridBagConstraints_8.gridx = 1;
		panel_3.add(expressionLabel, gridBagConstraints_8);

		fieldExpression = new JComboBox();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 24;
		gridBagConstraints.gridx = 4;
		panel_3.add(fieldExpression, gridBagConstraints);

		final JPanel panel_4 = new JPanel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.weighty = 1.0;
		gridBagConstraints_10.gridy = 26;
		gridBagConstraints_10.gridx = 5;
		panel_3.add(panel_4, gridBagConstraints_10);

		final JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		buttonCreate = new JButton();
		buttonCreate.setText("OK");
		panel.add(buttonCreate);

		buttonCancel = new JButton();
		buttonCancel.setText("Cancel");
		panel.add(buttonCancel);
	}
	public JComboBox getFieldFont() {
		return fieldFont;
	}
	public JSpinner getFieldFontSize() {
		return fieldFontSize;
	}
	public JButton getButtonCreate() {
		return buttonCreate;
	}
	public JButton getButtonCancel() {
		return buttonCancel;
	}
	public JComboBox getFieldExpression() {
		return fieldExpression;
	}

}
