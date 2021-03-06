package wblut.external.ProGAL;

import java.util.Collection;
import java.util.List;

import wblut.math.WB_Epsilon;

/**
 * Part of ProGAL: http://www.diku.dk/~rfonseca/ProGAL/
 * 
 * Original copyright notice:
 * 
 * Copyright (c) 2013, Dept. of Computer Science - Univ. of Copenhagen. All
 * rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. A sphere represented by a center-point and a
 * radius.
 */
public class Sphere implements Volume {
	protected Point center;
	protected double radius;

	/** Constructs a sphere with the specified center and the specified radius. */
	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Sphere(Point[] points) {
		computeSphere(points);
	}

	public Sphere(Point p0, Point p1, Point p2, Point p3) {
		Point[] points = new Point[4];
		points[0] = p0;
		points[1] = p1;
		points[2] = p2;
		points[3] = p3;
		computeSphere(points);
	}

	public Sphere(CTetrahedron tetr) {
		computeSphere(tetr.getCorners());
	}

	private void computeSphere(Point[] points) {
		Point p0 = points[0];
		Point p1 = points[1];
		Point p2 = points[2];
		Point p3 = points[3];
		computeSphere(p0, p1, p2, p3);
	}

	private void computeSphere(Point p0, Point p1, Point p2, Point p3) {
		double x0 = p0.x();
		double y0 = p0.y();
		double z0 = p0.z();
		double x1 = p1.x();
		double y1 = p1.y();
		double z1 = p1.z();
		double x2 = p2.x();
		double y2 = p2.y();
		double z2 = p2.z();
		double x3 = p3.x();
		double y3 = p3.y();
		double z3 = p3.z();

		double xx0 = x0 * x0 + y0 * y0 + z0 * z0, xx1 = x1 * x1 + y1 * y1 + z1
				* z1;
		double xx2 = x2 * x2 + y2 * y2 + z2 * z2, xx3 = x3 * x3 + y3 * y3 + z3
				* z3;

		double x1y2 = x1 * y2, x1y3 = x1 * y3, x1z2 = x1 * z2, x1z3 = x1 * z3;
		double x2y1 = x2 * y1, x2y3 = x2 * y3, x2z1 = x2 * z1, x2z3 = x2 * z3;
		double x3y2 = x3 * y2, x3y1 = x3 * y1, x3z2 = x3 * z2, x3z1 = x3 * z1;

		double y1z2 = y1 * z2, y1z3 = y1 * z3;
		double y2z1 = y2 * z1, y2z3 = y2 * z3;
		double y3z1 = y3 * z1, y3z2 = y3 * z2;

		double m11 = x0
				* (y1z2 + y3z1 + y2z3 - y1z3 - y2z1 - y3z2)
				- y0
				* (x1z2 + x3z1 + x2z3 - x1z3 - x2z1 - x3z2)
				+ z0
				* (x1y2 + x3y1 + x2y3 - x1y3 - x2y1 - x3y2)
				- ((x1y2 - x2y1) * z3 + (x3y1 - x1y3) * z2 + (x2y3 - x3y2) * z1);

		if (m11 != 0.0) {

			double m12 = xx0
					* (y1z2 + y3z1 + y2z3 - y1z3 - y2z1 - y3z2)
					- y0
					* (xx1 * (z2 - z3) + xx3 * (z1 - z2) + xx2 * (z3 - z1))
					+ z0
					* (xx1 * (y2 - y3) + xx3 * (y1 - y2) + xx2 * (y3 - y1))
					- (xx1 * (y2z3 - y3z2) + xx3 * (y1z2 - y2z1) + xx2
							* (y3z1 - y1z3));

			double m13 = xx0
					* (x1z2 + x3z1 + x2z3 - x1z3 - x2z1 - x3z2)
					- x0
					* (xx1 * (z2 - z3) + xx3 * (z1 - z2) + xx2 * (z3 - z1))
					+ z0
					* (xx1 * (x2 - x3) + xx3 * (x1 - x2) + xx2 * (x3 - x1))
					- (xx1 * (x2z3 - x3z2) + xx3 * (x1z2 - x2z1) + xx2
							* (x3z1 - x1z3));

			double m14 = xx0
					* (x1y2 + x3y1 + x2y3 - x1y3 - x2y1 - x3y2)
					- x0
					* (xx1 * (y2 - y3) + xx3 * (y1 - y2) + xx2 * (y3 - y1))
					+ y0
					* (xx1 * (x2 - x3) + xx3 * (x1 - x2) + xx2 * (x3 - x1))
					- (xx1 * (x2y3 - x3y2) + xx3 * (x1y2 - x2y1) + xx2
							* (x3y1 - x1y3));

			double m15 = xx0
					* (z3 * (x1y2 - x2y1) + z2 * (x3y1 - x1y3) + z1
							* (x2y3 - x3y2))
					- x0
					* (xx1 * (y2z3 - y3z2) + xx3 * (y1z2 - y2z1) + xx2
							* (y3z1 - y1z3))
					+ y0
					* (xx1 * (x2z3 - x3z2) + xx3 * (x1z2 - x2z1) + xx2
							* (x3z1 - x1z3))
					- z0
					* (xx1 * (x2y3 - x3y2) + xx3 * (x1y2 - x2y1) + xx2
							* (x3y1 - x1y3));

			double x = 0.5 * m12 / m11;
			double y = -0.5 * m13 / m11;
			double z = 0.5 * m14 / m11;
			center = new Point(x, y, z);
			radius = Math.sqrt(x * x + y * y + z * z - m15 / m11);
		} else
			System.out.println("Points are coplanar");
	}

	/** Get the center */
	@Override
	public Point getCenter() {
		return center;
	}

	/** Get the radius */
	public double getRadius() {
		return radius;
	}

	/** Get the squared radius */
	public double getRadiusSquared() {
		return radius * radius;
	}

	/** Get the surface area */
	public double getSurfaceArea() {
		return 4 * Math.PI * radius * radius;
	}

	/** Get the volume */
	@Override
	public double getVolume() {
		return getSurfaceArea() * radius / 3;
	}

	/** Returns true if the point is inside this sphere */
	public boolean isInside(Point p) {
		return center.distanceSquared(p) < getRadiusSquared();
	}

	public boolean isInside(Point p, double eps) {
		return center.distanceSquared(p) < radius * radius - eps;
	}

	public boolean isEmpty(Point[] points, double eps) {
		for (int i = 0; i < points.length; i++)
			if (isInside(points[i], eps))
				return false;
		return true;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public void setCenter(Point p0, Point p1, Point p2, Point p3) {
		computeSphere(p0, p1, p2, p3);
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	/** Returns true if this sphere is intersected or touched by another sphere. */
	public boolean isIntersected(Sphere sphere) {
		return overlaps(sphere);
	}

	/** Gets the secant on the line. TODO: Rename. */
	public LineSegment getIntersection(Line line) {
		Point p1 = line.getP();
		Point p2 = line.getPoint(1.0);
		double dx = p2.x() - p1.x();
		double dy = p2.y() - p1.y();
		double dz = p2.z() - p1.z();
		double ex = p1.x() - center.x();
		double ey = p1.y() - center.y();
		double ez = p1.z() - center.z();
		double a = dx * dx + dy * dy + dz * dz;
		double b = 2 * (dx * ex + dy * ey + dz * ez);
		double c = center.x()
				* center.x()
				+ center.y()
				* center.y()
				+ center.z()
				* center.z()
				+ p1.x()
				* p1.x()
				+ p1.y()
				* p1.y()
				+ p1.z()
				* p1.z()
				- 2
				* (center.x() * p1.x() + center.y() * p1.y() + center.z()
						* p1.z()) - radius * radius;
		double delta = b * b - 4 * a * c;
		if (delta < 0)
			return null;
		double u1, u2;
		if (delta == 0)
			u1 = u2 = -b / (2 * a);
		else {
			double sqr = Math.sqrt(delta);
			u1 = (-b + sqr) / (2 * a);
			u2 = (-b - sqr) / (2 * a);
		}
		return new LineSegment(new Point(p1.x() + u1 * dx, p1.y() + u1 * dy,
				p1.z() + u1 * dz), new Point(p1.x() + u2 * dx,
				p1.y() + u2 * dy, p1.z() + u2 * dz));
	}

	/**
	 * Returns the two line-parameters that indicate where <code>line</code>
	 * intersects this sphere. TODO: Coordinate line-intersection methods (see
	 * above).
	 */
	public double[] intersectionParameters(Line line) {
		Vector l = line.getDir();// .norm();
		Vector c = line.getP().vectorTo(center);
		double lc = l.dot(c);
		double cc = c.dot(c);
		double rr = radius * radius;
		double tmp = lc * lc - cc + rr;
		if (tmp < 0)
			return new double[0];
		else if (tmp == 0)
			return new double[] { lc };
		else {
			double d1 = lc - Math.sqrt(tmp);
			double d2 = lc + Math.sqrt(tmp);
			return new double[] { d1, d2 };
		}
	}

	public Point[] getIntersections(Circle c) {
		Plane plane = new Plane(c.getCenter(), c.getNormalVector());
		Circle c2 = plane.getIntersection(this);
		if (c2 != null)
			return c.getIntersection(c2);
		else
			return null;
	}

	public Double getIntersectionAngle(Circle c, Point p, Vector dir) {
		Plane plane = new Plane(c.getCenter(), c.getNormalVector());
		Circle c2 = plane.getIntersection(this);
		if (c2 != null)
			return c.getFirstIntersection(c2, p, dir);
		else
			return null;
	}

	/** Returns true if none of the given points is in the sphere. */
	public boolean containsNone(List<Point> points) {
		double rr = radius * radius - 0.000000001;
		for (Point p : points)
			if (p.distanceSquared(center) < rr)
				return false;
		return true;
	}

	/** Returns true if the given point is in the sphere. */
	public boolean contains(Point p) {
		double rr = radius * radius - 0.000000001;
		return p.distanceSquared(center) < rr;
	}

	/**
	 * Gets the squared distance of a point from a sphere surface (negative if
	 * the point is inside the sphere).
	 */
	public double powerDistance(Point p) {
		return center.distanceSquared(p) - radius * radius;
	}

	/** Return a string representation of this sphere. */
	@Override
	public String toString() {
		return toString(2);
	}

	/**
	 * Return a string representation of this sphere with <code>dec</code>
	 * decimals precision
	 */
	public String toString(int dec) {
		return String.format("Sphere3d[%s,%" + dec + "f]",
				center.toString(dec), radius);
	}

	/** Writes this sphere to <code>System.out</code>. */
	public void toConsole() {
		System.out.println(toString());
	}

	/**
	 * Writes this sphere to <code>System.out</code> with <code>dec</code>
	 * decimals precision.
	 */
	public void toConsole(int dec) {
		System.out.println(toString(dec));
	}

	/**
	 * Returns true if the sphere overlaps with <code>vol</code>. TODO:
	 * Implement for all volumes.
	 */
	@Override
	public boolean overlaps(Volume vol) {
		if (vol instanceof Sphere)
			return ((Sphere) vol).center.distance(this.center) <= ((Sphere) vol).radius
					+ radius;
		throw new IllegalArgumentException();
	}

	/** Returns a deep clone of this sphere. */
	@Override
	public Sphere clone() {
		return new Sphere(center.clone(), radius);
	}

	/** Get the sphere with the specified circle as equator */
	public static Sphere getMinSphere(Circle c) {
		return new Sphere(c.getCenter(), c.getRadius());
	}

	/** Get the smallest sphere through two given points. */
	public static Sphere getMinSphere(Point p1, Point p2) {
		return new Sphere(Point.getMidpoint(p1, p2), p1.distance(p2) / 2);
	}

	/** Get the smallest sphere through three points. */
	public static Sphere getMinSphere(Point p0, Point p1, Point p2) {
		Point center = new Point((p0.x() + p1.x() + p2.x()) / 3, (p0.y()
				+ p1.y() + p2.y()) / 3, (p0.z() + p1.z() + p2.z()) / 3);
		double radius = p0.distance(center);
		return new Sphere(center, radius);
	}

	/**
	 * Constructs the sphere through four points. An error is thrown if the
	 * points are coplanar.
	 */
	public static Sphere getMinSphere(Point p0, Point p1, Point p2, Point p3) {
		double x0 = p0.x();
		double y0 = p0.y();
		double z0 = p0.z();
		double x1 = p1.x();
		double y1 = p1.y();
		double z1 = p1.z();
		double x2 = p2.x();
		double y2 = p2.y();
		double z2 = p2.z();
		double x3 = p3.x();
		double y3 = p3.y();
		double z3 = p3.z();

		double xx0 = x0 * x0 + y0 * y0 + z0 * z0, xx1 = x1 * x1 + y1 * y1 + z1
				* z1;
		double xx2 = x2 * x2 + y2 * y2 + z2 * z2, xx3 = x3 * x3 + y3 * y3 + z3
				* z3;

		double x1y2 = x1 * y2, x1y3 = x1 * y3, x1z2 = x1 * z2, x1z3 = x1 * z3;
		double x2y1 = x2 * y1, x2y3 = x2 * y3, x2z1 = x2 * z1, x2z3 = x2 * z3;
		double x3y2 = x3 * y2, x3y1 = x3 * y1, x3z2 = x3 * z2, x3z1 = x3 * z1;

		double y1z2 = y1 * z2, y1z3 = y1 * z3;
		double y2z1 = y2 * z1, y2z3 = y2 * z3;
		double y3z1 = y3 * z1, y3z2 = y3 * z2;

		double m11 = x0
				* (y1z2 + y3z1 + y2z3 - y1z3 - y2z1 - y3z2)
				- y0
				* (x1z2 + x3z1 + x2z3 - x1z3 - x2z1 - x3z2)
				+ z0
				* (x1y2 + x3y1 + x2y3 - x1y3 - x2y1 - x3y2)
				- ((x1y2 - x2y1) * z3 + (x3y1 - x1y3) * z2 + (x2y3 - x3y2) * z1);

		if (m11 != 0.0) {

			double m12 = xx0
					* (y1z2 + y3z1 + y2z3 - y1z3 - y2z1 - y3z2)
					- y0
					* (xx1 * (z2 - z3) + xx3 * (z1 - z2) + xx2 * (z3 - z1))
					+ z0
					* (xx1 * (y2 - y3) + xx3 * (y1 - y2) + xx2 * (y3 - y1))
					- (xx1 * (y2z3 - y3z2) + xx3 * (y1z2 - y2z1) + xx2
							* (y3z1 - y1z3));

			double m13 = xx0
					* (x1z2 + x3z1 + x2z3 - x1z3 - x2z1 - x3z2)
					- x0
					* (xx1 * (z2 - z3) + xx3 * (z1 - z2) + xx2 * (z3 - z1))
					+ z0
					* (xx1 * (x2 - x3) + xx3 * (x1 - x2) + xx2 * (x3 - x1))
					- (xx1 * (x2z3 - x3z2) + xx3 * (x1z2 - x2z1) + xx2
							* (x3z1 - x1z3));

			double m14 = xx0
					* (x1y2 + x3y1 + x2y3 - x1y3 - x2y1 - x3y2)
					- x0
					* (xx1 * (y2 - y3) + xx3 * (y1 - y2) + xx2 * (y3 - y1))
					+ y0
					* (xx1 * (x2 - x3) + xx3 * (x1 - x2) + xx2 * (x3 - x1))
					- (xx1 * (x2y3 - x3y2) + xx3 * (x1y2 - x2y1) + xx2
							* (x3y1 - x1y3));

			double m15 = xx0
					* (z3 * (x1y2 - x2y1) + z2 * (x3y1 - x1y3) + z1
							* (x2y3 - x3y2))
					- x0
					* (xx1 * (y2z3 - y3z2) + xx3 * (y1z2 - y2z1) + xx2
							* (y3z1 - y1z3))
					+ y0
					* (xx1 * (x2z3 - x3z2) + xx3 * (x1z2 - x2z1) + xx2
							* (x3z1 - x1z3))
					- z0
					* (xx1 * (x2y3 - x3y2) + xx3 * (x1y2 - x2y1) + xx2
							* (x3y1 - x1y3));

			double x = 0.5 * m12 / m11;
			double y = -0.5 * m13 / m11;
			double z = 0.5 * m14 / m11;
			return new Sphere(new Point(x, y, z), Math.sqrt(x * x + y * y + z
					* z - m15 / m11));
		}
		throw new Error("Points are coplanar");
	}

	/**
	 * Gets the smallest sphere containing a set of points. Uses a randomized,
	 * O(n) expected time algorithm.
	 */
	public static Sphere getMinSphere(PointList points) {
		return getMinSphere(points.getRandomPermutation(), points.size(),
				new PointList());
	}

	private static Sphere getMinSphere(PointList points, int n,
			PointList boundaryPoints) {
		Sphere sphere = null;
		int k = 0;
		switch (boundaryPoints.size()) {
		case 0:
			sphere = getMinSphere(points.get(0), points.get(1));
			k = 2;
			break;
		case 1:
			sphere = getMinSphere(points.get(0), boundaryPoints.get(0));
			k = 1;
			break;
		case 2:
			sphere = getMinSphere(boundaryPoints.get(0), boundaryPoints.get(1));
			break;
		case 3:
			sphere = getMinSphere(boundaryPoints.get(0), boundaryPoints.get(1),
					boundaryPoints.get(2));
			break;
		}

		for (int i = k; i < n + boundaryPoints.size(); i++) {
			Point p = points.get(i);
			if (!boundaryPoints.contains(p)) {
				if (!sphere.isInside(p)) {
					if (boundaryPoints.size() < 3) {
						boundaryPoints.add(p);
						sphere = getMinSphere(points, i - 1, boundaryPoints);
						boundaryPoints.remove(p);
					} else
						sphere = getMinSphere(boundaryPoints.get(0),
								boundaryPoints.get(1), boundaryPoints.get(2), p);
				}
			}
		}
		return sphere;
	}

	/**
	 * Find the two, one or zero points that is at the intersection of the three
	 * sphere shells. If the three spheres intersect in more than two points or
	 * one sphere contains another, the result of this method not specified.
	 * 
	 */
	public static Point[] getIntersections(Sphere s1, Sphere s2, Sphere s3) {
		// See ProGAL/Doc/ThreeSphereIntersection.jpg for derivation of these
		// expressions
		// Inspired by http://mathforum.org/library/drmath/view/63138.html
		double x1 = s1.center.x();
		double y1 = s1.center.y();
		double z1 = s1.center.z();
		double c1Sq = s1.center.dot(s1.center); // 3HOp
		double c2Sq = s2.center.dot(s2.center); // 3HOp
		double r1 = s1.radius, r1Sq = r1 * r1; // 2HOp
		Vector v12 = s2.center.vectorTo(s1.center);
		Vector v23 = s3.center.vectorTo(s2.center);
		double c12 = c1Sq - c2Sq;
		double c23 = c2Sq - s3.center.dot(s3.center); // 3HOp
		double r2Sq = s2.radius * s2.radius; // 1HOp (12)
		double r12 = r2Sq - r1Sq + c12;
		double r23 = s3.radius * s3.radius - r2Sq + c23; // 3HOp
		double Dyz = v12.y() * v23.z() - v23.y() * v12.z(), DyzSq = Dyz * Dyz; // 3HOp
		double Dry = r12 * v23.y() - r23 * v12.y(), DrySq = Dry * Dry; // 3HOp
		double Dzx = v12.z() * v23.x() - v23.z() * v12.x(), DzxSq = Dzx * Dzx; // 3HOp
		double Dxr = v12.x() * r23 - v23.x() * r12, DxrSq = Dxr * Dxr; // 3HOp
		double Dxy = v12.x() * v23.y() - v23.x() * v12.y(), DxySq = Dxy * Dxy; // 3HOp
																				// (30)

		double k2 = (DyzSq + DzxSq) / DxySq + 1; // 1HOp
		double k1 = (Dyz * Dry + Dzx * Dxr) / DxySq - 4 * (x1 * Dyz + y1 * Dzx)
				/ Dxy - z1; // 7HOp
		double k0 = (DrySq + DxrSq) / (4 * DxySq) + c1Sq - r1Sq - 2
				* (x1 * Dry + y1 * Dxr) / Dxy; // 6HOp

		double d = k1 * k1 - 4 * k2 * k0; // 3HOp
		if (d < -WB_Epsilon.EPSILON)
			return new Point[] {};
		if (d < WB_Epsilon.EPSILON) { // One intersection-point
			double zi = -k1 / (2 * k2); // 2HOp (49)
			return new Point[] { new Point((2 * zi * Dyz + Dry) / (2 * Dxy), (2
					* zi * Dzx + Dxr)
					/ (2 * Dxy), zi) // 8HOp (57)
			};
		}
		double dRt = Math.sqrt(d); // 1HOp (50)
		double zi0 = (-k1 - dRt) / (2 * k2); // 2HOp
		double zi1 = (-k1 + dRt) / (2 * k2); // 2HOp

		return new Point[] {
				new Point((2 * zi0 * Dyz + Dry) / (2 * Dxy),
						(2 * zi0 * Dzx + Dxr) / (2 * Dxy), zi0), // 8HOp
				new Point((2 * zi1 * Dyz + Dry) / (2 * Dxy),
						(2 * zi1 * Dzx + Dxr) / (2 * Dxy), zi1) // 8HOp (70)
		};
	}

	/**
	 * Estimate the volume of the union of a set of spheres. A grid is placed
	 * around the spheres and the volume of the cells are used to compute an
	 * upper and a lower bound. The average value of the upper and lower bound
	 * is used. Typically the result is accurate to 1/100 (not to 1/1000) and
	 * takes around 150ms to compute.
	 */
	public static double unionVolume_Grid(Collection<Sphere> spheres) {
		Point[] centers = new Point[spheres.size()];
		double[] radSqs = new double[spheres.size()];
		double[] rads = new double[spheres.size()];
		Point minPoint = new Point(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		Point maxPoint = new Point(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		int c = 0;
		for (Sphere s : spheres) {
			centers[c] = s.center;
			radSqs[c] = s.getRadiusSquared();
			rads[c] = s.radius;
			c++;
			for (int i = 0; i < 3; i++) {
				if (s.center.get(i) - s.radius < minPoint.get(i))
					minPoint.set(i, s.center.get(i) - s.radius);
				if (s.center.get(i) + s.radius > maxPoint.get(i))
					maxPoint.set(i, s.center.get(i) + s.radius);
			}
		}

		// Each dimension is divided into <code>cells</code> cells.
		int cells = 80;
		double[] delta = { (maxPoint.x() - minPoint.x()) / cells,
				(maxPoint.y() - minPoint.y()) / cells,
				(maxPoint.z() - minPoint.z()) / cells };
		// Determine which grid-vertices are inside at least one sphere
		boolean[][][] bits = new boolean[cells + 1][cells + 1][cells + 1];
		int xC = 0, yC = 0, zC = 0;
		for (double x = minPoint.x(); x <= maxPoint.x(); x += delta[0]) {
			yC = 0;
			for (double y = minPoint.y(); y <= maxPoint.y(); y += delta[1]) {
				zC = 0;
				for (double z = minPoint.z(); z <= maxPoint.z(); z += delta[2]) {
					// Determine if (x,y,z) is inside a sphere
					for (int i = 0; i < centers.length; i++) {
						double dX = Math.abs(x - centers[i].x());
						double dY = Math.abs(y - centers[i].y());
						double dZ = Math.abs(z - centers[i].z());
						if (dX > rads[i] || dY > rads[i] || dZ > rads[i])
							continue;
						if (dX * dX + dY * dY + dZ * dZ < radSqs[i]) {
							bits[xC][yC][zC] = true;
							break;
						}
					}
					zC++;
				}
				yC++;
			}
			xC++;
		}

		// Determine how many cells are completely inside a sphere and how many
		// are on the border
		int borderCells = 0;
		int insideCells = 0;
		for (int x = 0; x < cells; x++) {
			for (int y = 0; y < cells; y++) {
				for (int z = 0; z < cells; z++) {
					if (bits[x][y][z] || bits[x][y][z + 1] || bits[x][y + 1][z]
							|| bits[x][y + 1][z + 1] || bits[x + 1][y][z]
							|| bits[x + 1][y][z + 1] || bits[x + 1][y + 1][z]
							|| bits[x + 1][y + 1][z + 1]) {
						if (bits[x][y][z] && bits[x][y][z + 1]
								&& bits[x][y + 1][z] && bits[x][y + 1][z + 1]
								&& bits[x + 1][y][z] && bits[x + 1][y][z + 1]
								&& bits[x + 1][y + 1][z]
								&& bits[x + 1][y + 1][z + 1]) {
							insideCells++;
						} else {
							borderCells++;
						}
					}
				}
			}
		}

		double cellVol = delta[0] * delta[1] * delta[2];
		double insideVol = cellVol * insideCells;
		double borderVol = cellVol * borderCells;
		return insideVol + borderVol / 2;
	}

	/** TODO: Comment, move up and test */
	public PointList generateRandomPointsOnSphere(int n) {
		PointList ret = PointList.generateRandomPointsOnSphere(n);

		for (Point p : ret) {
			p.scaleThis(radius);
			p.addThis(center.toVector());
		}
		return ret;
	}

	/** TODO: Comment, move up and test */
	public PointList generatePointsOnSphere(int n) {
		PointList ret = PointList.generatePointsOnSphere(n);

		for (Point p : ret) {
			p.scaleThis(radius);
			p.addThis(center.toVector());
		}
		return ret;
	}
}
