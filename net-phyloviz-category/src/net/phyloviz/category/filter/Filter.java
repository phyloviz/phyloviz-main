package net.phyloviz.category.filter;

import java.util.List;

public interface Filter {

	public List<Category> filtering(List<Category> list);
}
