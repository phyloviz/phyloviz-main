/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.project;

import net.phyloviz.upgmanjcore.visualization.Visualization;

/**
 *
 * @author Marta Nascimento
 */
public interface ProjectItem {
       
    public void addVisualization(Visualization cp);
    
    public Visualization getVisualization();
    
    public String getMethodProviderName();
    
    public String getFactoryName();
    
    public String getOutput();
    
    public String getDistanceProvider();
    
    public String getAlgorithmLevel();
}
