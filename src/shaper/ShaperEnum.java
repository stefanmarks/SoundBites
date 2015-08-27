package shaper;

/**
 * Enumeration for the shaper selection choices.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 20.09.2013: Created
 */
public enum ShaperEnum 
{
    RING1(   new Shaper_Ring()),
    RING2(   new Shaper_RingOld()),
    SPHERE(  new Shaper_Sphere()),
    CYLINDER(new Shaper_Cylinder());

    
    private ShaperEnum(Shaper shaper)
    {
        this.shaper = shaper;
    }
    
    
    @Override
    public String toString()
    {
        return shaper.toString();
    }
    
    
    public Shaper getInstance()
    {
        return shaper;
    }
    
    
    private final Shaper shaper;
}
