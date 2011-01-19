/**
 *    http://code.google.com/p/aephyr/source/browse/trunk/src/aephyr/swing/TreeModelTransformer.java
 *    Copyright (c) 2010 drael4389@gmail.com
 *
 *    Modified by cvaz@ for PHYLOViZ
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published
 *    by the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.google.code.aephyr;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.*;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel.CheckingMode;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SortOrder;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeModelTransformer<I> implements TreeModel {

        public ArrayList<TreePath>  toSelect;

        public TreeModelTransformer(CheckboxTree tree, TreeModel model) {
                if (tree == null)
                        throw new IllegalArgumentException();
                if (model == null)
                        throw new IllegalArgumentException();
                this.tree = tree;
                tree.getCheckingModel().setCheckingMode(CheckingMode.PROPAGATE_PRESERVING_CHECK);
                this.model = model;
                handler = createHandler();
                addListeners();
                toSelect=new ArrayList<TreePath>();
        }
       
        private CheckboxTree tree;
       
        private TreeModel model;

        private Handler handler;
       
        private Filter filter;
       
        private TreePath filterStartPath;
       
        private SortOrder sortOrder = SortOrder.UNSORTED;
       
        private Map<Object,Converter> converters;
       
        protected EventListenerList listenerList = new EventListenerList();

        protected Handler createHandler() {
                return new Handler();
        }
       
        protected void addListeners() {
                tree.addTreeExpansionListener(handler);
                model.addTreeModelListener(handler);
        }

        protected void removeListeners() {
                tree.removeTreeExpansionListener(handler);
                model.removeTreeModelListener(handler);
        }
       
        public void dispose() {
                removeListeners();
        }
       
        public TreeModel getModel() {
                return model;
        }

        private Converter getConverter(Object node) {
                return converters == null ? null : converters.get(node);
        }

        int convertRowIndexToView(Object parent, int index) {
                Converter converter = getConverter(parent);
                if (converter != null)
                        return converter.convertRowIndexToView(index);
                return index;
        }

        int convertRowIndexToModel(Object parent, int index) {
                Converter converter = getConverter(parent);
                if (converter != null)
                        return converter.convertRowIndexToModel(index);
                return index;
        }

        @Override
        public Object getChild(Object parent, int index) {
                return model.getChild(parent, convertRowIndexToModel(parent, index));
        }

        @Override
        public int getChildCount(Object parent) {
                Converter converter = getConverter(parent);
                if (converter != null)
                        return converter.getChildCount();
                return model.getChildCount(parent);
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
                int index = model.getIndexOfChild(parent, child);
                if (index < 0)
                        return -1;
                return convertRowIndexToView(parent, index);
        }

        @Override
        public Object getRoot() {
                return model.getRoot();
        }

        @Override
        public boolean isLeaf(Object node) {
                return model.isLeaf(node);
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
                model.valueForPathChanged(path, newValue);
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {
                listenerList.add(TreeModelListener.class, l);
        }
       
        @Override
        public void removeTreeModelListener(TreeModelListener l) {
                listenerList.remove(TreeModelListener.class, l);
        }
       
        /**
         * Set the comparator that compares nodes in sorting.
         * @param comparator
         * @see #getComparator()
         */
        public void setComparator(Comparator<I> comparator) {
                handler.setComparator(comparator);
        }
       
        /**
         * @return comparator that compares nodes
         * @see #setComparator(Comparator)
         */
        public Comparator<I> getComparator() {
                return handler.getComparator();
        }
       
        public void setSortOrder(SortOrder newOrder) {
                SortOrder oldOrder = sortOrder;
                if (oldOrder == newOrder)
                        return;
                sortOrder = newOrder;
                ArrayList<TreePath> paths = null;
                switch (newOrder) {
                case ASCENDING:
                        if (oldOrder == SortOrder.DESCENDING) {
                                flip();
                        } else {
                                paths = sort();
                        }
                        break;
                case DESCENDING:
                        if (oldOrder == SortOrder.ASCENDING) {
                                flip();
                        } else {
                                paths = sort();
                        }
                        break;
                case UNSORTED:
                        unsort();
                        break;
                }
                fireTreeStructureChangedAndExpand(new TreePath(getRoot()), paths);
        }
       
        public SortOrder getSortOrder() {
                return sortOrder;
        }
       
        public void toggleSortOrder() {
                setSortOrder(sortOrder == SortOrder.DESCENDING ?
                                SortOrder.ASCENDING : SortOrder.DESCENDING);
        }
       
       
        /**
         * Flip all sorted paths.
         */
        private void flip() {
                for (Converter c : converters.values()) {
                        flip(c.viewToModel);
                }
        }


        /**
         * Flip array.
         * @param array
         */
        private static void flip(int[] array) {
                for (int left=0, right=array.length-1;
                                left<right; left++, right--) {
                        int tmp = array[left];
                        array[left] = array[right];
                        array[right] = tmp;
                }
        }
       
        private void unsort() {
                if (filter == null) {
                        converters = null;
                } else {
                        Iterator<Converter> cons = converters.values().iterator();
                        while (cons.hasNext()) {
                                Converter converter = cons.next();
                                if (!converter.isFiltered()) {
                                        cons.remove();
                                } else {
                                        Arrays.sort(converter.viewToModel);
                                }
                        }
                }
        }
       
        /**
         * Sort root and expanded descendants.
         * @return list of paths that were sorted
         */
        private ArrayList<TreePath> sort() {
                if (converters == null)
                        converters = createConvertersMap(); //new IdentityHashMap<Object,Converter>();
                return sortHierarchy(new TreePath(model.getRoot()));
        }

        /**
         * Sort path and expanded descendants.
         * @param path
         * @return list of paths that were sorted
         */
        private ArrayList<TreePath> sortHierarchy(TreePath path) {
                ValueIndexPair<I>[] pairs = new ValueIndexPair[30];
                ArrayList<TreePath> list = new ArrayList<TreePath>();
                pairs = sort(path.getLastPathComponent(), pairs);
                list.add(path);
                Enumeration<TreePath> paths = tree.getExpandedDescendants(path);
                if (paths != null)
                        while (paths.hasMoreElements()) {
                                path = paths.nextElement();
                                pairs = sort(path.getLastPathComponent(), pairs);
                                list.add(path);
                        }
                return list;
        }
       
        private ValueIndexPair<I>[] sort(Object node, ValueIndexPair<I>[] pairs) {
                Converter converter = getConverter(node);
                TreeModel mdl = model;
                int[] vtm;
                if (converter != null) {
                        vtm = converter.viewToModel;
                        if (pairs.length < vtm.length)
                                pairs = new ValueIndexPair[vtm.length];
                        for (int i=vtm.length; --i>=0;) {
                                int idx = vtm[i];
                                pairs[i] = new ValueIndexPair(idx, mdl.getChild(node, idx));
                        }
                } else {
                        int count = mdl.getChildCount(node);
                        if (count <= 0)
                                return pairs;
                        if (pairs.length < count)
                                pairs = new ValueIndexPair[count];
                        for (int i=count; --i>=0;)
                                pairs[i] = new ValueIndexPair(i, mdl.getChild(node, i));
                        vtm = new int[count];
                }
                Arrays.sort(pairs, 0, vtm.length, handler);
                for (int i=vtm.length; --i>=0;)
                        vtm[i] = pairs[i].index;
                if (converter == null) {
                        converters.put(node, new Converter(vtm, false));
                }
                if (sortOrder == SortOrder.DESCENDING)
                        flip(vtm);
                return pairs;
        }
       
        public void setFilter(Filter filter) {
                setFilter(filter, null);
        }
       
        public void setFilter(Filter filter, TreePath startingPath) {
                if (filter == null && startingPath != null)
                        throw new IllegalArgumentException();
                if (startingPath != null && startingPath.getPathCount() == 1)
                        startingPath = null;
                Filter oldFilter = this.filter;
                TreePath oldStartPath = filterStartPath;
                this.filter = filter;
                filterStartPath = startingPath;
                applyFilter(oldFilter, oldStartPath, true);
        }
       
        public Filter getFilter() {
                return filter;
        }
       
        public TreePath getFilterStartPath() {
                return filterStartPath;
        }
       
       
        private void applyFilter(Filter oldFilter, TreePath oldStartPath, boolean sort) {
                TreePath startingPath = filterStartPath;
                ArrayList<TreePath> expand = null;
                toSelect=new ArrayList<TreePath>();
                if (filter == null) {
                        converters = null;
                } else {
                        if (converters == null || startingPath == null) {
                                converters = createConvertersMap(); //new IdentityHashMap<Object,Converter>();
                        } else if (oldFilter != null) {
                                // unfilter the oldStartPath if oldStartPath isn't descendant of startingPath
                                if (oldStartPath == null) {
                                        converters = createConvertersMap(); //new IdentityHashMap<Object,Converter>();
                                        fireTreeStructureChangedAndExpand(new TreePath(getRoot()), null);
                                } else if (!startingPath.isDescendant(oldStartPath)) {
                                        Object node = oldStartPath.getLastPathComponent();
                                        handler.removeConverter(getConverter(node), node);
                                        fireTreeStructureChangedAndExpand(oldStartPath, null);
                                }
                        }
                        expand = new ArrayList<TreePath>();
                       //???
                    //    toSelect.add(new TreePath(getRoot()));
                        TreePath path = startingPath != null ? startingPath : new TreePath(getRoot());
                        if (!applyFilter(filter, path, expand, toSelect)) {
                                converters.put(path.getLastPathComponent(), new Converter(Converter.EMPTY, true));
                        }
                }
               
                if (startingPath == null)
                        startingPath = new TreePath(getRoot());
                fireTreeStructureChanged(startingPath);
                expandPaths(expand);
             
                if (sort && sortOrder != SortOrder.UNSORTED) {
                        if (filter == null)
                                converters = createConvertersMap(); //new IdentityHashMap<Object,Converter>();
                        if (startingPath.getPathCount() > 1 && oldFilter != null) {
                                // upgrade startingPath or sort oldStartPath
                                if (oldStartPath == null) {
                                        startingPath = new TreePath(getRoot());
                                } else if (oldStartPath.isDescendant(startingPath)) {
                                        startingPath = oldStartPath;
                                } else if (!startingPath.isDescendant(oldStartPath)) {
                                        expand = sortHierarchy(oldStartPath);
                                        fireTreeStructureChanged(oldStartPath);
                                        expandPaths(expand);
                                }
                        }
                        expand = sortHierarchy(startingPath);
                        fireTreeStructureChanged(startingPath);
                        expandPaths(expand);
                        
                       
                    
                       
                       
                }

        }
       
        private boolean applyFilter(Filter filter, TreePath path, ArrayList<TreePath> expand, ArrayList<TreePath> toSelect) {
                Object node = path.getLastPathComponent();
                int count = model.getChildCount(node);
                int[] viewToModel = null;
                int viewIndex = 0;
                boolean needsExpand = false;
                boolean isExpanded = false;
               
                for (int i=0; i<count; i++) {
                        Object child = model.getChild(node, i);
                        boolean leaf = model.isLeaf(child);
                        if (filter.acceptNode(path, child, leaf)) {
                           // System.out.println(child);
                          //  toSelect.add(path);
                          path.pathByAddingChild(child); //AQUI
                           toSelect.add( new TreePath(((DefaultMutableTreeNode) child).getPath()));
                            // System.out.println(path);
                                if (viewToModel == null)
                                        viewToModel = new int[count-i];
                                viewToModel[viewIndex++] = i;
                                needsExpand = true;
                        }
                        else if(!leaf) {

                                if (applyFilter(filter, path.pathByAddingChild(child), expand,toSelect)) {
                                //   toSelect.add(path);
                                // tree.getCheckingModel().addCheckingPath(path);
                                        if (viewToModel == null)
                                                viewToModel = new int[count-i];
                                        viewToModel[viewIndex++] = i;
                                        isExpanded = true;
                                }

                               
                        }
             
                        
                }
                if (needsExpand && expand != null && !isExpanded && path.getPathCount() > 1) {
                	 //novo
                   
                    expand.add(path);
                }
                if (viewToModel != null) {
                        if (viewIndex < viewToModel.length)
                                viewToModel = Arrays.copyOf(viewToModel, viewIndex);
                        // a node must have a converter to signify that tree modifications
                        // need to query the filter, so have to put in converter
                        // even if viewIndex == viewToModel.length
                        converters.put(node, new Converter(viewToModel, true));
                        return true;
                }
                return false;
        }


       
        private void expandPaths(ArrayList<TreePath> paths) {
                if (paths == null || paths.isEmpty())
                        return;
                CheckboxTree tre = tree;
                for (TreePath path : paths)
                        tre.expandPath(path);
        }
       
       
        private void fireTreeStructureChangedAndExpand(TreePath path, ArrayList<TreePath> list) {
                Enumeration<TreePath> paths = list != null ?
                                Collections.enumeration(list) : tree.getExpandedDescendants(path);
                fireTreeStructureChanged(path);
                if (paths != null)
                        while (paths.hasMoreElements())
                                tree.expandPath(paths.nextElement());
                
        }

       
       
        protected void fireTreeStructureChanged(TreePath path) {
                Object[] listeners = listenerList.getListenerList();
                TreeModelEvent e = null;
                for (int i = listeners.length-2; i>=0; i-=2) {
                        if (listeners[i]==TreeModelListener.class) {
                                if (e == null)
                                        e = new TreeModelEvent(this, path, null, null);
                                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
                        }
                }
        }
       
        protected void fireTreeNodesChanged(TreePath path, int[] childIndices, Object[] childNodes) {
                Object[] listeners = listenerList.getListenerList();
                TreeModelEvent e = null;
                for (int i = listeners.length-2; i>=0; i-=2) {
                        if (listeners[i]==TreeModelListener.class) {
                                if (e == null)
                                        e = new TreeModelEvent(this, path, childIndices, childNodes);
                                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
                        }
                }
        }
       
        protected void fireTreeNodesInserted(TreePath path, int[] childIndices, Object[] childNodes) {
                Object[] listeners = listenerList.getListenerList();
                TreeModelEvent e = null;
                for (int i = listeners.length-2; i>=0; i-=2) {
                        if (listeners[i]==TreeModelListener.class) {
                                if (e == null)
                                        e = new TreeModelEvent(this, path, childIndices, childNodes);
                                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
                        }
                }
        }

        protected void fireTreeNodesRemoved(TreePath path, int[] childIndices, Object[] childNodes) {
                Object[] listeners = listenerList.getListenerList();
                TreeModelEvent e = null;
                for (int i = listeners.length-2; i>=0; i-=2) {
                        if (listeners[i]==TreeModelListener.class) {
                                if (e == null)
                                        e = new TreeModelEvent(this, path, childIndices, childNodes);
                                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
                        }
                }
        }
       
       
        protected class Handler implements Comparator<ValueIndexPair<I>>,
                        TreeModelListener, TreeExpansionListener {
               
                private Comparator<I> comparator;
               
                private Collator collator = Collator.getInstance();
               
                void setComparator(Comparator<I> cmp) {
                        comparator = cmp;
                        collator = cmp == null ? Collator.getInstance() : null;
                }
               
                Comparator<I> getComparator() {
                        return comparator;
                }
               
                @Override
                public void treeCollapsed(TreeExpansionEvent e) {
                        // I don't recall implementing a tree collapsion listener
                }

                @Override
                public void treeExpanded(TreeExpansionEvent e) {
                        if (sortOrder != SortOrder.UNSORTED) {
                                TreePath path = e.getPath();
                                Converter converter = getConverter(path.getLastPathComponent());
                                if (converter == null) {
                                        ArrayList<TreePath> paths = sortHierarchy(path);
                                        fireTreeStructureChangedAndExpand(path, paths);
                                }
                        }
                }
               
                private boolean isFiltered(Object node) {
                        Converter c = getConverter(node);
                        return c == null ? false : c.isFiltered();
                }
               
                private boolean acceptable(TreePath path, Object[] childNodes, int index, ArrayList<TreePath> expand, ArrayList<TreePath> toSelect) {
                        return acceptable(path, childNodes, index) ||
                                        applyFilter(filter, path.pathByAddingChild(childNodes[index]), expand,toSelect);
                }
               
                @Override
                public void treeNodesChanged(TreeModelEvent e) {
                        treeNodesChanged(e.getTreePath(), e.getChildIndices(), e.getChildren());
                }
               
                protected void treeNodesChanged(TreePath path, int[] childIndices, Object[] childNodes) {
                        if (childIndices == null) {
                                // path should be root path
                                // reapply filter
                                if (filter != null)
                                        applyFilter(null, null, true);
                                return;
                        }
                        Converter converter = getConverter(path.getLastPathComponent());
                        ArrayList<TreePath> expand = null;
                        toSelect=new ArrayList<TreePath>();
                        if (converter != null) {
                                expand = new ArrayList<TreePath>();
                                int childIndex = 0;
                                for (int i=0; i<childIndices.length; i++) {
                                        int idx = converter.convertRowIndexToView(childIndices[i]);
                                        if (idx >= 0) {
                                                // see if the filter dislikes the nodes new state
                                                if (converter.isFiltered() &&
                                                                !isFiltered(childNodes[i]) &&
                                                                !acceptable(path, childNodes, i)) {
                                                        // maybe it likes a child nodes state
                                                        if (!applyFilter(filter, path.pathByAddingChild(childNodes[i]), expand, toSelect)){
                                                                remove(path, childNodes[i]);
                                                        }
                                                        continue;
                                                }
                                                childNodes[childIndex] = childNodes[i];
                                                childIndices[childIndex++] = idx;
                                        } else if (acceptable(path, childNodes, i, expand, toSelect)) {
                                                int viewIndex = insert(path.getLastPathComponent(),
                                                                childNodes[i], childIndices[i], converter);
                                               // toSelect.add(path);  //MUDEI
                                                fireTreeNodesInserted(path, indices(viewIndex), nodes(childNodes[i]));
                                        }
                                }
                                if (childIndex == 0) {
                                        maybeFireStructureChange(path, expand);
                                        return;
                                }
                                if (sortOrder != SortOrder.UNSORTED && converter.getChildCount() > 1) {
                                        sort(path.getLastPathComponent(), new ValueIndexPair[converter.getChildCount()]);
                                        fireTreeStructureChangedAndExpand(path, null);
                                        expandPaths(expand);
                                        return;
                                }
                                if (childIndex != childIndices.length) {
                                        childIndices = Arrays.copyOf(childIndices, childIndex);
                                        childNodes = Arrays.copyOf(childNodes, childIndex);
                                }
                        } else if (filter != null && isFilteredOut(path)) {
                                // see if the filter likes the nodes new states
                                expand = new ArrayList<TreePath>();
                                int[] vtm = null;
                                int idx = 0;
                                for (int i=0; i<childIndices.length; i++) {
                                        if (acceptable(path, childNodes, i, expand,toSelect)) {
                                                if (vtm == null)
                                                        vtm = new int[childIndices.length-i];
                                                vtm[idx++] = childIndices[i];
                                        }
                                }
                                // filter in path if appropriate
                                if (vtm != null)
                                        filterIn(vtm, idx, path, expand);
                                return;
                        }
                        // must fire tree nodes changed even if a
                        // structure change will be fired because the
                        // expanded paths need to be updated first
                        fireTreeNodesChanged(path, childIndices, childNodes);
                        maybeFireStructureChange(path, expand);
                        
                }
               
                /**
                 * Helper method for treeNodesChanged...
                 * @param path
                 * @param expand
                 */
                private void maybeFireStructureChange(TreePath path, ArrayList<TreePath> expand) {
                        if (expand != null && !expand.isEmpty()) {
                                Enumeration<TreePath> expanded = tree.getExpandedDescendants(path);
                                fireTreeStructureChanged(path);
                                if (expanded != null)
                                        while (expanded.hasMoreElements())
                                                tree.expandPath(expanded.nextElement());
                                expandPaths(expand);
                        }
                }
               
                @Override
                public void treeNodesInserted(TreeModelEvent e) {
                        treeNodesInserted(e.getTreePath(), e.getChildIndices(), e.getChildren());
                }


                protected void treeNodesInserted(TreePath path, int[] childIndices, Object[] childNodes) {
                        Object parent = path.getLastPathComponent();
                        Converter converter = getConverter(parent);
                        ArrayList<TreePath> expand = null;
                        toSelect = new ArrayList<TreePath>();
                        if (converter != null) {
                                int childIndex = 0;
                                for (int i=0; i<childIndices.length; i++) {
                                        if (converter.isFiltered()) {
                                                // path hasn't met the filter criteria, so childNodes must be filtered
                                                if (expand == null)
                                                        expand = new ArrayList<TreePath>();
                                                if (!applyFilter(filter, path.pathByAddingChild(childNodes[i]), expand,toSelect))
                                                        continue;
                                        }
                                        // shift the appropriate cached modelIndices
                                        int[] vtm = converter.viewToModel;
                                        int modelIndex = childIndices[i];
                                        for (int j=vtm.length; --j>=0;) {
                                                if (vtm[j] >= modelIndex)
                                                        vtm[j] += 1;
                                        }
                                        // insert modelIndex to converter
                                        int viewIndex = insert(parent, childNodes[i], modelIndex, converter);
                                        childNodes[childIndex] = childNodes[i];
                                        childIndices[childIndex++] = viewIndex;
                                }
                                if (childIndex == 0)
                                        return;
                                if (childIndex != childIndices.length) {
                                        childIndices = Arrays.copyOf(childIndices, childIndex);
                                        childNodes = Arrays.copyOf(childNodes, childIndex);
                                }
                                if (childIndex > 1 && sortOrder != SortOrder.UNSORTED) {
                                        sort(childIndices, childNodes);
                                }
                        } else if (filter != null && isFilteredOut(path)) {
                                // apply filter to inserted nodes
                                int[] vtm = null;
                                int idx = 0;
                                expand = new ArrayList<TreePath>();
                                for (int i=0; i<childIndices.length; i++) {
                                        if (acceptable(path, childNodes, i, expand, toSelect)) {
                                                if (vtm == null)
                                                        vtm = new int[childIndices.length-i];
                                                vtm[idx++] = childIndices[i];
                                        }
                                }
                                // filter in path if appropriate
                                if (vtm != null)
                                        filterIn(vtm, idx, path, expand);
                                return;
                        }
                        fireTreeNodesInserted(path, childIndices, childNodes);
                        expandPaths(expand);
                }
               
                @Override
                public void treeNodesRemoved(TreeModelEvent e) {
                        treeNodesRemoved(e.getTreePath(), e.getChildIndices(), e.getChildren());
                }
               

                private boolean isFilterStartPath(TreePath path) {
                        if (filterStartPath == null)
                                return path.getPathCount() == 1;
                        return filterStartPath.equals(path);
                }
               
                protected void treeNodesRemoved(TreePath path, int[] childIndices, Object[] childNodes) {
                        Object parent = path.getLastPathComponent();
                        Converter converter = getConverter(parent);
                        if (converter != null) {
                                int childIndex = 0;
                                for (int i=0; i<childNodes.length; i++) {
                                        // TODO probably stuck with the same situation as treeStructureChanged
                                        // not enough information to remove all child node converters
                                        // thus memory leakage
                                        // Though as long as the node is a TreeNode and it's structure is intact,
                                        // DefaultTreeModel will still provide all its children so that the below method
                                        // will (seem to) work (with DefaultTreeModel)
                                        removeConverter(childNodes[i]);
                                        int viewIndex = converter.remove(childIndices[i]);
                                        switch (viewIndex) {
                                        default:
                                                childNodes[childIndex] = childNodes[i];
                                                childIndices[childIndex++] = viewIndex;
                                                break;
                                        case Converter.ONLY_INDEX:
                                                if (isFiltered(childNodes[i])) {
                                                        remove(path.getParentPath(), parent);
                                                } else {
                                                        // parent has become a leaf
                                                        if (filter != null && isFilterStartPath(path)) {
                                                                converters.put(path.getLastPathComponent(), new Converter(Converter.EMPTY, true));
                                                        } else {
                                                                converters.remove(parent);
                                                        }
                                                }
                                                break;
                                        case Converter.INDEX_NOT_FOUND:
                                        }
                                }
                                if (childIndex == 0)
                                        return;
                                if (childIndex != childIndices.length) {
                                        childIndices = Arrays.copyOf(childIndices, childIndex);
                                        childNodes = Arrays.copyOf(childNodes, childIndex);
                                }
                                if (childIndex > 1 && sortOrder != SortOrder.UNSORTED) {
                                        sort(childIndices, childNodes);
                                }
                        } else if (filter != null && isFilteredOut(path)) {
                                return;
                        }
                        fireTreeNodesRemoved(path, childIndices, childNodes);
                }

                @Override
                public void treeStructureChanged(TreeModelEvent e) {
                        if (converters != null) {
                                // not enough information to properly clean up
                                // reapply filter/sort
                                converters = createConvertersMap();
                                if (filter != null) {
                                        applyFilter(null, null, false);
                                }
                                if (sortOrder != SortOrder.UNSORTED) {
                                        TreePath path = new TreePath(getRoot());
                                        ArrayList<TreePath> expand = sortHierarchy(path);
                                        fireTreeStructureChangedAndExpand(path, expand);
                                }
                                // at least one of the above if statements better execute!
                        } else {
                                fireTreeStructureChanged(e.getTreePath());
                        }
                       
                }
               

                @Override
                public final int compare(ValueIndexPair<I> o1, ValueIndexPair<I> o2) {
                        return compareNodes(o1.value, o2.value);
                }

               
                protected int compareNodes(I a, I b) {
                        if (comparator != null)
                                return comparator.compare(a, b);
                        return collator.compare(a.toString(), b.toString());
                }

                private void removeConverter(Object node) {
                        Converter c = getConverter(node);
                        if (c != null)
                                removeConverter(c, node);
                }
               
                private void removeConverter(Converter converter, Object node) {
                        for (int i=converter.getChildCount(); --i>=0;) {
                                int index = converter.convertRowIndexToModel(i);
                                Object child = model.getChild(node, index);
                                Converter c = getConverter(child);
                                if (c != null)
                                        removeConverter(c, child);
                        }
                        converters.remove(node);
                }
               
                private boolean isFilteredOut(TreePath path) {
                        if (filterStartPath != null && !filterStartPath.isDescendant(path))
                                return false;
                        TreePath parent = path.getParentPath();
                        // root should always have a converter if filter is non-null,
                        // so if parent is ever null, there is a bug somewhere else
                        Converter c = getConverter(parent.getLastPathComponent());
                        if (c != null) {
                                return getIndexOfChild(
                                                parent.getLastPathComponent(),
                                                path.getLastPathComponent()) < 0;
                        }
                        return isFilteredOut(parent);
                }
               
                private void filterIn(int[] vtm, int vtmLength, TreePath path, ArrayList<TreePath> expand) {
                        Object node = path.getLastPathComponent();
                        if (vtmLength != vtm.length)
                                vtm = Arrays.copyOf(vtm, vtmLength);
                        Converter converter = new Converter(vtm, true);
                        converters.put(node, converter);
                        insert(path.getParentPath(), node);
                        tree.expandPath(path);
                        expandPaths(expand);
                         System.out.println("bubu");  System.out.println(converters.values());
                        System.out.println(converters.keySet());
                          System.out.println(expand);


                }

                private boolean acceptable(TreePath path, Object[] nodes, int index) {
                        Object node = nodes[index];
                        return filter.acceptNode(path, node, model.isLeaf(node));
                       
                }
               
                private int ascInsertionIndex(int[] vtm, Object parent, Object node, int idx) {
                        for (int i=vtm.length; --i>=0;) {
                                int cmp = compareNodes((I)node, (I)model.getChild(parent, vtm[i]));
                                if (cmp > 0 || (cmp == 0 && idx > vtm[i])) {
                                        return i+1;
                                }
                        }
                        return 0;
                }
               
               
                private int dscInsertionIndex(int[] vtm, Object parent, Object node, int idx) {
                        for (int i=vtm.length; --i>=0;) {
                                int cmp = compareNodes((I)node, (I)model.getChild(parent, vtm[i]));
                                if (cmp < 0) {
                                        return i+1;
                                } else if (cmp == 0 && idx < vtm[i]) {
                                        return i;
                                }
                        }
                        return 0;
                }


               
                /**
                 * Inserts the specified path and node and any parent paths as necessary.
                 * <p>
                 * Fires appropriate event.
                 * @param path
                 * @param node
                 */
                private void insert(TreePath path, Object node) {
                        Object parent = path.getLastPathComponent();
                        Converter converter = converters.get(parent);
                        int modelIndex = model.getIndexOfChild(parent, node);
                        if (converter == null) {
                                converter = new Converter(indices(modelIndex), true);
                                converters.put(parent, converter);
                                insert(path.getParentPath(), parent);
                        } else {
                                int viewIndex = insert(parent, node, modelIndex, converter);
                                fireTreeNodesInserted(path, indices(viewIndex), nodes(node));
                        }
                }
               
                /**
                 * Inserts node into parent in correct sort order.
                 * <p>
                 * Responsibility of caller to fire appropriate event with the returned viewIndex.
                 * @param path
                 * @param node
                 * @param modelIndex
                 * @param converter
                 * @return viewIndex
                 */
                private int insert(Object parent, Object node, int modelIndex, Converter converter) {
                        int[] vtm = converter.viewToModel;
                        int viewIndex;
                        switch (sortOrder) {
                        case ASCENDING:
                                viewIndex = ascInsertionIndex(vtm, parent, node, modelIndex);
                                break;
                        case DESCENDING:
                                viewIndex = dscInsertionIndex(vtm, parent, node, modelIndex);
                                break;
                        default: case UNSORTED:
                                viewIndex = unsortedInsertionIndex(vtm, modelIndex);
                                break;
                        }
                        int[] a = new int[vtm.length+1];
                        System.arraycopy(vtm, 0, a, 0, viewIndex);
                        System.arraycopy(vtm, viewIndex, a, viewIndex+1, vtm.length-viewIndex);
                        a[viewIndex] = modelIndex;
                        converter.viewToModel = a;
                        return viewIndex;
                }
               
                private void remove(TreePath path, Object node) {
                        Object parent = path.getLastPathComponent();
                        Converter converter = converters.get(parent);
                        int modelIndex = model.getIndexOfChild(parent, node);
                        int viewIndex = converter.remove(modelIndex);
                        switch (viewIndex) {
                        default:
                                removeConverter(node);
                                fireTreeNodesRemoved(path, indices(viewIndex), nodes(node));
                                break;
                        case Converter.ONLY_INDEX:
                                if (path.getParentPath() == null) {
                                        // reached root
                                        removeConverter(node);
                                        converters.put(parent, new Converter(Converter.EMPTY, true));
                                        fireTreeNodesRemoved(path, indices(0), nodes(node));
                                        return;
                                }
                                remove(path.getParentPath(), parent);
                                break;
                        case Converter.INDEX_NOT_FOUND:
                                removeConverter(node);
                        }
                }
               
               
               
        }
       
       

        private static int unsortedInsertionIndex(int[] vtm, int idx) {
                for (int i=vtm.length; --i>=0;)
                        if (vtm[i] < idx)
                                return i+1;
                return 0;
        }
       
        private static void sort(int[] childIndices, Object[] childNodes) {
                int len = childIndices.length;
                ValueIndexPair[] pairs = new ValueIndexPair[len];
                for (int i=len; --i>=0;)
                        pairs[i] = new ValueIndexPair(childIndices[i], childNodes[i]);
                Arrays.sort(pairs);
                for (int i=len; --i>=0;) {
                        childIndices[i] = pairs[i].index;
                        childNodes[i] = pairs[i].value;
                }
        }
       
        private static int[] indices(int...indices) {
                return indices;
        }
       
        private static Object[] nodes(Object...nodes) {
                return nodes;
        }
       


       
        /**
         * This class has a dual purpose, both related to comparing/sorting.
         * <p>
         * The Handler class sorts an array of ValueIndexPair based on the value.
         * Used for sorting the view.
         * <p>
         * ValueIndexPair sorts itself based on the index.
         * Used for sorting childIndices for fire* methods.
         */
        private static class ValueIndexPair<I> implements Comparable<ValueIndexPair<I>> {
                ValueIndexPair(int idx, I val) {
                        index = idx;
                        value = val;
                }
                I value;
                int index;
               
                public int compareTo(ValueIndexPair<I> o) {
                        return index - o.index;
                }
        }
       
        private static class Converter {
               
                static final int[] EMPTY = new int[0];
               
                static final int ONLY_INDEX = -2;
               
                static final int INDEX_NOT_FOUND = -1;
               
                Converter(int[] vtm, boolean filtered) {
                        viewToModel = vtm;
                        isFiltered = filtered;
                }
               
                private int[] viewToModel;
               
                private boolean isFiltered;
               
                boolean isFiltered() {
                        return isFiltered;
                }
               
                /**
                 * @param modelIndex
                 * @return viewIndex that was removed<br>
                 *              or <code>ONLY_INDEX</code> if the modelIndex is the only one in the view<br>
                 *              or <code>INDEX_NOT_FOUND</code> if the modelIndex is not in the view
                 */
                int remove(int modelIndex) {
                        int[] vtm = viewToModel;
                        for (int i=vtm.length; --i>=0;) {
                                if (vtm[i] > modelIndex) {
                                        vtm[i] -= 1;
                                } else if (vtm[i] == modelIndex) {
                                        if (vtm.length == 1) {
                                                viewToModel = EMPTY;
                                                return ONLY_INDEX;
                                        }
                                        int viewIndex = i;
                                        while (--i>=0) {
                                                if (vtm[i] > modelIndex)
                                                        vtm[i] -= 1;
                                        }
                                        int[] a = new int[vtm.length-1];
                                        if (viewIndex > 0)
                                                System.arraycopy(vtm, 0, a, 0, viewIndex);
                                        int len = a.length-viewIndex;
                                        if (len > 0)
                                                System.arraycopy(vtm, viewIndex+1, a, viewIndex, len);
                                        viewToModel = a;
                                        return viewIndex;
                                }
                        }
                        return INDEX_NOT_FOUND;
                }
               
                int getChildCount() {
                        return viewToModel.length;
                }
               
                /**
                 * @param modelIndex
                 * @return viewIndex corresponding to modelIndex<br>
                 *              or <code>INDEX_NOT_FOUND</code> if the modelIndex is not in the view
                 */
                int convertRowIndexToView(int modelIndex) {
                        int[] vtm = viewToModel;
                        for (int i=vtm.length; --i>=0;) {
                                if (vtm[i] == modelIndex)
                                        return i;
                        }
                        return INDEX_NOT_FOUND;
                }
               
                int convertRowIndexToModel(int viewIndex) {
                        return viewToModel[viewIndex];
                }
        }


        public interface Filter {
                boolean acceptNode(TreePath parent, Object node, boolean leaf);
        }

        public static class RegexFilter implements Filter {

            
               
                public RegexFilter(Pattern pattern, boolean leaf) {
                       
                            matcher = pattern.matcher("");


                        leafOnly = leaf;
                }
               
                private Matcher matcher;
               
                private boolean leafOnly;
               
                public boolean acceptNode(TreePath parent, Object node, boolean leaf) {
                        if (leafOnly && !leaf)
                                return false;
                       
                        matcher.reset(getStringValue(node));
                        return matcher.find();
                }
               
                protected String getStringValue(Object node) {
                        return node.toString();
                }
        }
       

        private static Map<Object,Converter> createConvertersMap() {
                return new HashMap<Object,Converter>();
        }
}