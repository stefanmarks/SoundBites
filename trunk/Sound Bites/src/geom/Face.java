package geom;

import java.io.PrintWriter;
import javax.media.opengl.GL2;

/**
 * Interface for generic 3D faces (e.g., triangles, quads, ...)
 * 
 * @author  Stefan Marks
 * @version 1.0 - 15.08.2013: Created
 */
public interface Face 
{
    public void update();
    public void render(GL2 gl);
    public void writeSTL(PrintWriter w);
}
