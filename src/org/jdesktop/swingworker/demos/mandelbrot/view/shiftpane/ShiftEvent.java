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
package org.jdesktop.swingworker.demos.mandelbrot.view.shiftpane;

import java.awt.Point;
import java.util.EventObject;

/**
 * ShiftEvent is thrown after the user shifts the panel.
 * The event contains a point representing a delta vector (dx,dy).
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class ShiftEvent
  extends EventObject
{
  private Point shift;
  /**
   * Creates a new instance of ShiftEvent
   */
  public ShiftEvent( Object aSource, Point aShift )
  {
    super( aSource );
    shift = aShift;
  }
  
  public Point getShift()
  {
    return shift;
  }
}
