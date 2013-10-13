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
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JToolBar.Separator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBContext;

import org.bouncycastle.crypto.RuntimeCryptoException;

import com.moss.jaxbhelper.JAXBHelper;
import com.moss.pdf.template.api.FontName;
import com.moss.pdf.template.api.PropertyMapping;
import com.moss.pdf.template.api.TextAlignment;
import com.moss.pdf.template.core.Parser;
import com.moss.pdf.template.core.Renderer;
import com.moss.pdf.template.xml.PdfOverlay;
import com.moss.pdf.template.xml.Template;
import com.moss.pdf.genericrenderer.AutoRendererSelector;
import com.moss.pdf.genericrenderer.GenericPdfDocument;
import com.moss.pdf.genericrenderer.GenericPdfException;
import com.moss.pdf.genericrenderer.GenericPdfRenderer;
import com.moss.pdf.genericrenderer.icepdf.IcePdfRenderer;
import com.moss.pdf.genericrenderer.sun.SunPdfRenderer;

@SuppressWarnings("serial")
public class PdfTemplateEditor extends JPanel {

	private GenericPdfRenderer renderer = new IcePdfRenderer();
	
	private final GenericPdfRenderer[] renderers = new GenericPdfRenderer[]{
			new SunPdfRenderer(),
			new IcePdfRenderer()
	};
	private static final float POINTS_IN_A_CM = 28.3464567f;
	
	private final PdfTemplateEditorView view;
	private final List<Page> editPages;
	private final List<Page> previewPages;
	
	private GenericPdfDocument document;
	private byte[] pdfData;
	private Parser parser;
	
	private File previewScratchFile;
	
	private final List<PdfEditorListener> listeners = new ArrayList<PdfEditorListener>();
	
	public PdfTemplateEditor(Action ... actions){
		this();
		Separator separator = new JToolBar.Separator();
		separator.setOrientation(SwingConstants.VERTICAL);
		view.buttonPanel().add(separator);
		for (Action action : actions) {
			view.buttonPanel().add(new JButton(action));
		}
	}
	
	public PdfTemplateEditor() {
		try {
			previewScratchFile = File.createTempFile("pdftemplatepreview", "scratch");
			previewScratchFile.deleteOnExit();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
		setLayout(new BorderLayout());
		view = new PdfTemplateEditorView();
		add(view);
		
		editPages = new ArrayList<Page>();
		previewPages = new ArrayList<Page>();
		
		view.getButtonIn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float zoom = getVisiblePagePanel().getZoom();
				getVisiblePagePanel().setZoom(zoom + .10f);
			}
		});
		
		view.getButtonOut().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float zoom = getVisiblePagePanel().getZoom();
				getVisiblePagePanel().setZoom(zoom - .10f);
			}
		});
		
		view.getButtonNext().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Page currentPage = getVisiblePagePanel().getPage();
				
				if (getVisiblePages().isEmpty() || getVisiblePages().get(getVisiblePages().size() - 1) == currentPage) {
					return;
				}
				else if (currentPage == null) {
					getVisiblePagePanel().setPage(getVisiblePages().get(0));
				}
				else {
					Page newPage = getVisiblePages().get(currentPage.pageNumber());
					getVisiblePagePanel().setPage(newPage);
				}
			}
		});
		
		view.getButtonPrevious().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Page currentPage = getVisiblePagePanel().getPage();
				
				if (getVisiblePages().isEmpty() || getVisiblePages().get(0) == currentPage) {
					return;
				}
				else if (currentPage == null) {
					getVisiblePagePanel().setPage(getVisiblePages().get(0));
				}
				else {
					Page newPage = getVisiblePages().get(currentPage.pageNumber() - 2);
					getVisiblePagePanel().setPage(newPage);
				}
			}
		});
		
		view.getButtonCreate().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ParserReference ref = new ParserReference() {
					public Parser getParser() {
						return parser;
					}
				};

				TextComponent component = new TextComponent(
						new Point2D.Float(100, 100), 
						FontName.HELVETICA, 
						10, 
						TextAlignment.LEFT, 
						null, 
						ref
						);
				
				TextComponentEditor d = new TextComponentEditor("Add Field", component, ref);
				d.show();
				
				PageComponent c = d.getComponent();
				
				if (c != null) {
					view.getPagePanel().addComponent(c);
				}
			}
		});
		
		view.getPagePanel().setComponentEditor(new ComponentEditor(){
			public void edit(PageComponent component) {
				System.out.println("Edit");
				ParserReference ref = new ParserReference() {
					public Parser getParser() {
						return parser;
					}
				};
				
				TextComponentEditor d = new TextComponentEditor("Edit Field", (TextComponent)component, ref);
				d.show();

				view.getPreviewPanel().reRender();
			}
		});
		
		view.getScrollPane().getViewport().setBackground(Color.DARK_GRAY);
//		view.getScrollPane().setFocusable(false);
//		view.getScrollPane().getViewport().setFocusable(false);
		disableArrowKeys(view.getScrollPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT));
		view.setFocusable(false);
		
		view.getPreviewScrollpane().getViewport().setBackground(Color.DARK_GRAY);
		
		view.getTabbedPane().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				try {
					if (view.getTabbedPane().getSelectedIndex() == 1) {
						renderPreview();
					}else{
						view.getPagePanel().requestFocusInWindow();
					}
				} catch (GenericPdfException e1) {
					JOptionPane.showMessageDialog(view, "Error:" + e1.getMessage());
				}
			}
		});
		
		this.parser = new DefaultParser();
	}
	
	
	/**
	 * Opens the template instance (pdf, mappings and all)
	 */
	public synchronized void setTemplate(Template template) throws Exception {
		openPDF(new ByteArrayInputStream(template.getPdf()));
		setMappings(template.getOverlay().getMappings());
		pdfData = template.getPdf();
		
		fireChanged();
	}
	
	/**
	 * convenience method which handles the deserialization of a Template xml document
	 */
	public synchronized void setTemplate(InputStream templateFile) throws Exception {
		JAXBContext context = JAXBContext.newInstance(Template.class);
		JAXBHelper helper = new JAXBHelper(context);
		
		Template template = helper.readFromStream(templateFile);
		
		setTemplate(template);
	}
	
	/**
	 * Gets the template instance (pdf, mappings, and all)
	 */
	public synchronized Template getTemplate() throws Exception {
		Template template = new Template();
		template.setOverlay(new PdfOverlay());
		template.getOverlay().getMappings().addAll(buildPropertyMappings());
		template.setPdf(pdfData);
		return template;
	}
	
	

	static void disableArrowKeys(InputMap im) {
		String[] keystrokeNames = {"UP","DOWN","LEFT","RIGHT"};
		for(int i=0; i<keystrokeNames.length; ++i)
			im.put(KeyStroke.getKeyStroke(keystrokeNames[i]), "none");
	}
	public void setEnabled(boolean enabled){
		view.getButtonCreate().setEnabled(enabled);
		view.getButtonIn().setEnabled(enabled);
		view.getButtonNext().setEnabled(enabled);
		view.getButtonOut().setEnabled(enabled);
		view.getButtonPrevious().setEnabled(enabled);
		view.getTabbedPane().setEnabled(enabled);
	}
	
	protected JPanel getView() {
		return view;
	}
	
	public synchronized void setParser(Parser parser) {
		this.parser = parser;
		view.getPagePanel().repaint();
	}
	
	public synchronized byte[] getPDF(){
		return pdfData;
	}
	
		
	public synchronized void openPDF(InputStream pdfInput) throws IOException, GenericPdfException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		byte[] buffer = new byte[1024 * 10]; //10k buffer
		for(int numRead = pdfInput.read(buffer); numRead!=-1; numRead = pdfInput.read(buffer)){
			out.write(buffer, 0, numRead);
		}
		
		pdfInput.close();
		out.close();

		this.pdfData = out.toByteArray();
		openPDF(pdfData);
	}
	
	public synchronized void openPDF(byte[] pdfData) {
		
		editPages.clear();
		
		
		System.out.println("Opening " + (pdfData.length/1024) + "k pdf)");
		
		this.renderer = new AutoRendererSelector().select(pdfData);
		
		try {
			document = renderer.read(pdfData);
			for (int i=1; i<=document.numPages(); i++) {
				Page page = new Page(document.page(i), i);
				page.addListener(new TemplateContentsListener());
				editPages.add(page);
				System.out.println("page " + i);
			}

		} catch (GenericPdfException e) {
			throw new RuntimeException("Error reading the pdf: " + e.getMessage(), e);
		}
		
		if (!editPages.isEmpty()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					view.getPagePanel().setPage(editPages.get(0));					
				}
			});
		}
	}
	
	public synchronized void setMappings(List<PropertyMapping> mappings){
		
		for (Page p : editPages) {
			p.clear();
		}
		
		for (PropertyMapping m : mappings) {
			
			Page page = editPages.get(m.getPageNumber() - 1);
			
			float x = m.getX() * POINTS_IN_A_CM;
			float y;
			{
				float pdfY = m.getY() * POINTS_IN_A_CM;
				
				float pageHeight = page.height();
				float baseLine = pageHeight - pdfY;
				
				float fontHeight;
				{
					String family = null;
					int style = Font.PLAIN;
					
					if (FontName.COURIER == m.getFontName()) {
						family = "Courier";
					}
					else if (FontName.COURIER_BOLD == m.getFontName()) {
						family = "Courier";
					}
					else if (FontName.COURIER_BOLD_OBLIQUE == m.getFontName()) {
						family = "Courier";
					}
					else if (FontName.COURIER_OBLIQUE == m.getFontName()) {
						family = "Courier";
					}
					else if (FontName.HELVETICA == m.getFontName()) {
						family = "Helvetica";
					}
					else if (FontName.HELVETICA_BOLD == m.getFontName()) {
						family = "Helvetica";
					}
					else if (FontName.HELVETICA_BOLD_OBLIQUE == m.getFontName()) {
						family = "Helvetica";
					}
					else if (FontName.HELVETICA_OBLIQUE == m.getFontName()) {
						family = "Helvetica";
					}
					else if (FontName.TIMES_BOLD == m.getFontName()) {
						family = "Times-Roman";
					}
					else if (FontName.TIMES_BOLD_ITALIC == m.getFontName()) {
						family = "Helvetica";
					}
					else if (FontName.TIMES_ITALIC == m.getFontName()) {
						family = "Helvetica";
					}
					else if (FontName.TIMES_ROMAN == m.getFontName()) {
						family = "Helvetica";
					}
					
					if (family == null) {
						throw new RuntimeException("Cannot determine the family for this font: " + m.getFontName());
					}
					
					Font font = new Font(family, style, m.getFontSize());
					
					BufferedImage i = new BufferedImage(100, 100, ColorSpace.TYPE_RGB);
					Graphics2D g = (Graphics2D) i.getGraphics();
					g.setFont(font);
					FontMetrics metrics = g.getFontMetrics();
					
					fontHeight = metrics.getHeight();
				}
				
				y = baseLine - fontHeight;
			}
		
			TextComponent c = new TextComponent(
				new Point2D.Float(x, y),
				m.getFontName(),
				m.getFontSize(),
				m.getAlignment(),
				m.getExpr(),
				new ParserReference() {
					public Parser getParser() {
						return parser;
					}
				}
			);
			
			page.add(c);
		}
		
		view.getPagePanel().repaint();
	}
	

	
	public PropertyMappings getMappings(){
		PropertyMappings mappings = new PropertyMappings();
		mappings.getMapping().addAll(buildPropertyMappings());
		return mappings;
	}
	
	public synchronized void addComponent(final PageComponent component) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				view.getPagePanel().addComponent(component);
			}
		});
	}
	
	public synchronized void close() {
		close(true);
	}
	
	public synchronized void close(boolean propertiesToo) {
		pdfData = null;
		editPages.clear();
		view.getPagePanel().setPage(null);
		view.getPreviewPanel().setPage(null);
		if(propertiesToo) parser = new DefaultParser();
		
		fireChanged();
	}
	
	public synchronized byte[] renderPreview() throws GenericPdfException {
		
		view.getPreviewPanel().setPage(null);
		
		List<PropertyMapping> mappings = buildPropertyMappings();
		
		GenericPdfDocument previewDocument;
		byte[] previewData;
		try {
			Renderer r = new Renderer(parser);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			r.render(new ByteArrayInputStream(pdfData), mappings, out);
			
			previewData = out.toByteArray();
			
			previewDocument = renderer.read(previewData);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		previewPages.clear();
		
		for (int i=1; i<=previewDocument.numPages(); i++) {
			previewPages.add(new Page(previewDocument.page(i), i));
		}
		
		if (!previewPages.isEmpty()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					view.getPreviewPanel().setPage(previewPages.get(0));					
				}
			});
		}
		
		return previewData;
	}
	
	public synchronized void setFieldProperties(final Properties p) {
		
		Parser parser = new Parser() {
			public Object eval(String expr) throws ParseException {
				return p.get(expr.trim());
			}
			
			public List<String> getValidExpressions() {
				List<String> expressions = new ArrayList<String>();
				
				for (Object o : p.keySet()) {
					expressions.add((String)o);
				}
				
				return expressions;
			}
		};
		
		setParser(parser);
		
		System.out.println("loaded " + p.size() + " properties");
	}
	

	
	private List<PropertyMapping> buildPropertyMappings() {
		
		List<PropertyMapping> mappings = new ArrayList<PropertyMapping>();
		
		for (Page page : editPages) {
			for (PageComponent c : page) {
				
				if (! (c instanceof TextComponent)) {
					continue;
				}
				
				TextComponent t = (TextComponent)c;
				PropertyMapping m = new PropertyMapping();
				
				m.setAlignment(t.getAlignment());
				m.setExpr(t.getExpr());
				m.setFontName(t.getFont());
				m.setFontSize(t.getFontSize());
				m.setPageNumber(page.pageNumber());
				
				float x = t.getRect().x / POINTS_IN_A_CM;
				float y;
				{
					float baseLine = t.getRect().y + t.getRect().height;
					float pageHeight = page.height();
					y = (pageHeight - baseLine) / POINTS_IN_A_CM;
				}
				
				m.setX(x);
				m.setY(y);

				mappings.add(m);
			}
		}
		
		return mappings;
	}
	
	private class DefaultParser implements Parser {
		public Object eval(String expr) throws ParseException {
			return "NO_PARSER_DEFINED";
		}

		public List<String> getValidExpressions() {
			return Collections.EMPTY_LIST;
		}
	}
	
	private PagePanel getVisiblePagePanel() {
		
		int index = view.getTabbedPane().getSelectedIndex();
		
		if (index == 0) {
			return view.getPagePanel();
		}
		else if (index == 1) {
			return view.getPreviewPanel();
		}
		else {
			return null;
		}
	}
	
	private List<Page> getVisiblePages() {
		
		int index = view.getTabbedPane().getSelectedIndex();
		
		if (index == 0) {
			return editPages;
		}
		else if (index == 1){
			return previewPages;
		}
		else {
			return null;
		}
	}
	
	public void addListener(PdfEditorListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PdfEditorListener l) {
		listeners.remove(l);
	}
	
	private class TemplateContentsListener implements PageListener {

		public void componentAdded(Page page, PageComponent component) {
			fireChanged();
		}

		public void componentChanged(Page page, PageComponent component) {
			fireChanged();
		}

		public void componentRemoved(Page page, PageComponent component) {
			fireChanged();
		}
	}
	
	protected void fireChanged() {
		for (PdfEditorListener l : listeners) {
			l.changed();
		}
	}
}

