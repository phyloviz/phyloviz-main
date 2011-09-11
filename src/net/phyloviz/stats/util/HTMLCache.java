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
 * @author ptgm
 */
public class HTMLCache {

	private TreeMap<Integer, StringBuffer> tmBodyCache;
	private TreeMap<String, Integer> tmCurrentBody;
	private int iCurrentTotal;
	private DecimalFormat df;

	public HTMLCache() {
		tmBodyCache = new TreeMap<Integer, StringBuffer>();
	}

	public void bodyInit() {
		tmCurrentBody = new TreeMap<String, Integer>();
		iCurrentTotal = 0;
		df = new DecimalFormat("#.#####");
	}

	public void bodyAddValue(String value) {
		if (tmCurrentBody != null) {
			int count = 1;
			if (tmCurrentBody.containsKey(value)) {
				count += tmCurrentBody.get(value);
			}
			tmCurrentBody.put(value, count);
			iCurrentTotal++;
		}
	}

	public void bodyFinish(int index, String sName) {
		StringBuffer sb = new StringBuffer("<table border=1>");
		sb.append("<tr><th><b>").append(sName).append("</b></th>");
		sb.append("<th><b>Absolute Frequency</b></th>");
		sb.append("<th><b>Relative Frequency</b></th>").append("</tr>");

		double dTotal = 0.0, dPartial;
		for (String id : sortByValue(tmCurrentBody)) {
			dPartial = (double) tmCurrentBody.get(id) / iCurrentTotal;
			dTotal += dPartial;
			sb.append("<tr><td>").append(id).append("</td><td>");
			sb.append(tmCurrentBody.get(id)).append("</td><td>");
			sb.append(df.format(dPartial)).append("</td></tr>");
		}
		sb.append("<tr><td><b>Uniques</b>: ").append(tmCurrentBody.size());
		sb.append("</td><td><b>Total</b>: ").append(iCurrentTotal);
		sb.append("</td><td><b>Total</b>: ");
		sb.append((new DecimalFormat("#.##")).format(dTotal));
		sb.append("</tr></table>");
		tmBodyCache.put(index, sb);
	}

	public boolean hasBody(int i) {
		return tmBodyCache.containsKey(i);
	}

	public String getBody(int i) {
		if (hasBody(i)) {
			return tmBodyCache.get(i).toString();
		}
		return "Not found!";
	}

	public List<String> sortByValue(final Map<String, Integer> m) {
		List<String> keys = new ArrayList();
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
}
