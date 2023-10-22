package me.Josh123likeme.Render3D4p0;

import java.util.Random;

public class Vector3D implements Cloneable {

	private static Random random = new Random();
	
	public double X;
	public double Y;
	public double Z;
	
	public Vector3D() { }
	
	public Vector3D(double x, double y, double z) {
		
		X = x;
		Y = y;
		Z = z;
		
	}
	
	public Vector3D clone() {
		
		return new Vector3D(X, Y, Z);
		
	}
	
	public double mag() {
		
		return Math.sqrt(X*X+Y*Y+Z*Z);
		
	}
	
	public Vector3D normalise() {
		
		double mag = mag();
		
		X /= mag;
		Y /= mag;
		Z /= mag;
		
		return this;
		
	}
	
	public Vector3D add(Vector3D vec) {
		
		X += vec.X;
		Y += vec.Y;
		Z += vec.Z;
		
		return this;
		
	}
	
	public Vector3D add(double x, double y, double z) {
		
		X += x;
		Y += y;
		Z += z;
		
		return this;
		
	}
	
	public Vector3D subtract(Vector3D vec) {
		
		X -= vec.X;
		Y -= vec.Y;
		Z -= vec.Z;
		
		return this;
		
	}
	
	public Vector3D scale(double scale) {
		
		X *= scale;
		Y *= scale;
		Z *= scale;
		
		return this;
		
	}
	
	public double dot(Vector3D vec) {
		
		return X*vec.X + Y*vec.Y + Z*vec.Z;
		
	}
	
	public Vector3D cross(Vector3D vec) {
		
		return new Vector3D(Y*vec.Z-Z*vec.Y,Z*vec.X-X*vec.Z,X*vec.Y-Y*vec.X);
		
	}
	
	public double distanceTo(Vector3D vec) {
		
		return Math.sqrt((X - vec.X)*(X - vec.X) + (Y - vec.Y)*(Y - vec.Y) + (Z - vec.Z)*(Z - vec.Z));
		
	}
	
	public double squaredDistanceTo(Vector3D vec) {
		
		return distanceTo(vec)*distanceTo(vec);
		
	}
	
	public double angleTo(Vector3D vec) {
		
		return Math.acos(this.dot(vec)/this.mag()*vec.mag());
		
	}
	
	public Vector3D mean(Vector3D vec) {
		
		return new Vector3D((X + vec.X) / 2, (Y + vec.Y) / 2, (Z + vec.Z) / 2);
		
	}
	
	public boolean sameAs(Vector3D vec) {
		
		if (Math.abs(X - vec.X) < 0.01 && Math.abs(Y - vec.Y) < 0.01 && Math.abs(Z - vec.Z) < 0.01) return true;
		
		return false;
		
	}
	
	public String format() {
		
		return "X:" + X + " Y: " + Y + " Z: " + Z;
		
	}
	
	public static Vector3D newRandom() {
		
		Vector3D vec = new Vector3D(2*random.nextDouble()-1,2*random.nextDouble()-1,2*random.nextDouble()-1);
		
		return vec.normalise();
		
	}
	
}
