package shaper;

import java.awt.Color;

/**
 * Class for mapping frequency/intensity to a single colour.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 22.08.2013: Created
 */
public class PlainColourMapper implements ColourMapper 
{
    public PlainColourMapper(Color c) 
    {
        colour = c;
    }
    
    public Color getColour()
    {
        return colour;
    }
            
    @Override
    public int mapSpectrum(float[] spectrum, int idx)
    {
        return colour.getRGB();
    }
    
    private Color colour;
}
