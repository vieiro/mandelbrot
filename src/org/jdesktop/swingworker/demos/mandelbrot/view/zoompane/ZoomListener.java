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

import java.util.EventListener;

/**
 * ZoomListener receives ZoomEvents.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public interface ZoomListener
  extends EventListener
{
  /**
   * Invoked when the user selects an area for zoom.
   * @param aZoomEvent the zoom event containing the area to zoom.
   */
  public void zoomRequested( ZoomEvent aZoomEvent );
}
