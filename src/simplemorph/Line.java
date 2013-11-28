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
public class Line {
    
    public Point start, end;
    public Feature shared = new Feature("blah"); // shared between all lines
    
    public Line (Point start, Point end)
    {
        this.start = (Point)start.clone();
        this.end = (Point)end.clone();
    }

    Line( Point start, Point end, Feature shared )
    {
        this(start, end);
        this.shared = shared;
    }
    
    @Override
    public Line clone()
    {
        Line out = new Line (new Point(start), new Point(end));
        out.shared = this.shared;
        return out;
    }
    
    public void add( Line line )
    {
        start.x += line.start.x;
        start.y += line.start.y;
        end.x += line.end.x;
        end.y += line.end.y;
    }
    
    public Line clone (double alpha)
    {
        Line out = new Line (
                new Point((int)(start.x * alpha),(int)(start.y * alpha)),
                new Point((int)(end.x   * alpha),(int)(end.y   * alpha)));
        out.shared = this.shared;
        return out;
    }

    @Override
    public String toString()
    {
        return start+" to "+end;
    }
    
    
}
