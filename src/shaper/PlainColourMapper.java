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
    /**
     * Creates a new plain colout mapper.
     * 
     * @param name the name of the mapper
     * @param c    the colour to use
     */
    public PlainColourMapper(String name, Color c) 
    {
        this.name = name;
        colour = c;
    }
    
    
    /**
     * Gets the colour used by this mapper.
     * 
     * @return the colour used for mapping
     */
    public Color getColour()
    {
        return colour;
    }
            
    
    @Override
    public int mapSpectrum(float[] spectrum, int idx)
    {
        return colour.getRGB();
    }
    
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    private String name;
    private Color  colour;
}
