/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.stats.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Pedro T. Monteiro
 */
public class Compute {

    private TreeMap<String, Integer> tmData;
    private int iSize;
    private DecimalFormat df;

    public Compute() {
	   tmData = new TreeMap<String, Integer>();
	   iSize = 0;
	   df = new DecimalFormat("#.####");
    }

    public void add(String value) {
	   int count = 1;
	   if (tmData.containsKey(value)) {
		  count += tmData.get(value);
	   }
	   tmData.put(value, count);
	   iSize++;
    }

    public StringBuffer getHTML(String fieldName) {
	   StringBuffer sb = new StringBuffer();
	   double sid = SID();
	   sb.append("<b>Simpson's Index of Diversity</b>: ");
	   sb.append(df.format(sid)).append("<br/>");
	   sb.append("<b>Confidence Interval<sub>95%</sub></b>: ");
	   sb.append(SIDci95(sid));
	   sb.append("<table border=1>");
	   sb.append("<tr><th><b>").append(fieldName).append("</b></th>");
	   sb.append("<th><b>Absolute Frequency</b></th>");
	   sb.append("<th><b>Relative Frequency</b></th>").append("</tr>");

	   double dTotal = 0.0, dPartial;
	   for (String id : sortByValue(tmData)) {
		  dPartial = (double) tmData.get(id) / iSize;
		  dTotal += dPartial;
		  sb.append("<tr><td>").append(id).append("</td><td>");
		  sb.append(tmData.get(id)).append("</td><td>");
		  sb.append(df.format(dPartial)).append("</td></tr>");
	   }
	   sb.append("<tr><td><b>Uniques</b>: ").append(tmData.size());
	   sb.append("</td><td><b>Total</b>: ").append(iSize);
	   sb.append("</td><td><b>Total</b>: ");
	   sb.append((new DecimalFormat("#.##")).format(dTotal));
	   sb.append("</tr></table>");
	   return sb;
    }

    private List<String> sortByValue(final Map<String, Integer> m) {
	   List<String> keys = new ArrayList<String>();
	   keys.addAll(m.keySet());
	   Collections.sort(keys, new Comparator<String>() {

		  @Override
		  public int compare(String o1, String o2) {
			 Integer v1 = m.get(o1);
			 Integer v2 = m.get(o2);
			 if (v1 < v2) // reverse order
			 {
				return 1;
			 } else if (v1 == v2) {
				return 0;
			 } else {
				return -1;
			 }
		  }
	   });
	   return keys;
    }

    private double SID() {
	   double fSID = 0;
	   for (String s : tmData.keySet()) {
		  int count = tmData.get(s);
		  fSID += count * (count - 1);
	   }
	   fSID /= iSize;
	   fSID /= (iSize - 1);
	   return 1 - fSID;
    }

    private String SIDci95(double sid) {
	   double sumPi2 = 0;
	   double sumPi3 = 0;
	   for (String s : tmData.keySet()) {
		  double pi = (double) tmData.get(s) / iSize;
		  double i2 = pi * pi;
		  double i3 = i2 * pi;
		  sumPi2 += i2;
		  sumPi3 += i3;
	   }
	   double diff = ((double) 4 / iSize) * (sumPi3 - sumPi2 * sumPi2);
	   diff = 2 * Math.sqrt(diff);
	   return "[" + df.format(sid - diff) + ", " + df.format(sid + diff) + "]";
    }
}