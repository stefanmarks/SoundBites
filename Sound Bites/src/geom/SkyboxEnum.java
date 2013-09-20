package geom;

/**
 * Enumeration for the skybox selection choices.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 20.09.2013: Created
 */
public enum SkyboxEnum 
{
    BLACK(    "Black",     null), 
    HEXSPHERE("HexSphere", "SkyboxHexSphere_PoT.jpg"), 
    PLANE(    "Plane",     "SkyboxGridPlane_PoT.jpg");
    
    
    private SkyboxEnum(String name, String resource)
    {
        this.name = name;
        skybox = (resource != null) ? new Skybox("/resources/skyboxes/" + resource, 2000) : null;
    }
    
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    public Skybox getSkybox()
    {
        return skybox;
    };
    
    private String name;
    private Skybox skybox;
}
