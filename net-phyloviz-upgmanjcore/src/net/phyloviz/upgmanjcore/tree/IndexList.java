/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

package net.phyloviz.upgmanjcore.tree;

import java.util.Iterator;
import net.phyloviz.upgmanjcore.tree.IndexList.IndexNode;

/**
 *
 * @author Adriano
 */
public class IndexList implements Iterable<IndexNode> {

    private final IndexNode dummy;
    private int idx = 0;
    private int size = 0;

    public IndexList() {
        dummy = new IndexNode(-1);
        dummy.next = dummy.prev = dummy;    //prev points to last, next point to first
    }

    /**
     * Adds new Index to IndexListNode
     *
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

    public int getSize() {
        return size;
    }

    /**
     *
     * @return iterator with all the elements
     */
    @Override
    public Iterator<IndexNode> iterator() {
        return new NodeIterator(dummy).iterator();
    }

    /**
     * Iterator for each NodeType that start from NideType index + 1
     */
    public class NodeIterator implements Iterable<IndexNode> {

        public final IndexNode start;

        private NodeIterator(IndexNode in) {
            start = in;
        }

        /**
         *
         * @return iterator started from this element
         */
        @Override
        public Iterator<IndexNode> iterator() {
            return new Iterator<IndexNode>() {
                IndexNode current = start;
                boolean hasNext = false;

                @Override
                public boolean hasNext() {
                    if (hasNext) {
                        return hasNext;
                    } else {
                        current = current.next;
                        hasNext = current != dummy;
                        return hasNext;
                    }
                }

                @Override
                public IndexNode next() {
                    if (hasNext()) {
                        hasNext = false;
                        IndexNode in = current;
                        return in;
                    }
                    return null;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    /**
     * Linked Node with index
     */
    public class IndexNode {

        public final int index;
        public IndexNode next;
        public IndexNode prev;

        public IndexNode(int idx) {
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
