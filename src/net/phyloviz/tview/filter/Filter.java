package net.phyloviz.tview.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import net.phyloviz.core.data.Isolate;

public class Filter implements IFilter {

	private String name;
	private int index;
	private TreeSet<String> filter;

	public TreeSet<String> getFilter() {
		return filter;
	}

	public Filter(TreeSet<String> t, int i, String n) {
		filter = t;
		index = i;
		name = n;
	}

	@Override
	public List<Group> filtering(List<Group> list) {
		List<Group> nova = new LinkedList<Group>();
		Iterator<Group> i = list.iterator();
		while (i.hasNext()) {
			Group g = i.next();
			nova = split(nova, g);
		}

		return nova;
	}

	private List<Group> split(List<Group> nova, Group list) {
		TreeMap<String, Group> map = new TreeMap<String, Group>();
		Iterator<Isolate> iter = list.iterator();
		while (iter.hasNext()) {
			Isolate l = iter.next();
			String key = l.get(index);
			if (this.getFilter().contains(key)) {
				Group g = map.get(key);
				if (g == null) {
					g = new Group(list.getName() + " " + name + ":" + key);
				}
				g.add(l);
				map.put(key, g);
			}
		}
		Collection<Group> col = map.values();
		nova.addAll(col);
		return nova;
	}
}
