/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.project.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.util.PopulationFactory;
import net.phyloviz.core.util.TypingFactory;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.project.ProjectItemFactory;
import net.phyloviz.upgmanjcore.visualization.PersistentClass;
import org.netbeans.api.project.Project;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

public final class LoadDataSetAction extends AbstractAction {

    public LoadDataSetAction() {
        putValue(Action.NAME, "Load DataSet");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        Properties prop = new Properties();
        String propFileName = "config.properties";

        String projectDir = getProjectLocation();

        try {

            InputStream inputStream = new FileInputStream(new File(projectDir, propFileName));
            prop.load(inputStream);

            String dataSetName = prop.getProperty("dataset-name");
            if (dataSetAlreadyOpened(dataSetName)) {
                WindowManager.getDefault().findTopComponent("DataSetExplorerTopComponent").requestActive();
                JOptionPane.showMessageDialog(WindowManager.getDefault().findTopComponent("DataSetExplorerTopComponent"), "Eggs are not supposed to be green.");
                return;
            }

            String typingFactory = prop.getProperty("typing-factory"),
                    typingFile = prop.getProperty("typing-file"),
                    populationFactory = prop.getProperty("population-factory"),
                    populationFile = prop.getProperty("population-file"),
                    populationFK = prop.getProperty("population-foreign-key"),
                    algorithmOutput = prop.getProperty("algorithm-output"),
                    algorithmOutputOutputFactory = prop.getProperty("algorithm-output-factory");

            String[] algoOutput = algorithmOutput != null
                    ? algorithmOutput.split(",")
                    : new String[]{""};
            String[] algoOutputFactory = algorithmOutputOutputFactory != null
                    ? algorithmOutputOutputFactory.split(",")
                    : new String[]{""};

            if (dataSetName != null && (!dataSetName.equals(""))) {

                DataSet ds = new DataSet(dataSetName);

                StatusDisplayer.getDefault().setStatusText("Loading typing data...");
                TypingFactory tf = (TypingFactory) Class.forName(typingFactory).newInstance();
                TypingData<? extends AbstractProfile> td = tf.loadData(new FileReader(new File(projectDir, typingFile)));
                td.setDescription(tf.toString());

                if (populationFile != null && (!populationFile.equals("")) && populationFK != null) {

                    StatusDisplayer.getDefault().setStatusText("Loading isolate data...");
                    Population pop = ((PopulationFactory) Class.forName(populationFactory).newInstance())
                            .loadPopulation(new FileReader(new File(projectDir, populationFile)));
                    ds.add(pop);

                    int key = Integer.parseInt(populationFK);
                    StatusDisplayer.getDefault().setStatusText("Integrating data...");
                    td = tf.integrateData(td, pop, key);
                    ds.setPopKey(key);
                }

                Collection<? extends ProjectItemFactory> pifactory = (Lookup.getDefault().lookupAll(ProjectItemFactory.class));
                for (int i = 0; i < algoOutput.length; i++) {

                    for (ProjectItemFactory pif : pifactory) {
                        String pifName = pif.getClass().getName();
                        if (pifName.equals(algoOutputFactory[i])) {
                            StatusDisplayer.getDefault().setStatusText("Loading algorithms...");
                            ProjectItem pi = pif.loadData(dataSetName, td, projectDir, algoOutput[i]);
                            td.add(pi);
                        }
                    }
                }

                ds.add(td);

                PersistentClass pc = null;
                try {
                    FileInputStream fileIn = new FileInputStream("C:\\Users\\Marta Nascimento\\Documents\\employee.ser");
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    pc = (PersistentClass) in.readObject();
                    in.close();
                    fileIn.close();
                } catch (IOException i) {
                    Exceptions.printStackTrace(i);
                } catch (ClassNotFoundException c) {
                    System.out.println("PersistentClass class not found");
                    Exceptions.printStackTrace(c);
                }

                Lookup.getDefault().lookup(DataSetTracker.class).add(ds);
                StatusDisplayer.getDefault().setStatusText("Dataset loaded.");
                WindowManager.getDefault().findTopComponent("DataSetExplorerTopComponent").requestActive();

            } else {
                Exceptions.printStackTrace(new IllegalArgumentException("DataSet name not found!"));
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    private String getProjectLocation() {

        Lookup lookup = Utilities.actionsGlobalContext();
        Project project = lookup.lookup(Project.class
        );
        FileObject projectDir = project.getProjectDirectory();

        return projectDir.getPath();
    }

    private boolean dataSetAlreadyOpened(String dataSetName) {

        Lookup lk = Lookup.getDefault().lookup(DataSetTracker.class
        ).getLookup();

        Collection<? extends DataSet> data = lk.lookupAll(DataSet.class);
        for (DataSet ds : data) {
            if (ds.toString().equals(dataSetName)) {
                return true;
            }
        }

        return false;
    }
}
