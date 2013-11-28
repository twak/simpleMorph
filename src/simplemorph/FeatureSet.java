package simplemorph;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of lines that define something (an outline, socket or plug). There is one
 * featureset for each part of the bodyPartSet hierarchy
 * 
 * @author twak
 */
public class FeatureSet implements Comparable<FeatureSet>
{
    public List<Feature> features;
    public String name;

    public FeatureSet(String name)
    {
        this( name, new ArrayList() );
    }
    
    public FeatureSet(String name, List<Feature> features)
    {
        this.features = features;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
    
    public int compareTo( FeatureSet o )
    {
        return o.name.compareTo(name);
    }
}
