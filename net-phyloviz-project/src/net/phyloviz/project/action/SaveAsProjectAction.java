/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.phyloviz.project.action;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.DistanceProvider;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.explorer.DataSetNode;
import net.phyloviz.core.util.PopulationFactory;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.project.ProjectItemFactory;
import net.phyloviz.project.ProjectTypingDataFactory;
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

            TypingData td = ds.getLookup().lookup(TypingData.class);
            String typingFactory = td.getNode().getDisplayName();

            boolean isSavable = false;
            Collection<? extends ProjectTypingDataFactory> ptdi = Lookup.getDefault().lookupAll(ProjectTypingDataFactory.class);
            for (ProjectTypingDataFactory tdf : ptdi) {

                String tdfName = tdf.toString();
                if (tdfName.equals(typingFactory)) {
                    isSavable = true;
                    props.put("typing-factory", tdf.getClass().getCanonicalName());
                    String filename = dataSetName + ".typing" + ext;
                    props.put("typing-file", filename);
                    String toSave = tdf.onSave(td);
                    save(directory, filename, toSave);

                    Population pop = ds.getLookup().lookup(Population.class);
                    if (pop != null) {
                        props.setProperty("population-factory", PopulationFactory.class.getCanonicalName());
                        String key = String.valueOf(pop.getKey());
                        props.setProperty("population-foreign-key", key);

                        filename = dataSetName + ".isolate" + ext;
                        props.put("population-file", filename);
                        toSave = pop.getSaver().toSave();
                        save(directory, filename, toSave);
                    }

                    Collection<? extends ProjectItem> c = td.getLookup().lookupAll(ProjectItem.class);
                    if (c.size() > 0) {
                        int algos = 1;
                        Collection<? extends ProjectItemFactory> pif = Lookup.getDefault().lookupAll(ProjectItemFactory.class);
                        StringBuilder algorithmsFactory = new StringBuilder();
                        StringBuilder algorithms = new StringBuilder();
                        StringBuilder algorithmsDistance = new StringBuilder();
                        StringBuilder algorithmsLevel = new StringBuilder();
                        StringBuilder visualization = new StringBuilder();
                        boolean firsttime = true;
                        for (ProjectItem item : c) {
                            for (ProjectItemFactory factory : pif) {

                                String itemFactory = factory.getClass().getName();
                                if (itemFactory.contains(item.getFactoryName())) {

                                    Class klass = item.getClass();
                                    for (TopComponent tc : TopComponent.getRegistry().getOpened()) {

                                        if (tc.getLookup().lookup(klass) == item) {
                                            Visualization viz = ((IGTPanel) tc).getVisualization();
                                            if (viz == null) {
                                                continue;
                                            }

                                            String vizName = item.getMethodProviderName() + algos + ".pviz";
                                            File visualizationFolder = new File(directory, VIZ_FOLDER);
                                            visualizationFolder.mkdir();
                                            try {
                                                try (FileOutputStream fileOut = new FileOutputStream(new File(visualizationFolder, vizName))) {
                                                    try (ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                                                        out.writeObject(viz.pv);
                                                        out.close();
                                                        visualization.append(vizName).append(",");

                                                    }
                                                }
                                            } catch (IOException i) {
                                                Exceptions.printStackTrace(i);
                                            }

                                            if (firsttime && viz.category != null) {
                                                firsttime = false;
                                                String output = readPalette(viz.category);
                                                save(visualizationFolder.getPath(),  "palette.pviz", output);
                                                props.put("visualization-palette", "palette.pviz");
                                                if (viz.filter != null) {
                                                    output = readTreeSet(viz.filter, viz.category.getDataModel());
                                                    save(visualizationFolder.getPath(), "filter.pviz.json", output);
                                                    props.put("visualization-filter", "filter.pviz.json");
                                                }
                                            }

                                        }
                                    }

                                    algorithmsFactory.append(itemFactory).append(",");
                                    algorithmsLevel.append(item.getAlgorithmLevel()).append(",");

                                    Collection<? extends DistanceProvider> dpLookup = (Lookup.getDefault().lookupAll(DistanceProvider.class));
                                    for (DistanceProvider dp : dpLookup) {
                                        AbstractDistance ad = dp.getDistance(td);
                                        if (ad == null) {
                                            continue;
                                        }
                                        String name = ad.getClass().getCanonicalName();
                                        if (name.equals(item.getDistanceProvider())) {
                                            algorithmsDistance.append(dp.getClass().getCanonicalName()).append(",");
                                        }
                                    }

                                    filename = dataSetName + ".output." + item.getMethodProviderName() + algos++ + ".json";
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
                        algorithmsLevel.deleteCharAt(algorithmsLevel.length() - 1);

                        if (visualization.length() > 0) {
                            visualization.deleteCharAt(visualization.length() - 1);
                            props.put("visualization", visualization.toString());
                        }
                        //add props
                        props.put("algorithm-output", algorithms.toString());
                        props.put("algorithm-output-factory", algorithmsFactory.toString());
                        props.put("algorithm-output-distance", algorithmsDistance.toString());
                        props.put("algorithm-output-level", algorithmsLevel.toString());
                    }

                    saveConfigFile(directory, props);
                    StatusDisplayer.getDefault().setStatusText("Project saved successfully.");

                    break;
                }

            }
            if (!isSavable) {
                JOptionPane.showMessageDialog(fc, typingFactory + " is not savable!", "Saving Project Error", JOptionPane.ERROR_MESSAGE);
            }

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

            try (PrintWriter pw = new PrintWriter(f)) {
                pw.print(output);
            }

        } catch (IOException ie) {
            Exceptions.printStackTrace(ie);
        }
    }

    /*private void save(String dir, String filename, AbstractTableModel tableModel) {
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
     }*/
    private void saveConfigFile(String dir, Properties prop) {

        try {
            FileOutputStream fos = new FileOutputStream(new File(dir, "config.properties.pviz"));
            prop.store(fos, "Project Configuration File");
            fos.close();
        } catch (IOException ie) {
            Exceptions.printStackTrace(ie);
        }
    }

    private String readTreeSet(TreeSet<String>[] filterSelection, DataModel dm) {
        String result = "{";
        String clas = dm.getClass().getCanonicalName();
        result += "\t\"datamodel\": \""+clas+"\", \n";
        result += "\t\"bounds\": { \"col-size\": " + filterSelection.length + "}, \n";
        result += "\t\"filter\": [\n";
        for (int i = 0; i < filterSelection.length; i++) {
            for (int j = 0; j < filterSelection[i].size(); j++) {
                TreeSet<String> t = filterSelection[i];
                Iterator<String> it = t.iterator();
                while (it.hasNext()) {
                    String value = it.next();
                    String filter = "\t\t{" + "\"row\":" + j + ", \"col\":" + i + ", \"value\":\"" + value + "\"},\n";
                    result += filter;
                }

            }
        }

        return result + "\t],\n}";
    }

    private String readPalette(CategoryProvider category) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, Integer>> si = category.getCategories().iterator();
        while (si.hasNext()) {
            Color color = category.getCategoryColor(si.next().getKey());
            sb.append(color.getRed()).append(",").append(color.getGreen()).append(",").append(color.getBlue()).append("\n");
        }
        return sb.toString();
    }

}
