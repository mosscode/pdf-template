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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.xml.bind.JAXBContext;

import com.moss.jaxbhelper.JAXBHelper;
import com.moss.pdf.template.xml.Template;

@SuppressWarnings("serial")
public class FileBasedPdfTemplateEditor extends PdfTemplateEditor {

	private File openedMappingsFile;
	
	public synchronized void saveTemplateToDisk(File file) throws Exception {
		Template template = getTemplate();
		JAXBContext context = JAXBContext.newInstance(Template.class);
		JAXBHelper helper = new JAXBHelper(context);
		helper.writeToFile(helper.writeToXmlString(template), file);
	}
	
	public synchronized void openTemplateFromDisk(File templateFile) throws Exception {
		JAXBContext context = JAXBContext.newInstance(Template.class);
		JAXBHelper helper = new JAXBHelper(context);
		
		Template template = helper.readFromFile(templateFile);
		
		setTemplate(template);
	}
	public synchronized void saveFieldMappings(File file) throws Exception {
		PropertyMappings mappings = getMappings();
		mappings.write(new FileOutputStream(file));
		
		if (openedMappingsFile == null) {
			openedMappingsFile = file;
		}
	}
	public synchronized void saveFieldMappings() throws Exception {
		PropertyMappings mappings = getMappings();
		
		FileOutputStream out = new FileOutputStream(openedMappingsFile);
		mappings.write(out);
	}
	public synchronized void openFieldMappings(File f) throws Exception {
		
		setMappings(PropertyMappings.read(new FileInputStream(f)).getMapping());
		
		openedMappingsFile = f;
		
		fireChanged();
	}
	@Override
	public synchronized void close() {
		super.close();
		openedMappingsFile = null;
	}
	
	public synchronized void openFieldProperties(File file) throws Exception {
		System.out.println("Loading data from " + file.getAbsolutePath());
		
		final Properties p = new Properties();
		p.load(new FileInputStream(file));
		
		setFieldProperties(p);
	}
}
