package net.phyloviz.loadmlst.xml;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Parser {

	private static Parser parser = null;
	private static Document document = null;
	private static ArrayList<String[]> alDatabases = null;

	private Parser() {
		loadXML();
	}

	private void loadXML() {
		String endPoint = org.openide.util.NbBundle.getMessage(
				Parser.class, "Parser.endpoint");
		try {
			SAXBuilder builder = new SAXBuilder();
			document = builder.build(new URL(endPoint));
			// TODO exception out of net
		} catch (IOException ex) {
			// No internet connection
			document = null;
		} catch (JDOMException e) {
			// JDOM Error Parsing XML
			document = null;
		}
	}

	public static Parser getParser() {
		if (parser == null) {
			parser = new Parser();
		}
		return parser;
	}

	public ArrayList<String[]> getDatabaseList() {
		if (document == null) {
			return null;
		}
		if (alDatabases != null) {
			return alDatabases;
		}

		alDatabases = new ArrayList<String[]>();
		for (Object o : document.getRootElement().getChildren("species")) {
			Element species = (Element) o;
			String[] sa = new String[2];
			sa[0] = species.getChild("name").getText();
			sa[1] = species.getChild("mlst").getChild("database").getChild("owner").getText();
//			sa[2] = species.getChild("mlst").getChild("database").getChild("profiles").getText();
//			sa[3] = species.getChild("mlst").getChild("database").getChild("profiles").getChild("count").getText();
			alDatabases.add(sa);
		}
		return alDatabases;
	}

	public int getProfileCount(int index) {
		int iRet = 0;
		if (document != null) {
			Element e = (Element) document.getRootElement().getChildren("species").get(index);
			String s = e.getChild("mlst").getChild("database").getChild("profiles").getChild("count").getText();
			try {
				iRet = Integer.parseInt(s);
			} catch (NumberFormatException nfe) {
			}
		}
		return iRet;
	}

	public String getProfileURL(int index) {
		if (document == null) {
			return null;
		}
		Element e = (Element) document.getRootElement().getChildren("species").get(index);
		return e.getChild("mlst").getChild("database").getChild("profiles").getChild("url").getText();
	}

	public ArrayList<String> getLoci(int index) {
		ArrayList<String> alRet = new ArrayList<String>();
		if (document != null) {
			Element e = (Element) document.getRootElement().getChildren("species").get(index);
			Element loci = e.getChild("mlst").getChild("database").getChild("loci");
			for (Object o : loci.getChildren("locus")) {
				alRet.add(((Element) o).getChild("name").getText());
			}
		}
		return alRet;
	}

	public void reset() {
		parser = null;
		document = null;
		alDatabases = null;
	}
}
