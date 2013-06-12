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
import java.util.List;

/**
 *
 * @author Morrighan
 */
public class Graph {
    
    private List<EdgeMST> edgeslist;
    private List<Integer> vertixlistL1;
    private List<Integer> vertixlistL23;
    private SparseDoubleMatrix2D matrix; //SparseDoubleMatrix2D
    private double det;


    
    public Graph(EdgeMST e){
        edgeslist = new ArrayList<EdgeMST>();
        vertixlistL1 = new ArrayList<Integer>();
        vertixlistL23 = new ArrayList<Integer>();
        edgeslist.add(e);
        if(!hasVertix(e.getSource())) {
            vertixlistL1.add(e.getSource());
        }
        if(!hasVertix(e.getDest())) {
            vertixlistL1.add(e.getDest());
        }
        det = 0;
    }
    
    public Graph(){
        edgeslist = new ArrayList<EdgeMST>();
        vertixlistL1 = new ArrayList<Integer>();
        vertixlistL23 = new ArrayList<Integer>();
        det = 0;
    }
    
    public void setDet(double det) {
        this.det = det;
    }

    public double getDet() {
        return det;
    }
    
    public List<EdgeMST> getEdges(){
        return edgeslist;
    }
    
    public boolean hasVertix(Integer v){
        return vertixlistL1.contains(v);
    }
    
    public boolean hasVertix23(Integer v){
        return vertixlistL23.contains(v);
    }
    
    public boolean hasEdge(EdgeMST e){
        return edgeslist.contains(e);
    }
    
    public void addEdge(EdgeMST e){
        edgeslist.add(e);
        if(!this.hasVertix(e.getSource())) {
            vertixlistL1.add(e.getSource());
        }
        if(!this.hasVertix(e.getDest())) {
            vertixlistL1.add(e.getDest());
        }     
    }
    
    public void addEdge23(EdgeMST e) {
       
        edgeslist.add(e);
        
        if(!this.hasVertix23(e.getSource())) {
            vertixlistL23.add(e.getSource());
        }
        if(!this.hasVertix23(e.getDest())) {
            vertixlistL23.add(e.getDest());
        }
        
    }
    
    public double calcDetMatrixL(int level){
        int l, i, j, s, d;  
        
        if(level == 1){
            l = vertixlistL1.size();
        }else {
            l = vertixlistL23.size();
        }
        
        matrix = new SparseDoubleMatrix2D(l, l);
        
        matrix.assign(0);
        

        
        Iterator<EdgeMST> eIter = edgeslist.iterator();
        while(eIter.hasNext()){
            EdgeMST e = eIter.next();
            if(level == 1){
                s = vertixlistL1.indexOf(e.getSource());
                d = vertixlistL1.indexOf(e.getDest());
               
            }else{
                s = vertixlistL23.indexOf(e.getSource());
                d = vertixlistL23.indexOf(e.getDest());
            }
            matrix.setQuick(s,d, matrix.getQuick(s, d) - 1);
            matrix.setQuick(d,s, matrix.getQuick(d, s) - 1);
            matrix.setQuick(s,s, matrix.getQuick(s, s) + 1);
            matrix.setQuick(d,d, matrix.getQuick(d, d) + 1);
        }       
        
        Algebra a = new Algebra();
        det = a.det(matrix.viewPart(1, 1, matrix.rows() - 1, matrix.columns() - 1));
        return det;
    }
        
    
    public void mergeGraph(Graph b){         
        Iterator<Integer> itB = b.vertixlistL1.iterator();
        while(itB.hasNext()){
            int v = itB.next();
            this.vertixlistL1.add(v);
        }       
        
    }  

    public void removeAllEdges() {
        this.edgeslist.removeAll(edgeslist);
    }

    public void removeAllVertixes23() {
        this.vertixlistL23.removeAll(vertixlistL23);
    }

    public void addVertix(int i) {
        this.vertixlistL1.add(i);
    }
    
    public void addVertix23(int i) {
        this.vertixlistL1.add(i);
    }
    
}
