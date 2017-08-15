package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShaderProgram {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public ShaderProgram(String vertexFile,String fragmentFile){
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	//get location of uniform variable in the shader code
	protected int getUniformLocation(String uniformName){
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	
	protected abstract void getAllUniformLocations();
	//start program
	public void start(){
		GL20.glUseProgram(programID);
	}
	
	protected void loadFloat(int location, float value){
		GL20.glUniform1f(location, value);
	}
	
	protected void loadVector(int location, Vector3f vector){
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	protected void loadBoolean(int location, boolean value){
		float toLoad = 0;
		if(value){
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}
	
	protected void loadMatrix(int location,Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}
	//stop program
	public void stop(){
		GL20.glUseProgram(0);
	}
	
	public void cleanUP(){
		stop();//stop program
		GL20.glDetachShader(programID, vertexShaderID); //detech shaders
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(fragmentShaderID);//delete shaders
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteProgram(programID);//delete program
	}
	
	protected void bindAttribute(int attribute,String variableName){
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected abstract void bindAttributes();
	
	private static int loadShader(String file, int type){
		StringBuilder shaderSource = new StringBuilder();
		//read shader file
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null){
				shaderSource.append(line).append("\n");
			}
			reader.close();			
		}catch(IOException ex){
			System.out.println("Could not read file");
		}
		int shaderID = GL20.glCreateShader(type); //create a shader as a type 
		GL20.glShaderSource(shaderID, shaderSource); //attribute code of shader to his ID
		GL20.glCompileShader(shaderID); //compile shader
		//check if shader shader has been compile right
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS)==GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader");
			System.exit(-1);
		}
		return shaderID;
	}
}