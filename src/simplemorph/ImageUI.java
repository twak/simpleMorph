/*
 * MorphImage.java
 *
 * Created on December 4, 2008, 11:06 PM
 */

package simplemorph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  tom
 */
public class ImageUI extends javax.swing.JPanel
{
    BufferedImage image;
//    private List<Line> lines = new ArrayList();
    public PropertyChangeSupport pcs = new PropertyChangeSupport( this );
    
    public BodyPart bodyPart;
    public FeatureSet fs;
    
    final static int tol = 30;
    
    // a line is in the process of being created(dragged out)
    Line creating = null;
    
    /** Creates new form MorphImage */
    public ImageUI() 
    {
        initComponents();
        
        ImageMouseListener iml = new ImageMouseListener() ;
        addMouseListener( iml );
        addMouseMotionListener( iml );
    }

    public void setBodyPart(BodyPart bp)
    {
        this.bodyPart = bp;
        repaint();
    }
    
    public void setFeatureSet(FeatureSet fs)
    {
        this.fs = fs;
        repaint();
    }
    
    public void setImage(BufferedImage image)
    {
        this.image = image;
        repaint();
    }
    
    @Override
    public void paint( Graphics g )
    {
        if (image == null)
        {
            g.setColor( Color.pink );
            g.fillRect( 0,0, getWidth(), getHeight());
            return;
        }
        else
        {
            g.setColor( Color.white );
            g.fillRect( 0,0, getWidth(), getHeight());
        }
        
        g.drawImage( image, 0,0,null);
        
        for (Line l : getLines())
            drawLine (l, Color.red, Color.magenta, Color.blue, g);
        
        if (creating != null)
            drawLine (creating, Color.orange, Color.magenta, Color.blue, g ); // memo to self: define some less ugly colours
    }

    public void drawLine (Line l, Color aCol, Color startCol,  Color endCol, Graphics g)
    {
        g.setColor( aCol );
            g.drawLine( l.start.x, l.start.y, l.end.x, l.end.y);
            g.setColor( startCol  );
            g.fillRect( l.start.x - 2, l.start.y - 2, 5, 5 );
            g.setColor( endCol  );
            g.fillRect( l.end.x - 2, l.end.y - 2, 5, 5 );
                
    }
    
    private List<Line> getLines()
    {
        List<Line> lines = new ArrayList();
        
        if (fs == null)
                return lines;
        // slow, but this is academia, punks
        for (Feature f : fs.features)
            lines.add( bodyPart.lines.get( f ) );
        
        return lines;
    }
    
    public void addLine(Line line, boolean fireListener)
    {
        if (fs == null)
        {
            System.out.println("Please select a feature to define lines for!");
            return;
        }
        Feature newFeature = new Feature( line.hashCode()+"" );
        line.shared = newFeature;
        // add to metadata/prototype
        bodyPart.bps.addFeature(fs, line);
                
        if (fireListener)
            pcs.firePropertyChange( "lines", false, true);
        
        ImageUI.this.repaint();
    }
    
    public void removeLine(Line line, boolean fireListener)
    {
        if ( fs == null )
        {
            System.out.println( "Please select a feature to remove lines" );
            return;
        }
                
        Feature toRemove = null;
        for (Map.Entry<Feature, Line> entry : bodyPart.lines.entrySet())
            if (entry.getValue() == line)
                toRemove = entry.getKey(); //broken!
        
        bodyPart.bps.removeFeature( fs, toRemove );
        
        if (fireListener)
            pcs.firePropertyChange( "lines", true, false);
        
        ImageUI.this.repaint();
    }

    private class ImageMouseListener extends MouseAdapter
    {
        final static int tolSquared = tol * tol;
        Point editing = null;
        
        @Override public void mousePressed( MouseEvent e )
        {
            Point p = e.getPoint();
            
            List<Line> lines = getLines();
            
            for (int i = 0; i < lines.size(); i++) // we might remove the line!
            {
                Line l = lines.get(i);
                for (Point startEnd : new Point[] {l.start, l.end})
                    if (startEnd.distanceSq( p ) < tol)
                    {
                        if (e.getButton() != MouseEvent.BUTTON3)
                            editing = startEnd;
                        else // right click to remove
                        {
                            removeLine( l, true );
                        }
                        
                        ImageUI.this.repaint();
                        
                        return;
                    }
            }
            
            /*
             *  Nothing clicked on, create a line
             */
            creating = new Line((Point)p.clone(), (Point) p.clone());
            
            
            ImageUI.this.repaint();
        }       
        
        @Override
        public void mouseReleased( MouseEvent e )
        {
            editing = null;
            if (creating != null)
                addLine( creating, true );
            creating = null;
        }


        @Override
        public void mouseDragged( MouseEvent e )
        {
            if (creating != null)
            {
                creating.end = e.getPoint();
                ImageUI.this.repaint();
                return;
            }
            
            if (editing == null)
                return;
            
            editing.setLocation( getSafePoint (e.getPoint()) );
            
            ImageUI.this.repaint();
        }       
        
        private Point getSafePoint (Point in)
        {
            return new Point(
                    Util.clamp( 0, in.x, image.getWidth()-1 ),
                    Util.clamp( 0, in.y, image.getHeight()-1) ) ;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
