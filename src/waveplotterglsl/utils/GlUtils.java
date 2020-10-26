/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waveplotterglsl.utils;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;
import java.util.Scanner;
import java.util.Vector;
import waveplotterglsl.MainFrame;

/**
 *
 * @author ANTONIO
 */
public class GlUtils {

    private static String[] readShaderAsResource(String filename) {
        Vector lines = new Vector();
        Scanner sc;
        sc = new Scanner(MainFrame.class.getClassLoader().getResourceAsStream(filename));

        while (sc.hasNext()) {
            lines.addElement(sc.nextLine());
        }

        String[] program = new String[lines.size()];

        for (int i = 0; i < lines.size(); ++i) {
            program[i] = (String) lines.elementAt(i) + "\n";
        }

        sc.close();
        return program;
    }

    public static int createShaderProgram(GL4 gl, String vertPath, String fragPath) {
        String vshaderSource[];
        String fshaderSource[];

        int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);

        vshaderSource = readShaderAsResource(vertPath);
        fshaderSource = readShaderAsResource(fragPath);

        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

        gl.glCompileShader(vShader);
        gl.glCompileShader(fShader);
        
        printShaderLog(vShader);
        printShaderLog(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    public static void printShaderLog(int shader) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;
        // determine the length of the shader compilation log
        gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0) {
            log = new byte[len[0]];
            gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
            System.out.println("Shader Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
            System.out.println();
        }
    }

    public static void printProgramLog(int prog) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;
        // determine the length of the program linking log
        gl.glGetProgramiv(prog, GL4.GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0) {
            log = new byte[len[0]];
            gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
            System.out.println("Program Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
            System.out.println();
        }
    }

    public static boolean checkOpenGLError() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        boolean foundError = false;
        GLU glu = new GLU();
        int glErr = gl.glGetError();
        while (glErr != GL4.GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl.glGetError();
        }
        return foundError;
    }
}
