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
package org.jdesktop.swingworker.demos.mandelbrot.view.infopane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import javax.swing.Timer;

/**
 * InfoPane shows a message that dissappears within one second.
 * @author Antonio Vieiro (vieiro@dev.java.net)  $Author$
 */
public class InfoPane extends javax.swing.JPanel
{
  private static Color [] COLORS =
  {
    new Color(0,255,0,255),
      new Color(0,255,0,208),
      new Color(0,255,0,161),
      new Color(0,255,0,114),
      new Color(0,255,0,67),
      new Color(0,255,0,20),
      new Color(0,255,0,8),
  };
  
  public enum State
  {
    IDLE,
    DRAWING_TEXT,
    FADING_TEXT
  };
  
  private State state;
  private Timer timer;
  private int fadingTickCount = 0;
  private int letterCount = 0;
  private String message;
  
  /** Creates new form InfoPane */
  public InfoPane()
  {
    initComponents();
    lblMessage.setFont( getLCDFont() );
    timer = new Timer( 75,
      new ActionListener()
    {
      public void actionPerformed( ActionEvent anActionEvent )
      {
        processTimerTick();
      }
    } );
  }
  
  
  private static Font LCDFont;
  /**
   * Tries to retrieve a fancy font from disk.
   */
  private static Font getLCDFont()
  {
    if ( LCDFont == null )
    {
      URL url = InfoPane.class.getResource("fonts/LCD-U___.TTF");
      if ( url != null )
      {
        try
        {
          InputStream input = new BufferedInputStream( url.openStream(), 4*1024 );
          LCDFont = Font.createFont( Font.TRUETYPE_FONT, input ).deriveFont(34.0f);
          input.close();
        }
        catch( Exception e )
        {
          LCDFont = new java.awt.Font("DialogInput", Font.BOLD, 24);
        }
      }
    }
    return LCDFont;
  }
  public void setMessage( String aMessage )
  {
    stop();
    this.message = aMessage;
    lblMessage.setText("");
    start();
  }
  
  
  private void start()
  {
    fadingTickCount = 0;
    letterCount = 0;
    state = State.DRAWING_TEXT;
    lblMessage.setForeground( COLORS[0] );
    timer.start();
  }
  
  private void stop()
  {
    message = "";
    lblMessage.setText("");
    timer.stop();
    state = State.IDLE;
    repaint();
    fadingTickCount = 0;
    letterCount = 0;
  }
  
  private void processTimerTick()
  {
    switch( state )
    {
      case IDLE:
        break;
      case DRAWING_TEXT:
        letterCount ++;
        if ( letterCount <= message.length() )
        {
          lblMessage.setText( message.substring(0,letterCount) + ((letterCount&1)==0? "" : "\u2588") );
          lblMessage.repaint();
        }
        else
        {
          state = State.FADING_TEXT;
          lblMessage.repaint();
        }
        break;
      case FADING_TEXT:
        if ( fadingTickCount >= COLORS.length )
        {
          stop();
        }
        else
        {
          lblMessage.setForeground( COLORS[fadingTickCount] );
          lblMessage.repaint();
          fadingTickCount++;
        }
    }
  }
  
  public void paintComponent( Graphics g )
  {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
    super.paintComponent( g2d );
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents()
  {
    java.awt.GridBagConstraints gridBagConstraints;

    lblMessage = new javax.swing.JLabel();

    setLayout(new java.awt.GridBagLayout());

    setOpaque(false);
    lblMessage.setForeground(java.awt.Color.green);
    lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    lblMessage.setText("Hola");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
    add(lblMessage, gridBagConstraints);

  }
  // </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel lblMessage;
  // End of variables declaration//GEN-END:variables
  
}