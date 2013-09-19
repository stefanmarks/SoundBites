package analyser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

/**
 * Class for managing and simplifying audio ports and lines.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 17.09.2013: Created
 */
public class AudioManager 
{   
    /**
     * Creates an audio manager instance.
     */
    public AudioManager()
    {
        collectPortMixerInformation();
        
        if ( audioInputs.isEmpty() )
        {
            // could not find any ports, now search lines
            collectLineMixerInformation();
        }
    }
    
    
    /**
     * Gets a list of audio inputs.
     * 
     * @return the list of audio inputs
     */
    public List<AudioInput> getInputs()
    {
        return Collections.unmodifiableList(audioInputs);
    }
    
    
    /**
     * Get a specific audio input.
     * 
     * @param idx the input index
     * @return the audio input object or null if the index is invalid
     */
    public AudioInput getInput(int idx)
    {
        return ((idx < 0) || (idx >= audioInputs.size())) ? null : audioInputs.get(idx);
    }
    
    
    /**
     * Gather information about mixer input ports.
     */
    private void collectPortMixerInformation()
    {
        audioInputs = new LinkedList<AudioInput>();
        
        final Port.Info searchInfos[] = { Port.Info.LINE_IN, Port.Info.MICROPHONE };
        
        for ( Mixer.Info mixerInfo : AudioSystem.getMixerInfo() )
        {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            // search input ports
            Port port = null;
            for ( Port.Info info : searchInfos )
            {
                if ( mixer.isLineSupported(info) )
                {
                    try
                    {
                        port = (Port) mixer.getLine(info);
                    }
                    catch ( LineUnavailableException e )
                    {
                        // nothing to do
                    }
                }
            }
            if ( port != null )
            {
                Mixer source = findMatchingMixer(mixer);
                AudioInput input = new AudioInput(port, source);
                audioInputs.add(input);
            }
        }
    }
    
    
    /**
     * Finds a mixer for a specific port.
     * 
     * @param mixer1 the port mixer to find the corresponding line mixer for.
     */
    private Mixer findMatchingMixer(Mixer mixer1)
    {
        Mixer retMixer = mixer1;
        for ( Mixer.Info mixerInfo : AudioSystem.getMixerInfo() )
        {
            Mixer mixer2 = AudioSystem.getMixer(mixerInfo);
            String searchFor = "Port " + mixer2.getMixerInfo().getName();
            if ( mixer1.getMixerInfo().getName().indexOf(searchFor) == 0 )
            {
                retMixer = mixer2;
            }
        }
        return retMixer;
    }
    
    
    /**
     * Gather information about any mixers that have "microp" or "line in" in their names.
     */
    private void collectLineMixerInformation()
    {
        for ( Mixer.Info mixerInfo : AudioSystem.getMixerInfo() )
        {
            String name = mixerInfo.getName().toLowerCase();
            if ( name.contains("microp") || 
                 name.contains("input")  ||
                 (name.contains("line") && name.contains("in")) )
            {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                audioInputs.add(new AudioInput(mixer, mixer));
            }
        }
    }
    
    
    /**
     * Method for reporting audio capabilities.
     */
    public String reportAudioCapabilities()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("OS: ").append(System.getProperty("os.name")).append(" ")
          .append(System.getProperty("os.version")).append("/").append(System.getProperty("os.arch"))
          .append("\n")
          .append("Java: ")
          .append(System.getProperty("java.version"))
          .append(" (").append(System.getProperty("java.vendor")).append(")")
          .append("\n");
        
        for ( Mixer.Info mixerInfo : AudioSystem.getMixerInfo() )
        {
            sb.append(reportMixerCapabilities(mixerInfo));
        }
        return sb.toString();
    }
    
    
    private String reportMixerCapabilities(Mixer.Info mixerInfo)
    {
        StringBuilder sb = new StringBuilder();
            sb.append("Mixer: ")
              .append(mixerInfo.getDescription())
              .append(" [").append(mixerInfo.getName()).append("]")
              .append("\n");
            
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
            
        try
        {
            for ( Line.Info lineInfo : mixer.getSourceLineInfo() )
            {
                Line thisLine = mixer.getLine(lineInfo);
                if ( !(thisLine instanceof Clip) ) { thisLine.open(); }
                sb.append("- Source Port: ").append(lineInfo.toString()).append("\n");
                for ( Control ctrl : thisLine.getControls() )
                {
                    sb.append(reportControlCapabilities(ctrl)).append("\n");
                }
                if ( !(thisLine instanceof Clip) ) { thisLine.close(); }
            }
            for ( Line.Info lineInfo : mixer.getTargetLineInfo() )
            {
                Line thisLine = mixer.getLine(lineInfo);
                thisLine.open();
                sb.append("- Target Port: ").append(lineInfo.toString()).append("\n");
                for ( Control ctrl : thisLine.getControls() )
                {
                    sb.append(reportControlCapabilities(ctrl)).append("\n");
                }
                thisLine.close();
            }
        }
        catch ( Exception e )
        {
            sb.append("Error while examining mixer ").append(mixerInfo.getName())
              .append(" (").append(e).append(")");
        }
        return sb.toString();
    }

    
    private String reportControlCapabilities(Control control)
    {
        String type = control.getType().toString();
        if ( control instanceof BooleanControl )
        {
            return "    Control: " + type + " (boolean)";
        }
        else if ( control instanceof EnumControl )
        {
            return "    Control:" + type + " (enum: " + control.toString() + ")";
        }
        else if ( control instanceof FloatControl )
        {
            FloatControl floatControl = (FloatControl) control;
            return "    Control: " + type + " (float: from "
                    + floatControl.getMinimum() + " to "
                    + floatControl.getMaximum() + ")";
        }
        else if ( control instanceof CompoundControl )
        {
            CompoundControl compoundCtrl = (CompoundControl) control;
            String toReturn = "    Control: " + type + " (compound - values below)\n";
            for ( Control children : compoundCtrl.getMemberControls())
            {
                toReturn += "  " + reportControlCapabilities(children) + "\n";
            }
            return toReturn.substring(0, toReturn.length() - 1);
        }
        return "    Control: unknown type";
    }

    private List<AudioInput> audioInputs;
}
