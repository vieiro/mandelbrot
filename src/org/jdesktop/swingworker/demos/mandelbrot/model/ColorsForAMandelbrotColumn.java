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

/**
 * ColorsForAMandelbrotColumn represents a set of columns on a Mandelbrot
 *  image.
 * Objects of this class are used to transfer partial information between
 *  a worker thread and the Swing thread. The worker thread computes pieces of
 *  the image that are sent (as instances of ColorsForAMandelbrotColumn) to
 *  the Swing thread, where the image is updated.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class ColorsForAMandelbrotColumn
{
  /**
   * Creates a new instance of ColorsForAMandelbrotColumn
   */
  public ColorsForAMandelbrotColumn()
  {
  }

  /**
   * Holds value of property columnIndex.
   */
  private int columnIndex;

  /**
   * Getter for property columnIndex.
   * @return Value of property columnIndex.
   */
  public int getColumnIndex()
  {

    return this.columnIndex;
  }

  /**
   * Setter for property columnIndex.
   * @param columnIndex New value of property columnIndex.
   */
  public void setColumnIndex(int columnIndex)
  {

    this.columnIndex = columnIndex;
  }

  /**
   * Holds value of property colors.
   */
  private int[] colors;

  /**
   * Getter for property colors.
   * @return Value of property colors.
   */
  public int[] getColors()
  {
    return this.colors;
  }

  /**
   * Setter for property colors.
   * @param colors New value of property colors.
   */
  public void setColors(int[] colors)
  {

    this.colors = colors;
  }

  /**
   * Holds value of property rowIndex.
   */
  private int rowIndex;

  /**
   * Getter for property rowIndex.
   * @return Value of property rowIndex.
   */
  public int getRowIndex()
  {

    return this.rowIndex;
  }

  /**
   * Setter for property rowIndex.
   * @param rowIndex New value of property rowIndex.
   */
  public void setRowIndex(int rowIndex)
  {

    this.rowIndex = rowIndex;
  }
  
}
