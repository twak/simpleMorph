/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simplemorph;

/**
 * The shared meta-data between lines that define the same thing
 * on different objects.
 * 
 * @author twak
 */
public class Feature implements Comparable<Feature>
{
    public String name;
    public Feature(String name)
    {
        this.name = name;
    }

    public int compareTo( Feature o )
    {
        return o.name.compareTo(name);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
