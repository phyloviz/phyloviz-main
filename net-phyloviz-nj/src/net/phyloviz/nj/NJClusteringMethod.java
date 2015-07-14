/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj;

import net.phyloviz.core.data.Profile;

/**
 *
 * @author Adriano Sousa
 * @param <T>
 */
public interface NJClusteringMethod <T extends Profile> {

    public AgglomerativeClusteringMethod<T> getMethod();
    
}
