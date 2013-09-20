package main;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParameter;
import com.illposed.osc.OSCPortIn;
import geom.RenderMode;
import geom.SkyboxEnum;
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
        
        camPos  = new OSCParameter<PVector>("/cam/pos", new PVector(0, 0, 700)); paramList.add(camPos);
        camRot  = new OSCParameter<PVector>("/cam/rot", new PVector(0, 0, 0));   paramList.add(camRot);
        camZoom = new OSCParameter<Float>("/cam/zoom", 1.0f);                    paramList.add(camZoom);

        shaper     = new OSCParameter<ShaperEnum>(      "/render/shaper", ShaperEnum.RING1);       paramList.add(shaper);
        mapper     = new OSCParameter<ColourMapperEnum>("/render/mapper", ColourMapperEnum.WHITE); paramList.add(mapper);
        skybox     = new OSCParameter<SkyboxEnum>(      "/render/skybox", SkyboxEnum.BLACK);       paramList.add(skybox);
        renderMode = new OSCParameter<RenderMode>(      "/render/mode",   RenderMode.SOLID);       paramList.add(renderMode);
        
        guiEnabled = new OSCParameter<Boolean>("/gui/enabled", true);         paramList.add(guiEnabled);

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
    
    
    // camera parameters 
    public OSCParameter<PVector>          camPos;
    public OSCParameter<PVector>          camRot;
    public OSCParameter<Float>            camZoom;

    // rendering parameters
    public OSCParameter<RenderMode>       renderMode;
    public OSCParameter<ShaperEnum>       shaper;
    public OSCParameter<ColourMapperEnum> mapper;
    public OSCParameter<SkyboxEnum>       skybox;

    public OSCParameter<Boolean>          guiEnabled;
    public OSCParameter<Boolean>          audioRecording;
    
    private List<OSCParameter> paramList;
}
