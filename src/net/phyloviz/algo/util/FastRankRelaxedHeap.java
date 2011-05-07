/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

/*-
 * Copyright (c) 2009, C. Vaz <cvaz@cc.isel.ipl.pt>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * Alternatively, this software may be distributed and/or modified under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any
 * later version.
 */

package net.phyloviz.algo.util;

import java.util.BitSet;
import java.util.Comparator;
                                                                               
/**
 * An implementation of rank relaxed heaps as described in:
 * 
 * <p>J. R. Driscoll, H. N. Gabow, R. Shrairman and R. E. Tarjan: "Relaxed
 * Heaps: An Alternative to Fibonacci Heaps with Applications to Parallel
 * computation". Communications of the ACM, 31(11), 1988.
 * 
 * <p> The heap entries are integers and their order is defined by a
 * {@linkplain Comparator} provided at heap construction time.
 * 
 * <p><strong>The heap is currently bounded</strong>, i.e., we must
 * explicitly state the desired size at heap construction time. It is
 * possible to implement a growth policy, however it should require
 * <i>O(n/log(n))</i> time to resize all nodes.
 * 
 * <p><strong>This implementation is not synchronized.</strong> Multiple
 * threads should not access a {@code FastRankRelaxedHeap} instance
 * concurrently if any of the threads modifies the queue.
 * 
 * <p><strong>Implementation notes:</strong>
 * 
 * <p>Let <i>n</i> denote the number of elements to store in the heap.
 * This implementation ensures <i>O(n)</i> space. The idea is to split
 * the elements in <i>n/log(n)</i> groups and each group is stored as a
 * heap node. Note that this implies <i>O(log(n))</i> time to scan the
 * group and update the minimum for the heap node.
 * 
 * <p>The heap nodes are stored in a contiguous array of integers, namely
 * <i>3 + O(log(n/log(n))</i> entries per node. The first <i>2</i> entries
 * store the minimum index of this group and the parent node, the third entry
 * stores the rank and the last <i>O(log(n/log(n))</i> entries store the
 * children of this node. Note that a node has at most rank
 * <i>log(n/log(n)</i>, i.e., it has at most
 * <i>log(n/log(n)</i> children. The space required to store this array is
 * <i>O(n/log(n)log(n/log(n)) = O(n-log(log(n))/log(n)) = O(n)</i>. We also
 * need two {@linkplain BitSet}s with size <i>n</i> to track active nodes
 * and which indexes are in the heap. An auxiliary data structure to store
 * the active nodes is also required. Since the number of active nodes is
 * at most <i>log(n/log(n)</i>, the size of this data structure requires
 * <i>O(log(n/log(n))</i>.
 * 
 * @author C&aacute;tia Vaz <cvaz@cc.isel.ipl.pt>
 */

public class FastRankRelaxedHeap implements Heap {
	
	/**
	 * The {@linkplain Comparator} that defines the heap order.
	 */
	private Comparator<Integer> cmp;
	
	/**
	 * The capacity of this heap.
	 */
	private int capacity;
	
	/**
	 * Group size.
	 */
	private int groupSize;

	/**
	 * Number of groups. The last group may not be complete. Each group
	 * will be associated to a node.
	 */
	private int groupN;

	/**
	 * Maximum rank.
	 */
	private int maxRank;

	/**
	 * The number of elements in the heap.
	 */
	private int size;
	
	/**
	 * {@linkplain BitSet} to track pushed indexes.
	 */
	private BitSet pushed;
	
	/**
	 * The <strong>heap</strong>. The head will be the last node.
	 */
	private int[] heap;
	private int nodeSize;
	private int headNode;
	private int minNode;
	
	/**
	 * An array track active nodes and a help variable.
	 */ 
	private int[] active;
	private int toClean;
	
	/**
	 * Data structure to track root ranks with two nodes.
	 */ 
	private PairListNode tplst;
		
	/**
	 * Creates a {@code FastRankRelaxedHeap}.
	 * 
	 * @param  capacity - the heap capacity.
	 * @param  comparator - the comparator that will be used to order this
	 *         heap. If {@code null}, the indexes will be used as keys.
	 * @throws IllegalArgumentException if {@code capacity} is less than
	 *         <i>1</i>. 
	 */
	public FastRankRelaxedHeap(int capacity, Comparator<Integer> comparator) {
		
		if (capacity < 1)
			throw new IllegalArgumentException();
		
		// Set comparator. Default comparator just use the indexes (!?)...
		if (comparator == null)
			this.cmp = new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					return o1 - o2;
				}
			};
		else
			this.cmp = comparator;
				
		// Set capacity.
		this.capacity = capacity;
		
		// Compute the group size.
		groupSize = log2(capacity);
		if (groupSize == 0) groupSize = 1;

		// Find the number of groups. The last group may not be complete.
		groupN = capacity / groupSize;
		if (capacity % groupSize > 0) groupN ++;
		
		// Compute the maximum rank.
		maxRank = log2(groupN);
		
		// Allocate memory for data structures. The last node will be the
		// head, thus it is a fake node. See help functions section ahead.
		// Head node can have an extra child since its rank is at most
		// maxRank + 1. In fact we have two fake nodes to allow insertion
		// in time O(1).
		nodeSize = 3 + maxRank;
		heap = new int[(groupN + 2)*nodeSize + 2];
		pushed = new BitSet(capacity);
		size = 0;
		
		// Allocate active array.
		active = new int[maxRank + 1];
		for (int i = 0; i <= maxRank; i++)
			active[i] = -1;
		toClean = -1;
		
		// Set the headNode. Reset children for both heads.
		headNode = groupN;
		reset(headNode);
		reset(headNode + 1);
		for (int i = 0; i <= maxRank; i++) {
			setChild(headNode, -1, i);
			setChild(headNode + 1, -1, i);
		}
		
		// Reset all nodes.
		for (int i = 0; i < groupN; i++)
			reset(i);
		
		// Set the minNode to unknown.
		minNode = -1;
	}

	/**
	 * Returns the comparator used to order the elements in this heap.
	 * 
	 * @return the heap comparator.
	 */
	@Override
	public Comparator<Integer> comparator() {
		return cmp;
	}

	/**
	 * Removes the specified element of this heap in time <i>O(log(n))</i>.
	 * 
	 * @param idx - the index of the pushed element.
	 */
	@Override
	public void delete(int idx) {

		if (size == 0 || idx < 0 || idx >= capacity || ! pushed.get(idx))
			return;
		
		// Check tree list...
		while (fixOnePair());
		
		int node = index2node(idx);

		// Remove the specified index.
		removeIndex(idx);
		size --;
		
		// Check if the group of minNode becomes empty.
		if (! groupIsEmpty(node)) {
			
			// If the node is active...
			if (isActive(node)) setInactive(node);
			
			// Note that the key is increasing, thus we must recombine the
			// node with its children.
			if (getRank(node) > 0) {
				
				// We must remember the parent of the node. We also delete
				// the node from its parent.
				int  p = getParent(node);
				
				// Check if any child of node is active and reset the parent.
				for (int i = 0; i < getRank(node); i++) {
					int aux = getChild(node, i);

					if (isActive(aux)) setInactive(aux);
				}

				// Combine 'node' with its children to form a new tree.
				int r = getRank(node);
				setRank(node, 0);
				int newNode = node;
				for (int i = 0; i < r; i++) {
					// We cannot use link and getChild because rank may
					// change while combining.
					// newNode = link(newNode, getChild(node, i));
					
					int ls = -1;
					int aux = heap[node2index(node) + 3 + i];
					
					if (nodeComparator(newNode, aux) <= 0) {
						incRank(newNode);
						setChild(newNode, aux, getRank(aux));
						setParent(aux, newNode);
			
						ls = getLeftSibling(aux);
					} else {
						incRank(aux);
						setChild(aux, newNode, getRank(newNode));
						setParent(newNode, aux);
						
						ls = getLeftSibling(newNode);
						newNode = aux;
					}
					
					if (ls != -1 && isActive(ls))
						clean(ls);
				}

				// Put the new tree in the place of 'node'.
				setParent(newNode, p);
				setChild(p, newNode, getRank(newNode));
				node = newNode;
			}
			
			// Lets see if the new node becomes active.
			if (isBadNode(node)) setActive(node);
			
		} else {
			
			// It is empty, thus we must remove this node.
			deleteNode(node);
			
		}
		
		// We must search for a new node...
		updateMinimumNode();
	}

	/**
	 * Retrieves and removes the index of the smallest element of this
	 * heap, or returns <i>-1</i> if this heap is empty, in time
	 * <i>O(log(n))</i>.
	 * 
	 * @return the index of the smallest element of this heap, or
	 * returns <i>-1</i> if this heap is empty.
	 */
	@Override
	public int pop() {
		
		if (size == 0)
			return -1;
		
		// Get minimum index.
		int idxMin = getMinIndex(minNode);

		// Delete the index.
		delete(idxMin);
		
		return idxMin;
	}

	/**
	 * Pushes the specified element into this heap in time <i>O(1)</i>. If
	 * the element is already in the heap, it will be updated.
	 * 
	 * @param idx - the index of the pushed element.
	 */
	@Override
	public void push(int idx) {
		
		if (idx < 0 || idx >= capacity)
			return;
		
		int node = index2node(idx);
		
		if (groupIsEmpty(node)) {
			
			// Create a new node and insert it.
			reset(node);
			addIndex(idx);
			size ++;
			
			if (getChild(headNode, 0) != -1) {
				setParent(node, headNode + 1);
				setChild(headNode + 1, node, 0);
				incRank(headNode + 1);
				
				tplst = new PairListNode(tplst, 0);
			} else {
				setParent(node, headNode);
				setChild(headNode, node, 0);
				incRank(headNode);
			}
				
			// Check tree list...
			fixOnePair();
			
			// Update minimum. This implies that delete and pop also leave
			// minimum updated... If they leave the minimum unknown, this
			// will not work.
			if (minNode == -1 || nodeComparator(minNode, node) > 0)
				minNode = node;
		} else {
			// The node is in the heap, but the index may not be...
			if (! pushed.get(idx)) {
				addIndex(idx);
				size ++;
			} else 
				updateMinIndexIfBetter(node, idx);
			
			// Check if the heap structure must be updated.
			updateNode(node);
		}
	}

	/**
	 * Returns the number of elements in this heap.
	 * 
	 * @return the size of this heap.
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Retrieves the index of the smallest element of this heap, or
	 * returns <i>-1</i> if this heap is empty, in time <i>O(1)</i>.
	 * 
	 * @return the index of the smallest element of this heap, or
	 * returns <i>-1</i> if this heap is empty.
	 */
	@Override
	public int top() {
		return (minNode == -1) ? -1 : getMinIndex(minNode);
	}

	/**
	 * Updates the specified element into this heap in time <i>O(1)</i>.
	 * <strong>It assumes that the element has decreased, otherwise the
	 * heap becomes inconsistent.</strong>
	 * 
	 * @param idx - the index of the updated element.
	 */
	@Override
	public void update(int idx) {
		
		if (idx < 0 || idx >= capacity || ! pushed.get(idx))
			return;
		
		// The node there this index belongs.
		int node = index2node(idx);
		
		// Check if we need to update the minimum of the group of this
		// node.
		updateMinIndexIfBetter(node, idx);
		
		// Update heap structure where needed.
		updateNode(node);
	}
	
	// Updates a node.
	private void updateNode(int node) {
		
		// We can only update nodes in the heap, i.e., node which group is
		// not empty.
		if (groupIsEmpty(node))
			return;
		
		// Update minimum. This implies that delete and pop also leave
		// minimum updated... If they leave the minimum unknown, this
		// will not work.
		if (minNode == -1 || nodeComparator(minNode, node) > 0)
			minNode = node;
			
		// Let us promote it.
		promote(node);
	}
	
	// Deletes a node.
	private void deleteNode(int node) {

		// If the node is active, we set it inactive.
		if (isActive(node)) setInactive(node);
		
		// Remove 'y' with lowest rank from the head.
		int y = -1;
		for (int i = 0; y == -1 && i <= maxRank; i++)
			y = getChild(headNode, i);
		setChild(headNode, -1, getRank(y));
		decRank(headNode);
		
		// Now we add the children of 'y' to the head. Since 'y' was the
		// node with lowest rank, we can add them keeping the heap
		// consistent. We insert them at the begin of the head child list.
		if (getRank(y) != 0) {
			
			// Check if any child is active and fix its parent.
			for (int i = 0; i < getRank(y); i++) {
				int aux = getChild(y, i);
				if (isActive(aux)) setInactive(aux);
				setParent(aux, headNode);
			}
			
			for (int i = 0; i < getRank(y); i++)
				setChild(headNode, getChild(y, i), i);
			
			// Update head rank.
			setRank(headNode, getRank(headNode) + getRank(y));
		}
		
		// 'y' now is an isolated node, thus:
		setRank(y, 0);
		
		// If 'y' is the node, we are done. If not...
		if (y != node) {
		
			if (getRank(node) > 0) {
				// Check if any child of node is active and reset the parent.
				for (int i = 0; i < getRank(node); i++) {
					int aux = getChild(node, i);

					if (isActive(aux)) setInactive(aux);
				}
				
				// Combine 'y' and 'node' children to form a new tree.
				for (int i = 0; i < getRank(node); i++)
					y = combine(y, getChild(node, i));				
			}
			
			// Put the new tree in the place of 'node'.
			setParent(y, getParent(node));
			setChild(getParent(node), y, getRank(y));
			
			// Let us check if 'y' is a active node...
			promote(y);
			
			// Reset 'node' just for sure.
			reset(node);
		}
	}
	
	// --- BEGIN RANK RELAXED HEAP TRANSFORMATIONS ---
	
	// Checks if we have any tree in the waiting list and try to fix one.
	private boolean fixOnePair() {
		
		if (tplst == null)
			return false;

		// Take the first element.
		int r = tplst.x;
		tplst = tplst.next;
		
		int x = getChild(headNode + 1, r);
		
		// Delete 'x'.
		setChild(headNode + 1, -1, r);
		decRank(headNode + 1);
		//setParent(x, -1);
		
		int y = getChild(headNode, getRank(x));
		
		// Delete 'y'...
		setChild(headNode, -1, getRank(y));
		decRank(headNode);
		
		// Link 'x' and 'y'.
		x = combine(x, y);
		
		if (getChild(headNode, getRank(x)) != -1) {
			setParent(x, headNode + 1);
			setChild(headNode + 1, x, getRank(x));
			incRank(headNode + 1);
			
			tplst = new PairListNode(tplst, getRank(x));
		} else {
			setParent(x, headNode);
			setChild(headNode, x, getRank(x));
			incRank(headNode);
		}
		
		return true;
	}
	
	// Promotes a node.
	private void promote(int node) {
		
		if (isBadNode(node) && ! isActive(node)) {
			
			// Check if 'node' is the last child.
			if (isLastChild(node)) {
				
				if (active[getRank(node)] == -1)
					setActive(node);
				else
					pairTransform(node, active[getRank(node)]);
				
			} else { // 'node' is not last child.
				
				if (isActive(getRightSibling(node)))
					activeSiblingTransform(node);
				else
					goodSiblingTransform(node);
			
			}
		}
		
	}
	
	// It reduces the number of active nodes when both active nodes are
	// last children.
	private void pairTransform(int a, int b) {
		
		if (isActive(a)) setInactive(a);
		if (isActive(b)) setInactive(b);
		
		int ap = getParent(a);
		int bp = getParent(b);
		
		a = combine(a, b);
		
		if (nodeComparator(ap, bp) <= 0) {
			
			int bpp = getParent(bp);
			setChild(bpp, a, getRank(a));
			setParent(a, bpp);
			
			decRank(bp);
			setChild(ap, bp, getRank(bp));
			setParent(bp, ap);
			
			if (active[getRank(ap)] == bp) {
				if (isBadNode(a)) setActive(a);
				else setInactive(ap);
			} else if (isBadNode(a))
				promote(a);
			
		} else {
			
			int app = getParent(ap);
			setChild(app, a, getRank(a));
			setParent(a, app);
			
			decRank(ap);
			setChild(bp, ap, getRank(ap));
			setParent(ap, bp);
			
			if (active[getRank(bp)] == ap) {
				if (isBadNode(a)) setActive(a);
				else setInactive(bp);
			} else if (isBadNode(a))
				promote(a);
			
		}
	}
	
	// It reduces the number of active nodes when there is a good sibling
	// and its last child is also good.
	private void goodSiblingTransform(int a) {
		
		int p = getParent(a);
		int s = getRightSibling(a);
		int c = getLastChild(s);
		
		if (isActive(c)) {
			setInactive(c);
			
			// Remove the last child of 's'.
			decRank(s);

			// Replace 'a' by 's', they now have the same rank. Note that the
			// parent is correct.
			setChild(p, s, getRank(a));

			// Reset 'a' and 'c'. Combine them.
			a = combine(a, c);

			// Add 'a' after 's'.
			setChild(p, a, getRank(a));
			setParent(a, p);

			// Since 'p <= s', 'a > p' and 'c > s', the new 'a' must be active.
			promote(a);
		} else {
			toClean = a;
			clean(a);
			promote(a);
		}
	}
	
	// It reduces the number of active nodes when there is an active
	// sibling ant it is a last child.
	private void activeSiblingTransform(int a) {
		
		int s = getRightSibling(a);
		int p = getParent(a);
		int pp = getParent(p);
		
		setInactive(s);
		
		// Assume that 'a' has rank 'r'. Then 's' has rank 'r+1' and the
		// parent of 'a' has at least rank 'r+2'.
		
		// Drop 's', 'a' and 'p'.
		decRank(p);
		decRank(p);
		
		// Note that 'a' is bad because of previous events.
		
		a = combine(p, a);
		a = combine(s, a);
		setChild(pp, a, getRank(a));
		setParent(a, pp);
		
		// The new 'a' may be active...
		if (active[getRank(a)] == p) {
			if (isBadNode(a)) setActive(a);
			else setInactive(a);
		} else if(isBadNode(a))
			promote(a);
	}
	
	private void updateMinimumNode() {
		
		minNode = -1;
		
		if (size == 0)
			return;

		// Root children.
		for (int i = 0; i < maxRank + 1; i++) {
			int aux = getChild(headNode, i);
			if (minNode == -1 
					|| (aux != -1 && nodeComparator(minNode, aux) > 0))
				minNode = aux;
			
			aux = getChild(headNode + 1, i);
			if (minNode == -1 
					|| (aux != -1 && nodeComparator(minNode, aux) > 0))
				minNode = aux;
		}

		// Check active nodes...
		for (int i = 0; i < maxRank; i++)
			if (active[i] != -1 && nodeComparator(minNode, active[i]) > 0)
					minNode = active[i];
	}
	
	// --- END RANK RELAXED HEAP TRANSFORMATIONS ---
	
	// --- BEGIN HEAP HELP FUNCTIONS ---
	
	/* Node structure:
	 * 
	 *   --+-----------+--------+------+-----+-----+--------+-
	 * ... | min index | parent | rank | c_1 | ... | c_rank | ...
	 *    -+-----------+--------+------+-----+-----+--------+--
	 *      <-------------------- NODE -------------------->
	 */
	
	// Reset a node to default values.
	private void reset(int node) {
		heap[node2index(node) + 0] = -1;
		heap[node2index(node) + 1] = -1;
		heap[node2index(node) + 2] = 0;
	}

	// Links two nodes.
	private int combine(int x, int y) {
		
		if (nodeComparator(x, y) <= 0) {
			incRank(x);
			setChild(x, y, getRank(y));
			setParent(y, x);
			if (! isFirstChild(y))
				clean(getLeftSibling(y));
			return x;
		} else {
			incRank(y);
			setChild(y, x, getRank(x));
			setParent(x, y);
			if (! isFirstChild(x))
				clean(getLeftSibling(x));
			return y;
		}
	}
	
	// Cleanup active node.
	private void clean(int x) {
		/* The clean operation is applied when 'x' is a bad node. Note
		 * that we should only apply the clean operation in that case,
		 * otherwise we may be exchange 'x' by 'c' and 'c' could be a bad
		 * node being 'x' a good node.
		 */  
		
		if (isActive(x) || toClean == x) {
			int p = getParent(x);
			int s = getRightSibling(x);
			int c = getLastChild(s);
			
			setChild(p, c, getRank(c));
			setParent(c, p);
			
			setChild(s, x, getRank(x));
			setParent(x, s);
			
			if (toClean == x) toClean = -1;
		}
	}
	
	// Given an index, it returns the group. The first group is special, it
	// will be used as root.
	private int index2node(int index) {
		return index / groupSize;
	}
	
	// Given a node, it computes its position in the heap.
	private int node2index(int node) {
		// We may relax this, after all we do not use the minimum index
		// of head nodes.
		return node*nodeSize;
	}
	
	// Check if a given node is the head.
	private boolean isHead(int node) {
		return node >= headNode;
	}
	
	// Check if the group of elements associated to a given node is empty.
	private boolean groupIsEmpty(int node) {
		return getMinIndex(node) == -1;
	}
	
	// Add an index to the group associated to a given node.
	private void addIndex(int index) {
		pushed.set(index);
		
		updateMinIndexIfBetter(index2node(index), index);
	}
	
	// Remove an index from the group associated to a given node.
	private void removeIndex(int index) {
		pushed.clear(index);
		
		if (index == getMinIndex(index2node(index)))
			updateMinIndex(index2node(index));
	}
	
	// Given a node, it returns the minimum index.
	private int getMinIndex(int node) {
		return heap[node2index(node) /*+ 0*/];
	}
	
	// Sets the minimum index of a node.
	private void setMinIndex(int node, int index) {
		heap[node2index(node) /*+ 0*/] = index;
	}

	// Update minimum index if current one is better.
	private void updateMinIndexIfBetter(int node, int index) {
		// The last group may be incomplete...
		if (index >= capacity)
			return;
		
		if (pushed.get(index) && (getMinIndex(node) == -1
				|| cmp.compare(getMinIndex(node), index) > 0))
			setMinIndex(node, index);
	}

	// Update minimum index. We must check if the index is pushed.
	private void updateMinIndex(int node) {
		//The last node, i.e, the root, has no elements. 
		
		/* The elements of this node are located at:
		 *   node*groupSize + 0
		 *   node*groupSize + 1
		 *   ...
		 *   node*groupSize + groupSize - 1 
		 */
		int min = -1;
		
		int block = node*groupSize;
		for (int i = 0; i < groupSize; i++) {
			int idx = block + i;
			
			// The last group may be incomplete...
			if (idx >= capacity)
				break;
			
			if (pushed.get(idx) && (min == -1 || cmp.compare(min, idx) > 0))
				min = idx;
		}
		heap[node2index(node) /*+ 0*/] = min;
	}
	
	// Compares two node.
	private int nodeComparator(int n1, int n2) {
		return cmp.compare(getMinIndex(n1), getMinIndex(n2));
	}
	
	// Given a node, it returns the parent.
	private int getParent(int node) {
		return heap[node2index(node) + 1];
	}
	
	// Sets the parent of a node.
	private void setParent(int node, int parent) {
		heap[node2index(node) + 1] = parent;
	}
	
	// Given a node, it returns the left sibling.
	private int getLeftSibling(int node) {
		if (getRank(node) - 1 >= 0)
			return getChild(getParent(node), getRank(node) - 1);
		
		// There is no left sibling.
		return -1;
	}
	
	// Given a node, it returns the right sibling.
	private int getRightSibling(int node) {
		if (getRank(node) + 1 < getRank(getParent(node)))
			return getChild(getParent(node), getRank(node) + 1);
		
		// There is no right sibling.
		return -1;
	}
	
	// Checks if a node is last child.
	private boolean isLastChild(int node) {
		return getChild(getParent(node), getRank(getParent(node)) - 1) == node;
	}
	
	// Checks if a node is first child.
	private boolean isFirstChild(int node) {
		return getChild(getParent(node), 0) == node;
	}
	
	// Given a node, it returns the rank.
	private int getRank(int node) {
		return heap[node2index(node) + 2];
	}
	
	// Sets the rank of a node.
	private void setRank(int node, int rank) {
		heap[node2index(node) + 2] = rank;
	}
	
	// Increments the rank of a node.
	private void incRank(int node) {
		heap[node2index(node) + 2] ++;
	}
	
	// Decrements the rank of a node.
	private void decRank(int node) {
		heap[node2index(node) + 2] --;
	}
	
	// Given a node, it returns child with rank childRank.
	private int getChild(int node, int childRank) {
		return heap[node2index(node) + 3 + childRank];
	}
	
	// Get last child.
	private int getLastChild(int node) {
		return getChild(node, getRank(node) - 1);
	}
	
	// Sets the child with rank childRank of a node.
	private void setChild(int node, int child, int childRank) {
		// We may want to set a child to null (-1).
		// We could update the rank of the node, however this may not be
		// desired... namely in some temporary situations.
		heap[node2index(node) + 3 + childRank] = child;
	}
	
	// Checks if a given node is bad. 
	public boolean isBadNode(int node) {
		int parent = getParent(node);
		return (node != -1)
			&& (parent != -1)
			&& (!isHead(parent))
			&& (nodeComparator(node, parent) < 0);
	}
	
	// Checks if a node is active.
	private boolean isActive(int node) {
		return active[getRank(node)] == node;
	}
	
	// Sets node active.
	private void setActive(int node) {
		active[getRank(node)] = node;
	}
	
	// Sets node inactive.
	private void setInactive(int node) {
		active[getRank(node)] = -1;
	}
	
	// Log2 function for integers.
	private static int log2(int n) {
		int leadingZeros = 0;
		do {
			int next = n << 1;
			if (n == (next >>1)) {
				++ leadingZeros;
				n = next;
			} else
				break;
		} while (true);
	
		return 31 - leadingZeros - 1;
	}
	
	// A simple list to store integers.
	private static class PairListNode {
		int x;
		PairListNode next;
		
		public PairListNode(PairListNode next, int x) {
			this.x = x;
			this.next = next;
		}
	}
	
	// --- END HEAP HELP FUNCTIONS ---
}
