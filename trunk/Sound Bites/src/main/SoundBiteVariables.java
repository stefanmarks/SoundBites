package main;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCParameter;
import com.illposed.osc.OSCPortIn;
import geom.RenderMode;
import geom.Skybox;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import processing.core.PVector;
import shaper.ColourMapper;
import shaper.ImageColourMapper;
import shaper.PlainColourMapper;
import shaper.Shaper;
import shaper.Shaper_Cylinder;
import shaper.Shaper_Ring;
import shaper.Shaper_RingOld;
import shaper.Shaper_Sphere;

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
        
        camPos  = new OSCParameter<PVector>("cam/pos", new PVector(0, 0, 700)); paramList.add(camPos);
        camRot  = new OSCParameter<PVector>("cam/rot", new PVector(0, 0, 0));   paramList.add(camRot);
        camZoom = new OSCParameter<Float>("/cam/zoom", 1.0f);                   paramList.add(camZoom);
        
        renderMode = new OSCParameter<RenderMode>("render/mode", RenderMode.SOLID); paramList.add(renderMode);
        guiEnabled = new OSCParameter<Boolean>("/gui/enabled", true);               paramList.add(guiEnabled);

        recordingPaused = new OSCParameter<Boolean>("/recording/paused", false);    paramList.add(recordingPaused);
        
        // populate shaper list
        shaperList = new LinkedList<Shaper>();
        shaperList.add(new Shaper_Ring());
        shaperList.add(new Shaper_RingOld());
        shaperList.add(new Shaper_Sphere());
        shaperList.add(new Shaper_Cylinder());
        shaper = null;

        // populate Mapper list
        mapperList = new LinkedList<ColourMapper>();
        mapperList.add(new PlainColourMapper("White", Color.white));
        mapperList.add(new PlainColourMapper("Brown", Color.decode("#603000")));
        mapperList.add(ImageColourMapper.create("Greyscale", "GreyMap.png"));
        mapperList.add(ImageColourMapper.create("Transparent", "TransparentMap.png"));
        mapperList.add(ImageColourMapper.create("Spectrum", "SpectrumMap.png"));
        mapperList.add(ImageColourMapper.create("Fire", "FireMap.png"));
        mapperList.add(ImageColourMapper.create("Ice", "IceMap.png"));
        mapper = null;
        
        // populate Skybox list
        skybox = new Skybox("/resources/skyboxes/SkyboxHexSphere_PoT.jpg", 2000);
        //skybox = new Skybox("/resources/skyboxes/SkyboxGridPlane_PoT.jpg", 2000);
    }

    
    public void register(OSCPortIn port)
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
    public OSCParameter<PVector> camPos;
    public OSCParameter<PVector> camRot;
    public OSCParameter<Float>   camZoom;

    // rendering parameters
    public OSCParameter<RenderMode> renderMode;
    
    public List<Shaper>        shaperList;
    public Shaper              shaper;
    public static final String RENDER_SHAPER = "/render/shaper";

    public List<ColourMapper>  mapperList;
    public ColourMapper        mapper;
    public static final String RENDER_MAPPER = "/render/mapper";

    public List<Skybox>        skyboxList;
    public Skybox              skybox;
    public static final String RENDER_SKYBOX = "/render/skybox";

    public OSCParameter<Boolean> guiEnabled;
    public OSCParameter<Boolean> recordingPaused;
    
    private List<OSCParameter>   paramList;
}
