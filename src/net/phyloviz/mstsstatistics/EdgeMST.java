/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.mstsstatistics;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Morrighan
 */
public class EdgeMST implements Comparable<EdgeMST> {
    
private int _source;
    private int _dest;
    private int _level;
    private double _rationmsts;
    
    public EdgeMST (int source, int dest, int level){
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
        
        int dif = 2;
        
        if(s==d) {
            dif = 1;
        }
        
        int[] array = new int[calcDet[mapaux[u]].size()-dif];
        int index = 0;
        Iterator<Integer> it = ((ArrayList<Integer>) calcDet[mapaux[u]]).iterator();
        
        while (it.hasNext()){
            int el = it.next();
            if(el!= s && el != d) {
                array[index++] = el;
            }
        }
        
        Algebra a = new Algebra();
        double det = a.det(m.viewSelection(array, array));
        if (det == 0) {
            det = 1;
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
        return _source;
    }

    public void setSource(int source) {
        this._source = source;
    }

    public int getDest() {
        return _dest;
    }

    public void setDest(int dest) {
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
