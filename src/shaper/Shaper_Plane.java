package shaper;

import controlP5.ControlP5;
import controlP5.Slider;
import geom.Surface;
import javax.media.opengl.GL2;

/**
 * Shaper module for creating planes.
 *
 * @author Stefan Marks
 * @version 1.0 - 04.09.2015: Created
 */
public class Shaper_Plane extends AbstractShaper
{
    public Shaper_Plane()
    {
        super("Plane");
    }
    
    
    @Override
    public void initialise(ControlP5 gui)
    {
        sldSizeX = gui.addSlider("sizeX").setCaptionLabel("Width")
                .setRange(10, 500)
                .setValue(400);
        controllers.add(sldSizeX);
        sldSizeZ = gui.addSlider("sizeZ").setCaptionLabel("Depth")
                .setRange(10, 500)
                .setValue(400);
        controllers.add(sldSizeZ);
        sldMultiplier = gui.addSlider("multiplier").setCaptionLabel("Height Multiplier")
                .setRange(0, 500)
                .setValue(100);
        controllers.add(sldMultiplier);

        surface = new Surface(2, 2);
    }


    @Override
    public void createSurface(float[][] spectrumData)
    {
        int idxCount  = spectrumData.length;
        int freqCount = spectrumData[0].length;

        surface = new Surface(idxCount, freqCount); 

        // create faces
        for (int x = 0; x < (idxCount - 1); x++)
        {
            int x0 = x;
            int x1 = x + 1;
            for (int y = 0; y < (freqCount - 1) ; y++)
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
        // convert array index into X coordinate centered around origin
        float x = sldSizeX.getValue() * ((float) idx / surface.getXSize() - 0.5f);
        
        // update frequencies along the length
        int   freqCount = spectrum.length;
        float zSize     = sldSizeZ.getValue();
        for ( int iF = 0 ; iF < freqCount ; iF++ )
        {
            float z = (float) iF / (freqCount - 1); 
                  z = zSize * (-z + 0.5f); // centre around origin
            float y = sldMultiplier.getValue() * spectrum[iF];
            surface.modifyVertex(idx, iF).set(x, y, z);
            surface.setVertexColour(idx, iF, mapper.mapSpectrum(spectrum, iF));
        }
    }

    
    @Override
    public void render(GL2 gl)
    {
        surface.render(gl);
    }
    
    
    private Slider       sldSizeX;
    private Slider       sldSizeZ;
    private Slider       sldMultiplier;
}
