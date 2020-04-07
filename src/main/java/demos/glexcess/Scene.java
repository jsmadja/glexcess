package demos.glexcess;

import com.jogamp.opengl.GLAutoDrawable;

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
interface Scene {
    void clean(GLAutoDrawable g);
    boolean drawScene(GLAutoDrawable g, float time);
}
