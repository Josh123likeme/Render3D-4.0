package me.Josh123likeme.Render3D4p0.World;

import java.util.ArrayList;
import java.util.List;

import me.Josh123likeme.Render3D4p0.Vector3D;

public class Model {

	private List<Triangle> triangles;
	
	private Vector3D modelPos;
	
	public Model() {
		
		triangles = new ArrayList<Triangle>();
		
		modelPos = new Vector3D();
		
	}
	
	public Model(List<Triangle> triangles) {
		
		this.triangles = triangles;
		
		modelPos = new Vector3D();
		
	}
	
	public List<Triangle> getTriangles() {
		
		return triangles;
		
	}
	
	public void moveModel(Vector3D vector) {
		
		for (Triangle triangle : triangles) {
			
			triangle.A.add(vector);
			triangle.B.add(vector);
			triangle.C.add(vector);
			
		}
		
		modelPos.add(vector);
		
	}
	
	public void resetModelPosition() {
		
		for (Triangle triangle : triangles) {
			
			triangle.A.subtract(modelPos);
			triangle.B.subtract(modelPos);
			triangle.C.subtract(modelPos);
			
		}
		
		modelPos.X = 0;
		modelPos.Y = 0;
		modelPos.Z = 0;
		
	}
	
	/**
	 * @return a deep copy of this model
	 */
	public Model clone() {
		
		Model newModel = new Model();
		
		for (Triangle triangle : triangles) {
			
			newModel.triangles.add(triangle.clone());
			
		}
		
		newModel.modelPos = modelPos.clone();
		
		return newModel;
		
	}
	
}
