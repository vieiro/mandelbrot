/*
 * $Id $
 *
 * Copyright 2005 Sun Microsystems
 *
 * Initially developed by Antonio Vieiro (vieiro@dev.java.net).
 *
 * This software is released under the Common Development and Distribution
 *  License, version 1.0. See http://www.opensource.org/licenses/cddl1.php
 *  for details.
 *
 * This software is distributed on an "AS IS" basis, without warranty of any
 *  kind, either express or implied.
 *
 * All this stuff basically means that you can use this source code in your
 *  software, either proprietary or not. See
 *  http://www.opensolaris.org/os/about/faq/licensing_faq/ for interesting
 *  information about the CDDL license.
 *
 */

package org.jdesktop.swingworker.demos.mandelbrot;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jdesktop.swingworker.demos.mandelbrot.model.ConfigurationModel;
import org.jdesktop.swingworker.demos.mandelbrot.model.ExportImageSwingWorker;
import org.jdesktop.swingworker.demos.mandelbrot.model.MandelbrotModel;
import org.jdesktop.swingworker.demos.mandelbrot.view.config.ConfigurationDialog;
import org.jdesktop.swingworker.demos.mandelbrot.view.progresspane.ProgressPane;
import org.jdesktop.swingworker.demos.mandelbrot.view.progresspane.ProgressPaneCancelListener;
import org.jdesktop.swingworker.demos.mandelbrot.view.shiftpane.ShiftPane;
import org.jdesktop.swingworker.demos.mandelbrot.view.zoompane.ZoomListener;
import org.jdesktop.swingworker.demos.mandelbrot.view.zoompane.ZoomablePane;
import org.jdesktop.swingworker.demos.mandelbrot.model.MandelbrotSwingWorker;
import org.jdesktop.swingworker.demos.mandelbrot.view.canvas.BufferedImagePane;
import org.jdesktop.swingworker.demos.mandelbrot.view.infopane.InfoPane;
import org.jdesktop.swingworker.demos.mandelbrot.view.shiftpane.ShiftListener;

/**
 * MandelbrotViewController is the controller of the Mandelbrot Fractal
 *  viewer.
 * This controller keeps references to different views (panes, etc.) and to a
 *  MandelbrotModel. The controller receives events from the views and
 *  manipulates the model accordingly.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 */
public class MandelbrotViewController
  extends javax.swing.JFrame
  implements PropertyChangeListener, ZoomListener, ProgressPaneCancelListener,
  ShiftListener
{
  /**
   * The array of SwingWorkers.
   * TODO: Build a SwingWorkerHandler or something similar to a task manager.
   */
  private MandelbrotSwingWorker[] mandelbrotWorkers;
  /**
   * A SwingWorker used to export images.
   */
  private ExportImageSwingWorker exportImageWorker;
  /**
   * The model representing the Mandelbrot fractal. Contains an image
   *  (I consider the image a part of the model).
   */
  private MandelbrotModel model;
  /**
   * A pane used to paint BufferedImages
   */
  private BufferedImagePane imagePane;
  /**
   * A pane used to allow the user to zoom an area of any drawing.
   */
  private ZoomablePane zoomablePane;
  /**
   * A pane used to show progress to the user.
   */
  private ProgressPane progressPane;
  /**
   * A pane used to allow the user to move an image.
   */
  private ShiftPane shiftPane;
  /**
   * A pane used to show information to the user.
   */
  private InfoPane infoPane;
  /**
   * A boolean used to indicate state of the view.
   */
  private boolean workingState;
  /**
   * A dialog used to allow the user to handle configuration parameters.
   */
  private ConfigurationDialog configurationDialog;
  
  //
  // Z-ORDER OF LAYERS:
  //
  //     FOREGROUND_LAYER (top, always visible)
  //     INFO_LAYER
  //     CANVAS_LAYER (image here)
  //     BACKGROUND_LAYER
  //     HIDDEN_LAYER (bottom, invisible).
  
  
  /**
   * A z-order for the JLayered pane.
   * This level is always invisible. It is hidden by the BACKGROUND_LAYER.
   *
   */
  private static Integer HIDDEN_LAYER = new Integer(1);
  /**
   * A z-order for the JLayered pane.
   * This level is always behind the image. Panes here should be hidden
   *  by the image.
   * This level, with the FOREGROUND_LAYER, is used to switch between
   *  tool panes (ZoomPane and ShiftPane). Active tool is always on the
   *  FOREGROUND_LAYER, whereas inactive tool is always in the BACKGROUND_LAYER.
   */
  private static Integer BACKGROUND_LAYER = new Integer(10);
  /**
   * A z-order for the JLayered pane.
   * This level is where the image is painted.
   */
  private static Integer CANVAS_LAYER = new Integer(50);
  /**
   * A z-order for the JLayered pane.
   * This level is above the image, but behind the active tool z-order
   */
  private static Integer INFO_LAYER = new Integer(51);
  /**
   * A z-order for the JLayered pane.
   * This level is always above the image. Panes here should NOT be hidden
   *  by the image.
   * This level, with the BACKGROUND_LAYER, is used to switch between
   *  tool panes (ZoomPane and ShiftPane). Active tool is always on the
   *  FOREGROUND_LAYER, whereas inactive tool is always in the BACKGROUND_LAYER.
   */
  private static Integer FOREGROUND_LAYER = new Integer(100);
  
  /**
   * A long to hold the System.currentTimeMillis() when we fire all
   *  SwingWorkers (i.e. initiate a repaint).
   * This is used for timing purposes.
   */
  private long startTimestamp = 0l;
  
  /**
   * Creates new form MandelbrotViewController
   */
  public MandelbrotViewController()
  {
    initComponents();
    // BUILD PANES
    imagePane = new BufferedImagePane();
    zoomablePane = new ZoomablePane();
    zoomablePane.addZoomListener( this );
    progressPane = new ProgressPane();
    progressPane.addProgressPaneCancelListener( this );
    shiftPane = new ShiftPane();
    shiftPane.addShiftListener( this );
    infoPane = new InfoPane();
    // SIZE PANELS INSIDE THE JLAYEREDPANE
    imagePane.setBounds(0,0, MandelbrotModel.XRESOLUTION, MandelbrotModel.YRESOLUTION );
    zoomablePane.setBounds(0,0, MandelbrotModel.XRESOLUTION, MandelbrotModel.YRESOLUTION );
    progressPane.setBounds(0,0,MandelbrotModel.XRESOLUTION, MandelbrotModel.YRESOLUTION );
    shiftPane.setBounds(0,0,MandelbrotModel.XRESOLUTION, MandelbrotModel.YRESOLUTION );
    infoPane.setBounds(0,0,MandelbrotModel.XRESOLUTION, MandelbrotModel.YRESOLUTION );
    // SET Z-ORDER FOR PANELS INSIDE THE JLAYEREDPANE
    layeredPane.add( imagePane, CANVAS_LAYER );
    layeredPane.add( infoPane, INFO_LAYER);
    layeredPane.add( zoomablePane, FOREGROUND_LAYER );
    layeredPane.add( progressPane, BACKGROUND_LAYER );
    layeredPane.add( shiftPane, HIDDEN_LAYER );
    // ADJUST THE PREFERRED SIZE OF THE JLAYEREDPANE, SO THE DIMENSIONS
    // ARE APPROPIATELY TAKEN INTO ACCOUNT BY THE SURROUNDING SCROLLPANE
    layeredPane.setPreferredSize( new Dimension(MandelbrotModel.XRESOLUTION, MandelbrotModel.YRESOLUTION ) );
    // SET A FILE FILTER FOR THE FILE CHOOSER
    exportFileChooser.setFileFilter( new PNGFileFilter() );
    // PACK COMPONENTS IN WINDOW
    pack();
    // BUILD A NEW MANDELBROT MODEL
    setModel( new MandelbrotModel() );
    // CENTER ON SCREEN
    setLocationRelativeTo( null );
    // AN ICON
    URL url = getClass().getResource("view/icons/mandelbrot-icon.png");
    if ( url != null )
    {
      setIconImage( Toolkit.getDefaultToolkit().getImage( url ) );
    }
      
      
    // TODO: INVOKE THIS LATER ON SO AS TO ALLOW SWING TO PAINT THE GUI
    updateModelToArea( model.getBounds() );
  }
  
  // SETS THE CURRENT WORKING MODEL.
  public void setModel( MandelbrotModel aModel )
  {
    this.model = aModel;
    lblNThreads.setText( model.getParallelizationRate() + " threads");
    lblResolution.setText( "Resolution: " + model.getMaxIterations() );
  }

  public MandelbrotModel getModel() {
      return model;
  }
  /**
   * Internal method used to enable/disable buttons and menus depending on
   *  our working state.
   * @param isWorking true if the processes are running, false otherwise.
   */
  private void setWorkingState( boolean isWorking )
  {
    boolean isZoomSelected = optZoomTool.isSelected();
    
    // Disable the menu and the new diagram actions
    menuExport.setEnabled( ! isWorking );
    cmdReset.setEnabled( ! isWorking );
    cmdExport.setEnabled( ! isWorking );
    menuConfig.setEnabled( ! isWorking );
    
    // Make the progress pane visible if working
    layeredPane.setLayer( progressPane, isWorking ? FOREGROUND_LAYER : BACKGROUND_LAYER );
    
    // Make the appropriate tool visible by moving it between the FOREGROUND_LAYER,
    // the BACKGROUND_LAYER and the HIDDEN_LAYER
    if ( isWorking )
    {
      layeredPane.setLayer( zoomablePane, isZoomSelected ? BACKGROUND_LAYER : HIDDEN_LAYER );
      layeredPane.setLayer( shiftPane, isZoomSelected ? HIDDEN_LAYER : BACKGROUND_LAYER );
    }
    else
    {
      layeredPane.setLayer( zoomablePane, isZoomSelected ? FOREGROUND_LAYER : HIDDEN_LAYER );
      layeredPane.setLayer( shiftPane, isZoomSelected ? HIDDEN_LAYER : FOREGROUND_LAYER );
    }
    workingState = isWorking;
  }
  
  /**
   * Updates the layers for the appropriate tool.
   */
  private void changeTool()
  {
    setWorkingState( workingState );
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        exportFileChooser = new javax.swing.JFileChooser();
        toolButtonGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        cmdReset = new javax.swing.JButton();
        optZoomTool = new javax.swing.JToggleButton();
        optHandTool = new javax.swing.JToggleButton();
        cmdExport = new javax.swing.JButton();
        pnlCanvasContainer = new javax.swing.JPanel();
        scrLayeredPane = new javax.swing.JScrollPane();
        layeredPane = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        lblNThreads = new javax.swing.JLabel();
        lblResolution = new javax.swing.JLabel();
        lblLastTime = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuExport = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        menuExit = new javax.swing.JMenuItem();
        menuConfig = new javax.swing.JMenu();
        mnuConfiguration = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenuItem();

        exportFileChooser.setDialogTitle("Export image");
        exportFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mandelbrot Explorer");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        cmdReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/nuvola16/filenew.png"))); // NOI18N
        cmdReset.setText("Reset");
        cmdReset.setToolTipText("Restore original size");
        cmdReset.setBorderPainted(false);
        cmdReset.setMultiClickThreshhold(300L);
        cmdReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdResetActionPerformed(evt);
            }
        });
        toolBar.add(cmdReset);

        toolButtonGroup.add(optZoomTool);
        optZoomTool.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/nuvola16/zoomarea.png"))); // NOI18N
        optZoomTool.setSelected(true);
        optZoomTool.setText("Zoom");
        optZoomTool.setToolTipText("Select an area to zoom");
        optZoomTool.setMultiClickThreshhold(300L);
        optZoomTool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optZoomToolActionPerformed(evt);
            }
        });
        toolBar.add(optZoomTool);

        toolButtonGroup.add(optHandTool);
        optHandTool.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/hand.png"))); // NOI18N
        optHandTool.setText("Move");
        optHandTool.setToolTipText("Moves the image");
        optHandTool.setMultiClickThreshhold(300L);
        optHandTool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optHandToolActionPerformed(evt);
            }
        });
        toolBar.add(optHandTool);

        cmdExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/nuvola16/filesave.png"))); // NOI18N
        cmdExport.setText("Export");
        cmdExport.setToolTipText("Export the image as a PNG file");
        cmdExport.setBorderPainted(false);
        cmdExport.setMultiClickThreshhold(300L);
        cmdExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdExportActionPerformed(evt);
            }
        });
        toolBar.add(cmdExport);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(toolBar, gridBagConstraints);

        pnlCanvasContainer.setLayout(new java.awt.BorderLayout());

        scrLayeredPane.setViewportView(layeredPane);

        pnlCanvasContainer.add(scrLayeredPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblNThreads.setText("Ready");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(lblNThreads, gridBagConstraints);

        lblResolution.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        jPanel1.add(lblResolution, gridBagConstraints);

        lblLastTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(lblLastTime, gridBagConstraints);

        pnlCanvasContainer.add(jPanel1, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(pnlCanvasContainer, gridBagConstraints);

        menuFile.setText("File");

        menuExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/nuvola16/filesave.png"))); // NOI18N
        menuExport.setText("Save as image ...");
        menuExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExportActionPerformed(evt);
            }
        });
        menuFile.add(menuExport);
        menuFile.add(jSeparator1);

        menuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/nuvola16/exit.png"))); // NOI18N
        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        menuBar.add(menuFile);

        menuConfig.setText("Configuration");

        mnuConfiguration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/nuvola16/configure.png"))); // NOI18N
        mnuConfiguration.setText("Configure");
        mnuConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConfigurationActionPerformed(evt);
            }
        });
        menuConfig.add(mnuConfiguration);

        menuBar.add(menuConfig);

        menuHelp.setText("Help");

        menuAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/swingworker/demos/mandelbrot/view/icons/nuvola16/help.png"))); // NOI18N
        menuAbout.setText("About");
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents
  
  
  private void mnuConfigurationActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuConfigurationActionPerformed
  {//GEN-HEADEREND:event_mnuConfigurationActionPerformed
    
    if ( workingState )
      return;
    
    ConfigurationModel config = new ConfigurationModel( model );
    getConfigurationDialog().setModel( config );
    getConfigurationDialog().setVisible( true );
    
    ConfigurationModel modified = getConfigurationDialog().getModel();
    if (  modified != null )
    {
      model.setMaxIterations( modified.getDetailLevel() );
      model.setParallelizationRate( modified.getParallelizationRate() );
      updateModelToArea( model.getBounds() );
      lblNThreads.setText( model.getParallelizationRate() + " threads");
      lblResolution.setText( "Resolution: " + model.getMaxIterations() );
    }
    
  }//GEN-LAST:event_mnuConfigurationActionPerformed
  
  private void menuAboutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuAboutActionPerformed
  {//GEN-HEADEREND:event_menuAboutActionPerformed
    
    AboutDialog ad = new AboutDialog( this, true );
    ad.setLocationRelativeTo( this );
    ad.setVisible( true );
    
  }//GEN-LAST:event_menuAboutActionPerformed
  
  private void cmdExportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmdExportActionPerformed
  {//GEN-HEADEREND:event_cmdExportActionPerformed
    
    exportImage();
    
  }//GEN-LAST:event_cmdExportActionPerformed
  
  private void menuExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuExitActionPerformed
  {//GEN-HEADEREND:event_menuExitActionPerformed
    
    System.exit(0);
    
  }//GEN-LAST:event_menuExitActionPerformed
  
  private void optHandToolActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_optHandToolActionPerformed
  {//GEN-HEADEREND:event_optHandToolActionPerformed
    changeTool();
  }//GEN-LAST:event_optHandToolActionPerformed
  
  private void optZoomToolActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_optZoomToolActionPerformed
  {//GEN-HEADEREND:event_optZoomToolActionPerformed
    changeTool();
  }//GEN-LAST:event_optZoomToolActionPerformed
  
  private void menuExportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuExportActionPerformed
  {//GEN-HEADEREND:event_menuExportActionPerformed
    
    exportImage();
    
  }//GEN-LAST:event_menuExportActionPerformed
  
  private void cmdResetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmdResetActionPerformed
  {//GEN-HEADEREND:event_cmdResetActionPerformed
    
    MandelbrotModel newModel = new MandelbrotModel();
    newModel.setMaxIterations( model.getMaxIterations() );
    newModel.setParallelizationRate( model.getParallelizationRate() );
    setModel( newModel );
    progressPane.setBusyMessage("Returning to original size. Please wait...");
    updateModelToArea( model.getBounds() );
    
  }//GEN-LAST:event_cmdResetActionPerformed
  
  private ConfigurationDialog getConfigurationDialog()
  {
    if ( configurationDialog == null )
    {
      configurationDialog = new ConfigurationDialog( this, true );
      configurationDialog.setLocationRelativeTo( this );
    }
    return configurationDialog;
  }
  
  private void cancel()
  {
    if ( workingState )
    {
      for ( MandelbrotSwingWorker worker : mandelbrotWorkers )
        worker.cancel( true );
      setWorkingState( false );
    }
  }
  
  /**
   * This is invoked after the user selects a zoom.
   * Basically it sets the area to visualize, by invoking SwingWorkers
   *  appropriately to visualize the area.
   */
  private void updateModelToArea( Rectangle2D.Double newRegion )
  {
    model.setBounds( newRegion );
    int nWorkers = model.getParallelizationRate();
    setWorkingState( true );
    mandelbrotWorkers = new MandelbrotSwingWorker[ nWorkers ];
    startTimestamp = System.currentTimeMillis();
    
    int workersPerRow = (int) Math.sqrt( nWorkers );
    
    int userSpaceDeltaX = MandelbrotModel.XRESOLUTION / workersPerRow;
    int userSpaceDeltaY = MandelbrotModel.YRESOLUTION / workersPerRow;
    int i=0;
    for( int row=0; row<workersPerRow; row++)
    {
      for( int column = 0; column<workersPerRow; column++)
      {
        Rectangle workerUserArea = new Rectangle( row*userSpaceDeltaX, column*userSpaceDeltaY, userSpaceDeltaX, userSpaceDeltaY );
        mandelbrotWorkers[i] = new MandelbrotSwingWorker( model, workerUserArea );
        mandelbrotWorkers[i].addPropertyChangeListener( this );
        mandelbrotWorkers[i].execute();
        i++;
      }
    }
  }
  
  /**
   * This is a controller method to export the current image on screen to a PNG file.
   * This method handles a file dialog and file overwrite.
   */
  private void exportImage()
  {
    if ( exportFileChooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION )
    {
      File exportFile = exportFileChooser.getSelectedFile();
      if ( exportFile.exists() )
      {
        int option = JOptionPane.showConfirmDialog( this, "File exists. Overwrite?", "Confirm file overwrite", JOptionPane.OK_CANCEL_OPTION );
        if ( option != JOptionPane.OK_OPTION ) // Yes, wants to overwrite
        {
          return;
        }
      }
      exportImageToFile( exportFile );
    }
  }
  
  /**
   * This is the method that actually performs the export to a PNG file.
   * This spawns a custom SwingWorker to export on a background thread.
   */
  private void exportImageToFile( File exportFile )
  {
    progressPane.setBusyMessage("Exporting image. Please wait...");
    exportImageWorker = new ExportImageSwingWorker( model, exportFile );
    setWorkingState( true );
    exportImageWorker.addPropertyChangeListener( this );
    exportImageWorker.execute();
  }
  
  
  /**
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    java.awt.EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        new MandelbrotViewController().setVisible(true);
      }
    });
  }
  
  public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent)
  {
    // EVENTS FROM MANDELBROT SWING WORKERS
    if ( propertyChangeEvent.getSource() instanceof MandelbrotSwingWorker )
    {
      // DONE OR CANCELLED EVENTS
      if ( "state".equals( propertyChangeEvent.getPropertyName() ) )
      {
        int finishedWorkerCount = 0;
        for( MandelbrotSwingWorker worker : mandelbrotWorkers )
        {
          if ( worker.isDone() || worker.isCancelled() )
          {
            worker.removePropertyChangeListener( this );
            finishedWorkerCount ++;
          }
        }
        if ( finishedWorkerCount == mandelbrotWorkers.length )
        {
          setWorkingState( false );
          double timeRequired = (System.currentTimeMillis()-startTimestamp)/1000.0;
          infoPane.setMessage( "Finished in " + timeRequired + " seconds");
          lblLastTime.setText( "Finished in " + timeRequired + " s.");
        }
        imagePane.setImage( model.getImage() );
      }
      // PROGRESS EVENTS
      else if ( "progress".equals( propertyChangeEvent.getPropertyName() ) )
      {
        int overallProgress = 0;
        for( MandelbrotSwingWorker worker : mandelbrotWorkers )
        {
          int workerProgress = worker.getProgress();
          overallProgress += workerProgress;
        }
        overallProgress /= mandelbrotWorkers.length;
        progressPane.setProgress( overallProgress );
        imagePane.setImage( model.getImage() );
      }
    }
    // EVENTS FROM EXPORT IMAGE WORKER
    else if ( propertyChangeEvent.getSource() == exportImageWorker )
    {
      // DONE OR CANCELLED EVENTS
      if ( "state".equals( propertyChangeEvent.getPropertyName() ) )
      {
        if ( exportImageWorker.isDone() || exportImageWorker.isCancelled() )
        {
          exportImageWorker.removePropertyChangeListener( this );
          setWorkingState( false );
        }
        if ( exportImageWorker.isCancelled() )
          JOptionPane.showMessageDialog(this,"Image export was cancelled.","Export cancelled", JOptionPane.INFORMATION_MESSAGE );
      }
      // PROGRESS EVENTS
      else if ( "progress".equals( propertyChangeEvent.getPropertyName() ) )
      {
        progressPane.setProgress( exportImageWorker.getProgress() );
      }
    }
  }
  
  public void zoomRequested(org.jdesktop.swingworker.demos.mandelbrot.view.zoompane.ZoomEvent aZoomEvent)
  {
    Rectangle userRectangle = aZoomEvent.getZoomRectangle();
    // userRectangle must be inside [0,0]x[MandelbrotModel.XRESOLUTION,MandelbrotModel.YRESOLUTION].
    Rectangle2D.Double bounds = (Rectangle2D.Double) model.getBounds().clone() ;
    
    double xscale = bounds.width / MandelbrotModel.XRESOLUTION;
    double yscale = bounds.height / MandelbrotModel.YRESOLUTION;
    
    double width = userRectangle.width * xscale;
    double height = userRectangle.height * yscale;
    
    double xmin = (double) bounds.getMinX() + (double) (userRectangle.x*xscale);
    double ymax = (double) bounds.getMaxY() - (double) ((MandelbrotModel.YRESOLUTION-userRectangle.y)*yscale);
    
    bounds.setRect( xmin, ymax, width, height );
    
    
    progressPane.setBusyMessage("Zooming image, please wait...");
    updateModelToArea( bounds );
    
  }
  
  public void progressPaneCancelPressed(org.jdesktop.swingworker.demos.mandelbrot.view.progresspane.ProgressPaneCancelEvent anEvent)
  {
    cancel();
  }
  
  public void componentShifted(org.jdesktop.swingworker.demos.mandelbrot.view.shiftpane.ShiftEvent aShiftEvent)
  {
    Rectangle2D.Double bounds = (Rectangle2D.Double) model.getBounds().clone() ;
    
    double width = model.getBounds().getWidth();
    double height = model.getBounds().getHeight();
    
    double xscale = width / MandelbrotModel.XRESOLUTION;
    double yscale = height / MandelbrotModel.YRESOLUTION;
    
    double dx = aShiftEvent.getShift().x * xscale;
    double dy = aShiftEvent.getShift().y * yscale;
    
    double xmin = bounds.getMinX() - dx;
    double ymax = bounds.getMinY() - dy;
    
    bounds.setRect( xmin, ymax, width, height );
    
    progressPane.setBusyMessage("Shifting image, please wait...");
    updateModelToArea( bounds );
    
  }
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdExport;
    private javax.swing.JButton cmdReset;
    private javax.swing.JFileChooser exportFileChooser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLayeredPane layeredPane;
    private javax.swing.JLabel lblLastTime;
    private javax.swing.JLabel lblNThreads;
    private javax.swing.JLabel lblResolution;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuConfig;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem mnuConfiguration;
    private javax.swing.JToggleButton optHandTool;
    private javax.swing.JToggleButton optZoomTool;
    private javax.swing.JPanel pnlCanvasContainer;
    private javax.swing.JScrollPane scrLayeredPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.ButtonGroup toolButtonGroup;
    // End of variables declaration//GEN-END:variables
  
}
