/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.visualization;

/**
 *
 * @author Marta Nascimento
 */
public interface IGTPanel {
    
    public GView getGView();
    public PersistentVisualization getPersistentVisualization();
    public void loadVisualization(PersistentVisualization pv);
}
