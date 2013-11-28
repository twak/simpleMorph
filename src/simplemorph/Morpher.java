
package simplemorph;

import com.thoughtworks.xstream.XStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.vecmath.Point2d;
import static simplemorph.Util.*;

/**
 * Replacement for MorphOutputWindow
 * @author twak
 */
public class Morpher 
{
    List<BodyPart> parts;
    
    // values from eqn. 4 in the paper
    static double b_ = 2,p_ = 1,a_ = 1;
    
    // output image
    BufferedImage destImage = null;
    
    BodyPartSet bodyPartSet;
    
    /**
     * @param parts List of parts with their .weight set. (normally normalized...but go wild ;) )
     */
    public Morpher (List<BodyPart> parts)
    {
        this.parts = new ArrayList<BodyPart>(parts);
        
        // remove any parts with low weight
        Iterator<BodyPart> bit = this.parts.iterator();
        while (bit.hasNext())
            if (bit.next().weight  < 0.01)
                bit.remove();   
    }
        
    public BufferedImage doMorph()
    {
        return doMorph(new ArrayList(), null);
    }
    
    private BufferedImage doMorph(List<Line> parentSocketLines, BufferedImage di)
    {
        destImage = di;
        
        if (parts.size() == 0)
            return new BufferedImage(1,1, BufferedImage.TYPE_4BYTE_ABGR);
     
        
        bodyPartSet = parts.get( 0 ).bps;
        
        FeatureSet featureSet = bodyPartSet.edgeFeatures;
        
        double normalizedAlpha = 0;
        Rectangle maxSize = new Rectangle();
        
        bodyPartSet.normaliseInstanceWeights();
        
        for (BodyPart part : parts)
        {
            // calculate normalization (denominator) alpha parameter for all weights
            normalizedAlpha += part.weight;
            // find maximum size of input image
            maxSize.add( new Rectangle(
                    part.getImage().getWidth(), 
                    part.getImage().getHeight()) );
        }
        
        List<Feature> plugFeatures = new ArrayList();
        for (Line l : parentSocketLines)
            plugFeatures.add( l.shared);
        
        // create a list of lines that define where the plug should be in the output image by the weight of each instance
        List<Line> plugLines = weighFeatures( plugFeatures );
        // set the location of the destination edge features by their weights
        List<Line> destLines = weighFeatures( featureSet.features );
        
        
            
        
        // move lines to match parent line location
        for (Line l : destLines)
        {
            moveLine (l, parentSocketLines, plugLines);
        }
        
        
        if (destImage == null) // root image!
            destImage = new BufferedImage( maxSize.width, maxSize.height, BufferedImage.TYPE_INT_RGB );
        
        
        
        Graphics g = destImage.createGraphics();
            
                
        // we draw the foreground components first (those "smaller" parts lower down the tree) so that we
        // build the alpha up from foreground to background
        
        // add in each child bodypart
        
        for (Map.Entry<FeatureSet, BodyPartSet> entry : bodyPartSet.children.entrySet())
        {
            List<Line> socketLines = weighFeatures( entry.getKey().features );
            
            
            for (Line l : socketLines)
            {
                moveLine (l,  parentSocketLines,plugLines);
            }
            
            new Morpher( entry.getValue().instances ).doMorph( socketLines, destImage );
            
            g.setColor( Color.green);
            for (Line l : socketLines)
                g.drawLine( l.start.x, l.start.y, l.end.x, l.end.y );
        }
        
        // blend in each instance
        for (BodyPart part: parts)
        {
            List<Line> srcLines = new ArrayList();
            
            for (Feature f : featureSet.features)
                srcLines.add(part.lines.get(f));
            
            morphImages(part.getImage(), destImage, srcLines, destLines, part.weight/normalizedAlpha);
        }
        
        
        for (Line l : destLines)
        {
            g.setColor( Color.cyan );
            g.drawLine( l.start.x, l.start.y, l.end.x, l.end.y );
        }
        
        g.dispose();
        
        return destImage;
    }
    
    private List<Line> weighFeatures( List<Feature> in)
    {
        // calculate weighted (by bodyPart.weight) position of each line
        Map<Feature, Line> tmp = new HashMap();
        for (BodyPart bp :parts)
            for (Feature f : in)
            {
                Line l = bp.lines.get(f);
                Line scaledL = l.clone(bp.weight);
                
                if (tmp.containsKey(f))
                   tmp.get(f).add(scaledL);
                else
                    tmp.put(f, scaledL);
            }
        
            
            
        List<Line> destLines=  new ArrayList();
        
        // ensure features are in the same order in the destination set
        for (Feature f : in)
        {
           destLines.add(tmp.get( f ));
        }
        return destLines;
    }
    
//    private List<Line> getLinesFor (FeatureS)
    
    private void moveLine (Line line, List<Line> src, List<Line> dest)
    {
        assert(src.size() == dest.size());
        
        if (src.size() == 0)
            return; // nothing to do
        
        line.start = translate( line.start.x, line.start.y, src, dest);
        line.end = translate( line.end.x, line.end.y, src, dest);
    }
    
    private void morphImages(
            BufferedImage source, 
            BufferedImage output, 
            List<Line> srcLines, 
            List<Line> destLines, 
            double blend)
    {
        assert (srcLines.size() == destLines.size());
                
        int width = output.getWidth();
        int height = output.getHeight();

        // x,y <- output coords
        for ( int x = 0; x < width; x++ )
        {
            for ( int y = 0; y < height; y++ )
            {
                Point srcLoc = getSafePoint( source, translate (x,y,srcLines, destLines) );
                if (srcLoc != null) // out of bounds check
                    output.setRGB( x, y, addAlpha ( blend, output.getRGB( x, y ), source.getRGB( srcLoc.x, srcLoc.y )  ));
            }
        }
    }
    
    /**
     * @see http://www1.cs.columbia.edu/%7Ecs4162/slides/p35-beier.pdf
     * @return a pt defined by moving pt(x,y) from a frame defined by srcLines to a frame defnd by destLines
     * note:result will need bounds check
     */
    private Point translate(int x, int y,List<Line> srcLines, 
            List<Line> destLines )
    {
        assert(srcLines.size() == destLines.size());
        
        if (srcLines.size() == 0)
            return new Point (x,y);
        
        Point2d DSUM = new Point2d(); //vector

        double weightsum = 0;

        Point X = new Point( x, y );

        for ( int i = 0; i < srcLines.size(); i++ ) //Map.Entry<Feature, LinePair> entry : lines.entrySet() )
        {
            Line la = srcLines.get( i );
            Line lb = destLines.get( i );

            Point P = lb.start, Q = lb.end;
            double u = dot( subtract( X, P ), subtract( Q, P ) ) / distSquared( Q, P );

            // invert vector from P to Q
            double v = dot( subtract( X, P ), flip90( subtract( Q, P ) ) ) / dist( Q, P );

            // i's are coords in src image
            Point Pi = la.start, Qi = la.end;

            Point Xi = Util.add( Pi, scale( u, subtract( Qi, Pi ) ),
                                 scale( 1 / dist( Qi, Pi ), ( scale( v, flip90( subtract( Qi, Pi ) ) ) ) ) );

            Point Di = subtract( Xi, X );
            double dist;
            if ( 0 < u && u < 1 )
                dist = Math.abs( v );
            else if ( u < 0 )
                dist = dist( X, P );
            else
                dist = dist( X, Q );

            double weight = Math.pow( Math.pow( dist( P, Q ), p_ ) / ( a_ + dist ), b_ );

            // do dsum in double prec...? still necessary?
            DSUM.add( new Point2d( weight * Di.x, weight * Di.y ) );
            weightsum += weight;
        }

        return new Point(
                ( int ) ( X.x + ( DSUM.x / weightsum ) ),
                ( int ) ( X.y + ( DSUM.y / weightsum ) ) );
                
    }
    
    private int addAlpha( double alpha, int orig, int toAdd    )
    {
        // alpha is limited to the remaining alpha eg: if pixel has been draw,
        // alpha is 1, and nothing else is drawn.
//        alpha = Math.min( alpha, (orig >> 24) / (double) 255);
        
        orig += ((int)(((toAdd & 0xff000000) >> 24) * alpha)) << 24;
        orig += ((int)(((toAdd & 0x00ff0000) >> 16) * alpha)) << 16;
        orig += ((int)(((toAdd & 0x0000ff00) >>  8) * alpha)) <<  8;
        orig += ((int)(((toAdd & 0x000000ff) >>  0) * alpha)) <<  0;
        
        return orig;
    }
    
    
    private Point getSafePoint(  BufferedImage image, Point in )
    {
        if (in.x >= image.getWidth() || in.x < 0 ||
            in.y >= image.getHeight() || in.y < 0 )
            return null;
        
        return new Point(
                Util.clamp( 0, in.x, image.getWidth()-1 ),
                Util.clamp( 0, in.y, image.getHeight()-1 ) );
    }
    
    private int mergeRGB(int a, int b, double alpha)
    {
        // merge each channel
        int out;
        double invAlpha = 1-alpha;
        
        // i bloody hate bloody bit munging
        out = ((int)((((a & 0xff000000) >> 24) * alpha) + 
                (((b & 0xff000000) >> 24)  * invAlpha)) << 24);
        out |= ((int)((((a & 0x00ff0000) >> 16) * alpha) + 
                (((b & 0x00ff0000) >> 16)  * invAlpha)) << 16);
        out |= ((int)((((a & 0x0000ff00) >> 8) * alpha) + 
                (((b & 0x0000ff00) >> 8)  * invAlpha)) << 8);
        
        out |= (int)((a & 0xFF) * alpha) + (int)((b & 0xFF) * invAlpha);
        
        
        return out;
    }
    
    public static void main(String[] args)
    {
        XStream xs = new XStream();
        
        // b: 0.5..2
        // p: 0...1
        // a: near 0 to infinine
        
        try
        {
            BodyPartSet root = ( BodyPartSet ) xs.fromXML( new FileInputStream( "fish" ) );
            
            int i = 1;//new File (".").getAbsolutePath()
            for ( Morpher.b_ = 0.5; Morpher.b_ < 2; Morpher.b_ += 0.01 )
            {
                System.out.println("B: "+Morpher.b_);
                BufferedImage out = new Morpher( root.instances ).doMorph();
                Graphics g = out.getGraphics();
                g.setFont( new Font( "arial",0, 20));
                
                g.setColor( Color.black );
                g.drawString( String.format( "b=%2.3f", Morpher.b_ ), 0, 25 );
                g.dispose();
                
                try
                {
                    ImageIO.write( out, "png", new File( String.format( "b__%04d.png", ( i++ ) ) ) );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }

            }
            
            root = ( BodyPartSet ) xs.fromXML( new FileInputStream( "fish" ) );
            
            Morpher.b_ = 2;
            
            i = 1;
            for ( Morpher.p_ = 0; Morpher.p_ < 1; Morpher.p_ += 0.005 )
            {
                System.out.println("P: "+Morpher.p_);
                BufferedImage out = new Morpher( root.instances ).doMorph();
                Graphics g = out.getGraphics();
                g.setFont( new Font( "arial",0, 20));
                
                g.setColor( Color.black );
                g.drawString( String.format( "p=%2.3f", Morpher.p_ ), 0, 25 );
                g.dispose();
                
                try
                {
                    ImageIO.write( out, "png", new File( String.format( "p__%04d.png", ( i++ ) ) ) );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
            
            
            Morpher.b_ = 2;
            Morpher.p_ = 1;
            
            for ( int q = 0; q < 200; q++ )
            {
                Morpher.a_ =Math.pow( 1.1, q) -1;
                System.out.println("A: "+Morpher.a_);
                BufferedImage out = new Morpher( root.instances ).doMorph();
                Graphics g = out.getGraphics();
                g.setFont( new Font( "arial",0, 20));
                
                g.setColor( Color.black );
                g.drawString( String.format( "a=%2.3f", Morpher.a_ ), 0, 25 );
                g.dispose();
                
                try
                {
                    ImageIO.write( out, "png", new File( String.format( "a__%04d.png", ( q ) ) ) );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
            
            
        }
        
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        
            
    }
}
