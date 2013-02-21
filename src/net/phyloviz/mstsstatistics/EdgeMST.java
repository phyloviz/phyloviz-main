/*-
 * Copyright (c) 2013, PHYLOViZ Team <phyloviz@gmail.com>
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
package net.phyloviz.mstsstatistics;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import java.util.ArrayList;
import java.util.Iterator;
import net.phyloviz.goeburst.tree.GOeBurstNode;

/**
 *
 * @author Morrighan
 */
public class EdgeMST implements Comparable<EdgeMST> {
    
    private GOeBurstNode _source;
    private GOeBurstNode _dest;
    private int _level;
    private double _rationmsts;
    
    public EdgeMST (GOeBurstNode source, GOeBurstNode dest, int level){
        _source = source;
        _dest = dest;
        _level = level;
        _rationmsts = 0;
    }

    public void setNmsts(int[] map, int[] mapaux, ArrayList[] calcDet, SparseDoubleMatrix2D m, double[] calcnmsts) {
        int u = this.getSource();
        int v = this.getDest();
        
        int s = map[u];
        int d = map[v];
        
        if(s==d) {
	    _rationmsts = 0;
	    return ;
        }
        
        int[] array = new int[calcDet[mapaux[u]].size()-2];
        int index = 0;
        Iterator<Integer> it = ((ArrayList<Integer>) calcDet[mapaux[u]]).iterator();
        
        while (it.hasNext()){
            int el = it.next();
            if(el!= s && el != d) {
                array[index++] = el;
            }
        }
        
        double det = 1;
        Algebra a = new Algebra();
        if (index != 0) {
            det = a.det(m.viewSelection(array, array));
        }
        _rationmsts = det/calcnmsts[mapaux[u]];
    }

    public double getNmsts() {
        return _rationmsts;
    }
    
    public void calcMatrixUV(SparseDoubleMatrix2D m){
        double d;
        Algebra a = new Algebra();
        int size = m.size();
        int[] members = new int[size-1];
        int index = 0;
        for (int i = 0; i < size; i++){
            if(i!=this.getSource() || i!= this.getDest()){
                members[index] = i;
                index++;
            }
        }
        _rationmsts = a.det(m.viewSelection(members, members)); 
    }

    public int getSource() {
        return _source.getUID();
    }

    public GOeBurstNode getSourceNode() {
	return _source;
    }
    
    public void setSource(GOeBurstNode source) {
        this._source = source;
    }

    public int getDest() {
        return _dest.getUID();
    }

    public GOeBurstNode getDestNode() {
	return _dest;
    }
    
    public void setDest(GOeBurstNode dest) {
        this._dest = dest;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(int level) {
        this._level = level;
    }

    @Override
    public int compareTo(EdgeMST o) {
        if(o.getLevel()!=this.getLevel()) {
            return this.getLevel() - o.getLevel();
        }
        if(o.getSource()!=this.getSource()) {
            return this.getSource() - o.getSource();
        }
        return this.getDest() - o.getDest();
    }
    
}
