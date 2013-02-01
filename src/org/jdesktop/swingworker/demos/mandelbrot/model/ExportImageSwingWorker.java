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

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

/**
 * ExportImageSwingWorker is a SwingWorker that saves a BufferedImage as
 *  a PNG file.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 * @version $Revision: $
 */
public class ExportImageSwingWorker
    extends SwingWorker<Boolean, String> {

    private File file;
    private BufferedImage image;
    private String errorMessage;

    /**
     * Creates a new instance of ExportImageSwingWorker
     */
    public ExportImageSwingWorker(MandelbrotModel aModel, File aFile) {
        image = aModel.getImage();
        file = aFile;
    }

    @Override
    protected Boolean doInBackground() {
        try {
            ImageIO.write(image, "png", file);
            return Boolean.TRUE;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (file.exists()) {
                file.delete();
            }
            return Boolean.FALSE;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    protected void done() {
        if (isCancelled() && file.exists()) {
            file.delete();
        }
    }
}
