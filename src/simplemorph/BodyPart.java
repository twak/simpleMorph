/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simplemorph;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Stores the actual information for an instance of a bodypart. Eg a Shark's left eyeball.
 * @author twak
 */
public class BodyPart 
{
    public String image;
    
    /**
     * This magic data structure contains lines. Identicle lines, shared between bodyparts have
     * the same Feature. All types of lines (plugs, sockets and edges) are stored in here.
     */
    Map<Feature, Line> lines = new HashMap();
    
    BodyPartSet bps;
    
    String name;
    
    double weight;
    
    private transient BufferedImage imageCache;
    
    public BodyPart(BodyPartSet bps, String image, String name )
    {
        this.bps= bps;
        this.image = image;
        this.name = name;
        
        // needs to set sensible lines for all defined in bps.edges, parent(? != null) & children
        if (bps.instances.size() == 0)
        {
            // a list of features to add that don't have a defined (by parents, us or children) location
            List<Feature> toAdd = new ArrayList();
            
            // no prototype for edge line location, but lines may have been defined on a deleted bodypart
            toAdd.addAll(bps.edgeFeatures.features);
            
            // if there is an instance of the parent that defines the socket, nab those line coords, else do something sensible
            if (bps.parent != null && bps.parentFeature != null)
            {
                // if parent has instances, nab coords
                if (bps.parent.instances.size() > 0)
                {
                    BodyPart parentInstance = bps.parent.instances.get(0);
                    for (Feature f : bps.parentFeature.features)
                        lines.put(f,parentInstance.lines.get(f).clone());
                }
                else //just chuck the plug lines in anywhoo
                    toAdd.addAll( bps.parentFeature.features );
            }
            
            // if there are instances for the children, should nab the coords? (will be offset, but wtf)
            for (FeatureSet fs : bps.children.keySet())
            {
                // if there is a child instance, nab the location of their plug
                if (bps.children.get(fs).instances.size() > 0)
                {
                    BodyPart childInstance = bps.children.get(fs).instances.get(0);
                    for (Feature f : fs.features)
                        lines.put(f, childInstance.lines.get(f).clone());
                }
                else // chuck children socket lines in anywhere
                    toAdd.addAll(fs.features);
                
            }
                
            // give each line a unique place
            int i = 1;
            for (Feature f : toAdd)
            {
                lines.put(f, new Line (new Point(0,i*30), new Point (100, i * 40),f));
                i++;
            }
        }
        else
        {
            BodyPart structure = bps.instances.get(0);
            for ( Map.Entry<Feature, Line> entry : structure.lines.entrySet() )
                lines.put( entry.getKey(), entry.getValue().clone() );
        }
        
        bps.instances.add(this);
    }
    
    public String toString()
    {
        return name;
    }

    /**
     * Remove all references to all features in the featureset.
     * @param parentFeature
     */
    public void removeAllFeatures( FeatureSet parentFeature )
    {
        for (Feature f : parentFeature.features)
            lines.remove(f);
    }
    
    /**
     * returns the cached image or loads from disk
     * @return
     */
    public BufferedImage getImage()
    {
        if ( imageCache != null )
        {
            return imageCache;
        }

        try
        {
            imageCache = ImageIO.read( new File( image ) );
        } catch ( IOException ex )
        {
            ex.printStackTrace();
        }
        return imageCache;
    }
}
