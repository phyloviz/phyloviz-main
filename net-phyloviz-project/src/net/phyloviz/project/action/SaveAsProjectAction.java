/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.project.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.explorer.DataSetNode;
import net.phyloviz.core.util.PopulationFactory;
import net.phyloviz.core.util.TypingFactory;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.project.ProjectItemFactory;
import net.phyloviz.project.utils.FileUtils;
import net.phyloviz.upgmanjcore.visualization.*;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.actions.NodeAction;
import org.openide.windows.TopComponent;

public final class SaveAsProjectAction extends NodeAction {

    private static final String VIZ_FOLDER = "visualization";
    private final String ext = ".csv";

    public SaveAsProjectAction() {
        putValue(Action.NAME, "Save as Project");
    }

    @Override
    protected void performAction(Node[] n) {

        DataSetNode dsn = (DataSetNode) n[0];
        DataSet ds = dsn.getLookup().lookup(DataSet.class);
        Properties props = new Properties();

        String dataSetName = dsn.getDisplayName();
        props.put("dataset-name", dataSetName);

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(dataSetName);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            StatusDisplayer.getDefault().setStatusText("Saving...");

            String directory = fc.getSelectedFile().getAbsolutePath();

            File f = new File(directory, dataSetName);
            boolean created = f.mkdir();
            if (!created) {
                int result = JOptionPane.showConfirmDialog(fc, "Do you want to replace the current project " + dataSetName + "?", "Saving Project", YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {

                    try {
                        FileUtils.cleanDirectory(f);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    }

                } else {
                    return;
                }

            }

            directory = f.getAbsolutePath();

            File visualizationFolder = new File(directory, VIZ_FOLDER);
            visualizationFolder.mkdir();

            TypingData td = ds.getLookup().lookup(TypingData.class);
            String typingFactory = td.getNode().getDisplayName();

            Collection<? extends TypingFactory> tfs = Lookup.getDefault().lookupAll(TypingFactory.class);
            for (TypingFactory next : tfs) {
                String tf = next.toString();
                if (tf.equals(typingFactory)) {
                    props.put("typing-factory", next.getClass().getCanonicalName());
                    String filename = dataSetName + ".typing" + ext;
                    props.put("typing-file", filename);
                    save(directory, filename, td.tableModel());
                    break;
                }
            }

            Population pop = ds.getLookup().lookup(Population.class);
            if (pop != null) {
                props.setProperty("population-factory", PopulationFactory.class.getCanonicalName());
                String key = String.valueOf(pop.getKey());
                props.setProperty("population-foreign-key", key);

                String filename = dataSetName + ".isolate" + ext;
                props.put("population-file", filename);
                save(directory, filename, pop.tableModel());
            }

            Collection<? extends ProjectItem> c = td.getLookup().lookupAll(ProjectItem.class);
            if (c.size() > 0) {
                int algos = 1;
                Collection<? extends ProjectItemFactory> pif = Lookup.getDefault().lookupAll(ProjectItemFactory.class);
                StringBuilder algorithmsFactory = new StringBuilder();
                StringBuilder algorithms = new StringBuilder();
                StringBuilder algorithmsDistance = new StringBuilder();
                StringBuilder visualization = new StringBuilder();
                for (ProjectItem item : c) {

                    for (ProjectItemFactory factory : pif) {

                        String itemFactory = factory.getClass().getName();

                        if (itemFactory.contains(item.getMainName())) {

                            Class klass = item.getClass();

                            for (TopComponent tc : TopComponent.getRegistry().getOpened()) {

                                if (tc.getLookup().lookup(klass) == item) {

                                    PersistentVisualization pv = ((IGTPanel) tc).getPersistentVisualization();
                                    if (pv == null) {
                                        continue;
                                    }

                                    String viz = item.getMethodProviderName() + algos + ".pviz";
                                    try {

                                        try (FileOutputStream fileOut = new FileOutputStream(new File(visualizationFolder, viz))) {

                                            try (ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                                                out.writeObject(pv);
                                                out.close();
                                                visualization.append(viz).append(",");

                                            }
                                        }
                                    } catch (IOException i) {
                                        Exceptions.printStackTrace(i);
                                    }

                                }
                            }

                            algorithmsFactory.append(itemFactory).append(",");
                            algorithmsDistance.append(item.getDistanceProvider()).append(",");
                            
                            String filename = dataSetName + ".output." + item.getMethodProviderName() + algos++ + ".json";
                            algorithms.append(filename).append(",");
                            save(directory, filename, item.getOutput());

                            break;
                        }
                    }
                }

                //removing last comma
                algorithmsFactory.deleteCharAt(algorithmsFactory.length() - 1);
                algorithms.deleteCharAt(algorithms.length() - 1);
                algorithmsDistance.deleteCharAt(algorithmsDistance.length() - 1);
                
                if (visualization.length() > 0) {
                    visualization.deleteCharAt(visualization.length() - 1);
                    props.put("visualization", visualization.toString());
                }
                //add props
                props.put("algorithm-output", algorithms.toString());
                props.put("algorithm-output-factory", algorithmsFactory.toString());
                props.put("algorithm-output-distance", algorithmsDistance.toString());
            }

            saveConfigFile(directory, props);
            StatusDisplayer.getDefault().setStatusText("Project saved successfully.");
        }
    }

    @Override
    protected boolean enable(Node[] nodes) {
        return nodes.length == 1;
    }

    @Override
    public String getName() {
        return "Saving Project";
    }

    @Override
    public boolean asynchronous() {
        return false;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    private void save(String dir, String filename, String output) {
        try {

            File f = new File(dir, filename);
            PrintWriter pw = new PrintWriter(f);

            pw.print(output);

            pw.close();

        } catch (IOException ie) {
            Exceptions.printStackTrace(ie);
        }
    }

    private void save(String dir, String filename, AbstractTableModel tableModel) {
        try {

            File f = new File(dir, filename);
            PrintWriter pw = new PrintWriter(f);
            TableModel tm = tableModel;

            pw.print(tm.getColumnName(0));
            for (int i = 1; i < tm.getColumnCount(); i++) {
                pw.print("\t" + tm.getColumnName(i));
            }
            pw.println();

            for (int k = 0; k < tm.getRowCount(); k++) {
                pw.print(tm.getValueAt(k, 0));
                for (int i = 1; i < tm.getColumnCount(); i++) {
                    pw.print("\t" + tm.getValueAt(k, i));
                }
                pw.println();
            }

            pw.close();

        } catch (IOException ie) {
            Exceptions.printStackTrace(ie);
        }
    }

    private void saveConfigFile(String dir, Properties prop) {

        try {
            FileOutputStream fos = new FileOutputStream(new File(dir, "config.properties.pviz"));
            prop.store(fos, "Project Configuration File");
            fos.close();
        } catch (IOException ie) {
            Exceptions.printStackTrace(ie);
        }
    }

}
