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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import com.moss.pdf.template.editor.print.PDFDocumentPrintable;
import com.moss.pdf.template.editor.print.PrintServiceSelector;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPrintPage;

public class TemplateDesignerApp {
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new TemplateDesignerApp();
	}
	
	static class QuickFilter extends FileFilter {
		private String description;
		private String extension;
		
		public QuickFilter(String description, String extension) {
			super();
			this.description = description;
			this.extension = extension.toLowerCase();
		}
		
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || (f.isFile() && f.getName().toLowerCase().endsWith(extension));
		}
		@Override
		public String getDescription() {
			return description;
		}
	}
	
	private enum FileType{
		TEMPLATE(new QuickFilter("Template Files", ".pdft")), 
		SAMPLE_DATA(new QuickFilter("Data Files", ".properties")), 
		ANY(null), 
		PDF(new QuickFilter("PDF Files", ".pdf")), 
		MAPPINGS(new QuickFilter("Mapping XML Files", ".xml"));
		
		private FileFilter filter;
		
		private FileType(FileFilter filter){
			this.filter = filter;
		}
		public FileFilter filter(){
			return filter;
		}
	}
	
	private final FileBasedPdfTemplateEditor editor = new FileBasedPdfTemplateEditor();
	private final JFileChooser fileChooser = new JFileChooser();
	private boolean rawMode = false;
	private boolean isUnsaved = false;
	private boolean hasOpenedData = false;
	private JFrame frame;
	
	private File openTemplate;
	
	private JFileChooser fetchChooser(FileType type){
		
		fileChooser.setFileFilter(type.filter());
		
		return fileChooser;
	}
	
	public TemplateDesignerApp(){
		
		JMenuItem chooseSampleDataMenuOption = new JMenuItem("Choose Sample Data");
		chooseSampleDataMenuOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = fetchChooser(FileType.SAMPLE_DATA);
				
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				fc.setDialogTitle("Select A Properties File");
				int result = fc.showOpenDialog(editor.getView());
				if (JFileChooser.APPROVE_OPTION != result) {
					return;
				}
				
				File file = fc.getSelectedFile();
				
				try {
					editor.openFieldProperties(file);
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		
		JMenuItem newXMLTemplate = new JMenuItem("New");
		newXMLTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!changesAreDisposable()) return;
				
//				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//				fc.setMultiSelectionEnabled(false);
//				fc.setDialogTitle("Choose a PDF");
//				
//				int result = fc.showOpenDialog(editor.getView());
//				if (JFileChooser.APPROVE_OPTION != result) {
//					return;
//				}
//				
//				File file = fc.getSelectedFile();
				
//				try {
					editor.close(false);
//					editor.openTemplate(new FileInputStream(file));
					isUnsaved=true;
					editor.setEnabled(false);
					openTemplate = null;
//				}
//				catch (IOException ex) {
//					fail(ex);
//				}
			}
		});
		JMenuItem exitMenuItem = new JMenuItem("Quit");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!changesAreDisposable()) return;
				System.exit(0);
			}
		});
		JMenuItem fileOpenXmlTemplate = new JMenuItem("Open");
		fileOpenXmlTemplate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = fetchChooser(FileType.TEMPLATE);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				fc.setDialogTitle("Open Template");

				int result = fc.showOpenDialog(editor.getView());
				if (JFileChooser.APPROVE_OPTION != result) {
					return;
				}
				
				File file = fc.getSelectedFile();
				
				try {
					editor.openTemplateFromDisk(file);
					editor.setEnabled(true);
					isUnsaved=true;
					openTemplate = file;
					
					if(!hasOpenedData){
						File templateData = new File(file.getParentFile(), file.getName() + ".data.properties");
						File genericData = new File(file.getParentFile(), "data.properties");
						
						if(templateData.exists() && templateData.isFile()){
							editor.openFieldProperties(templateData);
						}else if(genericData.exists() && genericData.isFile()){
							editor.openFieldProperties(genericData);
						}
					}
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem saveMenuOption = new JMenuItem("Save");
		saveMenuOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					if(openTemplate!=null){
						System.out.println("Saving to " + openTemplate.getAbsolutePath());
						editor.saveTemplateToDisk(openTemplate);
						isUnsaved=false;
					}
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem saveAsMenuOption = new JMenuItem("Save As...");
		saveAsMenuOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = fetchChooser(FileType.TEMPLATE);

					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setMultiSelectionEnabled(false);
					fc.setDialogTitle("Save Template As");
					
					fc.showOpenDialog(editor.getView());

					File selection = fc.getSelectedFile();
					
					if(selection!=null){
						System.out.println("Saving to " + selection.getAbsolutePath());
						editor.saveTemplateToDisk(selection);
						isUnsaved=false;
					}
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem printPreviewOption = new JMenuItem("Print Preview");
		printPreviewOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					Printable printable = new PDFDocumentPrintable(editor.renderPreview());
					
					PrintServiceSelector selector = new PrintServiceSelector(frame);
					
					if (!selector.serviceSelected()) {
						return;
					}
					
					PrintService printService = selector.getSelectedService();
					
					PrinterJob printerJob = PrinterJob.getPrinterJob();
					printerJob.setPrintService(printService);
					printerJob.setPrintable(printable);
					
					printerJob.print();
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem savePreviewOption = new JMenuItem("Save Preview As...");
		savePreviewOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					JFileChooser fc = fetchChooser(FileType.PDF);

					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setMultiSelectionEnabled(false);
					fc.setDialogTitle("Save Preview As");
					
					fc.showOpenDialog(editor.getView());

					File selection = fc.getSelectedFile();
					
					if(selection!=null){
						System.out.println("Saving to " + selection.getAbsolutePath());
						
						byte[] renderedPdf = editor.renderPreview();
						FileOutputStream out = new FileOutputStream(selection);
						out.write(renderedPdf);
						out.close();
					}
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem fileOpenMappings = new JMenuItem("Load Mappings");
		fileOpenMappings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = fetchChooser(FileType.MAPPINGS);

				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				fc.setDialogTitle("Load Mappings");

				int result = fc.showOpenDialog(editor.getView());
				if (JFileChooser.APPROVE_OPTION != result) {
					return;
				}
				
				File file = fc.getSelectedFile();
				
				try {
					editor.openFieldMappings(file);
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem fileSelectPDF = new JMenuItem("Select PDF");
		fileSelectPDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = fetchChooser(FileType.PDF);

				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				
				fc.setDialogTitle("Select a New PDF");
				int result = fc.showOpenDialog(editor.getView());
				if (JFileChooser.APPROVE_OPTION != result) {
					return;
				}
				
				File file = fc.getSelectedFile();
				
				try {
					File tempFile = File.createTempFile(getClass().getName(), "mappings.xml");
					editor.saveFieldMappings(tempFile);
					editor.openPDF(new FileInputStream(file));
					editor.openFieldMappings(tempFile);
					editor.setEnabled(true);

				}catch (Exception ex) {
					fail(ex);
				}
			}
		});
		JMenuItem fileSaveFieldMappingsItem = new JMenuItem("Save Mappings");
		fileSaveFieldMappingsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					editor.saveFieldMappings();
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem fileSaveFieldMappingsAsItem = new JMenuItem("Save Mappings As");
		fileSaveFieldMappingsAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = fetchChooser(FileType.MAPPINGS);

				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				fc.setDialogTitle("Save Mappings");

				int result = fc.showOpenDialog(editor.getView());
				if (JFileChooser.APPROVE_OPTION != result) {
					return;
				}
				
				File file = fc.getSelectedFile();
				
				try {
					editor.saveFieldMappings(file);
				}
				catch (Exception ex) {
					fail(ex);
				}
			}
		});
		
		JMenuItem fileCloseItem = new JMenuItem("Close");
		fileCloseItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.close();
			}
		});
		
		JMenu fileMenu = new JMenu("File");
		
		fileMenu.add(newXMLTemplate);
		fileMenu.add(fileOpenXmlTemplate);
		fileMenu.add(saveMenuOption);
		fileMenu.add(saveAsMenuOption);
		fileMenu.add(new JSeparator());
		fileMenu.add(savePreviewOption);
		fileMenu.add(printPreviewOption);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitMenuItem);
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(fileSelectPDF);
		
		JMenu dataMenu = new JMenu("Data");
		dataMenu.add(chooseSampleDataMenuOption);
		
		if(rawMode){
			fileMenu.add(fileOpenMappings);
			fileMenu.add(fileSaveFieldMappingsItem);
			fileMenu.add(fileSaveFieldMappingsAsItem);
			fileMenu.add(fileCloseItem);
		}
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(dataMenu);
		
		frame = new JFrame("Template Designer");
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(editor.getView());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				if(changesAreDisposable()){
					System.exit(0);
				}
			}
		});
		
		editor.setEnabled(false);
		frame.setVisible(true);
		
	}
	
	private boolean changesAreDisposable(){
		boolean throwAway = true;
		if(isUnsaved){
			int selection = JOptionPane.showConfirmDialog(frame, "Are you sure?  Unsaved changes will be lost.");
			if(selection!=JOptionPane.OK_OPTION){
				throwAway = false;
			}
		}
		return throwAway;
	}
	
	private void fail(Exception ex){
		ex.printStackTrace();
		JOptionPane.showMessageDialog(frame, "Error:" + ex.getMessage());
	}
}
