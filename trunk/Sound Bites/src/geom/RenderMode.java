package geom;

/**
 * Render modes for the 3D objects.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 04.09.2013: Created
 */
public enum RenderMode 
{
    POINTS("Points"), WIREFRAME("Wireframe"), SOLID("Solid");
    
    private RenderMode(String name)
    {
        this.name = name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    private String name;
}
