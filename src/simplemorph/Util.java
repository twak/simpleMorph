/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simplemorph;

import java.awt.Point;

/**
 *
 * @author tom
 */
public class Util 
{
    public static int clamp (int min, int val, int max)
    {
        return val < min ? min : val > max ? max : val;
    }
    
    public static double dot (Point a, Point b)
    {
        return a.x*b.x + a.y*b.y;
    }
    
    public static Point subtract (Point a, Point b)
    {
        return new Point (a.x - b.x, a.y- b.y );
    }
    
    public static double distSquared (Point a, Point b)
    {
        int 
                x = a.x-b.x, 
                y = a.y-b.y;
        return x*x+y*y;
    }
    
    public static double dist(Point a, Point b)
    {
        return Math.sqrt( distSquared( a, b ));
    }
    
    public static Point flip90 (Point vector)
    {
        return new Point ( -vector.y, vector.x );
    }
    
    public static Point scale (double d, Point in)
    {
//      ******Integer Precision******
        return new Point ((int)(in.x *d), (int)(in.y * d));
    }
    public static Point add(Point ... in)
    {
        Point out = new Point();
        for (Point p : in)
            out.setLocation( out.x+p.x, out.y + p.y);
        return out;
    }
}
