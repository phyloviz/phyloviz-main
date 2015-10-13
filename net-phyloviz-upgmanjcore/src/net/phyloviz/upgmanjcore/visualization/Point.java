/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.visualization;

import java.io.Serializable;

/**
 *
 * @author martanascimento
 */
public class Point implements Serializable{
    
    public double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
