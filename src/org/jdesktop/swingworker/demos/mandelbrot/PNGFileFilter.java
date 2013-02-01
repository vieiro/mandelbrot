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

import javax.swing.filechooser.FileFilter;

/**
 * PNGFileFilter is a filter for PNG files.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class PNGFileFilter
  extends FileFilter
{
  /**
   * Creates a new instance of PNGFileFilter
   */
  public PNGFileFilter()
  {
  }

  public boolean accept(java.io.File file)
  {
    return file.isFile() ? file.getName().toUpperCase().endsWith("PNG") : true;
  }

  public String getDescription()
  {
    return "PNG (Portable Network Graphics) files";
  }
  
}
