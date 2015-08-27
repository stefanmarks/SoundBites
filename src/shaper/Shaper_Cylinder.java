package shaper;

import controlP5.ControlP5;
import controlP5.Slider;
import geom.Surface;
import javax.media.opengl.GL2;

/**
 * Shaper module for creating cylinders.
 *
 * @author Stefan Marks
 * @version 1.0 - 15.08.2013: Created
 */
public class Shaper_Cylinder extends AbstractShaper
{
    public Shaper_Cylinder()
    {
        super("Cylinder");
    }
    
    
    @Override
    public void initialise(ControlP5 gui)
    {
        sldLength = gui.addSlider("size").setCaptionLabel("Length")
                .setRange(10, 1000)
                .setValue(400);
        controllers.add(sldLength);
        sldRadius = gui.addSlider("radius").setCaptionLabel("Radius")
                .setRange(10, 500)
                .setValue(100);
        controllers.add(sldRadius);
        sldMultiplier = gui.addSlider("multiplier").setCaptionLabel("Radius Multiplier")
                .setRange(0, 10)
                .setValue(1.5f);
        controllers.add(sldMultiplier);

        surface = new Surface(2, 2);
    }


    @Override
    public void createSurface(float[][] spectrumData)
    {
        int idxCount  = spectrumData.length;
        int freqCount = spectrumData[0].length;

        surface = new Surface(idxCount, freqCount + 2); // +2 for the caps

        // create faces
        for (int x = 0; x < idxCount; x++)
        {
            int x0 = x;
            int x1 = (x + 1) % idxCount;
            for (int y = 0; y < freqCount + 1 ; y++)
            {
                int y0 = y;
                int y1 = y + 1;
                surface.addTriangle(x0, y1, x0, y0, x1, y1);
                surface.addTriangle(x1, y1, x0, y0, x1, y0);
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
        
        // update frequencies along the length
        int   freqCount = spectrum.length;
        float xSize     = sldLength.getValue();
        for ( int iF = 0 ; iF < freqCount ; iF++ )
        {
            float fF = (float) iF / (freqCount - 1); 
            float x  = xSize * (fF - 0.5f); // centre around origin
            float r  = sldRadius.getValue();
            r *= (1 + (sldMultiplier.getValue() - 1) * spectrum[iF]);
            float y =  r * (float) Math.cos(Math.toRadians(fT));
            float z = -r * (float) Math.sin(Math.toRadians(fT));
            surface.modifyVertex(idx, iF + 1).set(x, y, z);
            surface.setVertexColour(idx, iF + 1, mapper.mapSpectrum(spectrum, iF));
        }
        // modify cap points
        surface.modifyVertex(idx, 0).set(-xSize / 2, 0, 0);
        surface.modifyVertex(idx, freqCount + 1).set(xSize / 2, 0, 0);
    }

    
    @Override
    public void render(GL2 gl)
    {
        // rotate this shape around the X axis
        gl.glRotatef(animAngle, 1, 0, 0);
        surface.render(gl);
    }
    
    
    private Slider       sldRadius;
    private Slider       sldLength;
    private Slider       sldMultiplier;
}
