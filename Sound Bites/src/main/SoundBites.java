package main;

import analyser.AudioManager;
import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import com.illposed.osc.OSCParameter;
import com.illposed.osc.OSCParameterListener;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.DropdownList;
import controlP5.Slider;
import controlP5.Textlabel;
import controlP5.Toggle;
import ddf.minim.Minim;
import geom.RenderMode;
import static geom.RenderMode.SOLID;
import static geom.RenderMode.WIREFRAME;
import geom.Skybox;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.SocketException;
import javax.media.opengl.GL2;
import javax.sound.sampled.FloatControl;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import processing.core.PApplet;
import static processing.core.PConstants.DISABLE_DEPTH_TEST;
import static processing.core.PConstants.ENABLE_DEPTH_TEST;
import processing.core.PVector;
import processing.event.MouseEvent;
import shaper.ColourMapper;
import shaper.ColourMapperEnum;
import shaper.Shaper;
import shaper.ShaperEnum;

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
 * @version 2.3.7 - 15.09.2013: Added volume controller, refactored audio system, added fullscreen dialog, rearranged GUI
 * @version 2.4.0 - 20.09.2013: Completed migration of central parameters and full remote control
 */
public class SoundBites extends PApplet
{
    public static final String VERSION = "2.4.0a";

    /**
     * Creates an instance of the SoundBites program.
     * 
     * @param fullscreen <code>true</code> if program should run in fullscreen mode,
     *                   <code>false</code> if program should run in window
     */
    public SoundBites(boolean fullscreen)
    {
        this.runInFullscreen = fullscreen;
    }
         
    /**
     * Sets up the program.
     */
    @Override
    public void setup()
    {
        if ( runInFullscreen )
        {
            size(displayWidth, displayHeight, OPENGL);
        }
        else
        {
            size(1024, 768, OPENGL);
        }
        
        frameRate(60);

        vars = new SoundBiteVariables();
        gui  = new ControlP5(this);
        dragToRotate = false;

        // setup parameter change listeners
        vars.shaper.registerListener(new OSCParameterListener<ShaperEnum>() {
            @Override
            public void valueChanged(OSCParameter<ShaperEnum> param)
            {
                updateShaper(shaper);
            }
        });
        vars.mapper.registerListener(new OSCParameterListener<ColourMapperEnum>() {
            @Override
            public void valueChanged(OSCParameter<ColourMapperEnum> param)
            {
                updateMapper();
            }
        });
        vars.renderMode.registerListener(new OSCParameterListener<RenderMode>() {
            @Override
            public void valueChanged(OSCParameter<RenderMode> param)
            {
                updateRenderMode();
            }
        });
        vars.audioRecording.registerListener(new OSCParameterListener<Boolean>() {
            @Override
            public void valueChanged(OSCParameter<Boolean> param)
            {
                updatePauseMode();
            }
        });
        
        // find inputs
        audioManager = new AudioManager();
        // create audio analyser
        audioAnalyser = new SpectrumAnalyser(60, 10);
        inputIdx = 0; 
        
        setupOSC();
        createGUI();
        
        selectAudioInput(null);
                        
        shaper = null; updateShaper(shaper);
        mapper = null; updateMapper();
    }
    
    
    /**
     * Sets up the OSC receiver.
     */
    private void setupOSC()
    {
        try
        {
            // open port
            oscReceiver = new OSCPortIn(OSCPort.defaultSCOSCPort());
            // register listener (reacts to every incoming message)
            vars.registerWithInputPort(oscReceiver);
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
    private void createGUI()
    {
        final int xPos = width - guiSpacing - guiMenuW;
              int yPos = guiSpacing;
                   
        // Dropdown list for shaper selection
        int shaperCount = ShaperEnum.values().length;
        lstShapers = gui.addDropdownList("shaper")
                .setPosition(xPos, yPos + guiSizeY)
                .setSize(guiMenuW, guiSizeY * (1 + shaperCount))
                .setItemHeight(guiSizeY)
                .setBarHeight(guiSizeY)
                .addListener(new controlP5.ControlListener()
        {
            @Override
            public void controlEvent(ControlEvent ce)
            {
                int iInput = (int) ce.getValue();
                vars.shaper.set(ShaperEnum.values()[iInput]); // listener will do the rest
            }
        });
        lstShapers.getCaptionLabel().getStyle().paddingTop = 5;
        for ( int i = 0 ; i < shaperCount ; i++ )
        {
            lstShapers.addItem(ShaperEnum.values()[i].toString(), i);
        }

        // Dropdown list for mapper selection
        yPos += guiSizeY + guiSpacing;
        int mapperCount = ColourMapperEnum.values().length;
        lstMappers = gui.addDropdownList("mapper")
                .setPosition(xPos, yPos + guiSizeY)
                .setSize(guiMenuW, guiSizeY * (1 + mapperCount))
                .setItemHeight(guiSizeY)
                .setBarHeight(guiSizeY)
                .addListener(new controlP5.ControlListener()
        {
            @Override
            public void controlEvent(ControlEvent ce)
            {
                int iInput = (int) ce.getValue();
                vars.mapper.set(ColourMapperEnum.values()[iInput]); // listener will do the rest
            }
        });
        lstMappers.getCaptionLabel().getStyle().paddingTop = 5;
        for ( int i = 0 ; i < mapperCount ; i++ )
        {
            lstMappers.addItem(ColourMapperEnum.values()[i].toString(), i);
        }

        // Button for selecting render mode
        yPos += guiSizeY + guiSpacing;
        btnRenderMode = gui.addButton("Render Mode: Solid")
                .setPosition(xPos, yPos)
                .setSize(guiMenuW, guiSizeY)
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
                .setPosition(xPos, yPos)
                .setSize(guiMenuW, guiSizeY)
                ;
        btnSplit.getCaptionLabel().setPadding(5, -14);
        
        // Button for saving shape as STL
        yPos += guiSizeY + guiSpacing;
        gui.addButton("Save STL")
                .setPosition(xPos, yPos)
                .setSize(guiMenuW, guiSizeY)
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
                .setPosition(xPos, yPos)
                .setSize(guiMenuW, guiSizeY)
                .addCallback(new controlP5.CallbackListener()
        {
            @Override
            public void controlEvent(CallbackEvent e)
            {
                if (e.getAction() == ControlP5.ACTION_PRESSED)
                {
                    vars.audioRecording.set(!vars.audioRecording.get());
                }
            }
        });
        btnPause.getCaptionLabel().setPadding(5, -14);
        
        // Drop Down list for selecting the input
        yPos += guiSizeY + guiSpacing;
        lstInputs = gui.addDropdownList("input")
                .setPosition(xPos, yPos + guiSizeY)
                .setSize(guiMenuW, guiSizeY * (3 + audioManager.getInputs().size()))
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
                else if ( iInput == -2 )
                {
                    selectAudioInput(null);
                }
                else
                {
                    selectAudioInput(audioManager.getInput(iInput));
                }
            }
        });
        // fill dropdown list with entries
        lstInputs.getCaptionLabel().getStyle().paddingTop = 5;
        lstInputs.addItem("None", -2);
        lstInputs.addItem("Spectrum File", -1);
        int idx = 0;
        for ( analyser.AudioInput input : audioManager.getInputs() )
        {
            String name = input.toString();
            if ( name.length() > 20 )
            {
                name = name.substring(0, 20);
            }
            lstInputs.addItem(name, idx);
            idx++;
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
                if ( inputGain != null )
                {
                    inputGain.setValue(ce.getValue());
                }
            }
        });
        
        // Label with filename and FPS at the bottom left
        lblFps = gui.addTextlabel("fps", "FPS: ---")
                .setPosition((width - guiMenuW) / 2, guiSpacing)
                .setSize(guiMenuW, 20);
        
        lblFilename = gui.addTextlabel("filename", "Filename: ---")
                .setPosition(guiSpacing, height - guiSizeY - guiSpacing)
                .setSize(width - 20, 20);

        gui.setAutoDraw(false);
        
        lstMappers.bringToFront();
        lstShapers.bringToFront();
    }

    
    /**
     * Draws a single frame.
     */
    @Override
    public synchronized void draw()
    {
        background(0);

        // do we have to completely recalculate the 3D shape?
        if ( (recomputeTime >= 0) && (recomputeTime < millis()) )
        {
            updateShape();
        }

        hint(ENABLE_DEPTH_TEST);
        
        GL2 gl = beginPGL().gl.getGL2();

        // revert the Processing version of the projection matrix to Standard OpenGL
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix(); // save current state of matrix
        gl.glLoadIdentity();
        float aspect = (float) width / (float) height;
        gl.glFrustum(-aspect, aspect, -1, +1, 1.5 * vars.camZoom.get(), 5000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // draw skybox (uses only rotation of camera)
        Skybox skybox = vars.skybox.get().getSkybox(); 
        if ( skybox != null )
        {
            gl.glPushMatrix();
            gl.glRotatef(vars.camRot.get().x, 1, 0, 0);
            gl.glRotatef(vars.camRot.get().y, 0, 1, 0);
            skybox.render(gl);
            gl.glPopMatrix();
        }
        
        // set up lighting
        gl.glEnable(GL2.GL_LIGHT0);
        float[] ambientLight = { 0.1f, 0.1f, 0.1f };    // little ambient light
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, ambientLight, 0);
        float[] diffuseLight = { 1.0f, 1.0f, 1.0f };    // white diffuse...
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);
        float[] lightPos = { 0.0f, 1.0f, 1.0f, 0.0f }; // ...directional light
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        // apply camera position and rotation
        gl.glTranslatef(-vars.camPos.get().x, -vars.camPos.get().y, -vars.camPos.get().z);
        gl.glRotatef(vars.camRot.get().x, 1, 0, 0);
        gl.glRotatef(vars.camRot.get().y, 0, 1, 0);
        
        /*
        // draw coordinate cross
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glBegin(GL2.GL_LINES);
            gl.glColor3f(1, 0, 0); gl.glVertex3f(0, 0, 0); gl.glVertex3f(100, 0, 0);
            gl.glColor3f(0, 1, 0); gl.glVertex3f(0, 0, 0); gl.glVertex3f(0, 100, 0);
            gl.glColor3f(0, 0, 1); gl.glVertex3f(0, 0, 0); gl.glVertex3f(0, 0, 100);
        gl.glEnd();
        */
        
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
        
        if ( vars.guiEnabled.get() )
        {
            lblFps.setStringValue(String.format("FPS: %.1f", frameRate));
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

            // only update shape when recording
            if ( vars.audioRecording.get() )
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
                int colour = mapper.mapSpectrum(info.intensity, i);
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
        if ( dragToRotate )
        {
            PVector rot = vars.camRot.get();
            rot.x += (float) (mouseY - pmouseY) / height * 180;
            rot.x  = constrain(rot.x, -90, 90);
            rot.y += (float) (mouseX - pmouseX) / width * 360;
            vars.camRot.set(rot);
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
        int   c    = e.getCount();
        float zoom = vars.camZoom.get();
        if      ( c > 0 ) { zoom /= 1.05f; }
        else if ( c < 0 ) { zoom *= 1.05f; }
        zoom = constrain(zoom, 0.25f, 5.0f);
        vars.camZoom.set(zoom);
    }
    
    
    /**
     * Called when a key is pressed
     */
    @Override
    public void keyPressed()
    {
        switch ( key )
        {
            case 'a' : reportAudioProperties(); break;
            case 'g' : vars.guiEnabled.set(!vars.guiEnabled.get()); break;
            case 'r' : toggleRenderMode(); break;
        }
    }
    
    
    /**
     * Selects the next render mode.
     */
    private void toggleRenderMode()
    {
        RenderMode mode = vars.renderMode.get();
        switch ( mode )
        {
            case SOLID     : mode = RenderMode.WIREFRAME; break;
            case WIREFRAME : mode = RenderMode.POINTS; break;
            default:         mode = RenderMode.SOLID; break;
        }
        vars.renderMode.set(mode); // this will automatically call updateRenderMode
    }

    
    /**
     * Called when the render mode has changed.
     */
    private void updateRenderMode()
    {
        RenderMode mode = vars.renderMode.get();
        shaper.setRenderMode(mode);
        btnRenderMode.setCaptionLabel("Render Mode: " + mode);
        System.out.println("Selected " + mode + " render mode");
    }
    
    
    /**
     * Called when the pause mode has changed.
     */
    private void updatePauseMode()
    {
        btnPause.setState(!vars.audioRecording.get());
    }

    
    /**
     * Shows a load dialog to select a spectrum file.
     */
    private void selectSpectrumFile()
    {
        selectInput("Select the Spectrum file to load", "openSpectrumFile", new File(dataPath(".")));
    }


    /**
     * Opens a spectrum file.
     * 
     * @param file the file to open
     */
    private void openSpectrumFile(File file)
    {
        if ( file != null )
        {
            System.out.println("Opening " + file);
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
            System.out.println("Read " + (data.length - 1) + " lines with " + spectrumData[0].length + " frequencies each");
            System.out.println("Stored as " + spectrumData.length + " lines of spectrum data (compression=" + specStep + ")");
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
    private void saveStlFile()
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
            System.err.println("Could not write STL file (" + e + ").");
            return;
        }
        
        shaper.writeSTL(w);
        w.close();
    }
    
    
    /**
     * Selects an audio input.
     * 
     * @param input  the input to use
     */
    private void selectAudioInput(analyser.AudioInput input)
    {
        // detach audio analyser and destroy Minim
        audioAnalyser.detachFromAudio();
        if ( minim != null )
        {
            minim.stop();
        }
        
        if ( input != null )
        {
            // create new Minim with new audio input
            minim = new Minim(this);
            minim.setInputMixer(input.getMixer());
            // attach to audio analyser
            audioAnalyser.attachToAudio(minim.getLineIn());
            // get volume/gain controller
            inputGain = input.getGainControl();
            if ( inputGain != null )
            {
                sldVolume.setRange(inputGain.getMinimum(), inputGain.getMaximum());
            }

            int spectrumCount = audioAnalyser.getSpectrumBandCount();
            spectrumData = new float[240][spectrumCount];
            spectrumFile = null;
            inputIdx = 0;
            lblFilename.setStringValue("Realtime Spectrum from " + input);
        }
        else
        {
            // no input selected
            spectrumData = new float[240][64]; // dummy data
            spectrumFile = null;     
            inputGain    = null; 
            lblFilename.setStringValue("No Input Selected");
        }
        
        // common code, mainly for updating the GUI
        btnPause.setVisible(input != null);
        sldVolume.setVisible(inputGain != null);
        lstInputs.setCaptionLabel("Select Input");
        System.out.println("Selected Audio Input: " + ((input != null) ? input : "none"));
        updateShape();
    }
    
    
    /**
     * Called when a new shaper module is selected.
     */
    private synchronized void updateShaper(Shaper oldShaper)
    {
        if ( oldShaper != null )
        {
            for ( Controller c : oldShaper.getControllers() )
            {
                c.removeListener(RECALC_LISTENER);
            }
            oldShaper.deinitialise();
        }
        
        shaper = vars.shaper.get().getInstance();
        
        if ( shaper != null )
        {
            shaper.initialise(gui);
            shaper.setColourMapper(mapper);
            shaper.createSurface(spectrumData);

            // add gui elements
            int y = guiSpacing; 
            for ( Controller c : shaper.getControllers() )
            {
                c.setPosition(guiSpacing, y);
                c.setSize(guiControlsW, 20);
                y += guiSizeY + guiSpacing;
                c.addListener(RECALC_LISTENER);
            }
            // update rest of GUI
            lstShapers.setCaptionLabel("Shaper: " + shaper.toString());
            System.out.println("Selected Shaper: " + shaper.toString());
        }
    }
        
    
    /**
     * Called when a new colour mapper is selected.
     */
    private synchronized void updateMapper()
    {
        mapper = vars.mapper.get().getInstance();
        if ( shaper != null ) 
        {
            shaper.setColourMapper(mapper);
        }
        updateShape();
        lstMappers.setCaptionLabel("Mapper: " + mapper.toString());
        System.out.println("Selected Mapper: " + mapper.toString());
    }
    
    
    /**
     * Selects plain colour mapping.
     */
    private void selectPlainMapping()
    {
//        Color c = JColorChooser.showDialog(frame, "Select Colour", mapperPlain.getColour());
//        if ( c != null )
//        {
//            mapperPlain = new PlainColourMapper(c);
//            shaper.setColourMapper(mapperPlain);
//        }
    }
    
    
    /**
     * Selects image pattern mapping.
     */
    private void selectPatternMapping()
    {
//        FileDialog fc = new FileDialog(frame, "Select Image", FileDialog.LOAD);
//        fc.setVisible(true);
//        String f = fc.getFile();
//        if ( f != null )
//        {
//            try
//            {
//                mapper = new ImageColourMapper(new File(fc.getDirectory(), f));
//                shaper.setColourMapper(mapper);
//            }
//            catch (IOException e)
//            {
//                JOptionPane.showMessageDialog(frame, 
//                        "Could not load image.\n" + e.getMessage(),
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }

    
    /**
     * Updates the whole 3D shape.
     */
    private void updateShape()
    {
        if ( (shaper == null) || 
             ( (spectrumData == null) && !btnPause.getState()) ) return;

        shaper.createSurface(spectrumData);
        shaper.setRenderMode(vars.renderMode.get());
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
        return runInFullscreen;
    }

    
    /**
     * Disposes of the applet.
     */
    @Override
    public void dispose()
    {        
        audioAnalyser.detachFromAudio();
        inputGain = null;
        if ( minim != null )
        {
            minim.stop();
        }
        
        super.dispose();
    }
    
    
    private void reportAudioProperties()
    {
        JTextArea txt = new JTextArea(audioManager.reportAudioCapabilities());
        txt.setEditable(false);
        JScrollPane scrl = new JScrollPane(txt);
        scrl.setPreferredSize(new Dimension(700, 400));
        JOptionPane.showMessageDialog(
                frame, 
                scrl,
                "Audio Capabilities", 
                JOptionPane.PLAIN_MESSAGE);
    }
    
    
    /**
     * Main method for the program.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        // create program instance
        final SoundBites p = new SoundBites(checkFullscreen());

        // in case of an exception, show a swing dialog box
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
    
    
    private static boolean checkFullscreen()
    {
        // check if fullscreen mode is desired
        int choice = JOptionPane.showConfirmDialog(null,
                        "Do you want to run the program in fullscreen mode?",
                        "Run in Fullscreen Mode?",
                        JOptionPane.YES_NO_CANCEL_OPTION);
        if ( (choice == JOptionPane.CLOSED_OPTION) ||
             (choice == JOptionPane.CANCEL_OPTION) ) 
        {
            // User doesn't want to run this at all
            System.exit(0); 
        }
        
        return choice == JOptionPane.YES_OPTION;
    }
    
    
    /**
     * Inner control listener that triggers shaper recalculation when parameters are adjusted
     */
    private class RecalculateListener implements ControlListener
    {
        @Override
        public void controlEvent(ControlEvent ce)
        {
            // trigger recomputation of shape
            recomputeTime = millis() + 1000; // 1s from now
        }
    }
    
    // and the static listener instance 
    final RecalculateListener RECALC_LISTENER = new RecalculateListener();
    
    
    // permanent GUI controls
    private boolean       runInFullscreen;
    private ControlP5     gui;
    private Textlabel     lblFilename, lblFps;
    private Button        btnRenderMode;
    private Toggle        btnPause, btnSplit;
    private DropdownList  lstInputs, lstShapers, lstMappers;
    private Slider        sldVolume;

    // GUI spacing and sizes
    private final int guiSizeY     = 20;
    private final int guiControlsW = 200;
    private final int guiMenuW     = 120;
    private final int guiSpacing   = 10;

    private OSCPortIn oscReceiver;
    
    // shortcut to active shaper and mapper;
    private Shaper       shaper;
    private ColourMapper mapper;
            
    // spectrum data
    private File        spectrumFile;
    private float[][]   spectrumData;
    
    // timestamp to trigger recalculation
    private long        recomputeTime;
    // some flags
    private boolean     dragToRotate;

    // live audio input
    private AudioManager      audioManager;
    private int               inputIdx;
    private Minim             minim;
    private FloatControl      inputGain;
    private SpectrumAnalyser  audioAnalyser;

    private SoundBiteVariables vars;
} 

