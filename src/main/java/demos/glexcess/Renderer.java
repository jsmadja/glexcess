package demos.glexcess;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

/**
 * GLExcess v1.0 Demo
 * Copyright (C) 2001-2003 Paolo Martella
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * @author Paolo "Bustard" Martella
 * @author Pepijn Van Eeckhoudt
 */
final class Renderer implements GLEventListener {
    private static final boolean loop = true;
    private float timing = 0;
    private float step = 1;
    private final Scene[] scenes = new Scene[]{
        new Scene1(),
        new Scene2(),
        new Scene3(),
        new Scene4(),
        new Scene5(),
        new Scene6(),
        new Scene7(),
        new Scene8(),
        new Scene9(),
        new Scene10(),
        new Scene11(),
        new Scene12()
    };
    private int currentScene = 0;
    private boolean switchScene;

    public final void init(GLDrawable drawable) {
    }

    public final void display(GLDrawable drawable) {
        timing += step;
        drawscene(drawable);
    }

    public final void reshape(GLDrawable drawable,
                        int xstart,
                        int ystart,
                        int width,
                        int height) {
        GL gl = drawable.getGL();
        GLU glu = drawable.getGLU();

        height = (height == 0) ? 1 : height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45, (float) width / height, 1, 1000);
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public final void displayChanged(GLDrawable drawable,
                               boolean modeChanged,
                               boolean deviceChanged) {
    }

    private void drawscene(GLDrawable g) {
        if (switchScene)
            nextScene(g);

        boolean rendered = scenes[currentScene].drawScene(g, timing);
        if (!rendered) {
            nextScene(g);
        }
    }

    public final void nextScene() {
        switchScene = true;
    }

    private void nextScene(GLDrawable g) {
        scenes[currentScene].clean(g);
        timing = 0;
        currentScene++;
        if (currentScene >= scenes.length) {
            if (loop) {
                currentScene = 0;
            } else {
                System.exit(0);
            }
        }
        switchScene = false;
    }

    public final void increaseStep() {
        step += 1;
    }

    public final void decreaseStep() {
        step = Math.max(-1, step - 1);
    }
}