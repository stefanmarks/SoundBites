package geom;

import static geom.RenderMode.POINTS;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL2;
import processing.core.PVector;

/**
 * Class for managing vertices, normals, and triangles for a surface
 * 
 * @author  Stefan Marks
 * @version 1.0 - 15.08.2013: Created
 */
public class Surface 
{
    public Surface(int xRes, int yRes) throws IllegalArgumentException
    {
        if ( (xRes < 2) || (yRes < 2) )
        {
            throw new IllegalArgumentException("X and Y resolution must be > 1");
        }
        
        this.xRes = xRes;
        this.yRes = yRes;
        
        int count = xRes * yRes;
        vertices = new PVector[count];
        colours  = new int[count];
        for ( int i = 0; i < vertices.length; i++ )
        {
            vertices[i] = new PVector();   
            colours[i]  = 0xFFFFFFFF;
        }
        
        connectedFaces = new LinkedList[count];
        for ( int i = 0; i < count ; i++ )
        {
            connectedFaces[i] = new LinkedList<Face>();            
        }
        
        modifiedFaces = new HashSet<Face>();
        normals  = new ArrayList(xRes * yRes / 3);
        faces    = new ArrayList<Face>();
        
        renderMode = RenderMode.SOLID;
    }
    
    public int getXSize()
    {
        return xRes;
    }
    
    public int getYSize()
    {
        return yRes;
    }
    
    private int getVertexIdx(int x, int y) throws IllegalArgumentException
    {
        if ( (x < 0) || (x >= xRes) )
        {
            throw new IllegalArgumentException("Invalid X surface vertex index (" + x + ")");
        }
        if ( (y < 0) || (y >= yRes) )
        {
            throw new IllegalArgumentException("Invalid Y surface vertex index (" + y + ")");
        }
        
        return x + y * xRes;
    }
    
    public PVector getVertex(int x, int y) throws IllegalArgumentException
    {
        int idx = getVertexIdx(x, y);
        PVector vec = vertices[idx];
        if ( vec == null )
        {
            vec = new PVector();
            vertices[idx] = vec;
        }
        return vec;
    }
    
    public PVector modifyVertex(int x, int y) throws IllegalArgumentException
    {
        int idx = getVertexIdx(x, y);
        PVector vec = vertices[idx];
        if ( vec == null )
        {
            vec = new PVector();
            vertices[idx] = vec;
        }
        for ( Face face : connectedFaces[idx] )
        {
            modifiedFaces.add(face);
        }
        return vec;
    }
    
    public void setVertex(int x, int y, PVector v) throws IllegalArgumentException
    {
        int idx = getVertexIdx(x, y);
        PVector vec = vertices[idx];
        if ( vec == null )
        {
            vec = new PVector();
            vertices[idx] = vec;
        }
        vec.set(v);
        for ( Face face : connectedFaces[idx] )
        {
            modifiedFaces.add(face);
        }
    }
    
    public void setVertexColour(int x, int y, int rgb) throws IllegalArgumentException
    {
        int idx = getVertexIdx(x, y);
        colours[idx] = rgb;
    }
    
    public void addTriangle(int x0, int y0, int x1, int y1, int x2, int y2)
    {
        int idx1 = getVertexIdx(x0, y0);
        int idx2 = getVertexIdx(x1, y1);
        int idx3 = getVertexIdx(x2, y2);
        PVector n = new PVector();
        Face    f = new Triangle(idx1, idx2, idx3, n);
        normals.add(n);
        faces.add(f);
        connectedFaces[idx1].add(f);
        connectedFaces[idx2].add(f);
        connectedFaces[idx3].add(f);
    }
    
    public void setRenderMode(RenderMode mode)
    {
        this.renderMode = mode;
    }
    
    public RenderMode getRenderMode()
    {
        return renderMode;
    }
    
    public void update()
    {
        for ( Face face : modifiedFaces )
        {
            face.update();
        }
        modifiedFaces.clear();
    }
    
    public List<PVector> getVertices()
    {
        return Arrays.asList(vertices);
    }
    
    public int[] getColours()
    {
        return colours;
    }

    public List<Face> getFaces()
    {
        return Collections.unmodifiableList(faces);
    }

    public void render(GL2 gl)
    {
        switch ( renderMode )
        {
            case POINTS    : renderPoints(gl); break;
            case WIREFRAME : renderWireframe(gl); break;
            default        : renderSolid(gl); break;
        }
    }

    private void renderPoints(GL2 gl)
    {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_POINT_SIZE);
        gl.glPointSize(2.0f);
        gl.glBegin(GL2.GL_POINTS);
        for ( int i = 0 ; i < vertices.length ; i++  )
        {
            int colour = colours[i];
            gl.glColor4ub((byte) ((colour >> 16) & 0xFF), (byte) ((colour >> 8) & 0xFF), (byte) (colour & 0xFF), (byte) ((colour >> 24) & 0xFF));
            PVector v = vertices[i];
            gl.glVertex3f(v.x, v.y, v.z);
        }
        gl.glEnd();
    }

    private void renderWireframe(GL2 gl)
    {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for ( int i = 0 ; i < vertices.length ; i++  )
        {
            int colour = colours[i];
            gl.glColor4ub((byte) ((colour >> 16) & 0xFF), (byte) ((colour >> 8) & 0xFF), (byte) (colour & 0xFF), (byte) ((colour >> 24) & 0xFF));
            PVector v = vertices[i];
            gl.glVertex3f(v.x, v.y, v.z);
        }
        gl.glEnd();
    }

    private void renderSolid(GL2 gl)
    {
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glBegin(GL2.GL_TRIANGLES);
        for ( Face f : faces)
        {
            f.render(gl);
        }
        gl.glEnd();
    }

    public void writeSTL(PrintWriter w)
    {
        // write header
        w.println("solid Surface");
        // draw the surface
        for ( Face f : faces)
        {
            f.writeSTL(w);
        }
        // finish file
        w.println("endsolid");
    }
    
    private class Triangle implements Face
    {
        public Triangle(int idx1, int idx2, int idx3, PVector n)
        {
            this.idx1 = idx1;
            this.idx2 = idx2;
            this.idx3 = idx3;
            this.n = n;
            update();
        }

        @Override
        public void update()
        {
            PVector tmp1 = PVector.sub(vertices[idx3], vertices[idx1]); // t1 = v3-v1
            PVector tmp2 = PVector.sub(vertices[idx2], vertices[idx1]); // t2 = v2-v1
            n.set(tmp1.cross(tmp2));            // n = t1 x t2
            n.normalize();
        }

        @Override
        public void render(GL2 gl)
        {
            gl.glNormal3f(n.x, n.y, n.z);
            int colour = colours[idx1];
            gl.glColor4ub((byte) ((colour >> 16) & 0xFF), (byte) ((colour >> 8) & 0xFF), (byte) (colour & 0xFF), (byte) ((colour >> 24) & 0xFF));
            gl.glVertex3f(vertices[idx1].x, vertices[idx1].y, vertices[idx1].z);
            colour = colours[idx2];
            gl.glColor4ub((byte) ((colour >> 16) & 0xFF), (byte) ((colour >> 8) & 0xFF), (byte) (colour & 0xFF), (byte) ((colour >> 24) & 0xFF));
            gl.glVertex3f(vertices[idx2].x, vertices[idx2].y, vertices[idx2].z);
            colour = colours[idx3];
            gl.glColor4ub((byte) ((colour >> 16) & 0xFF), (byte) ((colour >> 8) & 0xFF), (byte) (colour & 0xFF), (byte) ((colour >> 24) & 0xFF));
            gl.glVertex3f(vertices[idx3].x, vertices[idx3].y, vertices[idx3].z);
        }

        @Override
        public void writeSTL(PrintWriter w)
        {
            w.println("facet normal " + n.x + " " + n.y + " " + n.z);
            w.println("outer loop");
            w.println("vertex " + vertices[idx1].x + " " + vertices[idx1].y + " " + vertices[idx1].z);
            w.println("vertex " + vertices[idx2].x + " " + vertices[idx2].y + " " + vertices[idx2].z);
            w.println("vertex " + vertices[idx3].x + " " + vertices[idx3].y + " " + vertices[idx3].z);
            w.println("endloop");
            w.println("endfacet");
        }
        
        private int idx1, idx2, idx3;
        private PVector n;
    }

    
    private int xRes, yRes;
    private PVector[]           vertices;
    private int[]               colours;
    private List<Face>[]        connectedFaces;
    private Set<Face>           modifiedFaces;
    private ArrayList<PVector>  normals;
    private ArrayList<Face>     faces;
    private RenderMode          renderMode;
}
