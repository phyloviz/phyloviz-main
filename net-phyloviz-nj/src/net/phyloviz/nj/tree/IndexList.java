/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.tree;

import java.util.Iterator;
import net.phyloviz.nj.tree.IndexList.IndexNode;

/**
 *
 * @author Adriano
 */
public class IndexList implements Iterable<IndexNode>{
    
    private final IndexNode dummy;
    private int idx = 0;
    private int size = 0;
    
    public IndexList(){
        dummy = new IndexNode(-1);
        dummy.next = dummy.prev = dummy;    //prev points to last, next point to first
    }
    /**
     * Adds new Index to IndexListNode
     * @return NodeIterator for this new index
     */
    public NodeIterator Add() {
        IndexNode in = new IndexNode(idx++);
        in.prev = dummy.prev;
        in.next = dummy;
        dummy.prev.next = in;
        dummy.prev = in;
        size++;
        return new NodeIterator(in);
    }
    
    public int getSize(){
        return size;
    }

    @Override
    public Iterator<IndexNode> iterator() {
        return new NodeIterator(dummy).iterator();
    }
    
    /**
     * Iterator for each NodeType that start from NideType index + 1
     */
    public class NodeIterator implements Iterable<IndexNode>{

        public final IndexNode start;
        
        private NodeIterator(IndexNode in) {
            start = in;
        }
        
        @Override
        public Iterator<IndexNode> iterator() {
            return new Iterator<IndexNode>(){
                IndexNode current = start;
                boolean hasNext = false;
                @Override
                public boolean hasNext() {
                    if(hasNext)
                        return hasNext;
                    else {
                        current = current.next;
                        hasNext = current != dummy;
                        return hasNext;
                    }
                }

                @Override
                public IndexNode next() {
                    if(hasNext()){
                        hasNext = false;
                        IndexNode in = current;
                        return in;
                    } 
                    return null;
                }

				@Override
				public void remove() {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}
            };
        }
    }
 
    public class IndexNode{
        public final int index;
        public IndexNode next;
        public IndexNode prev;

        public IndexNode(int idx){
            this.index = idx;
        }
        /**
         * Removes current IndexNode from IndexListNode
         */
        public void remove() {
            this.prev.next = this.next;
            this.next.prev = this.prev;
            size--;
        }
    }
}
