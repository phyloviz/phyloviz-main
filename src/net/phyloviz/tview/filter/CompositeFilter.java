package net.phyloviz.tview.filter;

import java.util.List;
import java.util.TreeSet;

public class CompositeFilter implements IFilter{

	private IFilter[] f;
	
	public CompositeFilter(TreeSet<String>[] array, String[] names){
		f = new Filter[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && !array[i].isEmpty())
				f[i] = new Filter(array[i], i, names[i]);
		}
	}

	@Override
	public List<Group> filtering(List<Group> list) {
		for (int i = 0; i < f.length; i++){
			if(f[i] != null)
				list = f[i].filtering(list);
		}
		
		return list;
	}
}
