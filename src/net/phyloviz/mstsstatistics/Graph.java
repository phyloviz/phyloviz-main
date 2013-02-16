/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
