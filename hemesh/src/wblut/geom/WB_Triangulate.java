package wblut.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import wblut.WB_Epsilon;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;

public class WB_Triangulate {
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	private WB_Triangulate() {

	}

	public static WB_Triangulation3D getTriangulation3D(
			final WB_Coordinate[] points) {
		final WB_Triangulation3D result = new WB_Triangulation3D(
				WB_Delaunay.getTriangulation3D(points, 0.0001).Tri);
		return result;

	}

	public static WB_Triangulation3D getTriangulation3D(
			final List<? extends WB_Coordinate> points) {
		final WB_Triangulation3D result = new WB_Triangulation3D(
				WB_Delaunay.getTriangulation3D(points, 0.0001).Tri);
		return result;
	}

	public static WB_Triangulation3D getTriangulation3D(
			final WB_CoordinateSequence points) {
		final WB_Triangulation3D result = new WB_Triangulation3D(
				WB_Delaunay.getTriangulation3D(points, 0.0001).Tri);
		return result;

	}

	/**
	 * Planar Delaunay triangulation.
	 */
	public static WB_Triangulation2D getTriangulation2D(
			final WB_Coordinate[] points) {
		final int n = points.length;

		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(new Coordinate(points[i].xd(), points[i].yd(), i));
		}
		final WB_Triangulation2D result = getTriangles2D(coords);
		return result;
	}

	public static WB_Triangulation2D getTriangulation2D(
			final List<? extends WB_Coordinate> points) {
		final int n = points.size();

		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Coordinate p : points) {
			coords.add(new Coordinate(p.xd(), p.yd(), id));
			id++;
		}
		final WB_Triangulation2D result = getTriangles2D(coords);
		return result;
	}

	public static WB_Triangulation2D getTriangulation2D(
			final WB_CoordinateSequence points) {
		final int n = points.size();

		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int i = 0;
		for (int j = 0; j < points.size(); j++) {
			coords.add(new Coordinate(points.getRaw(i++), points.getRaw(i++), j));
			i++;

		}

		final WB_Triangulation2D result = getTriangles2D(coords);

		return result;
	}

	public static WB_Triangulation2D getTriangulation2D(
			final WB_Coordinate[] points, final WB_Context2D context) {
		final int n = points.length;

		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		final WB_Point tmp = geometryfactory.createPoint();
		for (int i = 0; i < n; i++) {
			context.pointTo2D(points[i], tmp);
			coords.add(new Coordinate(tmp.xd(), tmp.yd(), i));
		}
		final WB_Triangulation2D result = getTriangles2D(coords);

		return result;
	}

	public static WB_Triangulation2D getTriangulation2D(
			final List<? extends WB_Coordinate> points,
			final WB_Context2D context) {
		final int n = points.size();

		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		final WB_Point tmp = geometryfactory.createPoint();
		for (final WB_Coordinate p : points) {
			context.pointTo2D(p, tmp);
			coords.add(new Coordinate(tmp.xd(), tmp.yd(), id));
			id++;
		}
		final WB_Triangulation2D result = getTriangles2D(coords);
		return result;
	}

	public static WB_Triangulation2D getTriangulation2D(
			final WB_CoordinateSequence points, final WB_Context2D context) {
		final int n = points.size();

		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		final WB_Point tmp = geometryfactory.createPoint();
		for (int j = 0; j < points.size(); j++) {
			context.pointTo2D(points.get(j, 0), points.get(j, 1),
					points.get(j, 2), tmp);
			coords.add(new Coordinate(tmp.xd(), tmp.yd(), j));
		}
		final WB_Triangulation2D result = getTriangles2D(coords);

		return result;
	}

	private static WB_Triangulation2D getTriangles2D(
			final ArrayList<Coordinate> coords) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qesd = dtb.getSubdivision();
		final GeometryCollection tris = (GeometryCollection) qesd
				.getTriangles(new GeometryFactory());
		final int ntris = tris.getNumGeometries();
		List<int[]> result = new FastList<int[]>(ntris);
		for (int i = 0; i < ntris; i++) {
			final Polygon tri = (Polygon) tris.getGeometryN(i);
			final Coordinate[] tricoord = tri.getCoordinates();
			final int[] triind = new int[3];
			for (int j = 0; j < 3; j++) {
				triind[j] = (int) tricoord[j].z;
			}
			result.add(triind);
		}
		final int[][] T = new int[result.size()][3];
		for (int i = 0; i < result.size(); i++) {
			T[i][0] = result.get(i)[0];
			T[i][1] = result.get(i)[1];
			T[i][2] = result.get(i)[2];
		}
		final MultiLineString edges = (MultiLineString) qesd
				.getEdges(new GeometryFactory());
		final int nedges = edges.getNumGeometries();
		result = new FastList<int[]>(nedges);
		for (int i = 0; i < nedges; i++) {
			final LineString edge = (LineString) edges.getGeometryN(i);
			final Coordinate[] edgecoord = edge.getCoordinates();
			final int[] edgeind = new int[2];
			for (int j = 0; j < 2; j++) {
				edgeind[j] = (int) edgecoord[j].z;
			}
			result.add(edgeind);
		}
		final int[][] E = new int[result.size()][2];
		for (int i = 0; i < result.size(); i++) {
			E[i][0] = result.get(i)[0];
			E[i][1] = result.get(i)[1];
		}
		return new WB_Triangulation2D(T, E);
	}

	public static WB_Triangulation2DWithPoints getConformingTriangulation2D(
			final WB_Coordinate[] points, final WB_Context2D context) {
		final int[] constraints = new int[2 * points.length];
		for (int i = 0, j = points.length - 1; i < points.length; j = i++) {
			constraints[2 * i] = j;
			constraints[2 * i + 1] = i;
		}
		final int n = points.length;

		final Coordinate[] coords = new Coordinate[n];
		final WB_Point point = geometryfactory.createPoint();

		for (int i = 0; i < n; i++) {
			context.pointTo2D(points[i], point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}

		return getConformingTriangles2D(coords, constraints, WB_Epsilon.EPSILON);
	}

	public static WB_Triangulation2DWithPoints getConformingTriangulation2D(
			final WB_Coordinate[] points, final int[] constraints,
			final WB_Context2D context) {
		if (constraints == null) {
			return new WB_Triangulation2DWithPoints(getTriangulation2D(points,
					context));
		}
		final int m = constraints.length;
		if ((m == 0) || (m % 2 == 1)) {
			return new WB_Triangulation2DWithPoints(getTriangulation2D(points,
					context));
		}
		final int n = points.length;

		final Coordinate[] coords = new Coordinate[n];
		final WB_Point point = geometryfactory.createPoint();

		for (int i = 0; i < n; i++) {
			context.pointTo2D(points[i], point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}

		return getConformingTriangles2D(coords, constraints, WB_Epsilon.EPSILON);
	}

	public static WB_Triangulation2DWithPoints getConformingTriangulation2D(
			final WB_Coordinate[] points) {
		final int[] constraints = new int[2 * points.length];
		for (int i = 0, j = points.length - 1; i < points.length; j = i++) {
			constraints[2 * i] = j;
			constraints[2 * i + 1] = i;
		}
		final int n = points.length;

		final Coordinate[] coords = new Coordinate[n];
		for (int i = 0; i < n; i++) {
			coords[i] = new Coordinate(points[i].xd(), points[i].yd(), i);
		}

		return getConformingTriangles2D(coords, constraints, WB_Epsilon.EPSILON);
	}

	public static WB_Triangulation2DWithPoints getConformingTriangulation2D(
			final WB_Coordinate[] points, final int[] constraints) {
		if (constraints == null) {
			return new WB_Triangulation2DWithPoints(getTriangulation2D(points));
		}
		final int m = constraints.length;
		if ((m == 0) || (m % 2 == 1)) {
			return new WB_Triangulation2DWithPoints(getTriangulation2D(points));
		}
		final int n = points.length;

		final Coordinate[] coords = new Coordinate[n];
		for (int i = 0; i < n; i++) {
			coords[i] = new Coordinate(points[i].xd(), points[i].yd(), i);
		}

		return getConformingTriangles2D(coords, constraints, WB_Epsilon.EPSILON);
	}

	public static WB_Triangulation2DWithPoints getConformingTriangulation2D(
			final WB_Coordinate[] points, final double tol) {
		final int[] constraints = new int[2 * points.length];
		for (int i = 0, j = points.length - 1; i < points.length; j = i++) {
			constraints[2 * i] = j;
			constraints[2 * i + 1] = i;
		}
		final int n = points.length;

		final Coordinate[] coords = new Coordinate[n];
		for (int i = 0; i < n; i++) {
			coords[i] = new Coordinate(points[i].xd(), points[i].yd(), i);
		}

		return getConformingTriangles2D(coords, constraints, tol);
	}

	public static WB_Triangulation2DWithPoints getConformingTriangulation2D(
			final WB_Coordinate[] points, final int[] constraints,
			final double tol) {
		if (constraints == null) {
			return new WB_Triangulation2DWithPoints(getTriangulation2D(points));
		}
		final int m = constraints.length;
		if ((m == 0) || (m % 2 == 1)) {
			return new WB_Triangulation2DWithPoints(getTriangulation2D(points));
		}
		final int n = points.length;

		final Coordinate[] coords = new Coordinate[n];
		for (int i = 0; i < n; i++) {
			coords[i] = new Coordinate(points[i].xd(), points[i].yd(), i);
		}

		return getConformingTriangles2D(coords, constraints, tol);
	}

	private static WB_Triangulation2DWithPoints getConformingTriangles2D(
			final Coordinate[] coords, final int[] constraints, final double tol) {
		final int m = constraints.length;
		final GeometryFactory geomFact = new GeometryFactory();
		final LineString[] constraintlines = new LineString[m / 2];
		for (int i = 0; i < m; i += 2) {
			final Coordinate[] pair = { coords[constraints[i]],
					coords[constraints[i + 1]] };
			constraintlines[i / 2] = geomFact.createLineString(pair);
		}
		final ConformingDelaunayTriangulationBuilder dtb = new ConformingDelaunayTriangulationBuilder();
		dtb.setTolerance(tol);
		dtb.setSites(geomFact.createMultiPoint(coords));
		dtb.setConstraints(geomFact.createMultiLineString(constraintlines));
		final QuadEdgeSubdivision qesd = dtb.getSubdivision();
		final GeometryCollection tris = (GeometryCollection) qesd
				.getTriangles(new GeometryFactory());
		final Coordinate[] newcoords = tris.getCoordinates();
		final List uniquePoints = new FastList();
		final WB_KDTree<WB_Point, Integer> tree = new WB_KDTree<WB_Point, Integer>();
		int currentSize = 0;
		for (final Coordinate newcoord : newcoords) {
			final WB_Point p = geometryfactory.createPoint(newcoord.x,
					newcoord.y, 0);
			final Integer index = tree.add(p, currentSize);
			if (index == null) {
				currentSize++;
				uniquePoints.add(p);
			}
		}
		final int ntris = tris.getNumGeometries();
		List<int[]> result = new FastList<int[]>(ntris);
		for (int i = 0; i < ntris; i++) {
			final Polygon tri = (Polygon) tris.getGeometryN(i);
			final Coordinate[] tricoord = tri.getCoordinates();
			final int[] triind = new int[3];
			for (int j = 0; j < 3; j++) {
				triind[j] = tree.add(geometryfactory.createPoint(tricoord[j].x,
						tricoord[j].y, 0), 0);
			}
			result.add(triind);
		}
		final int[][] T = new int[result.size()][3];
		for (int i = 0; i < result.size(); i++) {
			T[i][0] = result.get(i)[0];
			T[i][1] = result.get(i)[1];
			T[i][2] = result.get(i)[2];
		}
		final MultiLineString edges = (MultiLineString) qesd
				.getEdges(new GeometryFactory());
		final int nedges = edges.getNumGeometries();
		result = new FastList<int[]>(nedges);
		for (int i = 0; i < nedges; i++) {
			final LineString edge = (LineString) edges.getGeometryN(i);
			final Coordinate[] edgecoord = edge.getCoordinates();
			final int[] edgeind = new int[2];
			for (int j = 0; j < 2; j++) {
				edgeind[j] = tree.add(geometryfactory.createPoint(
						edgecoord[j].x, edgecoord[j].y, 0), 0);
			}
			result.add(edgeind);
		}
		final int[][] E = new int[result.size()][2];
		for (int i = 0; i < result.size(); i++) {
			E[i][0] = result.get(i)[0];
			E[i][1] = result.get(i)[1];
		}
		final List Points = new FastList(uniquePoints.size());
		for (int i = 0; i < uniquePoints.size(); i++) {
			Points.add(uniquePoints.get(i));
		}
		return new WB_Triangulation2DWithPoints(T, E, Points);
	}

	/**
	 * @author Michael Bedward
	 */

	private static class EdgeFlipper {
		private final List<Coordinate> shellCoords;

		EdgeFlipper(final List<Coordinate> shellCoords) {
			this.shellCoords = Collections.unmodifiableList(shellCoords);
		}

		public boolean flip(final Triangle t0, final Triangle t1) {
			return flip(t0, t1, t0.getSharedVertices(t1));
		}

		public boolean flip(final Triangle ear0, final Triangle ear1,
				final int[] sharedVertices) {
			if (sharedVertices == null || sharedVertices.length != 2) {
				return false;
			}
			final Coordinate shared0 = shellCoords.get(sharedVertices[0]);
			final Coordinate shared1 = shellCoords.get(sharedVertices[1]);
			/*
			 * Find the unshared vertex of each ear
			 */
			int[] vertices = ear0.getVertices();
			int i = 0;
			while (vertices[i] == sharedVertices[0]
					|| vertices[i] == sharedVertices[1]) {
				i++;
			}
			final int v0 = vertices[i];
			final Coordinate c0 = shellCoords.get(v0);
			i = 0;
			vertices = ear1.getVertices();
			while (vertices[i] == sharedVertices[0]
					|| vertices[i] == sharedVertices[1]) {
				i++;
			}
			final int v1 = vertices[i];
			final Coordinate c1 = shellCoords.get(v1);
			/*
			 * The candidate new edge is from v0 to v1. First check if this is
			 * inside the quadrilateral
			 */
			final Coordinate[] quadRing = { c0, shared0, c1, shared1 };
			final int dir0 = CGAlgorithms.orientationIndex(c0, c1, shared0);
			final int dir1 = CGAlgorithms.orientationIndex(c0, c1, shared1);
			if (dir0 == -dir1) {
				// The candidate edge is inside. Compare its length to
				// the current shared edge and swap them if the candidate
				// is shorter.
				if (c0.distance(c1) < shared0.distance(shared1)) {
					ear0.setVertices(sharedVertices[0], v0, v1);
					ear1.setVertices(v1, v0, sharedVertices[1]);
					return true;
				}
			}
			return false;
		}
	}

	private static class Triangle {
		private final int[] vertices;

		/**
		 * Constructor. No checking is done on the values supplied.
		 * 
		 * @param v0
		 *            first vertex
		 * @param v1
		 *            second vertex
		 * @param v2
		 *            third vertex
		 */
		public Triangle(final int v0, final int v1, final int v2) {
			vertices = new int[3];
			setVertices(v0, v1, v2);
		}

		/**
		 * Set the vertex indices for this Triangle. No checking is done on the
		 * values supplied.
		 * 
		 * @param v0
		 *            first vertex
		 * @param v1
		 *            second vertex
		 * @param v2
		 *            third vertex
		 */
		public void setVertices(final int v0, final int v1, final int v2) {
			vertices[0] = v0;
			vertices[1] = v1;
			vertices[2] = v2;
		}

		/**
		 * Get this Triangle's vertex indices
		 * 
		 * @return a new array with the vertex indices
		 */
		public int[] getVertices() {
			final int[] copy = new int[3];
			for (int i = 0; i < 3; i++) {
				copy[i] = vertices[i];
			}
			return copy;
		}

		/**
		 * Return vertex indices shared with another triangle.
		 * 
		 * @param other
		 *            other Triangle
		 * @return {@code null} if no vertices are shared; otherwise an array
		 *         containing 1, 2 or 3 vertex indices
		 */
		public int[] getSharedVertices(final Triangle other) {
			int count = 0;
			final boolean[] shared = new boolean[3];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (vertices[i] == other.vertices[j]) {
						count++;
						shared[i] = true;
					}
				}
			}
			int[] common = null;
			if (count > 0) {
				common = new int[count];
				for (int i = 0, k = 0; i < 3; i++) {
					if (shared[i]) {
						common[k++] = vertices[i];
					}
				}
			}
			return common;
		}

		/**
		 * Return a string representation of this Triangle
		 * 
		 * @return string of the form "Triangle(%d %d %d)"
		 */
		@Override
		public String toString() {
			return String.format("Triangle(%d %d %d)", vertices[0],
					vertices[1], vertices[2]);
		}
	}

	private static List<Coordinate> shellCoords;
	private static boolean[] shellCoordAvailable;

	/**
	 * Get the result triangular polygons.
	 * 
	 * @return triangles as a GeometryCollection
	 */
	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final WB_Coordinate[] polygon, final WB_Context2D context) {
		return getPolygonTriangulation2D(polygon, true, context);
	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final WB_Coordinate[] polygon, final boolean optimize,
			final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[polygon.length + 1];
		WB_Point point = geometryfactory.createPoint();

		for (int i = 0; i < polygon.length; i++) {
			context.pointTo2D(polygon[i], point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(polygon[0], point);
		coords[polygon.length] = new Coordinate(point.xd(), point.yd(), 0);
		final Polygon inputPolygon = new GeometryFactory()
				.createPolygon(coords);
		final int[][] ears = triangulate(inputPolygon, optimize);
		final int[][] E = extractEdges(ears);
		final List Points = new FastList(shellCoords.size() - 1);
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			point = geometryfactory.createPoint();
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}

		return new WB_Triangulation2DWithPoints(ears, E, Points);
	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final int[] polygon, final WB_Coordinate[] points,
			final boolean optimize, final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[polygon.length + 1];
		WB_Point point = geometryfactory.createPoint();

		for (int i = 0; i < polygon.length; i++) {
			context.pointTo2D(points[polygon[i]], point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(points[polygon[0]], point);
		coords[polygon.length] = new Coordinate(point.xd(), point.yd(), 0);
		final Polygon inputPolygon = new GeometryFactory()
				.createPolygon(coords);
		final int[][] ears = triangulate(inputPolygon, optimize);
		for (int i = 0; i < ears.length; i++) {
			ears[i][0] = polygon[ears[i][0]];
			ears[i][1] = polygon[ears[i][0]];
			ears[i][2] = polygon[ears[i][0]];
		}
		final int[][] E = extractEdges(ears);

		final List Points = new FastList(shellCoords.size() - 1);

		for (int i = 0; i < shellCoords.size() - 1; i++) {
			point = geometryfactory.createPoint();
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}

		return new WB_Triangulation2DWithPoints(ears, E, Points);
	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final int[] polygon, final WB_CoordinateSequence points,
			final boolean optimize, final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[polygon.length + 1];
		WB_Point point = geometryfactory.createPoint();

		for (int i = 0; i < polygon.length; i++) {
			context.pointTo2D(points, polygon[i], point);

			coords[i] = new Coordinate(point.xd(), point.yd());
		}
		context.pointTo2D(points, polygon[0], point);

		coords[polygon.length] = new Coordinate(point.xd(), point.yd());

		final Polygon inputPolygon = new GeometryFactory()
				.createPolygon(coords);
		final int[][] ears = triangulate(inputPolygon, optimize);
		for (int i = 0; i < ears.length; i++) {
			ears[i][0] = polygon[ears[i][0]];
			ears[i][1] = polygon[ears[i][1]];
			ears[i][2] = polygon[ears[i][2]];
		}
		final int[][] E = extractEdges(ears);

		final List Points = new FastList(shellCoords.size() - 1);
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			point = geometryfactory.createPoint();
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}

		return new WB_Triangulation2DWithPoints(ears, E, Points);
	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final List<? extends WB_Coordinate> polygon,
			final WB_Context2D context) {
		return getPolygonTriangulation2D(polygon, true, context);
	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final List<? extends WB_Coordinate> polygon,
			final boolean optimize, final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[polygon.size() + 1];
		final WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < polygon.size(); i++) {
			context.pointTo2D(polygon.get(i), point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(polygon.get(0), point);
		coords[polygon.size()] = new Coordinate(point.xd(), point.yd(), 0);
		final Polygon inputPolygon = new GeometryFactory()
				.createPolygon(coords);
		final int[][] ears = triangulate(inputPolygon, optimize);
		final int[][] E = extractEdges(ears);
		final List Points = new FastList(shellCoords.size() - 1);
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}

		return new WB_Triangulation2DWithPoints(ears, E, Points);
	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final int[] polygon, final List<? extends WB_Coordinate> points,
			final boolean optimize, final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[polygon.length + 1];
		final WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < polygon.length; i++) {
			context.pointTo2D(points.get(polygon[i]), point);
			coords[i] = new Coordinate(point.xd(), point.yd(), polygon[i]);
		}
		context.pointTo2D(points.get(polygon[0]), point);
		coords[polygon.length] = new Coordinate(point.xd(), point.yd(),
				polygon[0]);
		final Polygon inputPolygon = new GeometryFactory()
				.createPolygon(coords);
		final int[][] ears = triangulate(inputPolygon, optimize);
		for (int i = 0; i < ears.length; i++) {
			ears[i][0] = (int) shellCoords.get(ears[i][0]).z;
			ears[i][1] = (int) shellCoords.get(ears[i][1]).z;
			ears[i][2] = (int) shellCoords.get(ears[i][2]).z;
		}
		final int[][] E = extractEdges(ears);

		final List Points = new FastList(shellCoords.size() - 1);
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}

		return new WB_Triangulation2DWithPoints(ears, E, Points);
	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final List<? extends WB_Coordinate> outerPolygon,
			final List<? extends WB_Coordinate> innerPolygon,
			final boolean optimize, final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[outerPolygon.size() + 1];
		final WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < outerPolygon.size(); i++) {
			context.pointTo2D(outerPolygon.get(i), point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(outerPolygon.get(0), point);
		coords[outerPolygon.size()] = new Coordinate(point.xd(), point.yd(), 0);
		final Coordinate[] icoords = new Coordinate[innerPolygon.size() + 1];
		for (int i = 0; i < innerPolygon.size(); i++) {
			context.pointTo2D(innerPolygon.get(i), point);
			icoords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(innerPolygon.get(0), point);
		icoords[innerPolygon.size()] = new Coordinate(point.xd(), point.yd(), 0);
		final LinearRing shell = new GeometryFactory().createLinearRing(coords);
		final LinearRing hole = new GeometryFactory().createLinearRing(icoords);
		final LinearRing[] holes = new LinearRing[1];
		holes[0] = hole;
		final Polygon inputPolygon = new GeometryFactory().createPolygon(shell,
				holes);
		final int[][] ears = triangulate(inputPolygon, optimize);
		final int[][] E = extractEdges(ears);
		final List Points = new FastList(shellCoords.size());
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}
		return new WB_Triangulation2DWithPoints(ears, E, Points);

	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final List<? extends WB_Coordinate> outerPolygon,
			final List<? extends WB_Coordinate>[] innerPolygons,
			final boolean optimize, final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[outerPolygon.size() + 1];
		final WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < outerPolygon.size(); i++) {
			context.pointTo2D(outerPolygon.get(i), point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(outerPolygon.get(0), point);
		coords[outerPolygon.size()] = new Coordinate(point.xd(), point.yd(), 0);
		final LinearRing[] holes = new LinearRing[innerPolygons.length];
		for (int j = 0; j < innerPolygons.length; j++) {
			final Coordinate[] icoords = new Coordinate[innerPolygons[j].size() + 1];
			for (int i = 0; i < innerPolygons[j].size(); i++) {
				context.pointTo2D(innerPolygons[j].get(i), point);
				icoords[i] = new Coordinate(point.xd(), point.yd(), i);
			}
			context.pointTo2D(innerPolygons[j].get(0), point);
			icoords[innerPolygons[j].size()] = new Coordinate(point.xd(),
					point.yd(), 0);

			final LinearRing hole = new GeometryFactory()
					.createLinearRing(icoords);

			holes[j] = hole;
		}
		final LinearRing shell = new GeometryFactory().createLinearRing(coords);
		final Polygon inputPolygon = new GeometryFactory().createPolygon(shell,
				holes);
		final int[][] ears = triangulate(inputPolygon, optimize);
		final int[][] E = extractEdges(ears);
		final List Points = new FastList(shellCoords.size());
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}
		return new WB_Triangulation2DWithPoints(ears, E, Points);

	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final WB_Coordinate[] outerPolygon,
			final WB_Coordinate[] innerPolygon, final boolean optimize,
			final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[outerPolygon.length + 1];
		final WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < outerPolygon.length; i++) {
			context.pointTo2D(outerPolygon[i], point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(outerPolygon[0], point);
		coords[outerPolygon.length] = new Coordinate(point.xd(), point.yd(), 0);
		final Coordinate[] icoords = new Coordinate[innerPolygon.length + 1];
		for (int i = 0; i < innerPolygon.length; i++) {
			context.pointTo2D(innerPolygon[i], point);
			icoords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(innerPolygon[0], point);
		icoords[innerPolygon.length] = new Coordinate(point.xd(), point.yd(), 0);
		final LinearRing shell = new GeometryFactory().createLinearRing(coords);
		final LinearRing hole = new GeometryFactory().createLinearRing(icoords);
		final LinearRing[] holes = new LinearRing[1];
		holes[0] = hole;
		final Polygon inputPolygon = new GeometryFactory().createPolygon(shell,
				holes);
		final int[][] ears = triangulate(inputPolygon, optimize);
		final int[][] E = extractEdges(ears);
		final List Points = new FastList(shellCoords.size());
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}
		return new WB_Triangulation2DWithPoints(ears, E, Points);

	}

	public static WB_Triangulation2DWithPoints getPolygonTriangulation2D(
			final WB_Coordinate[] outerPolygon,
			final WB_Coordinate[][] innerPolygons, final boolean optimize,
			final WB_Context2D context) {
		// Polygon inputPolygon=polygon.;

		final Coordinate[] coords = new Coordinate[outerPolygon.length + 1];
		final WB_Point point = geometryfactory.createPoint();
		for (int i = 0; i < outerPolygon.length; i++) {
			context.pointTo2D(outerPolygon[i], point);
			coords[i] = new Coordinate(point.xd(), point.yd(), i);
		}
		context.pointTo2D(outerPolygon[0], point);
		new Coordinate(point.xd(), point.yd(), 0);
		final LinearRing shell = new GeometryFactory().createLinearRing(coords);
		final LinearRing[] holes = new LinearRing[innerPolygons.length];
		for (int j = 0; j < innerPolygons.length; j++) {
			final Coordinate[] icoords = new Coordinate[innerPolygons[j].length + 1];
			for (int i = 0; i < innerPolygons[j].length; i++) {
				context.pointTo2D(innerPolygons[j][i], point);
				icoords[i] = new Coordinate(point.xd(), point.yd(), i);
			}
			context.pointTo2D(innerPolygons[j][0], point);
			new Coordinate(point.xd(), point.yd(), 0);

			final LinearRing hole = new GeometryFactory()
					.createLinearRing(icoords);

			holes[j] = hole;
		}
		final Polygon inputPolygon = new GeometryFactory().createPolygon(shell,
				holes);
		final int[][] ears = triangulate(inputPolygon, optimize);
		final int[][] E = extractEdges(ears);
		final List Points = new FastList(shellCoords.size());
		for (int i = 0; i < shellCoords.size() - 1; i++) {
			context.pointTo3D(shellCoords.get(i).x, shellCoords.get(i).y, point);
			Points.add(point);
		}
		return new WB_Triangulation2DWithPoints(ears, E, Points);

	}

	private static int[][] extractEdges(final int[][] ears) {
		final List<int[]> result = new ArrayList<int[]>();
		final int f = ears.length;
		final FastMap<Long, int[]> map = new FastMap<Long, int[]>();
		for (final int[] ear : ears) {
			final int v0 = ear[0];
			final int v1 = ear[1];
			final int v2 = ear[2];
			long index = getIndex(v0, v1, f);
			map.put(index, new int[] { v0, v1 });
			index = getIndex(v1, v2, f);
			map.put(index, new int[] { v1, v2 });
			index = getIndex(v2, v0, f);
			map.put(index, new int[] { v2, v0 });
		}
		final int[][] edges = new int[map.size()][2];
		final Collection<int[]> values = map.values();
		int i = 0;
		for (final int[] value : values) {
			edges[i][0] = value[0];
			edges[i][1] = value[1];
			i++;
		}
		return edges;
	}

	private static long getIndex(final int i, final int j, final int f) {
		return (i > j) ? j + i * f : i + j * f;
	}

	/**
	 * Perform the triangulation
	 * 
	 * @param improve
	 *            if true, improvement of the triangulation is attempted as a
	 *            post-processing step
	 * @return GeometryCollection of triangular polygons
	 */
	private static int[][] triangulate(final Polygon inputPolygon,
			final boolean improve) {
		final GeometryFactory gf = new GeometryFactory();
		final List<Triangle> earList = new ArrayList<Triangle>();
		createShell(inputPolygon);
		int N = shellCoords.size() - 1;
		shellCoordAvailable = new boolean[N];
		Arrays.fill(shellCoordAvailable, true);
		boolean finished = false;
		boolean found = false;
		int k0 = 0;
		int k1 = 1;
		int k2 = 2;
		int firstK = 0;
		do {
			found = false;
			while (CGAlgorithms.computeOrientation(shellCoords.get(k0),
					shellCoords.get(k1), shellCoords.get(k2)) == 0) {
				k0 = k1;
				if (k0 == firstK) {
					finished = true;
					break;
				}
				k1 = k2;
				k2 = nextShellCoord(k2 + 1);
			}
			if (!finished && isValidEdge(k0, k2)) {
				final LineString ls = gf.createLineString(new Coordinate[] {
						shellCoords.get(k0), shellCoords.get(k2) });
				if (inputPolygon.covers(ls)) {
					final Polygon earPoly = gf
							.createPolygon(
									gf.createLinearRing(new Coordinate[] {
											shellCoords.get(k0),
											shellCoords.get(k1),
											shellCoords.get(k2),
											shellCoords.get(k0) }), null);
					if (inputPolygon.covers(earPoly)) {
						found = true;
						// System.out.println(earPoly);
						final Triangle ear = new Triangle(k0, k1, k2);
						earList.add(ear);
						shellCoordAvailable[k1] = false;
						N--;
						k0 = nextShellCoord(0);
						k1 = nextShellCoord(k0 + 1);
						k2 = nextShellCoord(k1 + 1);
						firstK = k0;
						if (N < 3) {
							finished = true;
						}
					}
				}
			}
			if (!finished && !found) {
				k0 = k1;
				if (k0 == firstK) {
					finished = true;
				} else {
					k1 = k2;
					k2 = nextShellCoord(k2 + 1);
				}
			}
		} while (!finished);
		if (improve) {
			doImprove(earList);
		}
		final int[][] tris = new int[earList.size()][3];
		for (int i = 0; i < earList.size(); i++) {
			final int[] tri = earList.get(i).getVertices();
			boolean flip = false;
			if (improve) {
				if (CGAlgorithms.orientationIndex(shellCoords.get(tri[0]),
						shellCoords.get(tri[1]), shellCoords.get(tri[2])) > 0) {
					flip = true;

				}
			}
			if (flip) {
				tris[i][0] = tri[1];
				tris[i][1] = tri[0];
				tris[i][2] = tri[2];
			} else {
				tris[i][0] = tri[0];
				tris[i][1] = tri[1];
				tris[i][2] = tri[2];
			}

		}
		return tris;
	}

	/**
	 * Transforms the input polygon into a single, possible self-intersecting
	 * shell by connecting holes to the exterior ring, The holes are added from
	 * the lowest upwards. As the resulting shell develops, a hole might be
	 * added to what was originally another hole.
	 */
	private static void createShell(final Polygon inputPolygon) {
		final Polygon poly = (Polygon) inputPolygon.clone();
		// Normalization changes the order of the vertices and messes up any
		// indexed scheme
		// Not sure if commenting out line will give later side effects...
		// poly.normalize();
		shellCoords = new ArrayList<Coordinate>();
		final List<Geometry> orderedHoles = getOrderedHoles(poly);
		final Coordinate[] coords = poly.getExteriorRing().getCoordinates();
		shellCoords.addAll(Arrays.asList(coords));
		for (int i = 0; i < orderedHoles.size(); i++) {
			joinHoleToShell(inputPolygon, orderedHoles.get(i));
		}
	}

	/**
	 * Check if a candidate edge between two vertices passes through any other
	 * available vertices.
	 * 
	 * @param index0
	 *            first vertex
	 * @param index1
	 *            second vertex
	 * @return true if the edge does not pass through any other available
	 *         vertices; false otherwise
	 */
	private static boolean isValidEdge(final int index0, final int index1) {
		final Coordinate[] line = { shellCoords.get(index0),
				shellCoords.get(index1) };
		int index = nextShellCoord(index0 + 1);
		while (index != index0) {
			if (index != index1) {
				final Coordinate c = shellCoords.get(index);
				if (!(c.equals2D(line[0]) || c.equals2D(line[1]))) {
					if (CGAlgorithms.isOnLine(c, line)) {
						return false;
					}
				}
			}
			index = nextShellCoord(index + 1);
		}
		return true;
	}

	/**
	 * Get the index of the next available shell coordinate starting from the
	 * given candidate position.
	 * 
	 * @param pos
	 *            candidate position
	 * @return index of the next available shell coordinate
	 */
	private static int nextShellCoord(final int pos) {
		int pnew = pos % shellCoordAvailable.length;
		while (!shellCoordAvailable[pnew]) {
			pnew = (pnew + 1) % shellCoordAvailable.length;
		}
		return pnew;
	}

	/**
	 * Attempts to improve the triangulation by examining pairs of triangles
	 * with a common edge, forming a quadrilateral, and testing if swapping the
	 * diagonal of this quadrilateral would produce two new triangles with
	 * larger minimum interior angles.
	 */
	private static void doImprove(final List<Triangle> earList) {
		final EdgeFlipper ef = new EdgeFlipper(shellCoords);
		boolean changed;
		do {
			changed = false;
			for (int i = 0; i < earList.size() - 1 && !changed; i++) {
				final Triangle ear0 = earList.get(i);
				for (int j = i + 1; j < earList.size() && !changed; j++) {
					final Triangle ear1 = earList.get(j);
					final int[] sharedVertices = ear0.getSharedVertices(ear1);
					if (sharedVertices != null && sharedVertices.length == 2) {
						if (ef.flip(ear0, ear1, sharedVertices)) {
							changed = true;
						}
					}
				}
			}
		} while (changed);
	}

	/**
	 * Returns a list of holes in the input polygon (if any) ordered by y
	 * coordinate with ties broken using x coordinate.
	 * 
	 * @param poly
	 *            input polygon
	 * @return a list of Geometry objects representing the ordered holes (may be
	 *         empty)
	 */
	private static List<Geometry> getOrderedHoles(final Polygon poly) {
		final List<Geometry> holes = new ArrayList<Geometry>();
		final List<IndexedEnvelope> bounds = new ArrayList<IndexedEnvelope>();
		if (poly.getNumInteriorRing() > 0) {
			for (int i = 0; i < poly.getNumInteriorRing(); i++) {
				bounds.add(new IndexedEnvelope(i, poly.getInteriorRingN(i)
						.getEnvelopeInternal()));
			}
			Collections.sort(bounds, new IndexedEnvelopeComparator());
			for (int i = 0; i < bounds.size(); i++) {
				holes.add(poly.getInteriorRingN(bounds.get(i).index));
			}
		}
		return holes;
	}

	/**
	 * Join a given hole to the current shell. The hole coordinates are inserted
	 * into the list of shell coordinates.
	 * 
	 * @param hole
	 *            the hole to join
	 */
	private static void joinHoleToShell(final Polygon inputPolygon,
			final Geometry hole) {
		final GeometryFactory gf = new GeometryFactory();
		double minD2 = Double.MAX_VALUE;
		int shellVertexIndex = -1;
		final int Ns = shellCoords.size() - 1;
		final int holeVertexIndex = getLowestVertex(hole);
		final Coordinate[] holeCoords = hole.getCoordinates();
		final Coordinate ch = holeCoords[holeVertexIndex];
		final List<IndexedDouble> distanceList = new ArrayList<IndexedDouble>();
		/*
		 * Note: it's important to scan the shell vertices in reverse so that if
		 * a hole ends up being joined to what was originally another hole, the
		 * previous hole's coordinates appear in the shell before the new hole's
		 * coordinates (otherwise the triangulation algorithm tends to get
		 * stuck).
		 */
		for (int i = Ns - 1; i >= 0; i--) {
			final Coordinate cs = shellCoords.get(i);
			final double d2 = (ch.x - cs.x) * (ch.x - cs.x) + (ch.y - cs.y)
					* (ch.y - cs.y);
			if (d2 < minD2) {
				minD2 = d2;
				shellVertexIndex = i;
			}
			distanceList.add(new IndexedDouble(i, d2));
		}
		/*
		 * Try a quick join: if the closest shell vertex is reachable without
		 * crossing any holes.
		 */
		LineString join = gf.createLineString(new Coordinate[] { ch,
				shellCoords.get(shellVertexIndex) });
		if (inputPolygon.covers(join)) {
			doJoinHole(shellVertexIndex, holeCoords, holeVertexIndex);
			return;
		}
		/*
		 * Quick join didn't work. Sort the shell coords on distance to the hole
		 * vertex nnd choose the closest reachable one.
		 */
		Collections.sort(distanceList, new IndexedDoubleComparator());
		for (int i = 1; i < distanceList.size(); i++) {
			join = gf.createLineString(new Coordinate[] { ch,
					shellCoords.get(distanceList.get(i).index) });
			if (inputPolygon.covers(join)) {
				shellVertexIndex = distanceList.get(i).index;
				doJoinHole(shellVertexIndex, holeCoords, holeVertexIndex);
				return;
			}
		}
		// throw new IllegalStateException("Failed to join hole to shell");
	}

	/**
	 * Helper method for joinHoleToShell. Insert the hole coordinates into the
	 * shell coordinate list.
	 * 
	 * @param shellCoords
	 *            list of current shell coordinates
	 * @param shellVertexIndex
	 *            insertion point in the shell coordinate list
	 * @param holeCoords
	 *            array of hole coordinates
	 * @param holeVertexIndex
	 *            attachment point of hole
	 */
	private static void doJoinHole(final int shellVertexIndex,
			final Coordinate[] holeCoords, final int holeVertexIndex) {
		final List<Coordinate> newCoords = new ArrayList<Coordinate>();
		final List<Integer> newRingIndices = new ArrayList<Integer>();
		newCoords.add(new Coordinate(shellCoords.get(shellVertexIndex)));
		final int N = holeCoords.length - 1;
		int i = holeVertexIndex;
		do {
			newCoords.add(new Coordinate(holeCoords[i]));
			i = (i + 1) % N;
		} while (i != holeVertexIndex);
		newCoords.add(new Coordinate(holeCoords[holeVertexIndex]));
		shellCoords.addAll(shellVertexIndex, newCoords);
	}

	/**
	 * Return the index of the lowest vertex
	 * 
	 * @param geom
	 *            input geometry
	 * @return index of the first vertex found at lowest point of the geometry
	 */
	private static int getLowestVertex(final Geometry geom) {
		final Coordinate[] coords = geom.getCoordinates();
		final double minY = geom.getEnvelopeInternal().getMinY();
		for (int i = 0; i < coords.length; i++) {
			if (Math.abs(coords[i].y - minY) < WB_Epsilon.EPSILON) {
				return i;
			}
		}
		throw new IllegalStateException("Failed to find lowest vertex");
	}

	private static class IndexedEnvelope {
		int index;
		Envelope envelope;

		public IndexedEnvelope(final int i, final Envelope env) {
			index = i;
			envelope = env;
		}
	}

	private static class IndexedEnvelopeComparator implements
			Comparator<IndexedEnvelope> {
		@Override
		public int compare(final IndexedEnvelope o1, final IndexedEnvelope o2) {
			double delta = o1.envelope.getMinY() - o2.envelope.getMinY();
			if (Math.abs(delta) < WB_Epsilon.EPSILON) {
				delta = o1.envelope.getMinX() - o2.envelope.getMinX();
				if (Math.abs(delta) < WB_Epsilon.EPSILON) {
					return 0;
				}
			}
			return (delta > 0 ? 1 : -1);
		}
	}

	private static class IndexedDouble {
		int index;
		double value;

		public IndexedDouble(final int i, final double v) {
			index = i;
			value = v;
		}
	}

	private static class IndexedDoubleComparator implements
			Comparator<IndexedDouble> {
		@Override
		public int compare(final IndexedDouble o1, final IndexedDouble o2) {
			final double delta = o1.value - o2.value;
			if (Math.abs(delta) < WB_Epsilon.EPSILON) {
				return 0;
			}
			return (delta > 0 ? 1 : -1);
		}
	}
}