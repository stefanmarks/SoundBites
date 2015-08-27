package analyser;

import ddf.minim.analysis.FFT;

/**
 * Class with information about the spectrum at a specific time.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 12.05.2013: Created
 */
public class SpectrumInfo 
{
    public static final float MAX_SPECTRUM = 1.0f;
    
    /**
     * Creates a new spectrum information instance.
     */
    public SpectrumInfo()
    {
        reset();
    }

    /**
     * Copies spectrum analysis data from an FFT analyser.
     * 
     * @param posIdx   the position in ms in the audio file
     * @param posRel   the relative position in the audio file
     * @param analyser the spectrum analyser
     */
    public void copySpectrumData(int posIdx, float posRel, SpectrumAnalyser analyser)
    {
        FFT fft          = analyser.getFFT();
        int spectrumSize = fft.avgSize();
       
        if ( (intensity == null) || (intensity.length != spectrumSize) )
        {
            intensity    = new float[spectrumSize];
            intensityRaw = new float[fft.specSize()];
        }
        
        sampleIdx = posIdx;
        position  = posRel;
        
        SpectrumShaper shaper = analyser.getSpectrumShaper();
        float scale = MAX_SPECTRUM / fft.specSize() * 2;
        for (int i = 0; i < spectrumSize; i++)
        {
            intensity[i] = shaper.shape(fft.getAvg(i) * scale);
        }
        for (int i = 0; i < fft.specSize(); i++)
        {
            intensityRaw[i] = shaper.shape(fft.getBand(i) * scale);
        }
    }
    
    /**
     * Resets the spectrum information.
     */
    public void reset()
    {
        sampleIdx    = 0;
        intensity    = null;
        intensityRaw = null;
    }
    
    /**
     * Checks if the information in this dataset is defined or not.
     * 
     * @return <code>true</code> if the dataset is defined,
     *         <code>false</code> if not
     */
    public boolean isDefined()
    {
        return intensity != null;
    }
        
    
    // millisecond index into the sound file
    public int     sampleIdx;
    // relative play position of the soudn file
    public float   position;
    // array of frequency intensities
    public float[] intensity;
    // array of frequency intensities 
    public float[] intensityRaw;
}
