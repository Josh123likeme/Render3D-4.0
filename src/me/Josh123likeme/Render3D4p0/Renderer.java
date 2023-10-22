package me.Josh123likeme.Render3D4p0;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import me.Josh123likeme.Render3D4p0.World.Triangle;
import me.Josh123likeme.Render3D4p0.World.World;

public class Renderer {
	
	/*HOW DOES THIS WORK?
	 * 
	 * • do frustum culling (removes any faces that arent in the view range of the camera)
	 * • calculate distance to camera for every face (this is needed for sorting)
	 * • sort the faces in terms of distance
	 * • rasterisation calculation (calculating where the faces should be drawn on the screen)
	 * • backface culling (when going through the points of the triangle in order, if the direction is clockwise, the face is looking away, so is culled)
	 * • drawing the triangle
	 * 
	 */
	
	//debug:
	public long frustumCullTime;
	public long convertToArrayTime;
	public long distCalcTime;
	public long sortTime;
	public long drawTime;
	
	public int totalFaces;
	public int facesDrawn;
	public int facesFrustumCulled;
	public int facesBackfaceCulled;
	
	public void render(Graphics g, World world, Camera cam) {

		facesDrawn = 0;
		facesFrustumCulled = 0;
		facesBackfaceCulled = 0;
		
		totalFaces = world.getTriangles().size();
		
		long start;
		
		List<Triangle> trianglesList = new ArrayList<Triangle>();
		
		//FRUSTUM CULLING (WORK IN PROGRESS)
		
		start = System.nanoTime();
		
		Vector3D sensorDir = new Vector3D(Math.cos(cam.yaw)*Math.sin(cam.pitch),
				Math.sin(cam.yaw)*Math.sin(cam.pitch),
				-Math.cos(cam.pitch));
		
		double d = cam.pos.X * sensorDir.X + cam.pos.Y * sensorDir.Y + cam.pos.Z * sensorDir.Z;
		
		boolean forwardIncreasesD = false;
		
		Vector3D aheadOfCamera = cam.pos.clone().add(sensorDir);
		
		if (aheadOfCamera.X * sensorDir.X + aheadOfCamera.Y * sensorDir.Y + aheadOfCamera.Z * sensorDir.Z > d) forwardIncreasesD = true;
		
		for (Triangle triangle : world.getTriangles()) {
			
			if (!((triangle.A.X * sensorDir.X + triangle.A.Y * sensorDir.Y + triangle.A.Z * sensorDir.Z > d) ^ forwardIncreasesD) 
					|| !((triangle.B.X * sensorDir.X + triangle.B.Y * sensorDir.Y + triangle.B.Z * sensorDir.Z > d) ^ forwardIncreasesD)
					|| !((triangle.C.X * sensorDir.X + triangle.C.Y * sensorDir.Y + triangle.C.Z * sensorDir.Z > d) ^ forwardIncreasesD)) {
				
				trianglesList.add(triangle);
				
			}
			else facesFrustumCulled++;
				
		}
		
		frustumCullTime = System.nanoTime() - start;
		
		//CONVERT TRIANGLES LIST TO ARRAY
		
		start = System.nanoTime();
		
		Triangle[] triangles = new Triangle[trianglesList.size()];
		
		trianglesList.toArray(triangles);
		
		convertToArrayTime = System.nanoTime() - start;
		
		//DISTANCE CALCULATION
		
		start = System.nanoTime();
		
		double[] distSquareds = new double[triangles.length];

		for (int i = 0; i < triangles.length; i++) {
			
			Triangle focus = triangles[i];
			
			Vector3D mean = new Vector3D();
			
			mean.add(focus.A).add(focus.B).add(focus.C).scale(1D/3);
			
			distSquareds[i] = (cam.pos.X - mean.X)*(cam.pos.X - mean.X)+(cam.pos.Y - mean.Y)*(cam.pos.Y - mean.Y)+(cam.pos.Z - mean.Z)*(cam.pos.Z - mean.Z);
			
		}
		
		distCalcTime = System.nanoTime() - start;
		start = System.nanoTime();

		//SORTING
		
		if (triangles.length != 0) quickSort(distSquareds, triangles, 0, distSquareds.length - 1);
		
		sortTime = System.nanoTime() - start;
		start = System.nanoTime();
		
		//RASTERISATION
		
		//debug:
		final double sinY = Math.sin(cam.yaw - Math.PI/2);
		final double cosY = Math.cos(cam.yaw - Math.PI/2);
		final double sinP = Math.sin(-cam.pitch + Math.PI/2);
		final double cosP = Math.cos(-cam.pitch + Math.PI/2);
		
		for (Triangle triangle : triangles) {
			
			int[] pxs = new int[3];
			int[] pys = new int[3];
			
			for (int i = 0; i < 3; i++) {
				
				Vector3D point = null;
				
				switch (i) {
				
				case 0:
					point = triangle.A.clone();
					break;
				case 1:
					point = triangle.B.clone();
					break;
				case 2:
					point = triangle.C.clone();
					break;
					
				}
				
				point.subtract(cam.pos);
				
				//rotation
				
				final double fx = point.X;
				final double fy = point.Y;
				final double fz = point.Z;
				
				/* the compacted version of this is below
				final double fx2 = fx*cosY + fy*sinY;
				final double fy2 = -fx*sinY + fy*cosY;
				final double fz2 = fz;
				
				double x = fx2;    
				double y = fy2*cosP + -fz2*sinP;
				double z = fy2*sinP + fz2*cosP;
				*/
				
				double fy2 = -fx*sinY + fy*cosY;
				
				double x = fx*cosY + fy*sinY;
				double y = fy2*cosP + -fz*sinP;
				double z = fy2*sinP + fz*cosP;
				
				//perspective
				
				//this fixes a problem where the point is so close to the sensor that its distance from
				//the centre of the sensor tends to infinity
				if (cam.fl + y < 0.001) y = 0.001 + cam.fl;
				
				x = (cam.fl * x) / (cam.fl + y);
				z = (cam.fl * z) / (cam.fl + y);
				
				//sensor to image adjustment
				
				x *= (double) cam.iw / cam.sw;
				z *= (double) cam.ih / cam.sh;
				
				//offsetting onto screen
				
				int xint = (int) (x + cam.iw / 2);
				int zint = cam.ih - (int) (z + cam.ih / 2); //this already accounts for y being down in computer graphics
				
				pxs[i] = xint;
				pys[i] = zint;
				
			}
			
			if (clockwise2(pxs, pys))  {
				
				facesBackfaceCulled++;
				
				continue;
				
			}
			
			facesDrawn++;
			
			g.setColor(new Color(triangle.c >> 16 & 0xFF, triangle.c >> 8 & 0xFF, triangle.c >> 0 & 0xFF));
			
			g.fillPolygon(pxs, pys, 3);
			
			//this is a wire frame version of the renderer
			//g.drawLine(pxs[0], pys[0], pxs[1], pys[1]);
			//g.drawLine(pxs[1], pys[1], pxs[2], pys[2]);
			//g.drawLine(pxs[2], pys[2], pxs[0], pys[0]);
			
		}
		
		drawTime = System.nanoTime() - start;
		
	}
	
	static void quickSort(double[] distSquareds, Triangle[] triangles, int low, int high)
    {
        double pivot = distSquareds[low + ((high - low) / 2)];
        int i = low;
        int j = high;

        while (i <= j)
        {

            while (distSquareds[i] > pivot)
            {
                i++;  
            }
            while (distSquareds[j] < pivot)
            {
                j--;
            }
            if (i <= j)
            {
                double temp = distSquareds[i];
                distSquareds[i] = distSquareds[j];
                distSquareds[j] = temp;
                
                Triangle tempT = triangles[i];
                triangles[i] = triangles[j];
                triangles[j] = tempT;
                
                i++;
                j--;
            }
        }

        if (low < j)
        {
            quickSort(distSquareds, triangles, low, j);
        }

        if (i < high)
        {
            quickSort(distSquareds, triangles, i, high);
        }
    }
	
	//this only works with triangles
	private boolean clockwise2(int[] xs, int[] ys) {
		
		double total = 0;
		
		total += (xs[1] - xs[0]) * (ys[1] + ys[0]);
		total += (xs[2] - xs[1]) * (ys[2] + ys[1]);
		total += (xs[0] - xs[2]) * (ys[0] + ys[2]);
		
		return total < 0;
		
	}
	
	private boolean clockwise(int[] xs, int[] ys) {
		
		double total = 0;
		
		for (int i = 0; i < xs.length - 1; i++) {
			
			total += (xs[i + 1] - xs[i]) * (ys[i + 1] + ys[i]);
			
		}
		
		total += (xs[0] - xs[xs.length - 1]) * (ys[0] + ys[ys.length - 1]);
		
		return total < 0;
		
	}
	
	public void renderDebugInfo(Graphics g) {
		
		g.setColor(Color.white);
		
		int h = 10;
		int hs = 11;
		
		h += hs;
		
		String totalCulledPercentage = "" + (100 - 100 * (double) facesDrawn / totalFaces);
		
		g.drawString("surfaces drawn: " + facesDrawn + "/" + totalFaces + " (" + totalCulledPercentage + "% culled )" , 0, h); h += hs;
		g.drawString("frustum culling: " + facesFrustumCulled + " total culled", 0, h); h += hs;
		g.drawString("backface culling: " + facesBackfaceCulled + " total culled", 0, h); h += hs;
		
		h += hs;
		
		g.drawString("frustumCullTime: " + ((double) frustumCullTime / 1000000) + "ms", 0, h); h += hs;
		g.drawString("convertToArrayTime: " + ((double) convertToArrayTime / 1000000) + "ms", 0, h); h += hs;
		g.drawString("distCalcTime: " + ((double) distCalcTime / 1000000) + "ms", 0, h); h += hs;
		g.drawString("sortTime: " + ((double) sortTime / 1000000) + "ms", 0, h); h += hs;
		g.drawString("drawTime: " + ((double) drawTime / 1000000) + "ms", 0, h); h += hs;
		
	}
	
	//THIS STUFF IS OLD
	/*
	public void renderUnoptimised(Graphics g, World world, Camera cam) {
		
		List<Triangle> triangles = new ArrayList<Triangle>();
		
		//TODO culling
		for (Triangle triangle : world.triangles) {
			
			if (true) {
				
				triangles.add(triangle);
				
			}
			
		}
		
		//distance calc
		
		double[] distSquareds = new double[triangles.size()];
		
		for (int i = 0; i < triangles.size(); i++) {
			
			Triangle focus = triangles.get(i);
			
			Vector3D mean = new Vector3D();
			
			mean.add(focus.A).add(focus.B).add(focus.C).scale(1D/3);
			
			distSquareds[i] = (cam.pos.X - mean.X)*(cam.pos.X - mean.X)+(cam.pos.Y - mean.Y)*(cam.pos.Y - mean.Y)+(cam.pos.Z - mean.Z)*(cam.pos.Z - mean.Z);
			
		}
		
		//sorting
		
		boolean hasSwapped;
		
		do {
			
			hasSwapped = false;
			
			for (int i = 0; i < triangles.size() - 1; i++) {
				
				if (distSquareds[i] < distSquareds[i + 1]) {
					
					double tempDist = distSquareds[i];
					distSquareds[i] = distSquareds[i + 1];
					distSquareds[i + 1] = tempDist;
					
					Triangle tempTriangle = triangles.get(i);
					triangles.set(i, triangles.get(i + 1));
					triangles.set(i + 1, tempTriangle);
					
					hasSwapped = true;
					
				}
				
			}
			
		}
		while (hasSwapped);
		
		//rendering
		
		for (Triangle triangle : triangles) {
			
			int[] pxs = new int[3];
			int[] pys = new int[3];
			
			for (int i = 0; i < 3; i++) {
				
				Vector3D point = null;
				
				switch (i) {
				
				case 0:
					point = triangle.A.clone();
					break;
				case 1:
					point = triangle.B.clone();
					break;
				case 2:
					point = triangle.C.clone();
					break;
					
				}
				
				point.subtract(cam.pos);
				
				//yaw rotation
				
				double tx;
				double ty;
				double tz;
				
				double x = point.X;
				double y = point.Y;
				double z = point.Z;
				
				//yaw rotation
				
				tx = x * Math.cos(-cam.yaw + Math.PI/2) + z * Math.sin(-cam.yaw + Math.PI/2);
				ty = y;
				tz = x * -Math.sin(-cam.yaw + Math.PI/2) + z * Math.cos(-cam.yaw + Math.PI/2);
				
				x = tx; y = ty; z = tz;
				
				//pitch rotation

				tz = z * Math.cos(cam.pitch - Math.PI/2) - y * Math.sin(cam.pitch - Math.PI/2);
				ty = z * Math.sin(cam.pitch - Math.PI/2) + y * Math.cos(cam.pitch - Math.PI/2);
				
				x = tx; y = ty; z = tz;
				
				if (z > 0) continue; //TODO need to do this after vertices calculation for each face
				
				//perspective
				
				x = (cam.fl * x) / (cam.fl + z);
				y = (cam.fl * y) / (cam.fl + z);
				
				//sensor to image adjustment
				
				x *= (double) cam.iw / cam.sw;
				y *= (double) cam.ih / cam.sh;
				
				//offsetting onto screen
				
				pxs[i] = cam.iw - (int) (x + cam.iw / 2);
				pys[i] = (int) (y + cam.ih / 2);
				
			}
			
			g.setColor(new Color(triangle.c >> 16 & 0xFF, triangle.c >> 8 & 0xFF, triangle.c >> 0 & 0xFF));

			g.fillPolygon(pxs, pys, 3);
			
		}
		
	}
	*/
	
}
