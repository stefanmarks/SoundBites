package shaper;

/**
 * Interface for mapping frequency/intensity to a colour.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 22.08.2013: Created
 */
public interface ColourMapper 
{
    int mapSpectrum(float[] spectrum, int idx);
}
