package shaper;

import controlP5.ControlP5;
import controlP5.Controller;
import geom.RenderMode;
import java.io.PrintWriter;
import java.util.List;
import javax.media.opengl.GL2;

/**
 * Interface for modules that convert frequency spectra to 3D shapes.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 15.08.2013: Created
 */
public interface Shaper
{  
    void initialise(ControlP5 gui);
    void deinitialise();

    List<Controller> getControllers();

    ColourMapper getColourMapper();
    void setColourMapper(ColourMapper mapper);

    void createSurface(float[][] spectrumData);
    void updateSurface(int idx, float[] spectrum);
    
    void update(float angle);
    void render(GL2 gl);
    
    void setRenderMode(RenderMode mode);
    void setSplitMode(boolean split);
    
    void writeSTL(PrintWriter w, float scale);
    void writeOBJ(PrintWriter w, float scale);
}
