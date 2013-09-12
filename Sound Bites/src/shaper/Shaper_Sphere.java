package shaper;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Slider;
import geom.Surface;
import processing.core.PMatrix;
import processing.core.PMatrix3D;
import processing.core.PVector;

/**
 * Shaper module for creating cylinders.
 *
 * @author Stefan Marks
 * @version 1.0 - 15.08.2013: Created
 */
public class Shaper_Sphere extends AbstractShaper
{
    public Shaper_Sphere()
    {
        super("Sphere");
    }
    
    
    @Override
    public void initialise(ControlP5 gui)
    {
        controllers = new Controller[2];
        controllers[0] = sldRadius = gui.addSlider("Radius")
                .setRange(10, 500)
                .setValue(100);
        controllers[1] = sldMultiplier = gui.addSlider("Radius Multiplier")
                .setRange(0, 10)
                .setValue(1.5f);
        
        mtx = new PMatrix3D();
        mtx.reset();
        surface = new Surface(2, 2);
    }


    @Override
    public void createSurface(float[][] spectrumData)
    {
        int idxCount = spectrumData.length;
        int freqCount = spectrumData[0].length;

        surface = new Surface(idxCount, freqCount);

        // create faces
        for (int x = 0; x < idxCount; x++)
        {
            int x0 = x;
            int x1 = (x + 1) % idxCount;
            for (int y = 0; y < freqCount-1; y++)
            {
                int y0 = y;
                int y1 = y + 1;
                surface.addTriangle(x0, y1, x1, y1, x0, y0);
                surface.addTriangle(x1, y1, x1, y0, x0, y0);
            }
        }

        // update spectrum data
        for (int iT = 0; iT < idxCount; iT++)
        {
            updateSurface(iT, spectrumData[iT]);
        }
        // let surface recalculate normals, etc.
        surface.update();
    }

    
    @Override
    public void updateSurface(int idx, float[] spectrum)
    {
        // convert array index into degree angle [0..360]
        float fT = 360.0f * idx / surface.getXSize();
        mtx.reset();
        mtx.rotateY((float) Math.toRadians(fT));
        
        // update frequencies along the length
        int freqCount = spectrum.length;
        for ( int iF = 0 ; iF < freqCount ; iF++ )
        {
            float fF = 180.0f * iF / (freqCount - 1) - 90.0f; 
            float r  = sldRadius.getValue();
            r *= (1 + (sldMultiplier.getValue() - 1) * spectrum[iF]);
            float x = r * (float) Math.cos(Math.toRadians(fF));
            float y = r * (float) Math.sin(Math.toRadians(fF));
            PVector v = surface.modifyVertex(idx, iF);
            v.set(x, y, 0);
            mtx.mult(v, v);
            surface.setVertexColour(idx, iF, mapper.mapSpectrum(spectrum, iF));
        }
    }
    
    
    private Slider   sldRadius;
    private Slider   sldMultiplier;
    private PMatrix  mtx;
}
