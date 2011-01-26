package net.phyloviz.tview.filter;

import java.util.List;

public interface IFilter {

	public List<Group> filtering(List<Group> list);
}
