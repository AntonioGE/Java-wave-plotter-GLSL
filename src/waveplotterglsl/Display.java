/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waveplotterglsl;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;
import waveplotterglsl.utils.GlUtils;

/**
 *
 * @author ANTONIO
 */
public class Display extends GLJPanel implements GLEventListener,
        MouseWheelListener, MouseListener, MouseMotionListener {

    private float xMouse, yMouse;
    
    private int vao[] = new int[1];
    private int vbo[] = new int[1];

    private FPSAnimator animtr;

    private float[] positions = new float[]{
        -1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f

    };

    private int shaderProgram;
    private float zoom = 100.0f;

    private float dt = 0.5f;

    private static final int MAX_NUM_POINTS = 10;
    private int numPoints = 0;
    private float[] pts = new float[MAX_NUM_POINTS * 2];
    private int[] ptsEnabled = new int[MAX_NUM_POINTS];
    private float[] t = new float[MAX_NUM_POINTS];

    public Display() {
        addGLEventListener(this);
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glClearColor(0.5f, 0.0f, 1.0f, 1.0f);

        shaderProgram = GlUtils.createShaderProgram(gl, "shaders/vert.shader", "shaders/frag.shader");

        GlUtils.printProgramLog(shaderProgram);

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(positions);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vBuf.limit() * Float.BYTES, vBuf, GL4.GL_STATIC_DRAW);

        GlUtils.checkOpenGLError();

        animtr = new FPSAnimator(this, 30);
        animtr.start();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);

        int uniformWindowSize = gl.glGetUniformLocation(shaderProgram, "windowSize");
        gl.glUniform2f(uniformWindowSize, this.getWidth(), this.getHeight());

        int uniformZoom = gl.glGetUniformLocation(shaderProgram, "zoom");
        gl.glUniform1f(uniformZoom, zoom);

        int uniformMouse = gl.glGetUniformLocation(shaderProgram, "mouse");
        gl.glUniform2f(uniformMouse, xMouse / this.getWidth(), -yMouse / this.getHeight());

        int uniformT = gl.glGetUniformLocation(shaderProgram, "t");
        gl.glUniform1fv(uniformT, t.length, t, 0);

        int uniformNumPoints = gl.glGetUniformLocation(shaderProgram, "num_pts");
        gl.glUniform1i(uniformNumPoints, numPoints);

        int uniformPts = gl.glGetUniformLocation(shaderProgram, "pts");
        gl.glUniform2fv(uniformPts, pts.length, pts, 0);

        int uniformPtsEnabled = gl.glGetUniformLocation(shaderProgram, "pts_enabled");
        gl.glUniform2iv(uniformPtsEnabled, ptsEnabled.length, ptsEnabled, 0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // adjust OpenGL settings and draw model
        gl.glDrawArrays(GL4.GL_QUADS, 0, positions.length / 3);

        GlUtils.checkOpenGLError();

        for (int i = 0; i < numPoints; i++) {
            t[i] += dt;
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            zoom *= 1.1f;
        } else {
            zoom /= 1.1f;
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (numPoints < MAX_NUM_POINTS) {
            pts[numPoints * 2] = (e.getX() / (float) getWidth() - 0.5f) * zoom;
            pts[numPoints * 2 + 1] = (-e.getY() / (float) getHeight() + 0.5f) * zoom;
            ptsEnabled[numPoints] = 1;
            numPoints++;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

}
