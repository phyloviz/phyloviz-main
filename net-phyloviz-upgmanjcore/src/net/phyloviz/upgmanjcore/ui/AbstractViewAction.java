/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.ui;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Adriano
 */
public abstract class AbstractViewAction extends NodeAction {

    @Override
    protected boolean enable(Node[] nodes) {
        return nodes.length == 1;
    }
    @Override
    public String getName() {
        return "Output";
    }
    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
    @Override
    protected boolean asynchronous() {
        return false;
    }
}