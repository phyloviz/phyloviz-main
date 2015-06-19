/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.project;

import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;

/**
 *
 * @author Marta Nascimento
 */
public interface ProjectItemFactory {
    
    public ProjectItem loadData(String datasetName, TypingData<? extends AbstractProfile> td, String directory, String filename);
    
    public String getName();
    
}
