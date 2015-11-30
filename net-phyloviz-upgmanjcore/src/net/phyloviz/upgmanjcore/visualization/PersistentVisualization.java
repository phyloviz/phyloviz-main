/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.visualization;

import java.io.Serializable;
import java.util.Map;


/**
 *
 * @author Marta Nascimento
 */
public class PersistentVisualization implements Serializable{
    
    public float distanceFilterValue = -1;
    public boolean linearSize;
    public Map<String, Point> nodesPositions;
    public String filter;
}
