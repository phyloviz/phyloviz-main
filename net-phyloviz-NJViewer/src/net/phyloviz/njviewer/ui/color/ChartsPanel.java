package net.phyloviz.njviewer.ui.color;

import java.awt.GridLayout;
import org.openide.util.HelpCtx;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Cátia Vaz
 */
public class ChartsPanel extends TopComponent {

    public ChartsPanel(int charts) {
        this.setLayout(new GridLayout(1, charts));

    }

    @Override
    public void open() {
        Mode m = WindowManager.getDefault().findMode("output");
        m.dockInto(this);
        super.open();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return "ChartLegend";
    }
}
