package net.phyloviz.pubmlst.soap;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PubmlstSOAP {

	private String endpoint;

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
		String sAlleles = null;
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
			param = new ParameterDesc(new QName("", "complex"),
					ParameterDesc.OUT, new QName(
					"http://www.w3.org/2001/XMLSchema", "string"),
					String.class, false, false);
			param.setOmittable(true);
			oper.addParameter(param);
			oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
			call.setOperation(oper);
			call.setOperationName(new QName("http://pubmlst.org/MLST",
					"getProfile"));
			call.invoke(new Object[]{database, st});
			Map output = call.getOutputParams();
			AlleleNumber[] alleles = (AlleleNumber[]) output.get(new QName("",
					"profile"));
			sAlleles = "" + st;
			for (int i = 0; i < alleles.length; i++) {
				sAlleles += "\t" + alleles[i].getId();
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return sAlleles;
	}

	public HashMap<String, String> getIsolate(String database, int isolate) {
		HashMap<String, String> hmRet = new HashMap<String, String>();
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
			Vector ret = (Vector) call.invoke(new Object[]{database, isolate});
			for (int i = 0; i < ret.size(); i++) {
				Vector v = (Vector) ret.get(i);
				hmRet.put((String) v.get(0), "" + v.get(1));
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
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
			e.printStackTrace();
			System.err.println(e.toString());
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
			System.err.println(database + ": " + e.toString());
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
			System.err.println(database + ": " + e.toString());
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
			System.err.println(database + ": " + e.toString());
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
			System.err.println(database + ": " + e.toString());
		}
		return saRet;
	}
}
