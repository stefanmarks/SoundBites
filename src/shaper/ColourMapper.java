package shaper;

/**
 * Interface for mapping frequency/intensity to a colour.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 22.08.2013: Created
 */
public interface ColourMapper 
{
    /**
     * Maps an intensity of the frequency spectrum to a colour.
     * 
     * @param spectrum the spectrum to use
     * @param idx      the index within the spectrum to use
     * @return the colour in RGBA code
     */
    int mapSpectrum(float[] spectrum, int idx);
}
