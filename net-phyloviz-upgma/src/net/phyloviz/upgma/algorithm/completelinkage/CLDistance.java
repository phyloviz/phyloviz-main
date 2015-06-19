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
package net.phyloviz.upgma.algorithm.completelinkage;

import net.phyloviz.upgma.algorithm.*;
import java.util.Comparator;
import net.phyloviz.algo.Edge;
import net.phyloviz.upgma.tree.UPGMALeafNode;

public class CLDistance implements HierarchicalClusteringDistance {

    @Override
    public float distance(float d1, float d2) {
        return d1 > d2 ? d1 : d2;
    }
    
    @Override
    public int level(UPGMALeafNode px, UPGMALeafNode py) {
        int diffs = 0;
        for (int i = 0; i < px.profileLength(); i++) {
            if (px.getValue(i).compareTo(py.getValue(i)) != 0) {
                diffs++;
            }
        }
        return diffs;
    }

    @Override
    public int level(Edge<UPGMALeafNode> e) {
        throw new UnsupportedOperationException("Complete-Linkage - level");
    }

    @Override
    public int compare(Edge<UPGMALeafNode> f, Edge<UPGMALeafNode> e) {
        throw new UnsupportedOperationException("Complete-Linkage - compareEdges");
    }

    @Override
    public int compare(UPGMALeafNode px, UPGMALeafNode py) {
        throw new UnsupportedOperationException("Complete-Linkage - compareNodes");
    }

    @Override
    public Comparator<UPGMALeafNode> getProfileComparator() {
        throw new UnsupportedOperationException("Complete-Linkage - getProfileComparator");
    }

    @Override
    public Comparator<Edge<UPGMALeafNode>> getEdgeComparator() {
        throw new UnsupportedOperationException("Complete-Linkage - getEdgeComparator");
    }

    @Override
    public String toString() {
        return "Complete-Linkage distance";
    }

    @Override
    public String info(UPGMALeafNode px, UPGMALeafNode py) {
        return null;
    }

    @Override
    public String info(Edge<UPGMALeafNode> e) {
        return null;
    }

    @Override
    public boolean configurable() {
        return false;
    }

    @Override
    public void configure() {
        // Do nothing
    }

    @Override
    public int maxLevel() {
        return -1;
    }

}
