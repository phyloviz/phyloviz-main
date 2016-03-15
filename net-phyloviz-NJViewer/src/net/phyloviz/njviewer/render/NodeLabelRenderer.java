/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.njviewer.render;

import net.phyloviz.nj.tree.NJLeafNode;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

/**
 *
 * @author martanascimento
 */
public class NodeLabelRenderer extends LabelRenderer{
    
    public NodeLabelRenderer(String textField) {
        super(textField);
    }
    
    @Override
    protected String getText(VisualItem item) {
        String s = null;
        if (item.canGetString(m_labelName)) {
            if (item.get("st_ref") instanceof NJLeafNode) {
                return item.getString("st_id");
            } else {
                return s;
            }
        }
        return s;
    }
    
}
