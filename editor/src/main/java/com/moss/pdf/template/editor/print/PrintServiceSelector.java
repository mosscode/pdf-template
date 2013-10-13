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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JDialog;
import javax.swing.JFrame;


public class PrintServiceSelector {
	
	private final PrintService[] printServices;
	private final PrintService defaultService;

	private final JDialog dialog;
	private final PrintServiceSelectorView view;
	
	private PrintService selectedService;

	public PrintServiceSelector(JFrame rootFrame) throws NoPrintServicesFoundException {
		
		printServices = PrintServiceLookup.lookupPrintServices(null, null);
		defaultService = PrintServiceLookup.lookupDefaultPrintService();
		
		if (printServices.length == 0) {
			throw new NoPrintServicesFoundException();
		}
		
		view = new PrintServiceSelectorView();
		
		view.getButtonCancel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonPressed();
			}
		});
		
		view.getButtonPrint().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printButtonPressed();
			}
		});
		
		for (PrintService printService : printServices) {
			
			if (printService == null) {
				continue;
			}
			
			PrintServiceWrapper wrapper = new PrintServiceWrapper(printService);
			view.getFieldPrinter().addItem(wrapper);
		}
		
		if (defaultService != null) {
			view.getFieldPrinter().setSelectedItem(new PrintServiceWrapper(defaultService));
		}
		else {
			view.getFieldPrinter().setSelectedIndex(0);
		}

		dialog = new JDialog();
		dialog.setTitle("Select Printer");
		dialog.getContentPane().add(view, BorderLayout.CENTER);
		dialog.pack();
		dialog.setModal(true);
		dialog.setLocationRelativeTo(rootFrame);
		dialog.setVisible(true);
	}
	
	public boolean serviceSelected() {
		return selectedService != null;
	}
	
	public PrintService getSelectedService() {
		return selectedService;
	}

	private void cancelButtonPressed() {
		dialog.dispose();
	}
	
	private void printButtonPressed() {
		PrintServiceWrapper wrapper = (PrintServiceWrapper)view.getFieldPrinter().getSelectedItem();
		PrintService printService = wrapper.getPrintService();
		selectedService = printService;
		
		dialog.dispose();
	}
	
	class PrintServiceWrapper {
		private final PrintService printService;
		
		public PrintServiceWrapper(PrintService printService) {
			if (printService == null) {
				throw new NullPointerException();
			}
			this.printService = printService;
		}
		
		public PrintService getPrintService() {
			return printService;
		}

		public String toString() {
			return printService.getName();
		}
		
		public boolean equals(Object o) {
			return
				o != null
				&&
				o instanceof PrintServiceWrapper
				&&
				((PrintServiceWrapper)o).toString().equals(toString());
		}
		
		public int hashCode() {
			return toString().hashCode();
		}
	}
	
	public static void main(String[] args) throws NoPrintServicesFoundException {
		PrintServiceSelector selector = new PrintServiceSelector(null);
		System.out.println(selector.getSelectedService());
	}
}
