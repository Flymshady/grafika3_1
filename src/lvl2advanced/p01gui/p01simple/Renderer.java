package lvl2advanced.p01gui.p01simple;


import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.*;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL20.*;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

    private int shaderProgram;
    private OGLBuffers buffers;
    private int locView;
    private int locProjection;
    private Mat4PerspRH projection;
    private Camera camera;
    private int locTime;
    private float time;

    double ox, oy;
    boolean mouseButton1 = false;

    public void init(){
        glClearColor(0.1f,0.1f,0.1f,1);


        /*
        glPolygonMode();
        GL_FRONT_AND_BACK; //front/beck...

        GL_FILL;
        GL_LINE;
        */

        glEnable(GL_DEPTH_TEST);
        shaderProgram = ShaderUtils.loadProgram("/lvl1basic/p01start/start");

        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");

        locTime =  glGetUniformLocation(shaderProgram, "time");

        buffers = GridFactory.generateGrid(100,100);

        camera = new Camera()
                .withPosition(new Vec3D(0,0,0))
                .withAzimuth(5/4f* Math.PI)
                .withZenith(-1/5f*Math.PI)
                .withFirstPerson(false)
                .withRadius(4);

      //kamera pres view
        /*
      view = new Mat4ViewRH(
                new Vec3D(4,4,4),
                new Vec3D(1,1,1),
                new Vec3D(0,0,1)
        );
       */

        projection = new Mat4PerspRH(Math.PI/3,
                //aktualizovat po rozsireni okna pres windowsizecallback
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH, 1, 20);

    }

    public void display(){

        glUseProgram(shaderProgram);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glViewport(0,0, width, height);
        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());

        time += 0.1;
        glUniform1f(locTime, time);

        buffers.draw(GL_TRIANGLES, shaderProgram);
    }
    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key) {
                    case GLFW_KEY_W:
                        camera = camera.forward(1);
                        break;
                    case GLFW_KEY_D:
                        camera = camera.right(1);
                        break;
                    case GLFW_KEY_S:
                        camera = camera.backward(1);
                        break;
                    case GLFW_KEY_A:
                        camera = camera.left(1);
                        break;
                    case GLFW_KEY_LEFT_CONTROL:
                        camera = camera.down(1);
                        break;
                    case GLFW_KEY_LEFT_SHIFT:
                        camera = camera.up(1);
                        break;
                    case GLFW_KEY_SPACE:
                        camera = camera.withFirstPerson(!camera.getFirstPerson());
                        break;
                    case GLFW_KEY_R:
                        camera = camera.mulRadius(0.9f);
                        break;
                    case GLFW_KEY_F:
                        camera = camera.mulRadius(1.1f);
                        break;
                }
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };
    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };


    
    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
        }
    };
    

    
    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override public void invoke (long window, double dx, double dy) {
        }
    };

	@Override
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}
/*
	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}
*/

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}

	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}


}