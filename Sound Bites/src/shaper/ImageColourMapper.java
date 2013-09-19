package shaper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Class for mapping frequency/intensity to a colour.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 22.08.2013: Created
 */
public class ImageColourMapper implements ColourMapper 
{
    public static ColourMapper create(String name, String filename)
    {
        ColourMapper mapper = null;
        try
        {
            InputStream is = System.class.getResourceAsStream("/resources/colourmaps/" + filename);
            mapper = new ImageColourMapper(name, is);
        }
        catch (IOException e)
        {
            System.err.println("Could not load image colour map " + filename + ".");
        }
        return mapper;
    }
    
    
    /**
     * Creates a new image colour mapper.
     * 
     * @param name      the name of the mapper
     * @param imgStream the stream to read the image from
     * @throws IOException when the stream cannot be read
     */
    private ImageColourMapper(String name, InputStream imgStream) throws IOException
    {
        mapImage = ImageIO.read(imgStream);
        if ( mapImage == null )
        {
            throw new IOException("Invalid image type");
        }
        this.name = name;
        imgW = mapImage.getWidth() - 1;
        imgH = mapImage.getHeight() - 1;
    }
    
    
    @Override
    public int mapSpectrum(float[] spectrum, int idx)
    {
        int y = idx * imgW / spectrum.length;
        int x = (int) (Math.max(0, Math.min(1.0, spectrum[idx])) * imgH);
        return mapImage.getRGB(x, y);
    }
        
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    private String        name;
    private BufferedImage mapImage;
    private int           imgW, imgH;
}
