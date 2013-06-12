package net.phyloviz.category.filter;

import java.util.List;
import java.util.TreeSet;
import net.phyloviz.core.data.DataModel;

public class CompositeFilter implements Filter{

	private Filter[] f;
	
	public CompositeFilter(TreeSet<String>[] array, DataModel dm){
		f = new SimpleFilter[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && !array[i].isEmpty())
				f[i] = new SimpleFilter(array[i], i, dm.getDomainLabel(i));
		}
	}

	@Override
	public List<Category> filtering(List<Category> list) {
		for (int i = 0; i < f.length; i++){
			if(f[i] != null)
				list = f[i].filtering(list);
		}
		
		return list;
	}
}
