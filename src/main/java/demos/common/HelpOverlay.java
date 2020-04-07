package demos.common;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GLEventListener;
import net.java.games.jogl.GLU;
import net.java.games.jogl.util.GLUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class HelpOverlay implements GLEventListener {
    private List keyboardEntries = new ArrayList();
    private List mouseEntries = new ArrayList();
    private boolean visible = false;
    private GLUT glut = new GLUT();
    private static final int CHAR_HEIGHT = 12;
    private static final int OFFSET = 15;
    private static final int INDENT = 3;
    private static final String KEYBOARD_CONTROLS = "Keyboard controls";
    private static final String MOUSE_CONTROLS = "Mouse controls";

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void display(GLDrawable glDrawable) {
        GL gl = glDrawable.getGL();
        GLU glu = glDrawable.getGLU();

        // Store old matrices
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        Dimension size = glDrawable.getSize();
        gl.glViewport(0, 0, size.width, size.height);

        // Store enabled state and disable lighting, texture mapping and the depth buffer
        gl.glPushAttrib(GL.GL_ENABLE_BIT);
        gl.glDisable(GL.GL_BLEND);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_DEPTH_TEST);

        // Retrieve the current viewport and switch to orthographic mode
        int viewPort[] = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewPort);
        glu.gluOrtho2D(0, viewPort[2], viewPort[3], 0);

        // Render the text
        gl.glColor3f(1, 1, 1);

        int x = OFFSET;
        int maxx = 0;
        int y = OFFSET + CHAR_HEIGHT;

        if (keyboardEntries.size() > 0) {
            gl.glRasterPos2i(x, y);
            glut.glutBitmapString(gl, glut.BITMAP_HELVETICA_12, KEYBOARD_CONTROLS);
            maxx = Math.max(maxx, OFFSET + glut.glutBitmapLength(glut.BITMAP_HELVETICA_12, KEYBOARD_CONTROLS));

            y += OFFSET;
            x += INDENT;
            for (int i = 0; i < keyboardEntries.size(); i++) {
                gl.glRasterPos2f(x, y);
                String text = (String) keyboardEntries.get(i);
                glut.glutBitmapString(gl, glut.BITMAP_HELVETICA_12, text);
                maxx = Math.max(maxx, OFFSET + glut.glutBitmapLength(glut.BITMAP_HELVETICA_12, text));
                y += OFFSET;
            }
        }

        if (mouseEntries.size() > 0) {
            x = maxx + OFFSET;
            y = OFFSET + CHAR_HEIGHT;
            gl.glRasterPos2i(x, y);
            glut.glutBitmapString(gl, glut.BITMAP_HELVETICA_12, MOUSE_CONTROLS);

            y += OFFSET;
            x += INDENT;
            for (int i = 0; i < mouseEntries.size(); i++) {
                gl.glRasterPos2f(x, y);
                glut.glutBitmapString(gl, glut.BITMAP_HELVETICA_12, (String) mouseEntries.get(i));
                y += OFFSET;
            }
        }

        // Restore enabled state
        gl.glPopAttrib();

        // Restore old matrices
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    public void displayChanged(GLDrawable glDrawable, boolean b, boolean b1) {
    }

    public void init(GLDrawable glDrawable) {
    }

    public void reshape(GLDrawable glDrawable, int i, int i1, int i2, int i3) {
    }

    public void registerKeyStroke(KeyStroke keyStroke, String description) {
        String modifiersText = KeyEvent.getKeyModifiersText(keyStroke.getModifiers());
        String keyText = KeyEvent.getKeyText(keyStroke.getKeyCode());
        keyboardEntries.add(
                (modifiersText.length() != 0 ? modifiersText + " " : "") +
                keyText + ": " +
                description
        );
    }

    public void registerMouseEvent(int id, int modifiers, String description) {
        String mouseText = null;
        switch (id) {
            case MouseEvent.MOUSE_CLICKED:
                mouseText = "Clicked " + MouseEvent.getModifiersExText(modifiers);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                mouseText = "Dragged " + MouseEvent.getModifiersExText(modifiers);
                break;
            case MouseEvent.MOUSE_ENTERED:
                mouseText = "Mouse enters";
                break;
            case MouseEvent.MOUSE_EXITED:
                mouseText = "Mouse exits";
                break;
            case MouseEvent.MOUSE_MOVED:
                mouseText = "Mouse moves";
                break;
            case MouseEvent.MOUSE_PRESSED:
                mouseText = "Pressed " + MouseEvent.getModifiersExText(modifiers);
                break;
            case MouseEvent.MOUSE_RELEASED:
                mouseText = "Released " + MouseEvent.getModifiersExText(modifiers);
                break;
        }
        mouseEntries.add(
                mouseText + ": " + description
        );

    }
}
