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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SpinnerNumberModel;

import com.moss.pdf.template.api.FontName;
import com.moss.pdf.template.api.TextAlignment;

public class TextComponentEditor {

	private final JDialog dialog;
	private final TextComponentEditorView view;
	private TextComponent component;
	
	public TextComponentEditor(String title, TextComponent theComponent, final ParserReference ref) {
		this.component = theComponent;
		
		view = new TextComponentEditorView();
		
		for (FontName f : FontName.values()) {
			view.getFieldFont().addItem(f);
		}
		
		view.getFieldFont().setSelectedItem(component.getFont());
		
		List<String> expressions = new LinkedList<String>(ref.getParser().getValidExpressions());
		Collections.sort(expressions);
		for (String e : expressions) {
			view.getFieldExpression().addItem(e);
		}
		
		if (view.getFieldExpression().getItemCount() > 0) {
			view.getFieldExpression().setSelectedItem(component.getExpr());
		}
		
		view.getFieldFontSize().setModel(new SpinnerNumberModel());
		view.getFieldFontSize().setValue(component.getFontSize());
		
		view.getButtonCancel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				component = null;
				dialog.dispose();
			}
		});
		
		view.getButtonCreate().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				FontName font = (FontName)view.getFieldFont().getSelectedItem();
				int fontSize = ((Number)view.getFieldFontSize().getValue()).intValue();
				String expr = (String)view.getFieldExpression().getSelectedItem();
				
				System.out.println("Update");
				component.setFont(font);
				component.setFontSize(fontSize);
				component.setExpr(expr);
				component.setRef(ref);
				
				dialog.dispose();
			}
		});
		
		dialog = new JDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setTitle(title);
		dialog.getContentPane().add(view, BorderLayout.CENTER);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
	}
	
	public void show() {
		dialog.setVisible(true);
	}
	
	public PageComponent getComponent() {
		return component;
	}
	
//	public static void main(String[] args) {
//		TextComponentEditor d = new TextComponentEditor(new ParserReference() {
//			public Parser getParser() {
//				return null;
//			}
//		});
//		d.show();
//		PageComponent c = d.getComponent();
//		System.out.println(c);
//	}
}
