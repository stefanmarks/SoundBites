package analyser;

import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

/**
 * Class for an audio input, including the gain controller.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 17.09.2013: Created
 */
public class AudioInput 
{
    /**
     * Creates an audio input object.
     * 
     * @param port  the port mixer
     * @param mixer the line mixer
     */
    public AudioInput(Line line, Mixer mixer)
    {
        this.line  = line;
        this.mixer = mixer;
        
        try
        {
            line.open();
            findGainControl();
            line.close();
        }
        catch (LineUnavailableException e)
        {
            // do nothing
        }
    }
    
    
    /**
     * Gets the port mixer object.
     * 
     * @return the port mixer
     */
    public Mixer getMixer()
    {
        return mixer;
    }
    
    
    /**
     * Gets the gain controller for the port.
     * 
     * @return the port gain controller
     */
    public FloatControl getGainControl()
    {
        return gainControl;
    }
    
    
    private void findGainControl()
    {
        for ( Control lineCtrl : line.getControls() )
        {
            if ( lineCtrl.getType().equals(FloatControl.Type.MASTER_GAIN) ||
                 lineCtrl.getType().equals(FloatControl.Type.VOLUME) )
            {
                gainControl = (FloatControl) lineCtrl;
            }
            else if ( lineCtrl instanceof CompoundControl )
            {
                CompoundControl cctrl = (CompoundControl) lineCtrl;
                for ( Control ctrl : cctrl.getMemberControls() )
                {
                    if ( ctrl.getType().equals(FloatControl.Type.VOLUME) )
                    {
                        gainControl = (FloatControl) ctrl;
                        break;
                    }
                }
            }
            if ( gainControl != null ) break;
        }
    }
    
    
    @Override
    public String toString()
    {
        return mixer.getMixerInfo().getName();
    }
    
    private Line         line;
    private Mixer        mixer;
    private FloatControl gainControl;
}
