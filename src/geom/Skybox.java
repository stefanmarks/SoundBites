package geom;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import javax.media.opengl.GL2;

/**
 * Class for skyboxes consisting of 6 textures.
 * (Order: Front, Left, Back, Right, Up, Down)
 * 
 * @author  Stefan Marks
 * @version 1.0 - 04.09.2013: Created
 */
public class Skybox 
{
    
    public Skybox(String location, float size) 
    {
        this.size = size;
        textures  = new Texture[6];
        texCoords = new Rectangle2D.Float[6];
        
        try
        {
            InputStream is = System.class.getResourceAsStream(location);
//            if ( f.isDirectory() )
//            {
//                for ( int i = 0 ; i < 6 ; i++ )
//                {
//                    Texture tex = TextureIO.newTexture(
//                            new File(location + "/" + FILENAMES[i] + ".jpg"), false);
//                    textures[i] = tex;
//                    TextureCoords c = tex.getImageTexCoords();
//                    texCoords[i] = new Rectangle2D.Float();
//                    texCoords[i].setRect(c.left(), c.bottom(), c.right(), c.top());
//                }
//            }
//            else
//            {
                // Single file: load as Blender Environment Map
                // 3x2 Tiles, L B R
                //            D U F
                Texture tex = TextureIO.newTexture(is, false, "test");
                for ( int i = 0 ; i < 6 ; i++ )
                {
                    textures[i]  = tex;
                    texCoords[i] = new Rectangle2D.Float();
                }
                TextureCoords c = tex.getImageTexCoords();
                mapRect(c, 2f/3f, 0.5f, texCoords[0]);
                mapRect(c,    0f,   0f, texCoords[1]);
                mapRect(c, 1f/3f,   0f, texCoords[2]);
                mapRect(c, 2f/3f,   0f, texCoords[3]);
                mapRect(c, 1f/3f, 0.5f, texCoords[4]);
                mapRect(c,    0f, 0.5f, texCoords[5]);
//            }
        }
        catch ( Exception e )
        {
            System.err.println("Could not load skybox '" + location + "'.");
        }
    }
    
    private void mapRect(TextureCoords tc, float x, float y, Rectangle2D.Float rect)
    {
        float l = tc.left() + (tc.right() - tc.left()) *  x;
        float r = tc.left() + (tc.right() - tc.left()) * (x + (1 / 3.0f));
        float t = tc.top() + (tc.bottom() - tc.top())  *  y;
        float b = tc.top() + (tc.bottom() - tc.top())  *  (y + 0.5f);
        rect.setRect(l, b, r, t);
    }
    
    public void render(GL2 gl)
    {
        if ( textures[0] == null ) return;
        
        gl.glPushMatrix();
                
        // Enable/Disable features
        gl.glPushAttrib(GL2.GL_ENABLE_BIT);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_BLEND);
        gl.glColor4f(1, 1, 1, 1);
        
        // front
        textures[0].bind(gl);
        renderRect(gl, 0);
        // left
        gl.glRotatef(90, 0, 1, 0);
        textures[1].bind(gl);
        renderRect(gl, 1);
        // back
        gl.glRotatef(90, 0, 1, 0);
        textures[2].bind(gl);
        renderRect(gl, 2);
        // right
        gl.glRotatef(90, 0, 1, 0);
        textures[3].bind(gl);
        renderRect(gl, 3);
        // up
        gl.glRotatef(90, 0, 1, 0);
        gl.glRotatef(90, 1, 0, 0);
        textures[4].bind(gl);
        renderRect(gl, 4);
        // down
        gl.glRotatef(180, 1, 0, 0);
        textures[5].bind(gl);
        renderRect(gl, 5);

        // Restore enable bits and matrix
        gl.glPopAttrib();
        gl.glPopMatrix();
    }
    
    private void renderRect(GL2 gl, int idx)
    {
        Rectangle2D.Float r = texCoords[idx];
        gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(r.x,          r.y); gl.glVertex3f(-size, -size, -size);
            gl.glTexCoord2f(r.width,      r.y); gl.glVertex3f( size, -size, -size);
            gl.glTexCoord2f(r.width, r.height); gl.glVertex3f( size,  size, -size);
            gl.glTexCoord2f(r.x,     r.height); gl.glVertex3f(-size,  size, -size);
        gl.glEnd();
    }
    
    // private final static String[] FILENAMES = { "front", "left", "back", "right", "up", "down" };

    private Texture[]           textures;
    private Rectangle2D.Float[] texCoords;
    private float               size;
}
