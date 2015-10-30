/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.json;

import java.util.HashMap;

/**
 * Interface for root Node
 * @author Adriano
 */
public interface IJsonWriter{
    HashMap<String, String> getBuildersMap();
    String getRoot();
}
