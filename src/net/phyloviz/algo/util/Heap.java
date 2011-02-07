package net.phyloviz.algo.util;

import java.util.Comparator;

public interface Heap {
	
	public Comparator<Integer> comparator();
	
	public void push(int idx);
	
	public void update(int idx);
	
	public void delete(int idx);
	
	public int top();

	public int pop();
	
	public int size();
}
