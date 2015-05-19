package pr03;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.GL2ES2.GL_COMPILE_STATUS;
import static javax.media.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static javax.media.opengl.GL2ES2.GL_LINK_STATUS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Adam Jurcik <xjurc@fi.muni.cz>
 */
public class Scene implements GLEventListener {
    
    private GLU glu = new GLU();
    private GLUT glut = new GLUT();
    private FPSAnimator animator;
    
    private int mode = 2;
    private int polygonModes[] = { GL_POINT, GL_LINE, GL_FILL };
    
    private float time;
    private boolean userRotates = false;
    
    // GLSL program
    private int program = 0;
    
    // Display lists
    private int cube = 0;
    private int teapot = 0;
    private int skybox = 0;
    
    private float rotX = 0.0f;
    private float rotY = 0.0f;
    
    private static final float[] BLACK = { 0.0f, 0.0f, 0.0f, 1.0f };
    private static final float[] WHITE = { 1.0f, 1.0f, 1.0f, 1.0f };
    
    private static final float VERTICES[] = {
        // TexCoord ........ Normal ............. Position ............. Tangent ............ Bitangent ...
        // Front face
        0.0f, 1.0f,     0.0f, 0.0f, 1.0f,    -1.0f, 1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        0.0f, 0.0f,     0.0f, 0.0f, 1.0f,    -1.0f,-1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        1.0f, 0.0f,     0.0f, 0.0f, 1.0f,     1.0f,-1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        1.0f, 1.0f,     0.0f, 0.0f, 1.0f,     1.0f, 1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        // Right face
        0.0f, 1.0f,     1.0f, 0.0f, 0.0f,     1.0f, 1.0f, 1.0f,     0.0f, 0.0f, -1.0f,    0.0f, 1.0f, 0.0f,
        0.0f, 0.0f,     1.0f, 0.0f, 0.0f,     1.0f,-1.0f, 1.0f,     0.0f, 0.0f, -1.0f,    0.0f, 1.0f, 0.0f,
        1.0f, 0.0f,     1.0f, 0.0f, 0.0f,     1.0f,-1.0f,-1.0f,     0.0f, 0.0f, -1.0f,    0.0f, 1.0f, 0.0f,
        1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     1.0f, 1.0f,-1.0f,     0.0f, 0.0f, -1.0f,    0.0f, 1.0f, 0.0f,
        // Back face
        0.0f, 1.0f,     0.0f, 0.0f,-1.0f,     1.0f, 1.0f,-1.0f,    -1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        0.0f, 0.0f,     0.0f, 0.0f,-1.0f,     1.0f,-1.0f,-1.0f,    -1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        1.0f, 0.0f,     0.0f, 0.0f,-1.0f,    -1.0f,-1.0f,-1.0f,    -1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        1.0f, 1.0f,     0.0f, 0.0f,-1.0f,    -1.0f, 1.0f,-1.0f,    -1.0f, 0.0f, 0.0f,     0.0f, 1.0f, 0.0f,
        // Left face
        0.0f, 1.0f,    -1.0f, 0.0f, 0.0f,    -1.0f, 1.0f,-1.0f,     0.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f,
        0.0f, 0.0f,    -1.0f, 0.0f, 0.0f,    -1.0f,-1.0f,-1.0f,     0.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f,
        1.0f, 0.0f,    -1.0f, 0.0f, 0.0f,    -1.0f,-1.0f, 1.0f,     0.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f,
        1.0f, 1.0f,    -1.0f, 0.0f, 0.0f,    -1.0f, 1.0f, 1.0f,     0.0f, 0.0f, 1.0f,     0.0f, 1.0f, 0.0f,
        // Top face
        0.0f, 1.0f,     0.0f, 1.0f, 0.0f,    -1.0f, 1.0f,-1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, -1.0f,
        0.0f, 0.0f,     0.0f, 1.0f, 0.0f,    -1.0f, 1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, -1.0f,
        1.0f, 0.0f,     0.0f, 1.0f, 0.0f,     1.0f, 1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, -1.0f,
        1.0f, 1.0f,     0.0f, 1.0f, 0.0f,     1.0f, 1.0f,-1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, -1.0f,
        // Bottom face
        0.0f, 1.0f,     0.0f,-1.0f, 0.0f,    -1.0f,-1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, 1.0f,
        0.0f, 0.0f,     0.0f,-1.0f, 0.0f,    -1.0f,-1.0f,-1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, 1.0f,
        1.0f, 0.0f,     0.0f,-1.0f, 0.0f,     1.0f,-1.0f,-1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, 1.0f,
        1.0f, 1.0f,     0.0f,-1.0f, 0.0f,     1.0f,-1.0f, 1.0f,     1.0f, 0.0f, 0.0f,     0.0f, 0.0f, 1.0f,
    };
    
    public Scene(FPSAnimator animator) {
        this.animator = animator;
    }
    
    public void togglePolygonMode() {
        mode = (++mode) % 3;
    }
    
    @Override
    public void init(GLAutoDrawable glad) {
        // Get GL2 interface
        GL2 gl = glad.getGL().getGL2();
        
        // Enable important features
	gl.glEnable(GL_DEPTH_TEST);
	gl.glEnable(GL_LIGHTING);
	gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_CULL_FACE);
        
        try {
            program = loadProgram(gl, "/resources/shaders/vs.glsl", "/resources/shaders/fs.glsl");
        } catch (IOException e) {
            System.err.println("Resource loading failed. " + e.getMessage());
            System.exit(1);
        }
        
        // Loading resources (models, textures, shaders)
        initBumpMapping(gl);
        //initEnvirontmentMapping(gl);
        //initToonShading(gl);
    }
    
    private void initBumpMapping(GL2 gl) {
        try {
            Texture fieldstoneDiffuse = loadTexture(gl, "/resources/textures/fieldstone.png", TextureIO.PNG);
            Texture fieldstoneNormal = loadTexture(gl, "/resources/textures/fieldstoneBumpDOT3.png", TextureIO.PNG);
            cube = createCubeForBumpMapping(gl, fieldstoneDiffuse, fieldstoneNormal);
        } catch (IOException e) {
            System.err.println("Resource loading failed. " + e.getMessage());
            System.exit(1);
        }
        // Set uniforms to used program
        gl.glUseProgram(program);
        // Sampler 'rocks_color_tex' is used in the shader, bind it to the texture unit 0
        int rocksColorTexLoc = gl.glGetUniformLocation(program, "rocks_color_tex");
        gl.glUniform1i(rocksColorTexLoc, 0);
        // Sampler 'rocks_normal_tex' is used in the shader, bind it to the texture unit 1
        int rocksNormalTexLoc = gl.glGetUniformLocation(program, "rocks_normal_tex");
        gl.glUniform1i(rocksNormalTexLoc, 1);
        // Set fixed-function pipeline
        gl.glUseProgram(0);
    }
    
    private void initEnvirontmentMapping(GL2 gl) {
        try {
            String filenames[] = {
                "/resources/textures/skybox0.png",
                "/resources/textures/skybox1.png",
                "/resources/textures/skybox2.png",
                "/resources/textures/skybox3.png",
                "/resources/textures/skybox4.png",
                "/resources/textures/skybox5.png"
            };
            Texture skyboxTex = loadTextureCube(gl, filenames, TextureIO.PNG);
            teapot = createTeapot(gl);
            skybox = createSkybox(gl, skyboxTex);
        } catch (IOException e) {
            System.err.println("Resource loading failed. " + e.getMessage());
            System.exit(1);
        }
        // Set uniforms to used program
        gl.glUseProgram(program);
        // Sampler 'skybox_tex' is used in the shader, bind it to the texture unit 0
        int skyboxTexLoc = gl.glGetUniformLocation(program, "skybox_tex");
        gl.glUniform1i(skyboxTexLoc, 0);
        // Set fixed-function pipeline
        gl.glUseProgram(0);
    }
    
    private void initToonShading(GL2 gl) {
        teapot = createTeapot(gl);
    }
    
    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        // Get GL2 interface
        GL2 gl = glad.getGL().getGL2();
        
        if (animator.isAnimating() && !userRotates) {
            time += 0.1f;
        }
        
        // Clear buffers
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, polygonModes[mode]);

	// Set look at matrix
	gl.glLoadIdentity();
        
        // Setup the light
        float lightPosition[] = { 2.0f, 2.0f, 2.0f, 1.0f };
        float lightAmbient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        float lightDiffuse[] = { 0.8f, 0.8f, 0.8f, 1.0f };
	gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);
	gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse, 0);
	gl.glLightfv(GL_LIGHT0, GL_SPECULAR, WHITE, 0);
        
        glu.gluLookAt(0.0f, 0.0f, 5.0f,
                0.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f);
        
        // Rotate the object
        gl.glRotatef(rotX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotY, 0.0f, 1.0f, 0.0f);
        
	gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, WHITE, 0);
	gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, WHITE, 0);
	gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 60.0f);
        
	displayCubeForBumpMapping(gl);
        //displayTeapotForEnvironmentMapping(gl);
        //displayTeapotForToonShading(gl);
        
        // INFO: This is needed due to bug in JOGL
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }
    
    private void displayCubeForBumpMapping(GL2 gl) {
        // Rotate object
        gl.glRotatef(10.0f * time, 1.0f, 0.0f, 0.0f);
	gl.glRotatef(25.0f * time, 0.0f, 1.0f, 0.0f);
	gl.glRotatef(35.0f * time, 0.0f, 0.0f, 1.0f);
        // Enable GLSL program for rendering
	gl.glUseProgram(program);
        // Draw cube
	gl.glCallList(cube);
        // Disable GLSL program, use fixed-function pipeline
        gl.glUseProgram(0);
    }

    private void displayTeapotForEnvironmentMapping(GL2 gl) {
        // Draw skybox
        gl.glCallList(skybox);
        // Rotate object
        gl.glRotatef(10.0f * time, 1.0f, 0.0f, 0.0f);
	gl.glRotatef(25.0f * time, 0.0f, 1.0f, 0.0f);
	gl.glRotatef(35.0f * time, 0.0f, 0.0f, 1.0f);
        // Enable GLSL program for rendering
	gl.glUseProgram(program);
        // Draw teapot
	gl.glFrontFace(GL_CW);
	gl.glCallList(teapot);
        // INFO: This is needed due to bug in JOGL
        gl.glFrontFace(GL_CCW);
        // Disable GLSL program, use fixed-function pipeline
        gl.glUseProgram(0);
    }
    
    private void displayTeapotForToonShading(GL2 gl) {
        // Rotate object
        gl.glRotatef(10.0f * time, 1.0f, 0.0f, 0.0f);
	gl.glRotatef(25.0f * time, 0.0f, 1.0f, 0.0f);
	gl.glRotatef(35.0f * time, 0.0f, 0.0f, 1.0f);
        // Enable GLSL program for rendering
        gl.glUseProgram(program);
        // Draw teapot
        gl.glFrontFace(GL_CW);
	gl.glCallList(teapot);
        // INFO: This is needed due to bug in JOGL
        gl.glFrontFace(GL_CCW);
        // Disable GLSL program, use fixed-function pipeline
        gl.glUseProgram(0);
    }
    
    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        // Get GL2 interface
        GL2 gl = glad.getGL().getGL2();

        // Use projection matrix
        gl.glMatrixMode(GL_PROJECTION);

        // Set up perspective projection matrix
        gl.glLoadIdentity();
        glu.gluPerspective(60, ((double) width) / height, 1.0, 1000.0);

        // Part of the image where the scene will be renderer, (0, 0) is bottom left
        gl.glViewport(0, 0, width, height);

        // Use model view matrix
        gl.glMatrixMode(GL_MODELVIEW);
    }

    public void addPitch(float angle) {
        rotX += angle;
    }

    public void addYaw(float angle) {
        rotY += angle;
    }
    
    public void setUserRotates(boolean userRotates) {
        this.userRotates = userRotates;
    }

    private int createCubeForBumpMapping(GL2 gl, Texture diffuse, Texture normalmap) throws IOException {       
        int id = gl.glGenLists(1);
        gl.glNewList(id, GL_COMPILE);
        
        FloatBuffer texcoords = GLBuffers.newDirectFloatBuffer(VERTICES);
        FloatBuffer normals = GLBuffers.newDirectFloatBuffer(VERTICES, 2);
        FloatBuffer positions = GLBuffers.newDirectFloatBuffer(VERTICES, 5);
        FloatBuffer tangents = GLBuffers.newDirectFloatBuffer(VERTICES, 8);
        FloatBuffer bitangents = GLBuffers.newDirectFloatBuffer(VERTICES, 11, VERTICES.length - 11);
        
        // Cube data is stored in one interleaved array
        // Enable needed arrays (texture, normal, vertex, tangent, bitangent)
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL_FLOAT, 14 * GLBuffers.SIZEOF_FLOAT, positions);
        gl.glEnableClientState(GL_NORMAL_ARRAY);
	gl.glNormalPointer(GL_FLOAT, 14 * GLBuffers.SIZEOF_FLOAT, normals);
        gl.glClientActiveTexture(GL_TEXTURE0);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	gl.glTexCoordPointer(2, GL_FLOAT, 14 * GLBuffers.SIZEOF_FLOAT, texcoords);
        // Tangents are set as the second texture unit multi-texture coordinates
        gl.glClientActiveTexture(GL_TEXTURE1);
	gl.glTexCoordPointer(3, GL_FLOAT, 14 * GLBuffers.SIZEOF_FLOAT, tangents);
	gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        // Bitangents are set as the third texture unit multi-texture coordinates
        gl.glClientActiveTexture(GL_TEXTURE2);
	gl.glTexCoordPointer(3, GL_FLOAT, 14 * GLBuffers.SIZEOF_FLOAT, bitangents);
	gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        // Bind wood (unit 0) and rock (unit 1) textures
        gl.glActiveTexture(GL_TEXTURE0);
	diffuse.bind(gl);
	gl.glActiveTexture(GL_TEXTURE1);
	normalmap.bind(gl);
        
        gl.glDrawArrays(GL_QUADS, 0, 24);
        
	gl.glDisableClientState(GL_VERTEX_ARRAY);
	gl.glDisableClientState(GL_NORMAL_ARRAY);
	gl.glClientActiveTexture(GL_TEXTURE2);
	gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	gl.glClientActiveTexture(GL_TEXTURE1);
	gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	gl.glClientActiveTexture(GL_TEXTURE0);
	gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        
        gl.glEndList();
        
        return id;
    }
    
    private int createTeapot(GL2 gl) {
        int list = gl.glGenLists(1);
        gl.glNewList(list, GL_COMPILE);

        // Draw teapot
        glut.glutSolidTeapot(1.0);
        
        gl.glEndList();
        
        return list;
    }
    
    private int createSkybox(GL2 gl, Texture skybox) {
        int list = gl.glGenLists(1);
        gl.glNewList(list, GL_COMPILE);
        
        gl.glActiveTexture(GL_TEXTURE0);
        skybox.bind(gl);

        // Draw skybox
        gl.glEnable(GL_TEXTURE_CUBE_MAP);
        // Do not use lighting
        gl.glDisable(GL_LIGHTING);
        gl.glDisable(GL_CULL_FACE);
        
        FloatBuffer positions = GLBuffers.newDirectFloatBuffer(VERTICES, 5);
        
        // When rendering skybox with cube texture, texture coordinates can be the same as vertex coordinates
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL_FLOAT, 14 * GLBuffers.SIZEOF_FLOAT, positions);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(3, GL_FLOAT, 14 * GLBuffers.SIZEOF_FLOAT, positions);
        
        // Draw skybox 5x larger
        gl.glPushMatrix();
        gl.glScalef(5.0f, 5.0f, 5.0f);
        gl.glDrawArrays(GL_QUADS, 0, 24);
        gl.glPopMatrix();

        gl.glDisableClientState(GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_CULL_FACE);
        
        gl.glDisable(GL_TEXTURE_CUBE_MAP);

        gl.glEndList();
        
        return list;
    }
    
    private int loadShader(GL2 gl, String filename, int shaderType) throws IOException {
        String source = readSource(getClass().getResourceAsStream(filename));
        int shader = gl.glCreateShader(shaderType);
        
        // Create and compile GLSL shader
        gl.glShaderSource(shader, 1, new String[] { source }, new int[] { source.length() }, 0);
        gl.glCompileShader(shader);
        
        // Check GLSL shader compile status
        int[] status = new int[1];
        gl.glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if (status[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, length, 0);
            
            byte[] log = new byte[length[0]];
            gl.glGetShaderInfoLog(shader, length[0], length, 0, log, 0);
            
            String error = new String(log, 0, length[0]);
            System.err.println(error);
        }
        
        return shader;
    }
    
    private int loadProgram(GL2 gl, String vertexShaderFN, String fragmentShaderFN) throws IOException {
        // Load frament and vertex shaders (GLSL)
	int vs = loadShader(gl, vertexShaderFN, GL_VERTEX_SHADER);
	int fs = loadShader(gl, fragmentShaderFN, GL_FRAGMENT_SHADER);
        
	// Create GLSL program, attach shaders and compile it
	int program = gl.glCreateProgram();
	gl.glAttachShader(program, vs);
	gl.glAttachShader(program, fs);
	gl.glLinkProgram(program);
        
        int[] linkStatus = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, length, 0);
            
            byte[] log = new byte[length[0]];
            gl.glGetProgramInfoLog(program, length[0], length, 0, log, 0);
            
            String error = new String(log, 0, length[0]);
            System.err.println(error);
        }
        
        return program;
    }
    
    private Texture loadTexture(GL2 gl, String filename, String suffix) throws IOException {
        try (InputStream is = Scene.class.getResourceAsStream(filename)) {
            Texture tex =  TextureIO.newTexture(is, true, suffix);
            // Set texture filters
            tex.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            tex.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            // Set texture coordinates wrap mode
            tex.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
            tex.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
            // Unbind texture
            gl.glBindTexture(GL_TEXTURE_2D, 0);
            return tex;
        }
    }
    
    private Texture loadTextureCube(GL2 gl, String[] filenames, String suffix) throws IOException {
        Texture cubemap = TextureIO.newTexture(GL_TEXTURE_CUBE_MAP);
        // Load skybox images
        TextureData skybox[] = new TextureData[6];
        for (int index = 0; index < 6; index++) {
            try (InputStream is = Scene.class.getResourceAsStream(filenames[index])) {
                skybox[index] = TextureIO.newTextureData(gl.getGLProfile(), is, false, suffix);
            }
        }
        // Copy skybox images into texture
        cubemap.updateImage(gl, skybox[1], GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
        cubemap.updateImage(gl, skybox[3], GL_TEXTURE_CUBE_MAP_POSITIVE_X);
        cubemap.updateImage(gl, skybox[0], GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
        cubemap.updateImage(gl, skybox[5], GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
        cubemap.updateImage(gl, skybox[2], GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
        cubemap.updateImage(gl, skybox[4], GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
        // Set texture parameters
        cubemap.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        cubemap.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        cubemap.setTexParameteri(gl, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE); 
        cubemap.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        cubemap.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        
        return cubemap;
    }
    
    private String readSource(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        
        int c;
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
        }
        
        return sb.toString();
    }
    
}
