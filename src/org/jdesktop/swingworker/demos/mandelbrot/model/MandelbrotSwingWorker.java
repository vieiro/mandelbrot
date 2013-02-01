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
package org.jdesktop.swingworker.demos.mandelbrot.model;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.SwingWorker;


/**
 * MandelbrotSwingWorker is a SwingWorker that computes a Mandelbrot diagram.
 * This SwingWorker receives a MandelbrotModel and updates the image corresponding
 *   to a region of the complex plane.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class MandelbrotSwingWorker
  extends SwingWorker<MandelbrotModel, ColorsForAMandelbrotColumn>
{
  private MandelbrotModel model;
  private Rectangle userArea;
  /**
   * Creates a new instance of MandelbrotSwingWorker.
   * @param anInputModel the model (containing the region of the complex plane
   *  to compute, the maximum number of iterations, etc.) whose image is
   *  updated.
   * @param aRegion a region of the user space to analyze.
   */
  public MandelbrotSwingWorker( MandelbrotModel anInputModel, Rectangle anUserArea )
  {
    model = anInputModel;
    userArea = anUserArea;
  }
  
  
  public MandelbrotModel getModel()
  {
    return model;
  }
  
  /**
   * This is invoked in the Swing thread.
   * This method receives a set of pieces of the image and updates the colors
   *  of the image.
   * @param colorsForAMandelbrotColumn a set of columns of the diagram. Each
   *  object contains a column index and a set of colors for each one of the
   *  pixels of the column. The color of the pixels correspond to the number of
   *  iterations.
   */
  @Override
  protected void process(List<ColorsForAMandelbrotColumn> colorsForAMandelbrotColumn)
  {
    try
    {
      for( ColorsForAMandelbrotColumn columnColors : colorsForAMandelbrotColumn )
      {
        // Update the raster for this column
        int column = columnColors.getColumnIndex();
        int row = columnColors.getRowIndex();
        int [] colors = columnColors.getColors();
        model.updateColumnData( column, row, colors );
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }
  
  /**
   * Updates the image in the input mandelbrot model.
   * This method is invoked in a worker thread.
   * @return a MandelbrotModel corresponding to the input MandelbrotModel,
   *  but with an updated image.
   */
  @Override
    protected MandelbrotModel doInBackground()
    throws Exception
  {
    setProgress( 0 );
    try
    {
      // Some simple maths on the region to analyze...
      // TODO: ESTA AQUI EL MEOLLO EN DIVIDIR EL TEMA
      double xScale =
        ( model.getBounds().width ) / MandelbrotModel.XRESOLUTION ;
      double yScale =
        ( model.getBounds().height ) / MandelbrotModel.YRESOLUTION ;
      
      double x = model.getBounds().x + userArea.x * xScale;
      double y = model.getBounds().y + userArea.y * yScale;
      
      int progress = 0;
      
      // For each column "ix"...
      for( int ix = 0; ix < userArea.width; ix++ )
      {
        // Create a new "chunk" of information with colors for this column...
        ColorsForAMandelbrotColumn columnColors = new ColorsForAMandelbrotColumn();
        columnColors.setColors( new int[ userArea.height ] );
        columnColors.setColumnIndex( userArea.x+ix );
        columnColors.setRowIndex( userArea.y );
        y = model.getBounds().y + userArea.y * yScale;
        // For each row "iy"...
        for( int iy = 0; iy<userArea.height; iy++ )
        {
          // Compute the number of iterations needed for convergence at this point.
          int nIterations = getIterationsForPoint( x, y );
          // Update the colors for this column and row
          columnColors.getColors()[ iy ] = (nIterations % model.getMaxIterations());
          y += yScale;
        }
        // Publish this column colors (these will be processed in the "process()" method.
        publish( columnColors );
        x += xScale;
        
        // Update progress and notify PropertyChangeListeners...
        progress = ((int) ((1+ix)*100.0/userArea.width));
        if ( 0 == progress%5 )
        {
          setProgress( progress  );
          // This is important!! I'll comment on this later.
          try
          {
            Thread.sleep(1L);
          }
          catch( InterruptedException ie )
          {
            return model;
          }
        }
      }
      return model;
    }
    catch( Exception e )
    {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Given a point on the complex plane this method computes the
   *  number of iterations needed for divergenge.
   * @param a the real part of the complex point.
   * @param b the imaginary part of the complx point.
   * @return the number of iterations needed for divergence.
   */
  private int getIterationsForPoint( double a, double b )
  {
    double x = a;
    double y = b;
    double rho2=0.0f;
    int nIterations = 0;
    
    // rho2<4.0 means that rho<2.0 because if rho>=2.0 then there's divergence.
    for( nIterations=0;
    rho2 < 4.0f && nIterations<model.getMaxIterations();
    nIterations ++ )
    {
      double xnew = x*x - y*y + a;
      double ynew = 2*x*y + b;
      x=xnew;
      y=ynew;
      rho2 = x*x + y*y;
    }
    
    return nIterations;
  }
  
}
