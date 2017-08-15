package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

public class Loader {

	private List<Integer> vaos = new ArrayList<Integer>();// all vaos created
	private List<Integer> vbos = new ArrayList<Integer>(); // all vbos created
	private List<Integer> textures = new ArrayList<Integer>(); // all textures created
	
	// take positions of model vertices
	public RawModel loadToVAO(float[] positions,float[] textureCoords,float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoords);
		storeDataInAttributeList(2,3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public int loadTexture(String filename){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+filename+".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		return textureID;
		
	}
	// delete every vaos and vbos created
	public void cleanUP() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL30.glDeleteVertexArrays(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);;
		}
	}

	// create new empty VAO
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();// create a empty VAO
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID); // activate VAO bind then
		return vaoID;
	}

	// store data in attributeslList to VAO
	private void storeDataInAttributeList(int attributeNumber,int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();// create a empty VBO
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);// bind VBO
		FloatBuffer buffer = storeDataInFloatBuffer(data); // get a floatbuffer with data to stored in VBO
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // store data into VBO
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0); // put VBO into VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);// unbind vbo, because we finish to use him
	}

	// unbind when finish to use VAOs
	private void unbindVAO() {
		GL30.glBindVertexArray(0);// free VAO
	}
	
	//load and bind indices to vbo
	private void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers(); //create a empty vbo
		vbos.add(vboID); 
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID); //bind the vbo buffer
		IntBuffer buffer = storeDataInIntBuffer(indices); //convert and store int array indices to intbuffer
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //store into vbo
	}
	//convert int array into intbuffer
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length); //create an empty int buffer
		buffer.put(data); //put your data into buffer
		buffer.flip(); //buffer ready
		return buffer; 
	}

	// convert data in floatBuffer
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length); // create a empty floatbuffer
		buffer.put(data); // put data in floatbuffer
		buffer.flip();
		return buffer;
	}
}
