package shaper;

import controlP5.Controller;
import geom.RenderMode;
import geom.Surface;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL2;

/**
 * Abstract shaper module for common functionality.
 *
 * @author Stefan Marks
 * @version 1.0 - 09.09.2013: Created
 */
public abstract class AbstractShaper implements Shaper
{
    public AbstractShaper(String name)
    {
        this.name = name;
        controllers = new ArrayList<Controller>();
    }
    
    
    @Override
    public void deinitialise()
    {
        for ( Controller c : controllers )
        {
            c.remove();
        }
        controllers.clear();
    }
    
    
    @Override
    public ColourMapper getColourMapper()
    {
        return mapper;
    }

    
    @Override
    public void setColourMapper(ColourMapper mapper)
    {
        this.mapper = mapper;
    }
    
    
    @Override
    public List<Controller> getControllers()
    {
        return controllers;
    }

    
    @Override
    public void update(float angle)
    {
        this.animAngle = angle;
        if ( splitMode )
        {
            splitSurf1.update();
            splitSurf2.update();
        }
        else
        {
            surface.update();            
        }
    }

    
    protected void preRender(GL2 gl)
    {
        // rotate this shape around the Y axis
        gl.glRotatef(-animAngle, 0, 1, 0);
    }
    
    
    @Override
    public void render(GL2 gl)
    {
        preRender(gl);
        
        if ( splitMode )
        {
            splitSurf1.render(gl);
            splitSurf2.render(gl);
        }
        else
        {
            surface.render(gl);
        }
    }
    
    
    @Override
    public void setRenderMode(RenderMode mode)
    {
        surface.setRenderMode(mode);
    }

    
    @Override
    public void setSplitMode(boolean split)
    {
        splitMode = split;
        if ( splitSurf1 == null ) splitMode = false;
    }

    
    @Override
    public void writeSTL(PrintWriter w)
    {
        if ( splitMode )
        {
            splitSurf1.writeSTL(w);
            splitSurf2.writeSTL(w);
        }
        else
        {
            surface.writeSTL(w);
        }
    }

    
    @Override
    public String toString()
    {
        return name;
    }
    

    private   String                name;
    protected ColourMapper          mapper;
    protected ArrayList<Controller> controllers;
    protected Surface               surface, splitSurf1, splitSurf2;
    protected boolean               splitMode;
    protected float                 animAngle;
}