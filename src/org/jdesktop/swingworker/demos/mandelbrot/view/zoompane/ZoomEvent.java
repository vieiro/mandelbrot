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
package org.jdesktop.swingworker.demos.mandelbrot.view.zoompane;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;

/**
 * ZoomEvent is generated when the user selects an area to zoom.
 * The event contains the area the user has selected.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class ZoomEvent
  extends EventObject
{
  private Rectangle2D.Double userRegion;
  
  /**
   * Creates a new instance of ZoomEvent
   */
  public ZoomEvent( Object aSource, Rectangle anArea, Rectangle2D.Double anUserRegion )
  {
    super( aSource );
    setZoomRectangle( anArea );
    setUserRegion( anUserRegion );
  }

  public Rectangle2D.Double getUserRegion()
  {
    return userRegion;
  }
  
  public void setUserRegion( Rectangle2D.Double anUserRegion )
  {
    userRegion = anUserRegion;
  }
  
  /**
   * Holds value of property zoomRectangle.
   */
  private java.awt.Rectangle zoomRectangle;

  /**
   * Getter for property zoomRectangle.
   * @return Value of property zoomRectangle.
   */
  public java.awt.Rectangle getZoomRectangle()
  {

    return this.zoomRectangle;
  }

  /**
   * Setter for property zoomRectangle.
   * @param zoomRectangle New value of property zoomRectangle.
   */
  public void setZoomRectangle(java.awt.Rectangle zoomRectangle)
  {

    this.zoomRectangle = zoomRectangle;
  }
  
}
