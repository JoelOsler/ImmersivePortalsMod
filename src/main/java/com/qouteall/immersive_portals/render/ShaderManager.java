package com.qouteall.immersive_portals.render;

import com.qouteall.immersive_portals.Helper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

//this is not correlated with optifine shaders
public class ShaderManager {
    private int idContentShaderProgram = -1;
    private int idGradientSky = -1;
    
    public ShaderManager() {
        loadShaders();
    }
    
    public void loadShaders() {
        idContentShaderProgram = compileShaderPrograms(
            "immersive_portals:shaders/content_shader_vs.glsl",
            "immersive_portals:shaders/content_shader_fs.glsl"
        );
        idGradientSky = compileShaderPrograms(
            "immersive_portals:shaders/gradient_sky_vs.glsl",
            "immersive_portals:shaders/gradient_sky_fs.glsl"
        );
    }
    
    //return the shader id
    //shaderType:could be GL_VERTEX_SHADER
    private int compileShader(String resourceName, int shaderType) {
        
        try {
            InputStream inputStream =
                MinecraftClient.getInstance().getResourceManager().getResource(
                    new Identifier(resourceName)
                ).getInputStream();
            
            String shaderCode = IOUtils.toString(inputStream, Charset.defaultCharset());
            
            int idShader = GL20.glCreateShader(shaderType);
            GL20.glShaderSource(idShader, shaderCode);
            GL20.glCompileShader(idShader);
            
            //check compiling errors
            int logLength = GL20.glGetShaderi(idShader, GL20.GL_INFO_LOG_LENGTH);
            if (logLength > 0) {
                String errorLog = GL20.glGetShaderInfoLog(idShader, logLength);
                Helper.err("SHADER COMPILE ERROR");
                System.err.print(errorLog);
            }
            
            return idShader;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    private int compileShaderPrograms(
        String vertexShaderName,
        String fragmentShaderName
    ) {
        int idVertexShader = compileShader(vertexShaderName, GL20.GL_VERTEX_SHADER);
        int idFragmentShader = compileShader(fragmentShaderName, GL20.GL_FRAGMENT_SHADER);
        
        int idProgram = GL20.glCreateProgram();
        GL20.glAttachShader(idProgram, idVertexShader);
        GL20.glAttachShader(idProgram, idFragmentShader);
        GL20.glLinkProgram(idProgram);
        
        //check errors
        int logLength = GL20.glGetProgrami(idProgram, GL20.GL_INFO_LOG_LENGTH);
        if (logLength > 0) {
            Helper.err("LINKING ERROR");
        }
        
        //is it ok?
        GL20.glDeleteShader(idVertexShader);
        GL20.glDeleteShader(idFragmentShader);
        
        return idProgram;
    }
    
    public void loadContentShaderAndShaderVars(int textureSlot) {
        GL20.glUseProgram(idContentShaderProgram);
        
        int uniModelView = GL20.glGetUniformLocation(idContentShaderProgram, "modelView");
        int uniProjection = GL20.glGetUniformLocation(idContentShaderProgram, "projection");
        int uniSampler = GL20.glGetUniformLocation(idContentShaderProgram, "sampler");
        int uniWidth = GL20.glGetUniformLocation(idContentShaderProgram, "w");
        int uniHeight = GL20.glGetUniformLocation(idContentShaderProgram, "h");
        
        GL20.glUniformMatrix4fv(uniModelView, false, Helper.getModelViewMatrix());
        GL20.glUniformMatrix4fv(uniProjection, false, Helper.getProjectionMatrix());
        
        GL20.glUniform1i(uniSampler, textureSlot);
    
        GL20.glUniform1f(uniWidth, MinecraftClient.getInstance().getWindow().getFramebufferWidth());
        GL20.glUniform1f(
            uniHeight,
            MinecraftClient.getInstance().getWindow().getFramebufferHeight()
        );
    }
    
    public void unloadShader() {
        GL20.glUseProgram(0);
    }
    
    public void loadGradientSkyShader(float origin) {
        GL20.glUseProgram(idGradientSky);
        
        int uniModelView = GL20.glGetUniformLocation(idGradientSky, "modelView");
        int uniProjection = GL20.glGetUniformLocation(idGradientSky, "projection");
        GL20.glUniformMatrix4fv(uniModelView, false, Helper.getModelViewMatrix());
        GL20.glUniformMatrix4fv(uniProjection, false, Helper.getProjectionMatrix());
        
        int uniOrigin = GL20.glGetUniformLocation(idGradientSky, "origin");
        GL20.glUniform1f(uniOrigin, origin);
    }
}
