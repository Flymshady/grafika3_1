package lvl2advanced.p01gui.p01simple;


import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

    private int shaderProgram;
    private int shaderProgramLight;
    private OGLBuffers buffers;
    private int locView;
    private int locViewLight;
    private int locProjection;
    private int locProjectionLight;
    private int locLightVP;
    private Mat4PerspRH projection;
    private Camera camera;
    private int locTime;
    private int locType, locTypeLight;
    private int locTimeLight;
    private float time;

    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;
    private OGLTexture2D texture1;
    private OGLTextRenderer textRenderer;

    double ox, oy;
    boolean mouseButton1 = false;
    private Camera cameraLight;

    private boolean line=false;

    public void init(){

        glClearColor(0.1f,0.1f,0.1f,1);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        /*
        glPolygonMode();
        GL_FRONT_AND_BACK; //front/beck...

        GL_FILL;
        GL_LINE;
        */

        glEnable(GL_DEPTH_TEST);

        shaderProgram = ShaderUtils.loadProgram("/lvl1basic/p01start/start");
        shaderProgramLight = ShaderUtils.loadProgram("/lvl1basic/p01start/light");

        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");
        locType=  glGetUniformLocation(shaderProgram, "type");
        locTime =  glGetUniformLocation(shaderProgram, "time");

        locViewLight = glGetUniformLocation(shaderProgramLight, "view");
        locProjectionLight = glGetUniformLocation(shaderProgramLight, "projection");
        locTypeLight =  glGetUniformLocation(shaderProgramLight, "type");
        locTimeLight =  glGetUniformLocation(shaderProgramLight, "time");

        locLightVP = glGetUniformLocation(shaderProgram, "lightViewProjection");

        buffers = GridFactory.generateGrid(100,100);
        renderTarget = new OGLRenderTarget(1024,1024);
        textRenderer = new OGLTextRenderer(width, height);
        viewer = new OGLTexture2D.Viewer();


        try {
            texture1 = new OGLTexture2D("./textures/mosaic.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera = new Camera()
                .withPosition(new Vec3D(0,0,0))
                .withAzimuth(5/4f* Math.PI)
                .withZenith(-1/5f*Math.PI)
                .withFirstPerson(false)
                .withRadius(6);

        cameraLight = new Camera()
                .withPosition(new Vec3D(6,6,6))
                .withAzimuth(5/4f* Math.PI)
                .withZenith(-1/5f*Math.PI);


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
      //  glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); taj to asi byt nema
        time += 0.1;
        renderFromLight();
        renderFromViewer();



        textRenderer.clear();
        String text = new String(": look at console and try keys, mouse, wheel and window interaction " );
        textRenderer.addStr2D(3, height-3, text);
        textRenderer.addStr2D(width-90, height-3, "Štěpán Cellar");
        textRenderer.draw();


        viewer.view(renderTarget.getColorTexture(), -1,0,0.5);
        viewer.view(renderTarget.getDepthTexture(), -1,-0.5,0.5);
/*
        glUseProgram(shaderProgram);
        glViewport(0,0, width, height);
        glClearColor(0.5f,0f,0f,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

   //     renderTarget.bind();


        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());

        time += 0.1;
        glUniform1f(locTime, time);
//k here
        buffers.draw(GL_TRIANGLES, shaderProgram);

      //  glBindFramebuffer(GL_FRAMEBUFFER, 0);
       // glClearColor(0.1f,0.5f,0f,1);
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glViewport(0,0, width, height);

        //viewer.view(renderTarget.getColorTexture(), -1,0,0.5);


        //do sem
        */
    }

    private void renderFromLight() {

        glEnable(GL_DEPTH_TEST);
        glUseProgram(shaderProgramLight);
        renderTarget.bind();
        glClearColor(0f,0.5f,0f,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        glUniformMatrix4fv(locViewLight, false, cameraLight.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjectionLight, false, projection.floatArray());




        glUniform1f(locTimeLight, time);

            glUniform1f(locTypeLight, 0);
        buffers.draw(GL_TRIANGLES, shaderProgramLight);
            glUniform1f(locTypeLight, 1);
        buffers.draw(GL_TRIANGLES, shaderProgramLight);
        glUniform1f(locTypeLight, 2);
        buffers.draw(GL_TRIANGLES, shaderProgramLight);

        //tohle samy znova pro jinej jen s jinym ukazatelem
    }

    private void renderFromViewer() {
        if(line){
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }else{
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }


        glEnable(GL_DEPTH_TEST);
        glUseProgram(shaderProgram);
        glViewport(0,0, width, height);
        //defaultni framebuffer - render do obrazovky
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glClearColor(0.5f,0f,0f,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());
        glUniformMatrix4fv(locLightVP,false,cameraLight.getViewMatrix().mul(projection).floatArray());



        //misto depth dat color
        renderTarget.getDepthTexture().bind(shaderProgram, "depthTexture",1);

        texture1.bind(shaderProgram, "texture/mosaic",0);


      //  time += 0.1;
        glUniform1f(locTime, time);

        glUniform1f(locType, 0);
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 1);
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 2);
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
                    case GLFW_KEY_L:
                        if(line){
                            line=false;
                        }else{
                            line=true;
                        }
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