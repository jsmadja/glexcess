package demos.glexcess;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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
final class Texture {
    private final int[] tID = new int[1];

    private boolean create(GL gl) {
        kill(gl);
        gl.glGenTextures(1, tID);
        gl.glBindTexture(GL.GL_TEXTURE_2D, tID[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        return true;
    }

    public final void kill(GL gl) {
        if (tID[0] != 0) {
            gl.glDeleteTextures(1, tID);
            tID[0] = 0;
        }
    }

    private static int pow2(int exp) {
        int result = 1;
        if (exp == 0) return result;
        for (int a = 0; a < exp; a++) {
            result *= 2;
        }
        return result;
    }

    public final void use(GL gl) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, tID[0]);
    }

    public final void load(GL gl, GLU glu, InputStream inputStream) throws IOException {
        if (inputStream == null)
            throw new NullPointerException();
        if (!create(gl))
            throw new RuntimeException();

        DataInputStream din = new DataInputStream(inputStream);
        int ww = din.readUnsignedByte();
        int w = pow2(ww - 48);
        int hh = din.readUnsignedByte();
        int h = pow2(hh - 48);
        int size = w * h * 3;
        ByteBuffer rgbData = BufferUtils.newByteBuffer(size);
        for (int i = 0; i < size; i++) {
            rgbData.put(din.readByte());
        }

        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 3, w, h, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, rgbData);
    }
}
