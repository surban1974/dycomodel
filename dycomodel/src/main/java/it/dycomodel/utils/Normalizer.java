/**
* Creation date: (07/04/2006)
* @author: Svyatoslav Urbanovych svyatoslav.urbanovych@gmail.com 
*/

/********************************************************************************
*
*	Copyright (C) 2005  Svyatoslav Urbanovych
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*********************************************************************************/

package it.dycomodel.utils;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;

public class Normalizer {
	
public Normalizer() {
	super();
}
public static Document readXML(String uriXML, boolean valid) throws Exception{
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setValidating(valid);
	if(uriXML.toUpperCase().trim().indexOf("HTTP:")==-1){
		return  dbf.newDocumentBuilder().parse(new File(uriXML));
	}else return  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uriXML);
}
public static Document readXMLData(String dataXML, boolean valid) throws Exception{
	if(dataXML==null) return null;
	ByteArrayInputStream xmlSrcStream = new	ByteArrayInputStream(dataXML.getBytes());
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(valid);
	return dbf.newDocumentBuilder().parse(xmlSrcStream);
}

public static Document readXML(String uriXML) throws Exception{
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setValidating(false);
	if(uriXML.toUpperCase().trim().indexOf("HTTP:")==-1){
		return  dbf.newDocumentBuilder().parse(new File(uriXML));
	}else return  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uriXML);
}
public static Document readXMLData(String dataXML) throws Exception{
	if(dataXML==null) return null;
	ByteArrayInputStream xmlSrcStream = new	ByteArrayInputStream(dataXML.getBytes());
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
	return  dbf.newDocumentBuilder().parse(xmlSrcStream);
}
public static Document readXMLData(byte[] dataXML) throws Exception{
	if(dataXML==null) return null;
	ByteArrayInputStream xmlSrcStream = new	ByteArrayInputStream(dataXML);
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
	return  dbf.newDocumentBuilder().parse(xmlSrcStream);
}	
public static String removeNonUtf8CompliantCharacters( final String inString ) {
	if (null == inString ) return null;
	byte[] byteArr = inString.getBytes();
	for ( int i=0; i < byteArr.length; i++ ) {
	byte ch= byteArr[i];
	// remove any characters outside the valid UTF-8 range as well as all control characters
	// except tabs and new lines
	if ( !( (ch > 31 && ch < 253 ) || ch == '\t' || ch == '\n' || ch == '\r') ) {
	byteArr[i]=' ';
	}
	}
	return new String( byteArr );
	}

public static String normalXML (String input, String charSet) {
	
	if (input==null) return input;
	
	if(input.indexOf("<![CDATA[")==0){
		return input;
	}

/*	
	try{
		if(charSet==null) input = new String(input.getBytes(),"utf8");
		else input = new String(input.getBytes(),charSet);
	}catch(Exception e){
		input="";
	}
*/
	
	try{
		if(charSet!=null) input = new String(input.getBytes(),charSet);
	}catch(Exception e){

	}

	String result="";
	if (input.indexOf("&")>-1 ||
		input.indexOf("\\")>-1 ||
		input.indexOf(">")>-1 ||
		input.indexOf("<")>-1 ||
		input.indexOf("\"")>-1) { 

		for (int i=0;i<input.length();i++) {
			if (input.charAt(i)=='&') result+="&amp;";
//			else if (input.charAt(i)=='\'') result+="&apos;";
			else if (input.charAt(i)=='>') result+="&gt;";
			else if (input.charAt(i)=='<') result+="&lt;";
			else if (input.charAt(i)=='"') result+="&quot;";
			else result+=input.charAt(i);
		}
		return result;
	}
	else 
		return input;
}

public static String normalASCII(String input){
	if(input==null || input.length()==0) return "";
	String result="";
	for(int i=0;i<input.length();i++){
		char c = input.charAt(i); 
		int ascii = (int)c;
//		if(ascii!=19 && ascii!=7 && ascii!=25)
//			result+="&#"+ascii+";";
		
		if ((ascii == 0x9) ||
            (ascii == 0xA) ||
            (ascii == 0xD) ||
            ((ascii >= 0x20) && (ascii <= 0xD7FF)) ||
            ((ascii >= 0xE000) && (ascii <= 0xFFFD)) ||
            ((ascii >= 0x10000) && (ascii <= 0x10FFFF))){
			result+="&#"+ascii+";";
        }		
		
	}
	
	return result;
	
}

public static String normalHTML(String input, String charSet) {	
	if (input==null) return "";
	
	try{
		if(charSet!=null) input = new String(input.getBytes(),charSet);
	}catch(Exception e){

	}

	String result="";
	if (input.indexOf("&")>-1 ||
		input.indexOf("\\")>-1 ||
		input.indexOf(">")>-1 ||
		input.indexOf("<")>-1 ||
		input.indexOf("\"")>-1) { 

		for (int i=0;i<input.length();i++) {
			if (input.charAt(i)=='&') result+="&amp;";
			else if (input.charAt(i)=='\'') result+="&apos;";
			else if (input.charAt(i)=='>') result+="&gt;";
			else if (input.charAt(i)=='<') result+="&lt;";
			else if (input.charAt(i)=='"') result+="&quot;";
			else result+=input.charAt(i);
		}
		return result;
	}
	else 
		return input;
}

public static String spaces(int level){
	return new String(new char[level*5]).replace('\0', ' ');
}

}

