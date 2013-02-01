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
import java.awt.Color;
import java.awt.image.IndexColorModel;


/**
 * NiceIndexColorModel is a singleton with some nice colors.
 * Well, I know nice is a subjective thing, but this is a subjective
 *  project too ;-)
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
class NiceIndexColorModel
  extends IndexColorModel
{
  private static byte[] r;
  private static byte[] g;
  private static byte[] b;
  private static NiceIndexColorModel instance;
  
  private static void computeColors()
  {
    r = new byte[256];
    g = new byte[256];
    b = new byte[256];
    r[0]=g[0]=b[0]=0;
    
    // From 1 to 32 we go from black to red (000000 to FF0000)
    // We use a parabola here ( y = sqrt( 2048*y) )
    for( int i=1;i<=32;i++)
    {
      r[i] = (byte) Math.sqrt( 2048. * i );
    }
    // Now, from 32 to 128 we go from red to yellow
    // That is, from FF0000 to FFFF00
    for( int i=33; i<=128; i++ )
    {
      r[i] = (byte) 0xFF;
      g[i] = (byte) (256.0f*(i/128.f));
    }
    // Finally from 128 to 256 we go from yellow to white (FFFF00 to FFFFFF)
    for( int i=129; i<256; i++ )
    {
      r[i]=g[i]=(byte)0xff;
      b[i] = (byte) (256.0f*(i/256.f));
    }
    
    r[255]=g[255]=b[255]=(byte) 0xff;
    
      
  }
  /**
   * Creates a new instance of NiceIndexColorModel
   */
  private NiceIndexColorModel()
  {
    super(8,256,r,g,b);
  }
  
  public static final synchronized NiceIndexColorModel getInstance()
  {
    if ( instance == null )
    {
      computeColors();
      instance = new NiceIndexColorModel();
    }
    return instance;
  }
}
