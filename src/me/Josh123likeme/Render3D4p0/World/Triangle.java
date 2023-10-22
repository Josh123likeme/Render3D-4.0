package me.Josh123likeme.Render3D4p0.World;

import me.Josh123likeme.Render3D4p0.Vector3D;

public class Triangle {

	public Vector3D A;
	public Vector3D B;
	public Vector3D C;
	
	public int c;
	
	public Triangle(Vector3D a, Vector3D b, Vector3D c, int colour) {
		
		A = a;
		B = b;
		C = c;
		
		this.c = colour;
		
	}
	
	/**
	 * @return a deep copy of this triangle
	 */
	public Triangle clone() {
		
		return new Triangle(A.clone(), B.clone(), C.clone(), this.c);
		
	}
	
}
