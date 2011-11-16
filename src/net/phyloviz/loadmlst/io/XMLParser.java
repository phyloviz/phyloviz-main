package net.phyloviz.loadmlst.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLParser {

	private static XMLParser parser = null;
	private static Document document = null;
	private static ArrayList<String[]> alDatabases = null;

	private XMLParser() {
		loadXML();
	}

	private void loadXML() {
		String endPoint = org.openide.util.NbBundle.getMessage(
				XMLParser.class, "MLSTDBs.endpoint");
		try {
			SAXBuilder builder = new SAXBuilder();
			document = builder.build(new URL(endPoint));
		} catch (IOException e) {
			Logger.getLogger(XMLParser.class.getName()).log(Level.WARNING,
					e.getLocalizedMessage());
			// No internet connection
			document = null;
		} catch (JDOMException e) {
			Logger.getLogger(XMLParser.class.getName()).log(Level.WARNING,
					e.getLocalizedMessage());
			// JDOM Error Parsing XML
			document = null;
		}
	}

	public static boolean hasConnection() {
		boolean bHas = false;
		String url = org.openide.util.NbBundle.getMessage(
				XMLParser.class, "Connection.defaultURL");
		try {
			URL newURL = new URL(url);
			InputStream is = newURL.openStream();
			is.close();
			bHas = true;
		} catch (IOException e) {
			Logger.getLogger(XMLParser.class.getName()).log(Level.WARNING,
					e.getLocalizedMessage());
		}
		return bHas;
	}

	public static XMLParser getParser() {
		if (parser == null) {
			parser = new XMLParser();
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

	public static boolean fileExists(String file) {
		File fd = new File(file);
		return fd.exists();
	}

	private String readSequence(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line, s = "";
		while ((line = br.readLine()) != null) {
			s += line + "\n";
		}
		return s.trim();
	}

	public String getLocusSequence(String file) {
		String s = null;

		try {
			FileInputStream fstream = new FileInputStream(file);
			s = readSequence(fstream);
		} catch (IOException ex) {
			Logger.getLogger(XMLParser.class.getName()).log(Level.WARNING,
					ex.getLocalizedMessage());
		}
		return s;
	}

	public String getLocusSequence(int iDB, int iLocus) {
		if (document == null) {
			return null;
		}
		Element e = (Element) document.getRootElement().getChildren("species").get(iDB);
		Element loci = e.getChild("mlst").getChild("database").getChild("loci");
		Element locus = (Element) loci.getChildren("locus").get(iLocus);
		String s = null;

		try {
			URL url = new URL(locus.getChild("url").getText());
			s = readSequence(url.openStream());
		} catch (MalformedURLException ex) {
			Logger.getLogger(XMLParser.class.getName()).log(Level.WARNING,
					ex.getLocalizedMessage());
		} catch (IOException ex) {
			Logger.getLogger(XMLParser.class.getName()).log(Level.WARNING,
					ex.getLocalizedMessage());
		}
		return s;
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
