/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.project;

import net.phyloviz.upgmanjcore.visualization.PersistentVisualization;

/**
 *
 * @author Marta Nascimento
 */
public interface ProjectItem {
       
    public void addPersistentVisualization(PersistentVisualization cp);
    
    public PersistentVisualization getPersistentVisualization();
    
    public String getMethodProviderName();
    
    public String getFactoryName();
    
    public String getOutput();
    
    public String getDistanceProvider();
    
    public String getAlgorithmLevel();
}
