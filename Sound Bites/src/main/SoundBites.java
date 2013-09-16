package main;

import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.DropdownList;
import controlP5.Slider;
import controlP5.Textlabel;
import controlP5.Toggle;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import geom.RenderMode;
import geom.Skybox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.media.opengl.GL2;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import processing.core.PApplet;
import static processing.core.PApplet.println;
import static processing.core.PConstants.DISABLE_DEPTH_TEST;
import static processing.core.PConstants.ENABLE_DEPTH_TEST;
import processing.core.PVector;
import processing.event.MouseEvent;
import shaper.ColourMapper;
import shaper.ImageColourMapper;
import shaper.PlainColourMapper;
import shaper.Shaper;
import shaper.Shaper_Cylinder;
import shaper.Shaper_Ring;
import shaper.Shaper_RingOld;
import shaper.Shaper_Sphere;

/**
 * Program for visualising sound and creating STl files for 3D printing.
 * This project was created in collaboration with Gerbrand <G> van Melle.
 * 
 * @author Stefan Marks
 * @version 1.0   - 30.05.2013: Created
 * @version 1.1   - 08.06.2013: Switched to OpenGL rendering
 * @version 2.0   - 10.08.2013: Refactored shaper modules and parameter GUI
 * @version 2.1   - 20.08.2013: Refactored surface class
 * @version 2.2.1 - 22.08.2013: Added colour mapping
 * @version 2.2.2 - 27.08.2013: Added realtime recording
 * @version 2.2.3 - 04.09.2013: Added pause button
 * @version 2.3.1 - 04.09.2013: Switched rendering to OpenGL 2
 * @version 2.3.2 - 05.09.2013: Added skyboxes and the cylinder shaper
 * @version 2.3.3 - 09.09.2013: Added split mode
 * @version 2.3.4 - 13.09.2013: Moved skyboxes into JAR, renamed project to SoundBites
 * @version 2.3.5 - 13.09.2013: Added complete removal of GUI
 * @version 2.3.6 - 13.09.2013: Added zoom and OSC support. 
 * @version 2.3.7 - 15.09.2013: Added volume controller
 * 
 */
public class SoundBites extends PApplet implements OSCListener
{
    public static final String VERSION = "2.3.7";
         
    
    /**
     * Sets up the program.
     */
    @Override
    public void setup()
    {
        //size(1920, 1080, OPENGL);
        size(1024, 768, OPENGL);
        frameRate(60);

        gui = new ControlP5(this);
        guiEnabled = true;
        
        objRotX = 30;
        objRotY = 0;
        cameraPos  = new PVector(0, 0, 700);
        cameraZoom = 1.0f;

        skybox = new Skybox("/resources/skyboxes/SkyboxHexSphere.jpg", 2000);
        //skybox = new Skybox("/resources/skyboxes/SkyboxGridPlane.jpg", 2000);

                
        dragToRotate = false;
        renderMode   = RenderMode.SOLID;
        shaper       = null;
        mapperPlain  = new PlainColourMapper(Color.white);
        mapper       = mapperPlain;
        
        // find input mixers
        inputMixers = new LinkedList<Mixer.Info>();
        for( Mixer.Info info : AudioSystem.getMixerInfo() )
        {
            Mixer mixer = AudioSystem.getMixer(info);
            for ( Line.Info line : mixer.getTargetLineInfo() ) 
            {
                try 
                {
                    Line l = mixer.getLine(line);
                    if ( l instanceof javax.sound.sampled.TargetDataLine )
                    {
                        l.open();
                        // success: add to mixer list
                        inputMixers.add(info);
                        l.close();
                    }
                } 
                catch (LineUnavailableException e)
                {
                    // nothing to do here
                }    
            }
        } 

        // create audio analyser
        audioAnalyser = new SpectrumAnalyser(60, 10);
        inputIdx = 0; 
        
        // populate shaper list
        shaperList = new LinkedList<Shaper>();
        shaperList.add(new Shaper_Ring());
        shaperList.add(new Shaper_RingOld());
        shaperList.add(new Shaper_Sphere());
        shaperList.add(new Shaper_Cylinder());

        setupOSC();
        createGUI();
        selectAudioInput(inputMixers.get(0));
        selectRealtimeSpectrum();
        selectShaper(shaperList.get(0));
    }

    
    /**
     * Sets up the OSC receiver.
     */
    void setupOSC()
    {
        try
        {
            oscReceiver = new OSCPortIn(OSCPort.defaultSCOSCPort());
            oscReceiver.addListener(".*", this);
            oscReceiver.startListening();
        }
        catch (SocketException e)
        {
            System.err.println("Could not start OSC receiver (" + e + ")");
        }
    }
    
    
    /**
     * Creates the permanent GUI elements.
     */
    void createGUI()
    {
        int yPos     = guiSpacing;
        int guiWidth = 120;
        
        // Button for selecting render mode
        btnRenderMode = gui.addButton("Render Mode: Solid")
                .setPosition(width - guiWidth - guiSpacing, yPos)
                .setSize(guiWidth, guiSizeY)
                .addCallback(new controlP5.CallbackListener()
        {
            @Override
            public void controlEvent(CallbackEvent e)
            {
                if (e.getAction() == ControlP5.ACTION_PRESSED)
                {
                    toggleRenderMode();
                }
            }
        });
        
        // Button for selecting split mode
        yPos += guiSizeY + guiSpacing;
        btnSplit = gui.addToggle("Split Mode")
                .setPosition(width - guiWidth - guiSpacing, yPos)
                .setSize(guiWidth, guiSizeY)
                ;
        btnSplit.getCaptionLabel().setPadding(5, -14);
        
        // Button for selecting plain colour mapping
        yPos += guiSizeY + guiSpacing;
        gui.addButton("Plain Mapping")
                .setPosition(width - guiWidth - guiSpacing, yPos)
                .setSize(guiWidth, guiSizeY)
                .addCallback(new controlP5.CallbackListener()
        {
            @Override
            public void controlEvent(CallbackEvent e)
            {
                if (e.getAction() == ControlP5.ACTION_RELEASED)
                {
                    selectPlainMapping();
                }
            }
        });
        
        // Button for selecting image mapping
        yPos += guiSizeY + guiSpacing;
        gui.addButton("Image Mapping")
                .setPosition(width - guiWidth - guiSpacing, yPos)
                .setSize(guiWidth, guiSizeY)
                .addCallback(new controlP5.CallbackListener()
        {
            @Override
            public void controlEvent(CallbackEvent e)
            {
                if (e.getAction() == ControlP5.ACTION_RELEASED)
                {
                    selectPatternMapping();
                }
            }
        });
        
        // Button for saving shape as STL
        yPos += guiSizeY + guiSpacing;
        gui.addButton("Save STL")
                .setPosition(width - guiWidth - guiSpacing, yPos)
                .setSize(guiWidth, guiSizeY)
                .addCallback(new controlP5.CallbackListener()
        {
            @Override
            public void controlEvent(CallbackEvent e)
            {
                if (e.getAction() == ControlP5.ACTION_PRESSED)
                {
                    saveStlFile();
                }
            }
        });
        
        // Button for pausing recording
        yPos += guiSizeY + guiSpacing;
        btnPause = gui.addToggle("Pause")
                .setPosition(width - guiWidth - guiSpacing, yPos)
                .setSize(guiWidth, guiSizeY)
                ;
        btnPause.getCaptionLabel().setPadding(5, -14);
        
        // Drop Down list for selecting the input
        yPos += guiSizeY + guiSpacing;
        lstInputs = gui.addDropdownList("input")
                .setPosition(width - guiWidth - guiSpacing, yPos + guiSizeY)
                .setSize(guiWidth, guiSizeY * (2 + inputMixers.size()))
                .setItemHeight(guiSizeY)
                .setBarHeight(guiSizeY)
                .addListener(new controlP5.ControlListener()
        {
            @Override
            public void controlEvent(ControlEvent ce)
            {
                int iInput = (int) ce.getValue();
                if ( iInput == -1 )
                {
                    selectSpectrumFile();
                }
                else
                {
                    selectAudioInput(inputMixers.get(iInput));
                }
            }
        });
        // fill dropdown list with entries
        lstInputs.getCaptionLabel().getStyle().paddingTop = 5;
        lstInputs.addItem("Spectrum File", -1);
        for ( int i = 0 ; i < inputMixers.size() ; i++ )
        {
            String name = inputMixers.get(i).getName();
            if ( name.length() > 20 )
            {
                name = name.substring(0, 20);
            }
            lstInputs.addItem(name, i);
        }
        
        // Dropdown list for shaper selection
        lstShapers = gui.addDropdownList("shaper")
                .setPosition((width - guiWidth) / 2, guiSpacing + guiSizeY)
                .setSize(guiWidth, guiSizeY * (2 + shaperList.size()))
                .setItemHeight(guiSizeY)
                .setBarHeight(guiSizeY)
                .addListener(new controlP5.ControlListener()
        {
            @Override
            public void controlEvent(ControlEvent ce)
            {
                int iInput = (int) ce.getValue();
                selectShaper(shaperList.get(iInput));
            }
        });
        lstShapers.getCaptionLabel().getStyle().paddingTop = 5;
        for ( int i = 0 ; i < shaperList.size() ; i++ )
        {
            lstShapers.addItem(shaperList.get(i).getName(), i);
        }
        
        // Slider for live input volume there as well
        sldVolume = gui.addSlider("Volume")
                .setPosition(guiSpacing, height - (guiSizeY + guiSpacing) * 2)
                .setHeight(guiSizeY)
                .setRange(0, 100)
                .setValue(100)
                .addListener(new controlP5.ControlListener()
        {
            @Override
            public void controlEvent(ControlEvent ce)
            {
                if ( audioVolume != null )
                {
                    audioVolume.setValue(ce.getValue());
                }
            }
        });
        
        // Label with filename at the bottom left
        lblFilename = gui.addTextlabel("filename", "Filename: ---")
                .setPosition(guiSpacing, height - guiSizeY - guiSpacing)
                .setSize(width - 20, 20);
        gui.setAutoDraw(false);
    }

    
    /**
     * Draws a single frame.
     */
    @Override
    public void draw()
    {
        background(0);

        // do we have to completely recalculate the 3D shape?
        if ( (recomputeTime >= 0) && (recomputeTime < millis()) )
        {
            calculateShape();
        }

        hint(ENABLE_DEPTH_TEST);
        
        GL2 gl = beginPGL().gl.getGL2();

        // revert the Processing version of the projection matrix to Standard OpenGL
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix(); // save current state of matrix
        gl.glLoadIdentity();
        float aspect = (float) width / (float) height;
        gl.glFrustum(-aspect, aspect, -1, +1, 1.5 *  cameraZoom, 5000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // set up lighting
        gl.glEnable(GL2.GL_LIGHT0);
        float[] ambientLight = { 0.1f, 0.1f, 0.1f };    // little ambient light
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, ambientLight, 0);
        float[] diffuseLight = { 1.0f, 1.0f, 1.0f };    // white diffuse...
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);
        float[] lightPos = { 0.0f, 1.0f, 1.0f, 0.0f }; // ...directional light
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        // draw skybox (uses only rotation of camera)
        if ( skybox != null )
        {
            gl.glPushMatrix();
            gl.glRotatef(objRotX, 1, 0, 0);
            gl.glRotatef(objRotY, 0, 1, 0);
            skybox.render(gl);
            gl.glPopMatrix();
        }
        
        // apply camera position and rotation
        gl.glTranslatef(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        gl.glRotatef(objRotX, 1, 0, 0);
        gl.glRotatef(objRotY, 0, 1, 0);
        
        // draw coordinate cross
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glBegin(GL2.GL_LINES);
            gl.glColor3f(1, 0, 0); gl.glVertex3f(0, 0, 0); gl.glVertex3f(100, 0, 0);
            gl.glColor3f(0, 1, 0); gl.glVertex3f(0, 0, 0); gl.glVertex3f(0, 100, 0);
            gl.glColor3f(0, 0, 1); gl.glVertex3f(0, 0, 0); gl.glVertex3f(0, 0, 100);
        gl.glEnd();
        
        // animate and draw the shape
        shaper.setSplitMode(btnSplit.getState());
        shaper.update(inputIdx * 360 / spectrumData.length);
        shaper.render(gl);

        // undo transformations and depth testing for the GUI  
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix(); 
        hint(DISABLE_DEPTH_TEST);
        
        if ( spectrumFile == null )
        {
            updateRealtimeSpectrum();
        }
        
        if ( guiEnabled )
        {
            gui.draw();
        }
    }

    
    /**
     * Updates the spectrum information when running on realtime audio.
     */
    private void updateRealtimeSpectrum()
    {
        SpectrumInfo info = audioAnalyser.getSpectrumInfo(0);
        if ( (info != null) && (info.intensity != null) )
        {
            int len = info.intensity.length;

            // only update shape when not paused
            if ( !btnPause.getState() )
            {
                System.arraycopy(info.intensity, 0, spectrumData[inputIdx], 0, len);
                shaper.updateSurface(inputIdx, spectrumData[inputIdx]);
                inputIdx = (inputIdx + 1) % spectrumData.length;
                // enable the surface to recalculate changed normals
            }
            
            // draw live spectrum
            pushStyle();
            strokeWeight(2);
            for ( int i = 0 ; i < len ; i++ )
            {
                int x = width - guiSpacing - (len - i) * 2;
                int y = height - guiSpacing;
                int h = (int) (info.intensity[i] * 100);
                int colour = shaper.getColourMapper().mapSpectrum(info.intensity, i);
                stroke((colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF, 255);
                line(x, y, x, y-h);
            }
            popStyle();
        }
    }
    
    
    /**
     * Called when the mouse button is pressed.
     */
    @Override
    public void mousePressed()
    {
        dragToRotate = true;
    }

    
    /**
     * Called when the mouse button is released.
     */
    @Override
    public void mouseReleased()
    {
        dragToRotate = false;
    }

    
    /**
     * Called while the mouse is dragged
     */
    @Override
    public void mouseDragged()
    {
        if (dragToRotate)
        {
            objRotX += (float) (mouseY - pmouseY) / height * 180;
            objRotX  = constrain(objRotX, -90, 90);
            objRotY += (float) (mouseX - pmouseX) / width * 360;
        }
    }

    
    /**
     * Called when the mouse wheel is used.
     * 
     * @param e the event with the mouse wheel scroll amount
     */
    @Override
    public void mouseWheel(MouseEvent e)
    {
        int c = e.getCount();
        if      ( c > 0 ) { cameraZoom /= 1.05f; }
        else if ( c < 0 ) { cameraZoom *= 1.05f; }
        cameraZoom = constrain(cameraZoom, 0.25f, 5.0f);
    }
    
    
    /**
     * Called when a key is pressed
     */
    @Override
    public void keyPressed()
    {
        if ( key == 'g' )
        {
            guiEnabled = !guiEnabled;
        }
    }
        
    
    /**
     * Called when a controlP5 GUI elements was used.
     * 
     * @param event event that triggered this call
     */
    public void controlEvent(ControlEvent event)
    {
        dragToRotate = false;
        if ( event.isController() ) 
        {
            recomputeTime = millis() + 1000; // 1s from now
        }
    }
    
    
    /**
     * Receives, parses, and executes OSC messages.
     * 
     * @param time    the time of receiving a message
     * @param message the message content
     */
    @Override
    public void acceptMessage(Date time, OSCMessage message)
    {
        String   addr   = message.getAddress();
        Object[] params = message.getArguments();
        
        if ( addr.equals("/enableGUI") && (params.length > 0) )
        {
            guiEnabled = (Boolean) params[0];
        }
        else if ( addr.equals("/paused") && (params.length > 0) )
        {
            btnPause.setState((Boolean) params[0]);
        }
    }
    
    
    /**
     * Selects the next render mode.
     */
    public void toggleRenderMode()
    {
        String txtMode;
        switch ( renderMode )
        {
            case SOLID     : renderMode = RenderMode.WIREFRAME; txtMode = "Wireframe"; break;
            case WIREFRAME : renderMode = RenderMode.POINTS; txtMode = "Points"; break;
            default:         renderMode = RenderMode.SOLID; txtMode = "Solid"; break;
        }
        shaper.setRenderMode(renderMode);
        btnRenderMode.setCaptionLabel("Render Mode: " + txtMode);
        System.out.println("Selected " + txtMode + " render mode");
    }

    
    /**
     * Shows a load dialog to select a spectrum file.
     */
    public void selectSpectrumFile()
    {
        selectInput("Select the Spectrum file to load", "openSpectrumFile", new File(dataPath(".")));
    }


    /**
     * Opens a spectrum file.
     * 
     * @param file the file to open
     */
    public void openSpectrumFile(File file)
    {
        if ( file != null )
        {
            println("Opening " + file);
            String[] data = loadStrings(file);
            // how many lines are there (expluding header)
            int dataLen = data.length - 1;

            // what if there is too much data: reduce
            int specStep = ((dataLen - 1) / 720) + 1;
            int specLen = dataLen / specStep;

            // allocate spectrum data
            spectrumData = new float[specLen][];
            // go through the lines

            for (int i = 0; i < dataLen; i += 1)
            {
                // split each line into the single frequencies
                String[] freqStr = data[i + 1].split("\t");
                int freqLen = freqStr.length - 1;
                // create frequency array
                int specIdx = i / specStep;
                if (specIdx >= spectrumData.length)
                {
                    break;
                }

                if (spectrumData[specIdx] == null)
                {
                    spectrumData[specIdx] = new float[freqLen];
                }
                for (int f = 0; f < freqLen; f++)
                {
                    spectrumData[specIdx][f] += Float.parseFloat(freqStr[f + 1]) / specStep;
                }
            }
            println("Read " + (data.length - 1) + " lines with " + spectrumData[0].length + " frequencies each");
            println("Stored as " + spectrumData.length + " lines of spectrum data (compression=" + specStep + ")");
            recomputeTime = 0; // force recalculation
            spectrumFile = file;
            lblFilename.setText("Filename: " + file.getName());
            sldVolume.setVisible(false);
            btnPause.setVisible(false);
        }
    }
    
    
    
    
    /**
     * Saves the 3D shape as an STL file.
     */
    public void saveStlFile()
    {
        if ( spectrumFile == null )
        {
            return;
        }

        PrintWriter w;
        try
        {
            w = new PrintWriter(new File(spectrumFile + ".stl"));
        }
        catch (FileNotFoundException e)
        {
            println("Could not write STL file (" + e + ").");
            return;
        }
        
        shaper.writeSTL(w);
        w.close();
    }
    
    
    /**
     * Switches to using realtime audio spectrum.
     */
    public void selectRealtimeSpectrum()
    {
        int spectrumCount = audioAnalyser.getSpectrumBandCount();
        spectrumData = new float[240][spectrumCount];
        spectrumFile = null;
        inputIdx = 0;
        sldVolume.setVisible(true);
        btnPause.setVisible(true);
        calculateShape();
    }
    
    
    /**
     * Selects plain colour mapping.
     */
    public void selectPlainMapping()
    {
        Color c = JColorChooser.showDialog(frame, "Select Colour", mapperPlain.getColour());
        if ( c != null )
        {
            mapperPlain = new PlainColourMapper(c);
            shaper.setColourMapper(mapperPlain);
        }
    }
    
    
    /**
     * Selects image pattern mapping.
     */
    public void selectPatternMapping()
    {
        FileDialog fc = new FileDialog(frame, "Select Image", FileDialog.LOAD);
        fc.setVisible(true);
        String f = fc.getFile();
        if ( f != null )
        {
            try
            {
                mapper = new ImageColourMapper(new File(fc.getDirectory(), f));
                shaper.setColourMapper(mapper);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(frame, 
                        "Could not load image.\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    /**
     * Selects an audio input.
     * 
     * @param mixer the input to use
     */
    private void selectAudioInput(Mixer.Info mixerInfo)
    {
        // detach audio analyser and destroy Minim
        audioAnalyser.detachFromAudio();
        if ( minim != null )
        {
            minim.stop();
        }
        
        // create new Minim with new audio input
        minim = new Minim(this);
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        minim.setInputMixer(mixer);
        // attach to audio analyser
        AudioInput input = minim.getLineIn();
        audioAnalyser.attachToAudio(input);
        System.out.println("Selected Audio Input: " + mixerInfo.getName() );
        // get volume/gain controller
        audioVolume = (FloatControl) input.gain();
        sldVolume.setRange(audioVolume.getMinimum(), audioVolume.getMaximum());
        // update GUI
        lstInputs.setCaptionLabel("Select Input");
        lblFilename.setStringValue("Realtime Spectrum from " + mixerInfo.getName());
        selectRealtimeSpectrum();
    }
    
    
    /**
     * Selects a new shaper module.
     * 
     * @param s the new shaper to use
     */
    void selectShaper(Shaper s)
    {
        if (shaper != null)
        {
            shaper.deinitialise();
        }
        
        shaper = s;
        
        if ( shaper != null )
        {
            shaper.initialise(gui);
            shaper.setColourMapper(mapper);
            int y = 10;
            for ( Controller c : shaper.getControllers() )
            {
                c.setPosition(10, y);
                c.setSize(200, 20);
                y += 30;
            }
            shaper.createSurface(spectrumData);
            lstShapers.setCaptionLabel("Shaper: " + shaper.getName());
        }
    }
        
    
    /**
     * Calculates the whole 3D shape.
     */
    public void calculateShape()
    {
        if ( (shaper == null) || 
             ( (spectrumData == null) && !btnPause.getState()) ) return;

        shaper.createSurface(spectrumData);
        shaper.setRenderMode(renderMode);
        recomputeTime = -1; // done
    }

    
    /**
     * Checks if the applet should run fullscreen.
     * 
     * @return <code>true</code> if applet should run fullscreen,
     *         <code>false</code> if not
     */
    @Override
    public boolean sketchFullScreen()
    {
        return false;//true;
    }

    
    /**
     * Disposes of the applet.
     */
    @Override
    public void dispose()
    {        
        audioAnalyser.detachFromAudio();
        audioVolume = null;
        if ( minim != null )
        {
            minim.stop();
        }
        
        super.dispose();
    }
    
    
    /**
     * Main method for the program.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        // reportAudioCapabilities();
        
        final SoundBites p = new SoundBites();

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                StringWriter sw = new StringWriter();
                PrintWriter  pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                JTextArea txt = new JTextArea(sw.getBuffer().toString());
                txt.setEditable(false);
                JScrollPane scrl = new JScrollPane(txt);
                scrl.setPreferredSize(new Dimension(700, 400));
                JOptionPane.showMessageDialog(
                        p.frame, 
                        scrl,
                        "Sorry, but there was a Problem", 
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
        
        runSketch(new String[]
        {
            "Sound Bites v" + VERSION
        }, p);
    }
    

    public static void reportAudioCapabilities()
    {
        try
        {
            System.out.println("OS: " + System.getProperty("os.name") + " "
                    + System.getProperty("os.version") + "/"
                    + System.getProperty("os.arch") + "\nJava: "
                    + System.getProperty("java.version") + " ("
                    + System.getProperty("java.vendor") + ")\n");
            for (Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo())
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
                            System.out.println(analyzeControl(thisControl));
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
                            System.out.println(analyzeControl(thisControl));
                        }
                        thisLine.close();
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public static String analyzeControl(Control thisControl)
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
                toReturn += "  " + analyzeControl(children) + "\n";
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

    
        // permanent GUI controls
    private ControlP5     gui;
    private boolean       guiEnabled;
    private Textlabel     lblFilename;
    private Button        btnRenderMode;
    private Toggle        btnPause, btnSplit;
    private DropdownList  lstInputs, lstShapers;
    private Slider        sldVolume;
    
    private final int guiSizeY   = 20;
    private final int guiSpacing = 10;

    private OSCPortIn oscReceiver;
    
    // object rotation
    private float objRotX, objRotY;
    
    // spectrum data
    private File      spectrumFile;
    private float[][] spectrumData;
    
    // Camera and skybox
    private PVector cameraPos;
    private float   cameraZoom;
    private Skybox  skybox;
    
    // the shaper module
    private List<Shaper>      shaperList;
    private Shaper            shaper;
    private ColourMapper      mapper;
    private PlainColourMapper mapperPlain;
    
    // timestamp to trigger recalculation
    private long        recomputeTime;
    // some flags
    private boolean     dragToRotate;
    private RenderMode  renderMode;

    // live audio input
    private List<Mixer.Info>  inputMixers;
    private Minim             minim;
    private FloatControl      audioVolume;
    private SpectrumAnalyser  audioAnalyser;
    private int               inputIdx;
       
} 

