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
public class Shaper_Ring extends AbstractShaper
{
    public Shaper_Ring()
    {
        super("Ring");
    }
    
    
    @Override
    public void initialise(ControlP5 gui)
    {
        controllers = new Controller[4];
        controllers[0] = sldTorusRadius = gui.addSlider("Torus Radius")
                .setRange(0, 1000)
                .setValue(200);
        controllers[1] = sldRingRadius = gui.addSlider("Ring Radius")
                .setRange(0, 200)
                .setValue(20);
        controllers[2] = sldMultiplier = gui.addSlider("Ring Multiplier")
                .setRange(0, 10)
                .setValue(1.5f);
        controllers[3] = sldRevolutions = gui.addSlider("Revolutions")
                .setRange(0, 8)
                .setNumberOfTickMarks(8)
                .setDecimalPrecision(0)
                .setValue(0);

        surface    = new Surface(2, 2);
        splitSurf1 = new Surface(2, 2);
        splitSurf2 = new Surface(2, 2);

        mtx = new PMatrix3D();
        mtx.reset();
    }

    
    @Override
    public void createSurface(float[][] spectrumData)
    {
        int idxCount   = spectrumData.length;
        int freqCount  = spectrumData[0].length * 2;
        int freqCount2 = freqCount / 2 + 1;
        
        surface    = new Surface(idxCount, freqCount);
        splitSurf1 = new Surface(idxCount, freqCount2);
        splitSurf2 = new Surface(idxCount, freqCount2);

        // create faces
        for ( int x = 0; x < idxCount; x++ )
        {
            int x0 = x;
            int x1 = (x + 1) % idxCount;
            
            for ( int y = 0; y < freqCount; y++ )
            {
                int y0 = y;
                int y1 = (y + 1) % freqCount;
                surface.addTriangle(x0, y1, x1, y1, x0, y0);
                surface.addTriangle(x1, y1, x1, y0, x0, y0);
            }
            
            for ( int y = 0; y < freqCount2; y++ )
            {
                int y0 = y;
                int y1 = (y + 1) % freqCount2;
                splitSurf1.addTriangle(x0, y1, x1, y1, x0, y0);
                splitSurf1.addTriangle(x1, y1, x1, y0, x0, y0);
                splitSurf2.addTriangle(x0, y1, x1, y1, x0, y0);
                splitSurf2.addTriangle(x1, y1, x1, y0, x0, y0);
            }
        }

        // update spectrum data
        for ( int iT = 0 ; iT < idxCount ; iT++ )
        {
            updateSurface(iT, spectrumData[iT]);
        }
    }

    
    @Override
    public void updateSurface(int idx, float[] spectrum)
    {
        // convert array index into fraction 
        float fT = (float) idx / surface.getXSize();
        
        // prepare coordinate system for drawing a circle
        mtx.reset();
        mtx.rotateY((float) Math.toRadians(fT * 360));
        mtx.translate(sldTorusRadius.getValue(), 0, 0);

        // draw the circle
        int freqCount  = spectrum.length * 2;
        int freqCount2 = freqCount / 2;
        for ( int iF = 0 ; iF < freqCount ; iF++ )
        {
            float fF   = (float) iF / (freqCount - 1);
            float angF = 360.0f * fF; 
            // revolutions are implemented by shifting the frequency index
            int   idxF = (int) (iF + fT * freqCount * sldRevolutions.getValue());
                  idxF = getFreqIdx(idxF, spectrum.length);
            float r = sldRingRadius.getValue();
            r *= (1 + (sldMultiplier.getValue() - 1) * spectrum[idxF]);
            float x = r * (float) Math.cos(Math.toRadians(angF));
            float y = r * (float) Math.sin(Math.toRadians(angF));
            
            PVector v = surface.modifyVertex(idx, iF);
            v.set(x, y, 0);
            mtx.mult(v, v);
            int col = mapper.mapSpectrum(spectrum, idxF);
            surface.setVertexColour(idx, iF, col);
            
            int iF2 = (iF + freqCount2) % (freqCount2 + 1);
            if ( iF <= freqCount2 )
            {
                splitSurf1.modifyVertex(idx, iF).set(v.x, v.y + 50, v.z);
                splitSurf1.setVertexColour(idx, iF, col);
            }
            if ( iF2 <= freqCount2 )
            {
                splitSurf2.modifyVertex(idx, iF2).set(v.x, v.y - 50, v.z);
                splitSurf2.setVertexColour(idx, iF2, col);
            }
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