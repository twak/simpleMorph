/*
 * BPSUI.java
 *
 * Created on 18 December 2008, 17:49
 */

package simplemorph;

import com.thoughtworks.xstream.XStream;
import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import utils.ComponentList;

/**
 *
 * @author  twak
 */
public class BPSUI extends javax.swing.JFrame {
    
    BodyPartSet root;
    InstanceEditor instanceEditor = new InstanceEditor( this );
    BodyPart selectedInstance;
    
    /** Creates new form BPSUI */
    public BPSUI() 
    {
            initComponents();
            BPSTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
//            instanceList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
//            instanceList.setCellRenderer( new InstanceListRenderer() );
            
            featureSetList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            setRoot( new BodyPartSet( new FeatureSet( "root" ), null ) );

            instanceEditor.setVisible( false );
            
        try
        {
            LineTypeItem[] items = new LineTypeItem[]{
                new LineTypeItem( "Edge" ), 
                new LineTypeItem( "Socket" ), // features", BodyPart.class.getField( "sockets" ) ), 
                new LineTypeItem( "Plug" ) // features", BodyPart.class.getField( "plugs" ) )
            };

            lineTypeCombo.setModel( new DefaultComboBoxModel(items) );
        } catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        
    }
    
    public void setRoot(BodyPartSet root)
    {
        int[] selectedRows = BPSTree.getSelectionRows();//BodyPartSet selected = getSelected();
        Enumeration<TreePath> expandedPaths = BPSTree.getExpandedDescendants(BPSTree.getPathForRow(0));
        
        List<BodyPartSet> expanded = new ArrayList();
        if (expandedPaths != null)
        while (expandedPaths.hasMoreElements())
        {
            TreePath tp = expandedPaths.nextElement();
             expanded.add( (BodyPartSet) ((DefaultMutableTreeNode)tp.getLastPathComponent()).getUserObject() );
        }
        
        
        this.root = root;
        BPSTree.setModel( new DefaultTreeModel( buildTree(root) ));
        updateEnables();
        
        for (int i = 0; i < BPSTree.getRowCount(); i++)
        {
            TreePath tp = BPSTree.getPathForRow(i);
            if (
                    expanded.contains(
                    (BodyPartSet)(
                    (DefaultMutableTreeNode)tp.getLastPathComponent())
                    .getUserObject()))
                BPSTree.expandRow(i); // treepath or row
        }
        
        if (selectedRows != null && selectedRows.length > 0)
        {
            BPSTree.setSelectionRow(selectedRows[0]);
        }
        
        
    }

    private DefaultMutableTreeNode buildTree( BodyPartSet root )
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(root.toString());
        node.setUserObject(root);
        for (BodyPartSet bps : root.getChildren())
            node.add(buildTree(bps));
        return node;
    }

    private void removeNode( BodyPartSet selected )
    {
        if (selected == null)
            return;
        
        if (selected.parent == null)
        {
            JOptionPane.showMessageDialog(this, "can't delete root :P");
            return;
        }
        
        selected.parent.removeChild(selected);
        setRoot(root);
    }
    
    private void addNode( BodyPartSet selected )
    {
        if ( selected == null || addName.getText().length() == 0 )
        {
            JOptionPane.showMessageDialog( this, "Pls select a node and enter a name :(" );
            return;
        }
        
        for (FeatureSet name : selected.children.keySet())
            if (name.name.compareTo(addName.getText()) == 0)
            {
                JOptionPane.showMessageDialog(this, "name already taken");
                return;
            }
        
        FeatureSet f = new FeatureSet(addName.getText());
        selected.addChild(f, new BodyPartSet(f, selected));
        
        addName.setText("");
        
        lineTypeComboItemStateChanged( null );
        
        setRoot(root);
    }
    
    private BodyPartSet getSelected()
    {
        TreePath tp = 
                BPSTree.getSelectionModel().getSelectionPath();
        
        if (tp == null)
            return null;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                BPSTree.getSelectionModel().getSelectionPath().getLastPathComponent();
        
        if (node == null)
            return null;
        
        return (BodyPartSet)node.getUserObject();
    }

    private void showInstanceEditor( BodyPart bp )
    {
        if (bp == null)
        {
            instanceEditor.setVisible(false);
            return;
        }
        instanceEditor.setBodyPart(bp);
        instanceEditor.setFeatureSet( (FeatureSet) featureSetList.getSelectedValue() );
        
//        if (!instanceEditor.isVisible())
//        {
            instanceEditor.setLocation(getLocation().x+getWidth(), getLocation().y);
            instanceEditor.setVisible( true );
//        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        randomScrollPane = new javax.swing.JScrollPane();
        instanceList = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        BPSTree = new javax.swing.JTree();
        addButton = new javax.swing.JButton();
        addName = new javax.swing.JTextField();
        deleteButton = new javax.swing.JToggleButton();
        bottomPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        featureSetList = new javax.swing.JList();
        lineTypeCombo = new javax.swing.JComboBox();
        newInstanceButton = new javax.swing.JButton();
        deleteInstanceButton = new javax.swing.JButton();
        instanceScroller = new javax.swing.JScrollPane();
        morphButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        loadMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();

        instanceList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        instanceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                instanceListValueChanged(evt);
            }
        });
        randomScrollPane.setViewportView(instanceList);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BPSTree.setModel(null);
        BPSTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                BPSTreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(BPSTree);

        addButton.setText("add bodyPart");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        addName.setText("enter name");
        addName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNameActionPerformed(evt);
            }
        });

        deleteButton.setText("delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        featureSetList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        featureSetList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                featureSetListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(featureSetList);

        lineTypeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        lineTypeCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                lineTypeComboItemStateChanged(evt);
            }
        });

        newInstanceButton.setText("add instance");
        newInstanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newInstanceButtonActionPerformed(evt);
            }
        });

        deleteInstanceButton.setText("delete");
        deleteInstanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteInstanceButtonActionPerformed(evt);
            }
        });

        instanceScroller.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addComponent(lineTypeCombo, 0, 233, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bottomPanelLayout.createSequentialGroup()
                        .addComponent(newInstanceButton, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteInstanceButton))
                    .addComponent(instanceScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                        .addComponent(instanceScroller, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newInstanceButton)
                            .addComponent(deleteInstanceButton)))
                    .addGroup(bottomPanelLayout.createSequentialGroup()
                        .addComponent(lineTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)))
                .addContainerGap())
        );

        morphButton.setText("morph!");
        morphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                morphButtonActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        newMenuItem.setText("new");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(newMenuItem);

        loadMenuItem.setText("load");
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(loadMenuItem);

        saveMenuItem.setText("save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addName, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton))
                    .addComponent(morphButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteButton)
                    .addComponent(addButton)
                    .addComponent(addName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(morphButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addNode ( getSelected() );
        
}//GEN-LAST:event_addButtonActionPerformed

    private void BPSTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_BPSTreeValueChanged
        updateEnables();
        lineTypeComboItemStateChanged(null);
    }//GEN-LAST:event_BPSTreeValueChanged

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        removeNode(getSelected());
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void lineTypeComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lineTypeComboItemStateChanged
        
        if (getSelected() == null)
            return; 
        
        try
        {
            LineTypeItem item = (LineTypeItem) lineTypeCombo.getSelectedItem();
            
            List<FeatureSet> features = (List<FeatureSet>) item.BPSMethod.invoke( getSelected() );
            DefaultListModel listModel = new DefaultListModel();
            for (FeatureSet f : features)
                listModel.addElement(f);
            
            featureSetList.setModel(listModel);
            featureSetList.setSelectedIndex( 0 );
            
            updateInstanceList();
        } catch ( Exception ex )
        {
           ex.printStackTrace();
        }
    }//GEN-LAST:event_lineTypeComboItemStateChanged

    private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMenuItemActionPerformed
        FileInputStream fis = null;
        try
        {
            XStream xs = new XStream();
            JFileChooser jf = new JFileChooser();
            
            int res = jf.showOpenDialog(this);
            
            if (res == JFileChooser.APPROVE_OPTION)
            {
                fis = new FileInputStream( jf.getSelectedFile() );
                setRoot( (BodyPartSet) xs.fromXML( fis ) );
            }
        } catch ( Exception ex )//GEN-LAST:event_loadMenuItemActionPerformed
        {
            JOptionPane.showMessageDialog( this, "error loading file :(" );
            ex.printStackTrace();
        } finally
        {
            try
            {
                if (fis != null)
                    fis.close();
            } catch ( IOException ex )
            {
                ex.printStackTrace();
            }
        }
    }

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        FileOutputStream fos = null;
        try
        {
            XStream xs = new XStream();
            JFileChooser jf = new JFileChooser();//GEN-LAST:event_saveMenuItemActionPerformed
            int res = jf.showSaveDialog( this );

            if ( res == JFileChooser.APPROVE_OPTION )
            {
                fos = new FileOutputStream( jf.getSelectedFile() );
                xs.toXML( root, fos );
            }
        } catch ( Exception ex )
        {
            JOptionPane.showMessageDialog( this, "error saving file :(" );
            ex.printStackTrace();
        } finally
        {
            try
            {
                fos.close();
            } catch ( IOException ex )
            {
                ex.printStackTrace();
            }
        }
    }

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        setRoot( new BodyPartSet( new FeatureSet( "root" ), null ) );
}//GEN-LAST:event_newMenuItemActionPerformed

    private void newInstanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newInstanceButtonActionPerformed
        BodyPart bp = new BodyPart(getSelected(), "start.jpg", getSelected().getUniqeInstanceName() );
        showInstanceEditor (bp);
        lineTypeComboItemStateChanged(null);
        
//        bp.bps.normaliseInstanceWeights();
        
    }//GEN-LAST:event_newInstanceButtonActionPerformed

    private void instanceListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_instanceListValueChanged
        deleteInstanceButton.setEnabled( instanceList.getSelectedValue() != null );
        showInstanceEditor( (BodyPart) instanceList.getSelectedValue() );
    }//GEN-LAST:event_instanceListValueChanged

    
private void addNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNameActionPerformed
    addButton.doClick();
    addName.grabFocus();//GEN-LAST:event_addNameActionPerformed
}

private void featureSetListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_featureSetListValueChanged
    
    FeatureSet fs = (FeatureSet)featureSetList.getSelectedValue();
    instanceEditor.setFeatureSet(fs);
}//GEN-LAST:event_featureSetListValueChanged

private void deleteInstanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteInstanceButtonActionPerformed
    if (selectedInstance != null)
    {
        getSelected().instances.remove( selectedInstance );
        updateInstanceList();
    }
    else
    {
        JOptionPane.showMessageDialog( this, "Pls select an instance to delete");
    }
}//GEN-LAST:event_deleteInstanceButtonActionPerformed

private void morphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_morphButtonActionPerformed
    BufferedImage out = new Morpher( getSelected().instances ).doMorph();
    new ShowImage(out);
}//GEN-LAST:event_morphButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BPSUI().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree BPSTree;
    private javax.swing.JButton addButton;
    private javax.swing.JTextField addName;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JToggleButton deleteButton;
    private javax.swing.JButton deleteInstanceButton;
    private javax.swing.JList featureSetList;
    private javax.swing.JList instanceList;
    private javax.swing.JScrollPane instanceScroller;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox lineTypeCombo;
    private javax.swing.JMenuItem loadMenuItem;
    private javax.swing.JButton morphButton;
    private javax.swing.JButton newInstanceButton;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JScrollPane randomScrollPane;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration//GEN-END:variables
    // End of variables declaration

    private void updateEnables()
    {
        boolean enabled = getSelected() != null;
        addButton.setEnabled(enabled);
        addName.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        bottomPanel.setVisible( enabled );
    }

    void repaintInstanceList()
    {
        for (Component c : ((Container)instanceScroller.getViewport().getView()).getComponents())
        {
            if (c instanceof NameSlider)
                ((NameSlider)c).update();
        }
    }
    
    void instanceSelected( BodyPart part )
    {
        if (selectedInstance == part)
            return;
        
        selectedInstance = part;
        deleteInstanceButton.setEnabled( selectedInstance != null );
        showInstanceEditor( selectedInstance );
        
    }
        
    // crusty selection code warning!
    public void updateInstanceList()
    {
//        instanceScroller.removeAll();
        BodyPart sI = selectedInstance;
        instanceSelected( null );
        
        ComponentList list = new ComponentList();
        
        boolean selected = false;
        
        for ( BodyPart bp : getSelected().instances )
        {
            NameSlider ns = new NameSlider(bp, this);
            list.add( ns );
            if (bp == sI)
            {
                list.select( ns );
                selected = true;
            }
        }
        
        list.validate();
        instanceScroller.setViewportView( list );
        if (!selected && list.getComponentCount() > 0)
            list.select( list.getComponent( 0 ) );
        
        deleteInstanceButton.setEnabled( selectedInstance != null );
    }
    
    public class LineTypeItem
    {
        Method BPSMethod;
        
        String name;
        public LineTypeItem(String stub) //, Method BPSMethod, Field BPField)
        {
            this.name = stub +" feature";
            try
            {
                this.BPSMethod = BodyPartSet.class.getMethod( "get" + stub + "s" );
//                this.BPField = BodyPart.class.getField( stub.toLowerCase() + "s" );
            }
            catch (Exception x)
            {
                x.printStackTrace();
            }
        }
        public String toString()
        {
            return name;
        }
    }
}
