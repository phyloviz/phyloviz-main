/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.project;

import java.io.File;
import java.io.Reader;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;

/**
 *
 * @author martanascimento
 */
public interface ProjectTypingDataFactory {
    
    String onSave(TypingData<? extends AbstractProfile> td);
    
    TypingData<? extends AbstractProfile> onLoad(Reader r);
}
