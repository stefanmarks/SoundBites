package analyser;

import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

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
    public AudioInput(Port port, Mixer mixer)
    {
        this.port  = port;
        this.mixer = mixer;
        
        findGainControl();
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
        for ( Control portCtrl : port.getControls() )
        {
            if ( portCtrl.getType().equals(FloatControl.Type.MASTER_GAIN) )
            {
                gainControl = (FloatControl) portCtrl;
            }
            else if ( portCtrl instanceof CompoundControl )
            {
                CompoundControl cctrl = (CompoundControl) portCtrl;
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
    
    private Port         port;
    private Mixer        mixer;
    private FloatControl gainControl;
}
