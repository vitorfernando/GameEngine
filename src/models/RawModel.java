package models;

public class RawModel {
	private int vaoID;
	private int vertexCount; //qdt vertex in 3d model
	
	public RawModel(int vaoID, int vertexCount){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	
	
	
}
