/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package demos.common;

import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.Animator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <P> An Animator can be attached to a GLDrawable to drive its
 * display() method in a loop. For efficiency, it sets up the
 * rendering thread for the drawable to be its own internal thread,
 * so it can not be combined with manual repaints of the
 * surface. </P>
 *
 * <P> The Animator currently contains a workaround for a bug in
 * NVidia's drivers (80174). The current semantics are that once an
 * Animator is created with a given GLDrawable as a target, repaints
 * will likely be suspended for that GLDrawable until the Animator is
 * started. This prevents multithreaded access to the context (which
 * can be problematic) when the application's intent is for
 * single-threaded access within the Animator. It is not guaranteed
 * that repaints will be prevented during this time and applications
 * should not rely on this behavior for correctness. </P>
 */

public class FPSAnimator extends Animator {
    private GLDrawable drawable;
    private RenderRunnable runnable = new RenderRunnable();
    private Thread thread;
    private boolean shouldStop;
    private long delay;
    private Timer renderTimer;
    private ExceptionHandler exceptionHandler;

    public FPSAnimator(GLDrawable drawable, int fps) {
        this(drawable, fps, null);
    }

    /** Creates a new Animator for a particular drawable. */
    public FPSAnimator(GLDrawable drawable, int fps, ExceptionHandler exceptionHandler) {
        super(drawable);
        this.exceptionHandler = exceptionHandler;
        this.drawable = drawable;
        this.delay = 1000 / fps;
    }

    /** Starts this animator. */
    public synchronized void start() {
        if (thread != null) {
            throw new GLException("Already started");
        }
        thread = new Thread(runnable);
        thread.start();

        renderTimer = new Timer();
        renderTimer.schedule(new TimerTask() {
            public void run() {
                runnable.nextFrame();
            }
        }, 0, delay);
    }

    /** Stops this animator, blocking until the animation thread has
     finished. */
    public synchronized void stop() {
        shouldStop = true;
        while (shouldStop && thread != null) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
            renderTimer.cancel();
        }
    }

    public boolean isFrameRateLimitEnabled() {
        return runnable.isFrameRateLimitEnabled();
    }

    public void setFrameRateLimitEnabled(boolean frameRateLimit) {
        runnable.setFrameRateLimitEnabled(frameRateLimit);
    }

    private class RenderRunnable implements Runnable {
        private boolean frameRateLimitEnabled = true;

        public boolean isFrameRateLimitEnabled() {
            return frameRateLimitEnabled;
        }

        public void setFrameRateLimitEnabled(boolean frameRateLimit) {
            frameRateLimitEnabled = frameRateLimit;
            nextFrameSync();
        }

        public void nextFrame() {
            if (frameRateLimitEnabled)
                nextFrameSync();
        }

        private synchronized void nextFrameSync() {
            notify();
        }

        public void run() {
            boolean noException = false;
            try {
                // Try to get OpenGL context optimization since we know we
                // will be rendering this one drawable continually from
                // this thread; make the context current once instead of
                // making it current and freeing it each frame.
                drawable.setRenderingThread(Thread.currentThread());

                // Since setRenderingThread is currently advisory (because
                // of the poor JAWT implementation in the Motif AWT, which
                // performs excessive locking) we also prevent repaint(),
                // which is called from the AWT thread, from having an
                // effect for better multithreading behavior. This call is
                // not strictly necessary, but if end users write their
                // own animation loops which update multiple drawables per
                // tick then it may be necessary to enforce the order of
                // updates.
                drawable.setNoAutoRedrawMode(true);

                while (!shouldStop) {
                    noException = false;
                    drawable.display();
                    if (frameRateLimitEnabled) {
                        synchronized (this) {
                            if (frameRateLimitEnabled) {
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    }
                    noException = true;
                }
            } catch (Exception e) {
                if (exceptionHandler != null)
                    exceptionHandler.handleException(e);
            } finally {
                shouldStop = false;
                drawable.setNoAutoRedrawMode(false);
                try {
                    // The surface is already unlocked and rendering
                    // thread is already null if an exception occurred
                    // during display(), so don't disable the rendering
                    // thread again.
                    if (noException) {
                        drawable.setRenderingThread(null);
                    }
                } finally {
                    synchronized (FPSAnimator.this) {
                        thread = null;
                        FPSAnimator.this.notify();
                    }
                }
            }
        }
    }
}
