package analyser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
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
    public AudioManager()
    {
        collectMixerInformation();
    }
    
    
    public List<AudioInput> getInputs()
    {
        return Collections.unmodifiableList(audioInputs);
    }
    
    
    public AudioInput getInput(int idx)
    {
        return audioInputs.get(idx);
    }
    
    
    private void collectMixerInformation()
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
                        port.open();
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
                System.out.println("- " + input);
            }
        }
    }
    
    private Mixer findMatchingMixer(Mixer mixer1)
    {
        Mixer retMixer = null;
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
    
    public void reportAudioCapabilities()
    {
        System.out.println("OS: " + System.getProperty("os.name") + " "
                + System.getProperty("os.version") + "/"
                + System.getProperty("os.arch") + "\nJava: "
                + System.getProperty("java.version") + " ("
                + System.getProperty("java.vendor") + ")\n");
        
        for ( Mixer.Info mixerInfo : AudioSystem.getMixerInfo() )
        {
            reportMixerCapabilities(mixerInfo);
        }
    }
    
    private void reportMixerCapabilities(Mixer.Info thisMixerInfo)
    {
        try
        {
            System.out.println("Mixer: " + thisMixerInfo.getDescription()
                    + " [" + thisMixerInfo.getName() + "]");
            
            Mixer thisMixer = AudioSystem.getMixer(thisMixerInfo);
            
            for (Line.Info thisLineInfo : thisMixer.getSourceLineInfo())
            {
                if (thisLineInfo.getLineClass().getName().equals(
                        "javax.sound.sampled.Port"))
                {
                    Line thisLine = thisMixer.getLine(thisLineInfo);
                    thisLine.open();
                    System.out.println("  Source Port: "
                            + thisLineInfo.toString());
                    for (Control thisControl : thisLine.getControls())
                    {
                        System.out.println(reportControlCapabilities(thisControl));
                    }
                    thisLine.close();
                }
            }
            for (Line.Info thisLineInfo : thisMixer.getTargetLineInfo())
            {
                if (thisLineInfo.getLineClass().getName().equals(
                        "javax.sound.sampled.Port"))
                {
                    Line thisLine = thisMixer.getLine(thisLineInfo);
                    thisLine.open();
                    System.out.println("  Target Port: "
                            + thisLineInfo.toString());
                    for (Control thisControl : thisLine.getControls())
                    {
                        System.out.println(reportControlCapabilities(thisControl));
                    }
                    thisLine.close();
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private String reportControlCapabilities(Control thisControl)
    {
        String type = thisControl.getType().toString();
        if (thisControl instanceof BooleanControl)
        {
            return "    Control: " + type + " (boolean)";
        }
        if (thisControl instanceof CompoundControl)
        {
            System.out.println("    Control: " + type
                    + " (compound - values below)");
            String toReturn = "";
            for (Control children
                    : ((CompoundControl) thisControl).getMemberControls())
            {
                toReturn += "  " + reportControlCapabilities(children) + "\n";
            }
            return toReturn.substring(0, toReturn.length() - 1);
        }
        if (thisControl instanceof EnumControl)
        {
            return "    Control:" + type + " (enum: " + thisControl.toString() + ")";
        }
        if (thisControl instanceof FloatControl)
        {
            return "    Control: " + type + " (float: from "
                    + ((FloatControl) thisControl).getMinimum() + " to "
                    + ((FloatControl) thisControl).getMaximum() + ")";
        }
        return "    Control: unknown type";
    }

    private List<AudioInput> audioInputs;
    
}
