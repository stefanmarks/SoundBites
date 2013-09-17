package shaper;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Slider;
import geom.Surface;
import processing.core.PMatrix;
import processing.core.PMatrix3D;
import processing.core.PVector;

/**
 * Shaper module for creating rings.
 *
 * @author Stefan Marks
 * @version 1.0 - 15.08.2013: Created
 */
public class Shaper_RingOld extends AbstractShaper
{
    public Shaper_RingOld()
    {
        super("Ring (old)");
    }
    
    
    @Override
    public void initialise(ControlP5 gui)
    {
        sldTorusRadius = gui.addSlider("Torus Radius")
                .setRange(0, 1000)
                .setValue(200);
        controllers.add(sldTorusRadius);
        sldRingRadius = gui.addSlider("Ring Radius")
                .setRange(0, 200)
                .setValue(20);
        controllers.add(sldRingRadius);
        sldMultiplier = gui.addSlider("Ring Multiplier")
                .setRange(0, 10)
                .setValue(1.5f);
        controllers.add(sldMultiplier);
        sldRevolutions = gui.addSlider("Revolutions")
                .setRange(0, 8)
                .setNumberOfTickMarks(8)
                .setDecimalPrecision(0)
                .setValue(0);
        controllers.add(sldRevolutions);
        
        mtx = new PMatrix3D();
        mtx.reset();
        surface = new Surface(2, 2);
    }

    
    @Override
    public void createSurface(float[][] spectrumData)
    {
        int idxCount = spectrumData.length;
        int freqCount = spectrumData[0].length * 2;

        surface = new Surface(idxCount, freqCount);

        // create faces
        for (int x = 0; x < idxCount; x++)
        {
            int x0 = x;
            int x1 = (x + 1) % idxCount;
            for (int y = 0; y < freqCount; y++)
            {
                int y0 = y;
                int y1 = (y + 1) % freqCount;
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
        // convert array index into fraction 
        float fT   = (float) idx / surface.getXSize();
        float angT = fT * 360.0f;
        // prepare coordinate system for drawing a circle
        mtx.reset();
        mtx.rotateY((float) Math.toRadians(angT));
        mtx.translate(sldTorusRadius.getValue(), 0, 0);

        // revolve along the timeline
        mtx.rotateZ((float) Math.toRadians((int) sldRevolutions.getValue() * angT));
        
        // draw the circle
        int freqCount = spectrum.length * 2;
        for ( int iF = 0 ; iF < freqCount ; iF++ )
        {
            float fF   = (float) iF / (freqCount - 1);
            float angF = 360.0f * fF; 
            int   idxF = getFreqIdx(iF, spectrum.length);
            float r = sldRingRadius.getValue();
            r *= (1 + (sldMultiplier.getValue() - 1) * spectrum[idxF]);
            float x = r * (float) Math.cos(Math.toRadians(angF));
            float y = r * (float) Math.sin(Math.toRadians(angF));
            PVector v = surface.modifyVertex(idx, iF);
            v.set(x, y, 0);
            mtx.mult(v, v);
            surface.setVertexColour(idx, iF, mapper.mapSpectrum(spectrum, idxF));
        }
    }

    
    private int getFreqIdx(int idx, int count)
    {
        // translate absolute index into relative frequency idx 
        // (considering mirroring of frequencies)
        idx = idx % (count * 2);
        if (idx >= count)
        {
            idx = 2 * count - 1 - idx;
        }
        return idx;
    }

    
    private Slider  sldTorusRadius;
    private Slider  sldRingRadius;
    private Slider  sldMultiplier;
    private Slider  sldRevolutions;
    private PMatrix mtx;
}