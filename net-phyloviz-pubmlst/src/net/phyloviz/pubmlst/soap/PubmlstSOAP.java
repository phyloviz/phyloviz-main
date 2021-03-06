/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.phyloviz.pubmlst.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

public class PubmlstSOAP {

	private String endpoint;
	private final int WAIT = 200;//500;
//	private final int TIMEOUT = 2000;

	public PubmlstSOAP() {
		endpoint = org.openide.util.NbBundle.getMessage(
				PubmlstSOAP.class, "PubmlstSOAP.endpoint");
	}

	protected static class AlleleNumber {

		private String locus;
		private int id;

		public AlleleNumber(String locus, int id) {
			this.locus = locus;
			this.id = id;
		}

		public java.lang.String getLocus() {
			return locus;
		}

		public void setLocus(String locus) {
			this.locus = locus;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

	public String getProfile(String database, int st) {
		String sProfile = null;
		boolean bTimeOut = false;
		do {
			try {
				Service service = new Service();
				Call call = (Call) service.createCall();
				call.setTargetEndpointAddress(new java.net.URL(endpoint));
				call.registerTypeMapping(AlleleNumber.class, new QName(
						"http://pubmlst.org/MLST", "alleleNumber"),
						BeanSerializerFactory.class, BeanDeserializerFactory.class,
						false);
				OperationDesc oper = new OperationDesc();
				oper.addParameter(new ParameterDesc(new QName("", "database"),
						ParameterDesc.IN, new QName(
						"http://www.w3.org/2001/XMLSchema", "string"),
						String.class, false, false));
				oper.addParameter(new ParameterDesc(new QName("", "ST"),
						ParameterDesc.IN, new QName(
						"http://www.w3.org/2001/XMLSchema", "int"),
						int.class, false, false));
				ParameterDesc param = new ParameterDesc(new QName("", "profile"),
						ParameterDesc.OUT, new QName("http://pubmlst.org/MLST",
						"profile"), AlleleNumber[].class, false, false);
				param.setItemQName(new QName("", "alleleNumber"));
				oper.addParameter(param);
// TODO: ask Keith to correct this (test with bcc-st-3)
//			param = new ParameterDesc(new QName("", "complex"),
//					ParameterDesc.OUT, new QName(
//					"http://www.w3.org/2001/XMLSchema", "string"),
//					String.class, false, false);
//			param.setOmittable(true);
				oper.addParameter(param);
				oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
				call.setOperation(oper);
				call.setOperationName(new QName("http://pubmlst.org/MLST",
						"getProfile"));
//				call.setTimeout(TIMEOUT);
				call.invoke(new Object[]{database, st});
				Map output = call.getOutputParams();
				AlleleNumber[] alleles = (AlleleNumber[]) output.get(new QName("",
						"profile"));
				sProfile = "" + st;
				for (int i = 0; i < alleles.length; i++) {
					sProfile += "\t" + alleles[i].getId();
				}
// Same as above: Ask Keith
//			String complex = ((String) output.get(new QName("", "complex")));
//			if (complex != null) {
//				sProfile += complex;
//			}
			} catch (ServiceException e) {
				Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
						"ServiceException: " + e.toString());
			} catch (MalformedURLException e) {
				Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
						"MalformedURLException: " + e.toString());
			} catch (RemoteException e) {
				Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
						"RemoteException: " + e.toString());
				if (e.toString().contains("java.net.ConnectException")
						|| e.toString().contains("java.net.SocketTimeoutException")) {
					bTimeOut = true;
					try {
						Thread.sleep(WAIT);
					} catch (InterruptedException ex) {
					}
				}
			}
		} while (bTimeOut);
		return sProfile;
	}

	public HashMap<String, String> getIsolate(String database, int isolate) {
		HashMap<String, String> hmRet = new HashMap<String, String>();
		boolean bTimeOut = false;
		do {
			try {
				Service service = new Service();
				Call call = (Call) service.createCall();
				call.setTargetEndpointAddress(new java.net.URL(endpoint));
				call.setOperationName(new QName("http://pubmlst.org/MLST/",
						"getIsolate"));
				call.addParameter("database", org.apache.axis.Constants.XSD_STRING,
						javax.xml.rpc.ParameterMode.IN);
				call.addParameter("id", org.apache.axis.Constants.XSD_INT,
						javax.xml.rpc.ParameterMode.IN);
				call.setReturnType(org.apache.axis.Constants.SOAP_VECTOR);
//				call.setTimeout(TIMEOUT);
				Vector ret = (Vector) call.invoke(new Object[]{database, isolate});
				for (int i = 0; i < ret.size(); i++) {
					Vector v = (Vector) ret.get(i);
					hmRet.put((String) v.get(0), "" + v.get(1));
				}
			} catch (ServiceException e) {
				Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
						"ServiceException: " + e.toString());
			} catch (MalformedURLException e) {
				Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
						"MalformedURLException: " + e.toString());
			} catch (RemoteException e) {
				Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
						"RemoteException: " + e.toString());
				if (e.toString().contains("java.net.ConnectException")
						|| e.toString().contains("java.net.SocketTimeoutException")) {
					bTimeOut = true;
					try {
						Thread.sleep(WAIT);
					} catch (InterruptedException ex) {
					}
				}
			}
		} while (bTimeOut);
		return hmRet;
	}

	public Vector getDatabaseList() {
		Vector ret = null;
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("http://pubmlst.org/MLST/",
					"getDatabaseList"));
			call.setReturnType(org.apache.axis.Constants.SOAP_VECTOR);
			ret = (Vector) call.invoke(new Object[]{});
		} catch (Exception e) {
			Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
					"Exception: " + e.toString());
		}
		return ret;
	}

	public int getProfileCount(String database) {
		int iRet = 0;
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("http://pubmlst.org/MLST/",
					"getProfileCount"));
			iRet = (Integer) call.invoke(new Object[]{database});
		} catch (Exception e) {
			Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
					"Exception: " + e.toString());
		}
		return iRet;
	}

	public int getIsolateCount(String database) {
		int iRet = 0;
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("http://pubmlst.org/MLST/",
					"getIsolateCount"));
			iRet = (Integer) call.invoke(new Object[]{database});
		} catch (Exception e) {
			Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
					"Exception: " + e.toString());
		}
		return iRet;
	}

	public ArrayList<String> getLocusList(String database) {
		ArrayList<String> alRet = new ArrayList<String>();
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("http://pubmlst.org/MLST/",
					"getLocusList"));
			ArrayList ret = (ArrayList) call.invoke(new Object[]{database});
			for (int i = 0; i < ret.size(); i++) {
				alRet.add((String) ret.get(i));
			}
		} catch (Exception e) {
			Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
					"Exception: " + e.toString());
		}
		return alRet;
	}

	public String[] getIsolateFields(String database) {
		String[] saRet = null;
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setOperationName(new QName("http://pubmlst.org/MLST/",
					"getIsolateFields"));
			call.addParameter("database", org.apache.axis.Constants.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);
			call.setReturnType(org.apache.axis.Constants.SOAP_VECTOR);
			Vector ret = (Vector) call.invoke(new Object[]{database});
			saRet = new String[ret.size()];
			for (int i = 0; i < ret.size(); i++) {
				Vector v = (Vector) ret.get(i);
				saRet[i] = (String) v.get(0);
			}
		} catch (Exception e) {
			Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
					"Exception: " + e.toString());
		}
		return saRet;
	}
	
	public boolean hasConnection() {
		boolean bHas = false;
		String url = org.openide.util.NbBundle.getMessage(
				PubmlstSOAP.class, "Connection.defaultURL");
		try {
			URL newURL = new URL(url);
			InputStream is = newURL.openStream();
			is.close();
			bHas = true;
		} catch (IOException e) {
			Logger.getLogger(PubmlstSOAP.class.getName()).log(Level.WARNING,
					e.getLocalizedMessage());
		}
		return bHas;
	}
}
