/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.visualization;

import java.io.Serializable;
import net.phyloviz.category.CategoryProvider;


/**
 *
 * @author Marta Nascimento
 */
public class PersistentVisualization implements Serializable{
    
    public CategoryProvider categoryProvider;
    
    public float distanceFilterValue = -1;
    
    public boolean linearSize;
    
}
