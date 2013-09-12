package shaper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Class for mapping frequency/intensity to a colour.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 22.08.2013: Created
 */
public class ImageColourMapper implements ColourMapper 
{
    public ImageColourMapper(File mapFile) throws IOException
    {
        mapImage = ImageIO.read(mapFile);
        if ( mapImage == null )
        {
            throw new IOException("Invalid image type");
        }
        w = mapImage.getWidth() - 1;
        h = mapImage.getHeight() - 1;
    }
    
    @Override
    public int mapSpectrum(float[] spectrum, int idx)
    {
        int y = idx * w / spectrum.length;
        int x = (int) (Math.max(0, Math.min(1.0, spectrum[idx])) * h);
        return mapImage.getRGB(x, y);
    }
    
    private BufferedImage mapImage;
    private int w, h;
}
