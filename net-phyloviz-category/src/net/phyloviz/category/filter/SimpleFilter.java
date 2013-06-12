package net.phyloviz.category.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import net.phyloviz.core.data.DataItem;

public class SimpleFilter implements Filter {

	private String name;
	private int index;
	private TreeSet<String> filter;

	public TreeSet<String> getFilter() {
		return filter;
	}

	public SimpleFilter(TreeSet<String> t, int i, String n) {
		filter = t;
		index = i;
		name = n;
	}

	@Override
	public List<Category> filtering(List<Category> list) {
		List<Category> newList = new LinkedList<Category>();
		Iterator<Category> i = list.iterator();
		while (i.hasNext()) {
			Category g = i.next();
			newList = split(newList, g);
		}

		return newList;
	}

	private List<Category> split(List<Category> newList, Category list) {
		TreeMap<String, Category> map = new TreeMap<String, Category>();
		Iterator<DataItem> iter = list.iterator();
		while (iter.hasNext()) {
			DataItem l = iter.next();
			String key = l.get(index);
			if (key != null && this.getFilter().contains(key)) {
				Category g = map.get(key);
				if (g == null) {
					g = new Category(list.getName() + " " + name + ":" + key);
				}
				g.add(l);
				map.put(key, g);
			}
		}
		Collection<Category> col = map.values();
		newList.addAll(col);
		return newList;
	}
}
