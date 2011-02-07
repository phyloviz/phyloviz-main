package net.phyloviz.algo.tree;

public class Edge {

	private Node u;
	private Node v;

	public Edge(Node u, Node v) {
		this.u = u;
		this.v = v;
	}

	public Node getU() {
		return u;
	}

	public Node getV() {
		return v;
	}
}
