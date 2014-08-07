package main;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParameter;
import com.illposed.osc.OSCPortIn;
import geom.RenderMode;
import geom.SkyboxEnum;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import processing.core.PVector;
import shaper.ColourMapperEnum;
import shaper.ShaperEnum;

/**
 * 
 * @author  Stefan Marks
 * @version 1.0 - 20.09.2013: Created
 */
public class SoundBiteVariables 
{
    public SoundBiteVariables()
    {
        paramList = new LinkedList<OSCParameter>();
        
        shaper = new OSCParameter<ShaperEnum>("/render/shaper", ShaperEnum.RING1); paramList.add(shaper);
        
        shapeSize        = new OSCParameter<Float>("/shaper/size",       200.0f); paramList.add(shapeSize);
        shapeRadius      = new OSCParameter<Float>("/shaper/radius",      50.0f); paramList.add(shapeRadius);
        shapeMultiplier  = new OSCParameter<Float>("/shaper/multiplier",   1.0f); paramList.add(shapeMultiplier);
        shapeRevolutions = new OSCParameter<Integer>("/shaper/revolutions",   0); paramList.add(shapeRevolutions);

        camPos  = new OSCParameter<PVector>("/cam/pos", new PVector(0, 0, 700)); paramList.add(camPos);
        camRot  = new OSCParameter<PVector>("/cam/rot", new PVector(0, 0, 0));   paramList.add(camRot);
        camZoom = new OSCParameter<Float>(  "/cam/zoom", 1.0f);                  paramList.add(camZoom);

        mapper     = new OSCParameter<ColourMapperEnum>("/render/mapper", ColourMapperEnum.WHITE); paramList.add(mapper);
        skybox     = new OSCParameter<SkyboxEnum>(      "/render/skybox", SkyboxEnum.BLACK); paramList.add(skybox);
        renderMode = new OSCParameter<RenderMode>(      "/render/mode",   RenderMode.SOLID); paramList.add(renderMode);
        
        guiControlsEnabled = new OSCParameter<Boolean>("/gui/controls/enabled", true); paramList.add(guiControlsEnabled);
        guiSpectrumEnabled = new OSCParameter<Boolean>("/gui/spectrum/enabled", true); paramList.add(guiSpectrumEnabled);

        audioSource    = new OSCParameter<Integer>("/audio/source", 0);       paramList.add(audioSource);
        audioRecording = new OSCParameter<Boolean>("/audio/recording", true); paramList.add(audioRecording);
    }

    
    public void registerWithInputPort(OSCPortIn port)
    {
        for ( OSCParameter param : paramList )
        {
            param.registerWithPort(port);
        }
    }

    
    public OSCPacket getFullPacket()
    {
        OSCBundle bundle = new OSCBundle();
        for ( OSCParameter param : paramList )
        {
            bundle.addPacket(param.prepareMessage());
        }
        return bundle;
    }
    
    
    public OSCPacket getUpdatePacket()
    {
        OSCBundle bundle = new OSCBundle();
        for ( OSCParameter param : paramList )
        {
            if ( param.isChanged() )
            {
                bundle.addPacket(param.prepareMessage());
                param.resetChanged();
            }
        }
        return bundle;
    }
    
    
    public OSCParameter findShaperVar(String name)
    {
        for ( OSCParameter param : paramList )
        {
            if ( param.getAddress().equals("/shaper/" + name) ) 
            {
                return param;
            }
        }
        return null;
    }
    
    
    public void writeToStream(PrintStream os)
    {
        for ( OSCParameter param : paramList )
        {
            String line = param.getAddress()+ " = " + param.valueToString();
            os.println(line);
        }
    }
    
    
    public void readFromStream(InputStream is) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ( br.ready() )
        {
            String   line  = br.readLine();
            String[] parts = line.split(" = ");
            if ( parts.length >= 2 )
            {
                for ( OSCParameter param : paramList )
                {
                    if ( param.getAddress().equals(parts[0]))
                    {
                        param.valueFromString(parts[1]);
                    }
                }
            }
        }
    }

    
    // common shaper parameters 
    public OSCParameter<Float>            shapeSize, shapeRadius, shapeMultiplier;
    public OSCParameter<Integer>          shapeRevolutions;

    // camera parameters 
    public OSCParameter<PVector>          camPos;
    public OSCParameter<PVector>          camRot;
    public OSCParameter<Float>            camZoom;

    // rendering parameters
    public OSCParameter<RenderMode>       renderMode;
    public OSCParameter<ShaperEnum>       shaper;
    public OSCParameter<ColourMapperEnum> mapper;
    public OSCParameter<SkyboxEnum>       skybox;

    public OSCParameter<Boolean>          guiControlsEnabled, guiSpectrumEnabled;
    public OSCParameter<Integer>          audioSource;
    public OSCParameter<Boolean>          audioRecording;
    
    private final List<OSCParameter>      paramList;
}
