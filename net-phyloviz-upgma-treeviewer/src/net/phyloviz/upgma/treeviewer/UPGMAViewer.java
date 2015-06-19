/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.treeviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.Profile;
import net.phyloviz.upgma.tree.NodeType;
import net.phyloviz.upgma.tree.UPGMALeafNode;
import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgma.tree.UPGMAUnionNode;
import net.phyloviz.upgma.treeviewer.action.EdgeLevelLabelAction;
import net.phyloviz.upgma.treeviewer.action.ExportAction;
import net.phyloviz.upgma.treeviewer.action.InfoControlAction;
import net.phyloviz.upgma.treeviewer.action.LinearSizeControlAction;
import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 *
 * @author Marta Nascimento
 */
public final class UPGMAViewer extends GView{
    
    private JComponent _treeview;
    private UPGMARoot _root;
    private final String label = "node";
    private final String distance = "distance";
    private final String position = "position";
    private final String childrenSize = "childrenSize";
    private final String idx = "idx";
    private final String profile = "profile";
    private final String p_id = "p_id";
    
    private TreeView tview;
    private boolean linear = false;
    private boolean labelBool = true;
    
    private JPanel groupPanel;
    private boolean groupPanelStatus;
    private JList groupList;
    private int itemFound = -1;
    private InfoPanel infoPanel;
    private JPopupMenu popupMenu;
    private LabelRenderer lr;
    private JSpinner sp;
    private final String _name;
    private Color BACKGROUND, FOREGROUND;
    
    private JSlider horizontalSlider;
   
    public UPGMAViewer(String name, UPGMARoot root){
        _name = name;
        _root = root;
        _treeview = generateTreeViewComponent();

    }
  
    public JComponent generateTreeViewComponent() {
        
        BACKGROUND = Color.WHITE;
        FOREGROUND = Color.BLACK;

        
        Tree t = new Tree();
        Table nodes = t.getNodeTable();
        Table edges = t.getEdgeTable();
        edges.addColumn(distance, double.class);
        nodes.addColumn(label, String.class);
        nodes.addColumn(profile, Profile.class);
        nodes.addColumn(p_id, String.class);
        nodes.addColumn(distance, double.class);
        nodes.addColumn(position, double.class);
        nodes.addColumn(childrenSize, int.class);
        nodes.addColumn(idx, int.class);
        
        
        
        Node n = t.addRoot();
        n.set(label, null);
        n.setDouble(distance, _root.getDistance());
        
        createTree(t, n, _root.getNodeLeft());
        createTree(t, n, _root.getNodeRight());

        setChildrenSize(n);
        
        // create a new treemap
        tview = new TreeView(t, label);
        
        tview.setBackground(BACKGROUND);
        tview.setForeground(FOREGROUND);
        
        tview.setHighQuality(false);

        // create a search panel for the tree map
        JSearchPanel search = new JSearchPanel(tview.getVisualization(),
                TreeView.treeNodes, Visualization.SEARCH_ITEMS, label, true, true);
        search.setShowResultCount(true);
        search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
        search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
        search.setBackground(BACKGROUND);
        search.setForeground(FOREGROUND);

        final JFastLabel title = new JFastLabel("                 ");
        title.setPreferredSize(new Dimension(350, 20));
        title.setVerticalAlignment(SwingConstants.BOTTOM);
        title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
        title.setBackground(BACKGROUND);
        title.setForeground(FOREGROUND);

        tview.addControlListener(new ControlAdapter() {
            
            @Override
            public void itemClicked(VisualItem item, MouseEvent e) {

                if (item.canGetString(label) ) {
                    if(tview.cp != null){
                        appendLineToInfoPanel("Chart details:");
                        int total = 0;
                        Profile st = (Profile)item.get("profile");
                        DecimalFormat df = new DecimalFormat("#.##");
                        Collection<Category> groupsList = tview.cp.getCategories(st.getID());
                        if (groupsList != null) {
                            Iterator<Category> groups = groupsList.iterator();
                            while (groups.hasNext()) {
                                Category group = groups.next();
                                double percent = (((double) group.weight() * 100) / st.getFreq());
                                appendLineToInfoPanel(" +" + group.getName() + " " + df.format(percent) + "%");
                                total += group.weight();
                            }
                        }
                        double percent = (((double) (st.getFreq() - total) * 100) / st.getFreq());
                        if (percent > 0) {
                            appendLineToInfoPanel(" + 'others' " + df.format(percent) + "%");
                        }
                    } 

                    else
                        appendLineToInfoPanel(((Profile)item.get("profile")).toString());
                    
                } else if (item instanceof EdgeItem){
                    Double d = item.getDouble("distance");
                    appendLineToInfoPanel(d / horizontalSlider.getValue() + "");
                }
            }
//            @Override
//            public void itemEntered(VisualItem item, MouseEvent e){
//                if (item.canGetString(label) || item instanceof EdgeItem){
//                   setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                }
//            }
//            @Override
//            public void itemExited(VisualItem item, MouseEvent e){
//                if (item.canGetString(label) || item instanceof EdgeItem){
//                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//                }
//            }

        });

        
        
        
        groupList = new JList();
        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        groupList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                System.out.println("groupList valueChanged");
            }
        });
        //groupList.setCellRenderer(new GroupCellRenderer());

        groupPanel = new JPanel();
        groupPanel.setLayout(new BorderLayout());

        JScrollPane groupListPanel = new JScrollPane(groupList,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        groupListPanel.getViewport().setBackground(Color.WHITE);
        groupListPanel.setBackground(Color.WHITE);
        TitledBorder tb = new TitledBorder("Groups");
        tb.setBorder(new LineBorder(Color.BLACK));
        groupListPanel.setBorder(tb);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        //top.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JLabel ll = new JLabel("Level: ");
        //ll.setMargin( new Insets(1, 1, 1, 1));
        ll.setBackground(Color.WHITE);

        sp = new JSpinner();
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        sp.setBackground(Color.WHITE);


        top.add(ll, BorderLayout.WEST);
        top.add(sp, BorderLayout.CENTER);

        JPanel bs = new JPanel(new BorderLayout());
        bs.setBackground(Color.WHITE);

        bs.add(Box.createVerticalStrut(3), BorderLayout.CENTER);

        top.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 3, 2, 3));
        top.add(bs, BorderLayout.SOUTH);

        groupPanel.add(top, BorderLayout.SOUTH);
        groupPanel.add(groupListPanel, BorderLayout.CENTER);
        groupPanel.setPreferredSize(new Dimension(90, 600));
        //add(groupPanel, BorderLayout.WEST);
        groupPanelStatus = false;

        infoPanel = new InfoPanel(_name + " Info");
        infoPanel.open();
        infoPanel.requestActive();

        //Create the horizontal slider.
        final TreeSlider tsh = new TreeSlider(JSlider.HORIZONTAL, 0, 500, 250);
        horizontalSlider = tsh.getSlider();
        horizontalSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tview.changeDistance(horizontalSlider.getValue());
                tsh.setCurrentValueText(horizontalSlider.getValue() + "");
            }
        });
        //tsh.setValuesLabel();
        tsh.setTickSpacing(50);
        horizontalSlider.setBackground(BACKGROUND);
       
        
        //Create the vertical slider.
        final TreeSlider tsv = new TreeSlider(JSlider.VERTICAL, 0, 100, 40);
        final JSlider verticalSlider = tsv.getSlider();
        verticalSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tview.changeHigh(verticalSlider.getValue());
                tsv.setCurrentValueText(verticalSlider.getValue() + "");
            }
        });
        tsv.setValuesLabel();
        tsv.setTickSpacing(10);
        verticalSlider.setBackground(BACKGROUND);
        
        JPanel verticalLabelPanel = new JPanel(new GridLayout(10, 1));
        verticalLabelPanel.add(tsv.getLabel());
        verticalLabelPanel.setBackground(BACKGROUND);
        
        
        
        
        popupMenu = new JPopupMenu();
        popupMenu.add(new InfoControlAction(this).getMenuItem());
        //popupMenu.add(new EdgeViewControlAction(this).getMenuItem());
        //popupMenu.add(new EdgeFullViewControlAction(this).getMenuItem());
        //popupMenu.add(new ShowLabelControlAction(this).getMenuItem());
        popupMenu.add(new EdgeLevelLabelAction(this).getMenuItem());
        //popupMenu.add(new EdgePercentageLabelAction(this).getMenuItem());
        popupMenu.add(new LinearSizeControlAction(this).getMenuItem());
        //popupMenu.add(new HighQualityAction(this).getMenuItem());
        //popupMenu.add(new ExportAction(this).getMenuItem());

        JButton optionsButton = new JButton("Options");
        optionsButton.setMargin(new Insets(1, 1, 1, 1));
        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        JButton exportButton = new JButton();
        exportButton.setIcon(new ImageIcon(UPGMAViewer.class.getResource("export.png")));
        exportButton.setMargin(new Insets(0, 0, 0, 0));
        exportButton.addActionListener(new ExportAction(this));
        
        
        Box horizontalBox = new Box(BoxLayout.X_AXIS);
        horizontalBox.add(Box.createHorizontalStrut(3));
        horizontalBox.add(optionsButton);
        horizontalBox.add(Box.createHorizontalStrut(5));
        horizontalBox.add(exportButton);
        horizontalBox.add(Box.createHorizontalStrut(5));
        horizontalBox.add(horizontalSlider);
       // horizontalBox.add(Box.createHorizontalStrut(5));
        horizontalBox.add(tsh.getLabel());
       // horizontalBox.add(Box.createHorizontalGlue());
       // horizontalBox.add(Box.createHorizontalGlue());
       // horizontalBox.add(Box.createHorizontalGlue());
        
       // horizontalBox.add(search);
       // horizontalBox.add(Box.createHorizontalStrut(3));
        horizontalBox.setOpaque(true);
        horizontalBox.setBackground(Color.WHITE);
        
        JPanel horizontalLabelPanel = new JPanel(new GridLayout(1, 3));
        horizontalLabelPanel.add(horizontalBox);
        horizontalLabelPanel.add(emptyJPanel());
        horizontalLabelPanel.add(search);
        horizontalLabelPanel.setBackground(BACKGROUND);
        
        Box verticalBox = new Box(BoxLayout.Y_AXIS);
        verticalBox.add(verticalSlider);  
        verticalBox.setOpaque(true);
        verticalBox.setBackground(BACKGROUND);
        
        JPanel verticalPanel = new JPanel(new GridLayout(2, 1));
        verticalPanel.add(verticalBox);
        verticalPanel.add(verticalLabelPanel);
        verticalPanel.setBackground(BACKGROUND);
                
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setForeground(FOREGROUND);
        panel.add(tview, BorderLayout.CENTER);
        panel.add(horizontalLabelPanel, BorderLayout.SOUTH);
        panel.add(verticalPanel, BorderLayout.EAST);
        
        return panel;
    }

    private void createTree(Tree t, Node root, NodeType child){
        
        Node n = t.addChild(root);
        //n.set(label, child.getDisplayName());
        if(child.getType().equals("Leaf")){
            n.set(label, child.getDisplayName());
            n.set(profile, ((UPGMALeafNode)child).p);
            n.set(p_id, ((UPGMALeafNode)child).p.getID());
            return;
        }

        UPGMAUnionNode newRoot = (UPGMAUnionNode) child;
        double d = newRoot.getDistance();
        n.setDouble(distance, d);
        createTree(t, n, newRoot.getNodeLeft());
        createTree(t, n, newRoot.getNodeRight());
    }
    
    public JComponent getTreeViewComponent(){
        return _treeview;
    }
    int id = 1;
    private int setChildrenSize(Node n) {
        
        if(n.getChildCount() == 0){
            n.setInt(childrenSize, 1);
            n.setInt("idx", id++);
            return 1;
        }
        int sizeL = setChildrenSize(n.getFirstChild());
        int sizeR = setChildrenSize(n.getLastChild());
        
        n.setInt(childrenSize, sizeL + sizeR);
        
        return sizeL + sizeR;
        
    }

    @Override
    public JComponent getDisplay() {
        return tview;
    }

    @Override
    public Visualization getVisualization() {
        return tview.getVisualization();
    }

    @Override
    public boolean getLinearSize() {
        return linear;
    }
    
    @Override
    public void setLinearSize(boolean status) {
        if (linear != status) {
            linear = status;
            tview.setLinearSize(status);
        }
    }

     @Override
    public void setLevelLabel(boolean status) {
        
        tview.setDistanceLabel(status);
        
    }
    
    @Override
    public void setHighQuality(boolean status) {
        tview.setHighQuality(status);
    }
    
    @Override
    public boolean showLabel() {
        return labelBool;
    }
    @Override
    public void setShowLabel(boolean status) {
        if (labelBool != status) {
            labelBool = status;            
        }
    }

    @Override
    public void showGroupPanel(boolean status) {
        if (status == groupPanelStatus) {
            return;
        }
        if (status) {
            add(groupPanel, BorderLayout.WEST);
        } else {
            remove(groupPanel);
        }
        groupPanelStatus = status;
    }

       @Override
    public void showInfoPanel() {
        if (infoPanel == null) {
            infoPanel = new InfoPanel(_name + " Info");
        }
        if (!infoPanel.isOpened()) {
            infoPanel.open();
        }
        infoPanel.requestActive();
    }
    @Override
    public void closeInfoPanel() {
        infoPanel.close();
    }

    @Override
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    private JPanel emptyJPanel() {
        JPanel j = new JPanel();
        j.setBackground(BACKGROUND);
        return j;
    }
    
    public void appendLineToInfoPanel(String text) {
        if (infoPanel != null) {
            infoPanel.append(text + "\n");
            infoPanel.flush();
            infoPanel.requestActive();
        }
    }

    public void resetDefaultRenderer() {
        tview.resetDefaultRenderer();
    }

    public void setDefaultRenderer(AbstractShapeRenderer r) {
        tview.setDefaultRenderer(r);
    }
    
    public void setCategoryProvider(CategoryProvider cp) {
        tview.setCategoryProvider(cp);
    }
    
}
