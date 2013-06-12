package net.phyloviz.category;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import net.phyloviz.category.color.Palette;
import net.phyloviz.category.filter.CompositeFilter;
import net.phyloviz.category.filter.Filter;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.DataItem;
import net.phyloviz.core.data.DataModel;

@SuppressWarnings("unchecked")
public class CategoryProvider {

	private DataModel dm;
	private TreeMap<String, List<Category>> idMap;
	private TreeMap<String, Integer> fullCats;
	private TreeMap<String, Color> colorMap;
	private Palette pal;
	private List listeners = Collections.synchronizedList(new LinkedList());
	private boolean on;

	public CategoryProvider(DataModel dm) {
		this.dm = dm;
		this.on = false;
		fullCats = new TreeMap<String, Integer>();
		colorMap = new TreeMap<String, Color>();
	}

	public boolean isOn() {
		return on;
	}

	public List<Entry<String, Integer>> getCategories() {
		List<Entry<String, Integer>> entries = new ArrayList(fullCats.entrySet());

		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> c1, Entry<String, Integer> c2) {
				return c2.getValue() - c1.getValue();
			}
		});

		return entries;
	}

	public List<Category> getCategories(String id) {

		List<Category> lst = idMap.get(id);
		if (lst != null)
			Collections.sort(idMap.get(id), new Comparator<Category>() {

				@Override
				public int compare(Category c1, Category c2) {
					return c2.size() - c1.size();
				}
			});

		return lst;
	}

	public Color getCategoryColor(String catName) {
		return colorMap.get(catName);
	}

	public void putCategoryColor(String catName, Color c) {
		colorMap.put(catName, c);
	}

	public Palette getPalette() {
		return pal;
	}

	public synchronized void setSelection(TreeSet<String>[] selection) {

		if (selection == null) {
			on = false;
			fire();
			return;
		}

		Filter f = new CompositeFilter(selection, dm);
		idMap = new TreeMap<String, List<Category>>();
		fullCats = new TreeMap<String, Integer>();
		colorMap = new TreeMap<String, Color>();

		// Initialize groups for each id...
		Iterator<? extends DataItem> di = dm.iterator();
		while (di.hasNext()) {
			DataItem d = di.next();
			String id = d.get(dm.getKey());

			List<Category> gl = idMap.get(id);
			if (gl == null) {
				gl = new LinkedList<Category>();
				gl.add(new Category(""));
				idMap.put(id, gl);
			}

			gl.get(0).add(d);
		}

		// Filtering each ST...
		Iterator<String> ii = idMap.keySet().iterator();
		while (ii.hasNext()) {
			String id = ii.next();
			List<Category> gl = idMap.get(id);
			gl = f.filtering(gl);

			// Update full categories...
			Iterator<Category> gIter = gl.iterator();
			while (gIter.hasNext()) {
				Category grp = gIter.next();

				Integer x = fullCats.get(grp.getName());
				if (x != null) {
					x += grp.weight();
				} else {
					x = grp.weight();
				}

				fullCats.put(grp.getName(), x);
			}

			idMap.put(id, gl);
		}

		pal = new Palette(fullCats.size());

		int i = 0;
		Iterator<String> labels = fullCats.keySet().iterator();
		while (labels.hasNext()) {
			String label = labels.next();
			colorMap.put(label, pal.getColors()[i++]);
		}

		on = true;
		fire();
	}

	public void addCategoryChangeListener(CategoryChangeListener pcl) {
		listeners.add(pcl);
	}

	public void removeCategoryChangeListener(CategoryChangeListener pcl) {
		listeners.remove(pcl);
	}

	private void fire() {
		CategoryChangeListener[] pcls = (CategoryChangeListener[]) listeners.toArray(new CategoryChangeListener[0]);
		for (int i = 0; i < pcls.length; i++) {
			pcls[i].categoryChange(this);
		}
	}
}
