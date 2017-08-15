package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.ModelTexture;
import toolsbox.Maths;

public class TerrainRenderer {
	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f ProjectionMaxtrix){
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(ProjectionMaxtrix);
		shader.stop();
	}
	
	public void render(List<Terrain> terrains){
		for(Terrain terrain : terrains){
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().
					getVertexCount(),GL11.GL_UNSIGNED_INT,0); //draw vertices
			unbindTexturedModel();
		}
		
	}
	

	public void prepareTerrain(Terrain terrain){
		RawModel rawModel = terrain.getModel(); //get a rawmodel of texturedmodel
		GL30.glBindVertexArray(rawModel.getVaoID()); //bind the vao we want to use
		GL20.glEnableVertexAttribArray(0); //activate the attributelist0 what data is stored
		GL20.glEnableVertexAttribArray(1); //activate the attributelist1 what data is stored
		GL20.glEnableVertexAttribArray(2); //activate the attributelist2 what data is stored
		ModelTexture texture = terrain.getTexture();
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0); //active texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
	
	public void loadModelMatrix(Terrain terrain){
		Matrix4f transformationMatrix = Maths.createTranformationMatrix(new Vector3f(terrain.getX(),0,terrain.getZ()),
				0, 0, 0, 1); //create the transformationmatrix
		shader.loadTransforationMatrix(transformationMatrix); //loadtransformationmatrix to the shader
	}
	

	public void unbindTexturedModel(){
		GL20.glDisableVertexAttribArray(0); //free the attributelist0 
		GL20.glDisableVertexAttribArray(1); //free the attributelist1
		GL20.glDisableVertexAttribArray(2); //free the attributelist2
		GL30.glBindVertexArray(0);
	}

}
