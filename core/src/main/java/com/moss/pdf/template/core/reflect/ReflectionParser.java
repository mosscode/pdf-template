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
package com.moss.pdf.template.core.reflect;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moss.pdf.template.core.Parser;

public class ReflectionParser implements Parser {
	
	private final Map<String, Object> bindings;
	
	public ReflectionParser() {
		bindings = new HashMap<String, Object>();
	}

	public void bind(String name, Object object) {
		bindings.put(name, object);
	}
	
	public Object eval(String expr) throws ParseException {
		
		Pattern pathRoot = Pattern.compile("(^\\w+)");
		Pattern pathSegment = Pattern.compile("\\.(\\w+)(?:\\[([a-zA-Z0-9 \\.]+)\\])?");
		
		/*
		 * locate the root path
		 */
		
		Matcher m = pathRoot.matcher(expr);
		
		if (!m.find()) {
			throw new ParseException("Could not match initial path root: " + expr, 0);
		}
		
		String boundName = m.group();
		
		Object boundObject = bindings.get(boundName);
		
		if (boundObject == null) {
			throw new ParseException("Name not bound '" + boundName + "': " + expr, 0);
		}
		
		/*
		 * find the specific property path requested
		 */
		
		m.usePattern(pathSegment);
		
		Object property = boundObject;
		
		while (m.find() && property != null) {
			
			String propertyName = m.group(1);
			String keyName = m.group(2);
			
			if (propertyName == null) {
				throw new ParseException("Cannot determine the next path element: " + expr, 0);
			}
			
			String methodName = getMethodName(propertyName);
			
			try {
				Method method = property.getClass().getDeclaredMethod(methodName);
				property = method.invoke(property);
			}
			catch (NoSuchMethodException ex) {
				throw new RuntimeException("Expression " + expr + " resolves to " + property.getClass().getName() + "." + methodName + "(), but this method does not exist", ex);
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			if (keyName != null) {
				
				if (property instanceof MapProvider) {
					MapProvider mapProvider = (MapProvider) property;
					property = mapProvider.get(keyName);
				}
				else {
					throw new ParseException(propertyName + " does not refer to an instance of " + MapProvider.class.getName() + ", cannot retrive value for " + keyName, 0);
				}
			}
		}
		
		return property;
	}
	
	public List<String> getValidExpressions() {
		return Collections.EMPTY_LIST; // not supported yet
	}
	
	private String getMethodName(String propertyName) {
		String first = propertyName.substring(0, 1).toUpperCase();
		String last = propertyName.substring(1);
		return "get" + first + last;
	}
}
