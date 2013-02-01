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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * MandelbrotModel holds information about a Mandelbrot fractal.
 * It basically contains the region (of the complex plane) where the
 *   diagram is going to be drawn, a maximum number of iterations (the
 *   bigger the more accurate the diagram is) and an image that contains
 *   the number of iterations for each point in the complex plane.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class MandelbrotModel
{
  public static final int XRESOLUTION=640;
  
  public static final int YRESOLUTION=480;
  
  private BufferedImage image;
  
  /**
   * Creates a new instance of MandelbrotModel
   */
  public MandelbrotModel()
  {
    // Some default values
    setBounds( new Rectangle2D.Double(-2.0, -1.2, 3.2, 2.4) );
    setMaxIterations( 1000 );
    image = new BufferedImage( XRESOLUTION, YRESOLUTION,
      BufferedImage.TYPE_BYTE_INDEXED, NiceIndexColorModel.getInstance() );
    setParallelizationRate( 4 );
  }  
  
  /**
   * Holds value of property xResolution.
   */
  private int xResolution;
  
  /**
   * Holds value of property yResolution.
   */
  private int yResolution;
  
  /**
   * Holds value of property maxIterations.
   */
  private int maxIterations;
  
  /**
   * Getter for property maxIterations.
   * @return Value of property maxIterations.
   */
  public int getMaxIterations()
  {
    
    return this.maxIterations;
  }
  
  /**
   * Setter for property maxIterations.
   * @param maxIterations New value of property maxIterations.
   */
  public void setMaxIterations(int maxIterations)
  {
    
    this.maxIterations = maxIterations;
  }
  
  /**
   * Holds value of property bounds.
   */
  private java.awt.geom.Rectangle2D.Double bounds;
  
  /**
   * Getter for property bounds.
   * @return Value of property bounds.
   */
  public java.awt.geom.Rectangle2D.Double getBounds()
  {
    
    return this.bounds;
  }
  
  /**
   * Setter for property bounds.
   * @param bounds New value of property bounds.
   */
  public void setBounds(java.awt.geom.Rectangle2D.Double bounds)
  {
    
    this.bounds = bounds;
  }
  
  public void updateColumnData( int aColumn, int aRow, int [] columnColors )
  {
    image.getRaster().setPixels( aColumn, aRow, 1, columnColors.length, columnColors );
  }
     
  public BufferedImage getImage()
  {
    return image;
  }

  /**
   * Holds value of property parallelizationRate.
   */
  private int parallelizationRate;

  /**
   * Getter for property parallelizationRate.
   * @return Value of property parallelizationRate.
   */
  public int getParallelizationRate()
  {

    return this.parallelizationRate;
  }

  /**
   * Setter for property parallelizationRate.
   * @param parallelizationRate New value of property parallelizationRate.
   */
  public void setParallelizationRate(int parallelizationRate)
  {

    this.parallelizationRate = parallelizationRate;
  }
}
