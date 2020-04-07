package demos.glexcess;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.gl2.GLUgl2;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * GLExcess v1.0 Demo
 * Copyright (C) 2001-2003 Paolo Martella
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @author Paolo "Bustard" Martella
 * @author Pepijn Van Eeckhoudt
 */
final class Texture {
    private final IntBuffer tID = IntBuffer.allocate(1);

    private static int pow2(int exp) {
        int result = 1;
        if (exp == 0) return result;
        for (int a = 0; a < exp; a++) {
            result *= 2;
        }
        return result;
    }

    private boolean create(GL gl) {
        kill(gl);
        gl.glGenTextures(1, tID);
        gl.glBindTexture(GL.GL_TEXTURE_2D, tID.get(0));
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        return true;
    }

    public final void kill(GL gl) {
        if (tID.get(0) != 0) {
            gl.glDeleteTextures(1, tID);
            tID.put(0, 0);
        }
    }

    public final void use(GL gl) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, tID.get(0));
    }

    public final void load(GL gl, GLUgl2 glu, InputStream inputStream) throws IOException {
        if (inputStream == null)
            throw new NullPointerException();
        if (!create(gl))
            throw new RuntimeException();

        DataInputStream datain = new DataInputStream(new BufferedInputStream(inputStream));
        DataInputStream din = new DataInputStream(inputStream);
        int ww = din.readUnsignedByte();
        int w = pow2(ww - 48);
        int hh = din.readUnsignedByte();
        int h = pow2(hh - 48);
        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 3, w, h, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(datain.readAllBytes()));
    }
}
