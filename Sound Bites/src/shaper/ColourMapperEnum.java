package shaper;

import java.awt.Color;

/**
 * Enumeration for the colour mapper selection choices.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 20.09.2013: Created
 */
public enum ColourMapperEnum 
{
    WHITE(new PlainColourMapper("White", Color.white)),
    BROWN(new PlainColourMapper("Brown", Color.decode("#603000"))),
    GREYSCALE(ImageColourMapper.create("Greyscale", "GreyMap.png")),
    TRANSPARENT(ImageColourMapper.create("Transparent", "TransparentMap.png")),
    SPECTRUM(ImageColourMapper.create("Spectrum", "SpectrumMap.png")),
    FIRE(ImageColourMapper.create("Fire", "FireMap.png")),
    ICE(ImageColourMapper.create("Ice", "IceMap.png"));

    
    private ColourMapperEnum(ColourMapper mapper)
    {
        this.mapper = mapper;
    }
    
    
    @Override
    public String toString()
    {
        return mapper.toString();
    }
    
    
    public ColourMapper getInstance()
    {
        return mapper;
    }
    
    private ColourMapper mapper;
}
