package wblut.external.ProGAL;

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
 * POSSIBILITY OF SUCH DAMAGE. A tetrahedron is a polyhedron with four
 * triangular faces. It is defined using four corner-points.
 * 
 * @author R.Fonseca
 */
public class Tetrahedron implements Simplex, Volume {
	protected Point[] corners = new Point[4];

	public Tetrahedron(Point p1, Point p2, Point p3, Point p4) {
		corners[0] = p1;
		corners[1] = p2;
		corners[2] = p3;
		corners[3] = p4;
	}

	public Tetrahedron(Point[] corners) {
		this.corners = corners;
	}

	/** Return the specified corner. Throws an error if <code>c<0 || c>3</code>. */
	public Point getCorner(int c) {
		if (c < 0 || c > 3)
			throw new IllegalArgumentException();
		return corners[c];
	}

	/** Return all four corners */
	public Point[] getCorners() {
		return corners;
	}

	/**
	 * Return the specified corner-point. Throws an error if
	 * <code>c<0 || c>3</code>.
	 */
	@Override
	public Point getPoint(int c) {
		if (c < 0 || c > 3)
			throw new IllegalArgumentException();
		return corners[c];
	}

	/**
	 * Return the 'dimension' of this object. Required by the interface Simplex.
	 */
	@Override
	public int getDimension() {
		return 3;
	}

	/** TODO: Comment */
	public void setPoint(int c, Point point) {
		if (c < 0 || c > 3)
			throw new IllegalArgumentException();
		corners[c] = point;
	}

	@Override
	public boolean overlaps(Volume vol) {
		// TODO Auto-generated method stub
		return false;
	}

	/** Get the volume of the tetrahedron. */
	@Override
	public double getVolume() {
		Vector a = corners[3].vectorTo(corners[0]);
		Vector b = corners[3].vectorTo(corners[1]);
		Vector c = corners[3].vectorTo(corners[2]);
		return Math.abs(a.dot(b.crossThis(c))) / 6.0;
	}

	/** Calculate the radius of the insphere. */
	public double getInradius() {
		Vector a = corners[3].vectorTo(corners[0]);
		Vector b = corners[3].vectorTo(corners[1]);
		Vector c = corners[3].vectorTo(corners[2]);
		Vector bXc = b.crossNew(c);
		double sixV = Math.abs(a.dot(bXc));
		Vector cXa = c.crossThis(a);
		Vector aXb = a.crossThis(b);
		double denom = bXc.length() + cXa.length() + aXb.length()
				+ (bXc.addThis(cXa).addThis(aXb).length());
		return sixV / denom;
	}

	/** Calculate the radius of the circumsphere. */
	public double circumradius() {
		Vector a = corners[3].vectorTo(corners[0]);
		Vector b = corners[3].vectorTo(corners[1]);
		Vector c = corners[3].vectorTo(corners[2]);
		Vector O = b.crossNew(c).multiplyThis(a.dot(a));
		O.addThis(c.crossNew(a).multiplyThis(b.dot(b)));
		O.addThis(a.crossNew(b).multiplyThis(c.dot(c)));
		O.multiplyThis(1.0 / (2 * a.dot(b.crossThis(c))));
		return O.length();
	}

	/** Find the center of the circumscribing sphere. */
	public Point circumcenter() {
		Vector a = corners[3].vectorTo(corners[0]);
		Vector b = corners[3].vectorTo(corners[1]);
		Vector c = corners[3].vectorTo(corners[2]);
		Vector O = b.crossNew(c).multiplyThis(a.dot(a));
		O.addThis(c.crossNew(a).multiplyThis(b.dot(b)));
		O.addThis(a.crossNew(b).multiplyThis(c.dot(c)));
		O.multiplyThis(1.0 / (2 * a.dot(b.crossThis(c))));
		return corners[3].add(O);
	}

	/** Find the circumscribing sphere */
	public Sphere circumsphere() {
		Vector a = corners[3].vectorTo(corners[0]);
		Vector b = corners[3].vectorTo(corners[1]);
		Vector c = corners[3].vectorTo(corners[2]);
		Vector O = b.crossNew(c).multiplyThis(a.dot(a));
		O.addThis(c.crossNew(a).multiplyThis(b.dot(b)));
		O.addThis(a.crossNew(b).multiplyThis(c.dot(c)));
		O.multiplyThis(1.0 / (2 * a.dot(b.crossThis(c))));
		return new Sphere(corners[3].add(O), O.length());
	}

	/** Find the center of the inscribed sphere. */
	public Point incenter() {
		Vector a = corners[3].vectorTo(corners[0]);
		Vector b = corners[3].vectorTo(corners[1]);
		Vector c = corners[3].vectorTo(corners[2]);
		Vector bXc = b.crossNew(c);
		Vector cXa = c.crossNew(a);
		Vector aXb = a.crossNew(b);
		double bXcLength = bXc.length();
		double cXaLength = cXa.length();
		double aXbLength = aXb.length();
		double dLength = bXc.addThis(cXa).addThis(aXb).length();
		Vector O = a.multiplyThis(bXcLength);
		O.addThis(b.multiplyThis(cXaLength));
		O.addThis(c.multiplyThis(aXbLength));
		O.divideThis(bXcLength + cXaLength + aXbLength + dLength);
		return corners[3].add(O);
	}

	@Override
	public Point getCenter() {
		Vector v = corners[0].vectorTo(corners[1]);
		v.addThis(corners[0].vectorTo(corners[2]));
		v.addThis(corners[0].vectorTo(corners[3]));
		return corners[0].add(v.multiplyThis(0.25));
	}

	/** Returns true if the point p is inside this tetrahedron. */
	// public boolean isInside(Point p) {
	// return isBehind(p,p1,p3,p2) && isBehind(p,p0,p2,p3) &&
	// isBehind(p,p0,p3,p1) && isBehind(p,p0,p1,p2);
	// TODO implement
	// return false;
	// }

	public boolean isInside(Point p) {
		Plane pl012 = new Plane(getCorner(0), getCorner(1), getCorner(2));
		Plane pl013 = new Plane(getCorner(0), getCorner(1), getCorner(3));
		Plane pl023 = new Plane(getCorner(0), getCorner(2), getCorner(3));
		Plane pl123 = new Plane(getCorner(1), getCorner(2), getCorner(3));
		return (((pl012.above(p) == 1) && (pl013.above(p) == 1)
				&& (pl023.above(p) == 1) && (pl123.above(p) == 1)) || ((pl012
				.below(p) == 1)
				&& (pl013.below(p) == 1)
				&& (pl023.below(p) == 1) && (pl123.below(p) == 1)));
	}

	/*
	 * returns TRUE if the tetrahedron is acute. Tetrahedron is acute if all its
	 * dihedral angles are acute (< 90�) added by pawel 12-11-2011
	 */

	public boolean isAcute() {
		return ((Point.getCosDihedralAngle(corners[0], corners[1], corners[2],
				corners[3]) > 0.0)
				&& (Point.getCosDihedralAngle(corners[0], corners[1],
						corners[3], corners[2]) > 0.0)
				&& (Point.getCosDihedralAngle(corners[0], corners[2],
						corners[3], corners[1]) > 0.0)
				&& (Point.getCosDihedralAngle(corners[2], corners[0],
						corners[1], corners[3]) > 0.0)
				&& (Point.getCosDihedralAngle(corners[1], corners[0],
						corners[2], corners[3]) > 0.0) && (Point
				.getCosDihedralAngle(corners[1], corners[0], corners[3],
						corners[2]) > 0.0));
	}

	@Override
	public Volume clone() {
		return new Tetrahedron(corners[0].clone(), corners[1].clone(),
				corners[2].clone(), corners[3].clone());
	}

	/** Return a string representation of this tetrahedron. */
	@Override
	public String toString() {
		return toString(2);
	}

	/**
	 * Return a string representation of this tetrahedron with <code>dec</code>
	 * decimals precision
	 */
	public String toString(int dec) {
		return String.format("Tetrahedron[%s,%s,%s,%s]",
				corners[0].toString(dec), corners[1].toString(dec),
				corners[2].toString(dec), corners[3].toString(dec));
	}

	/** Writes this tetrahedron to <code>System.out</code>. */
	public void toConsole() {
		System.out.println(toString());
	}

	/**
	 * Writes this tetrahedron to <code>System.out</code> with <code>dec</code>
	 * decimals precision.
	 */
	public void toConsole(int dec) {
		System.out.println(toString(dec));
	}

	public static void main(String[] args) {
		Point p1 = new Point(1, 0, 0);
		Point p2 = new Point(1, 1, 0);
		Point p3 = new Point(2, 2, 3);
		Point p4 = new Point(3, 4, 2);
		Point p5 = new Point(0, 0, 3);
		Tetrahedron tetr = new Tetrahedron(p2, p1, p3, p4);
		if (tetr.isInside(p5))
			System.out.println("inside");
		else
			System.out.println("outside");

	}
}
