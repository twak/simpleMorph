/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simplemorph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author twak
 */
public class BodyPartSet implements Comparable <BodyPartSet>
{
    FeatureSet edgeFeatures = new FeatureSet("edge features");//new ArrayList();
    Map<FeatureSet,BodyPartSet> children = new HashMap();
    
    FeatureSet parentFeature; // the feature in the parent that defines us
    BodyPartSet parent; // null if root
    
    List<BodyPart> instances = new ArrayList();
    
    public BodyPartSet(FeatureSet parentFeature, BodyPartSet parent)
    {
        this.parent = parent;
        this.parentFeature = parentFeature;
    }
    
    public FeatureSet getChild(BodyPartSet bps)
    {
        for (Map.Entry<FeatureSet, BodyPartSet> entry : children.entrySet())
            if (entry.getValue() == bps)
                return entry.getKey();
        
        return null;
    }
    
    public BodyPartSet gedChild(Feature feature)
    {
        return children.get(feature);
    }
    
    public void addChild (FeatureSet feature, BodyPartSet bps)
    {
        children.put(feature, bps);
    }

    /**
     * Adds a line-descriptor to all feature sets that use it
     * @param fs 
     * @param prototype the line ot be added to the other images (with
     * it's Feature properly set)
     */
    void addFeature( FeatureSet fs, Line prototype )
    {
        // add to the meta data
        fs.features.add( prototype.shared );
        
        List<BodyPart> instancesToUpdate = findInstancesForFS(fs);
        
        // apply new part to all instances
        for ( BodyPart bp : instancesToUpdate )
            bp.lines.put( prototype.shared, prototype.clone() );
    }

    void normaliseInstanceWeights()
    {
        double weight = 0;
        for (BodyPart bp : instances)
            weight += bp.weight;
        
        if (weight < 0.01)
            for (BodyPart bp : instances)
                bp.weight = 1/(double)instances.size();
        else
            for ( BodyPart bp : instances )
                bp.weight = bp.weight / weight;
        
    }
    
    void removeFeature( FeatureSet fs, Feature key )
    {
        // remove meta data
        fs.features.remove( key );
        
        List<BodyPart> instancesToUpdate = findInstancesForFS( fs );
        
        // remove new part from instances
        for ( BodyPart bp : instancesToUpdate )
            bp.lines.remove( key );
    }
    
    List<BodyPart> findInstancesForFS(FeatureSet fs)
    {
        List<BodyPart> instancesToUpdate = new ArrayList();
        
        instancesToUpdate.addAll( instances );
        
        // is fs a descriptor for one of the children?
        for (FeatureSet childFS : children.keySet())
            if (childFS == fs)
            {
//        if (instancesToUpdate == null)
//              instancesToUpdate = 
//                    fs== edgeFeatures ? instances : 
//                        parent == null ? new ArrayList() : parent.instances;
//        
//        if (instancesToUpdate == null)
//            throw new Error ("this BPS doesn't own the specified FeatureSet");
                instancesToUpdate.addAll( children.get( childFS ).instances );
            }
        
        // is fs a descriptor for the edges or our parent (else are we w00t?)
        
        if (fs == edgeFeatures)
        {
            // nothing to do
        }
        else if (fs == parentFeature && parent != null)
        {
            instancesToUpdate.addAll( parent.instances );   
        }
        
        return instancesToUpdate;
    }

    void removeChild( BodyPartSet selected )
    {
        // remove meta data
        children.remove( selected.parentFeature );
        // remove all 
        for (BodyPart bp : instances)
        {
            bp.removeAllFeatures(selected.parentFeature);
        }
    }
    
    Iterable<BodyPartSet> getChildren()
    {
        ArrayList al = new ArrayList(children.values());
        Collections.sort( al );
        return al;
    }

    public int compareTo( BodyPartSet o )
    {
        return parentFeature.name.compareTo(o.parentFeature.name);
    }
    
    public String toString()
    {
        return parentFeature.name;
    }
    
        
    /*********** reflection accessed methods for editing featureSETs***************/
    public List<FeatureSet> getEdges()
    {
        List<FeatureSet> out = new ArrayList();
        out.add(edgeFeatures);
        return out;
    }
    
    public List<FeatureSet> getPlugs()
    {
        List<FeatureSet> out= new ArrayList();
        out.add( parentFeature );
        return out;
    }
    
    public List<FeatureSet> getSockets()
    {
        List<FeatureSet> al = new ArrayList(children.keySet());
        Collections.sort( al );
        return al;
    }
    /******************************************************************************/
    
    public String getUniqeInstanceName()
    {
        String stub = "new "+(parent == null ? "root" : parent).toString()+" instance ";
        int i = 0;
        vile:
        while (true)
        {
            String proposal = stub + (i++);
            for (BodyPart bp : instances)
            {
                if (bp.name.compareTo(proposal) == 0)
                    continue vile;
            }
            return proposal;
        }
    }
}
