package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolsbox.Maths;

public class EntityRenderer {
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader,Matrix4f projectionMatrix){
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render( Map<TexturedModel, List<Entity>> entities){
		for(TexturedModel model:entities.keySet()){
			prepareTexturedModel(model); //prepare every sigle texturedmodel
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch){
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().
						getVertexCount(),GL11.GL_UNSIGNED_INT,0); //draw vertices
			}
			unbindTexturedModel();
		}
	}
	
	public void prepareTexturedModel(TexturedModel model){
		RawModel rawModel = model.getRawModel(); //get a rawmodel of texturedmodel
		GL30.glBindVertexArray(rawModel.getVaoID()); //bind the vao we want to use
		GL20.glEnableVertexAttribArray(0); //activate the attributelist0 what data is stored
		GL20.glEnableVertexAttribArray(1); //activate the attributelist1 what data is stored
		GL20.glEnableVertexAttribArray(2); //activate the attributelist2 what data is stored
		ModelTexture texture = model.getTexture();
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0); //active texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}
	
	public void prepareInstance(Entity entity){
		Matrix4f transformationMatrix = Maths.createTranformationMatrix(entity.getPosition(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale()); //create the transformationmatrix
		shader.loadTransforationMatrix(transformationMatrix); //loadtransformationmatrix to the shader
	}
	

	public void unbindTexturedModel(){
		GL20.glDisableVertexAttribArray(0); //free the attributelist0 
		GL20.glDisableVertexAttribArray(1); //free the attributelist1
		GL20.glDisableVertexAttribArray(2); //free the attributelist2
		GL30.glBindVertexArray(0);
	}

}
