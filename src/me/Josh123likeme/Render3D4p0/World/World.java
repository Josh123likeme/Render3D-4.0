package me.Josh123likeme.Render3D4p0.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.Josh123likeme.Render3D4p0.Vector3D;

public class World {
	
	private static Random random = new Random();
	
	private boolean modified = true;
	
	private List<Model> models = new ArrayList<Model>();
	
	private List<Triangle> triangles = new ArrayList<Triangle>();
	
	private void addTriangle(Triangle triangle) {
		
		triangles.add(triangle);
		
	}
	
	public void addModel(Model model) {
		
		models.add(model);
		
		modified = true;
		
	}
	
	public List<Triangle> getTriangles() {
		
		if (modified) {
			
			triangles = new ArrayList<Triangle>();
			
			for (Model model : models) {
				
				Model newModel = model.clone();
				
				triangles.addAll(newModel.getTriangles());
				
			}
			
			modified = false;
			
		}
		
		return triangles;
		
	}
	
	public void refreshTriangles() {
		
		modified = true;
		
		getTriangles();
		
	}
	
	public World() { }
	
	public World(int preset) {
		
		switch (preset) {
		
		case 0: break;
		
		case 1: pregenCubeAndArrow();
			break;
		
		case 2: pregenNoiseLand();
			break;
			
		case 3: pregenPoint();
			break;
		
		case 4: pregenTriangle();
			break;
		}
		
	}
	
	private void pregenCubeAndArrow() {
		
		Vector3D v1 = new Vector3D(-1, 1, -1);
		Vector3D v2 = new Vector3D(1, 1, -1);
		Vector3D v3 = new Vector3D(-1, -1, -1);
		Vector3D v4 = new Vector3D(1, -1, -1);
		Vector3D v5 = new Vector3D(-1, 1, 1);
		Vector3D v6 = new Vector3D(1, 1, 1);
		Vector3D v7 = new Vector3D(-1, -1, 1);
		Vector3D v8 = new Vector3D(1, -1, 1);
		
		addTriangle(new Triangle(v1, v2, v3, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v2, v3, v4, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v5, v6, v7, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v6, v7, v8, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v1, v3, v5, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v3, v5, v7, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v2, v4, v6, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v4, v6, v8, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v1, v2, v5, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v2, v5, v6, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v3, v4, v7, 0xFF << 24 | random.nextInt()));
		addTriangle(new Triangle(v4, v7, v8, 0xFF << 24 | random.nextInt()));

		addTriangle(new Triangle(new Vector3D(-0.5, 0, 1.5), new Vector3D(0.5, 0, 1.5), new Vector3D(0, 0, 3), 0xFF << 24 | random.nextInt()));
		
	}
	
	private void pregenNoiseLand() {
		
		Model model = new Model();
		
		//double[][] ps = new double[10][10]; //~100
		//double[][] ps = new double[72][72]; //~10k
		double[][] ps = new double[100][100]; //~20k
		//double[][] ps = new double[225][225]; //~100k
		//double[][] ps = new double[709][709]; //~1m
		
		for (int row = 0; row < ps.length; row++) {
			
			for (int col = 0; col < ps[row].length; col++) {
				
				//ps[row][col] = Main.random.nextDouble();
				ps[row][col] = f(col * 0.1, row * 0.1);
				
			}
			
		}
		
		for (int row = 0; row < ps.length - 1; row++) {
			
			for (int col = 0; col < ps[row].length - 1; col++) {
				
				Vector3D a;
				Vector3D b;
				Vector3D c;
				
				Triangle t;
				
				//first triangle
				
				a = new Vector3D(row, col, ps[row][col]);
				b = new Vector3D(row+1, col, ps[row+1][col]);
				c = new Vector3D(row, col+1, ps[row][col+1]);
				
				t = new Triangle(a, b, c, 0xFF << 24 | random.nextInt());
				
				model.getTriangles().add(t);

				//second triangle
				
				a = new Vector3D(row+1, col+1, ps[row+1][col+1]);
				b = new Vector3D(row+1, col, ps[row+1][col]);
				c = new Vector3D(row, col+1, ps[row][col+1]);
				
				t = new Triangle(a, c, b, 0xFF << 24 | random.nextInt());
				
				model.getTriangles().add(t);

			}
			
		}
		
		addModel(model);
 		
	}
	
	private double f(double x, double y) {
		
		//return x * y;
		return 10 * Math.sin(x) + 10 * Math.cos(y);
		//return Math.cos(Math.abs(x/3) + Math.abs(y/3)) * (Math.abs(x/3) + Math.abs(y/3));
		
		//return ((int) x ^ 1287412 % 10017 + (int) y ^ 123971 % 12809) % 10;
		
		//return Math.sin(x) * Math.cos(y);
		
	}
	
	private void pregenPoint() {
		
		Vector3D vec = new Vector3D(0, 0, 0);
		
		triangles.add(new Triangle(vec, vec, vec, 0xFF << 24 | random.nextInt()));
		
	}
	
	private void pregenTriangle() {
		
		Vector3D v1 = new Vector3D(0, 0, 0);
		Vector3D v2 = new Vector3D(1, 0, 0);
		Vector3D v3 = new Vector3D(0, 0, 1);
		
		Triangle t = new Triangle(v1, v2, v3, 0xFF << 24 | random.nextInt());
		
		triangles.add(t);

	}
	
}
