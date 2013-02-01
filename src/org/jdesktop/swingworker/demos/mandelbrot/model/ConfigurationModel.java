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
 * ConfigurationModel holds information for configuration.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class ConfigurationModel
{
  /**
   * Creates a new instance of ConfigurationModel.
   */
  public ConfigurationModel( MandelbrotModel aModel )
  {
    setDetailLevel( aModel.getMaxIterations() );
    setParallelizationRate( aModel.getParallelizationRate() );
  }
  
  public ConfigurationModel( ConfigurationModel anotherModel )
  {
    setDetailLevel( anotherModel.getDetailLevel() );
    setParallelizationRate( anotherModel.getParallelizationRate() );
  }

  /**
   * Holds value of property detailLevel.
   */
  private Integer detailLevel;

  /**
   * Getter for property detailLevel.
   * @return Value of property detailLevel.
   */
  public Integer getDetailLevel()
  {

    return this.detailLevel;
  }

  /**
   * Setter for property detailLevel.
   * @param detailLevel New value of property detailLevel.
   */
  public void setDetailLevel(Integer detailLevel)
  {

    this.detailLevel = detailLevel;
  }

  /**
   * Holds value of property parallelizationRate.
   */
  private Integer parallelizationRate;

  /**
   * Getter for property parallelizationRate.
   * @return Value of property parallelizationRate.
   */
  public Integer getParallelizationRate()
  {

    return this.parallelizationRate;
  }

  /**
   * Setter for property parallelizationRate.
   * @param parallelizationRate New value of property parallelizationRate.
   */
  public void setParallelizationRate(Integer parallelizationRate)
  {

    this.parallelizationRate = parallelizationRate;
  }
  
}
