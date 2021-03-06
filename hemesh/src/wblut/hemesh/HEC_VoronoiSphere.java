package wblut.hemesh;

import java.util.ArrayList;

import wblut.geom.WB_Distance;
import wblut.geom.WB_KDTree;
import wblut.geom.WB_KDTree.WB_KDEntry;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_RandomOnSphere;

/**
 * Creates the Voronoi cell of one point in a collection of points, constrained
 * by a maximum radius.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_VoronoiSphere extends HEC_Creator {

	/** points. */
	private WB_Point[] points;

	/** Number of points. */
	private int numberOfPoints;

	/** Cell index. */
	private int cellIndex;

	/** Level of geodesic sphere in exact mode. */
	private int level;

	/** Maximum radius. */
	private double cutoff;

	/** Point directions. */
	private WB_Vector[] dir;

	/** Approximate mode?. */
	private boolean approx;

	/** Number of tracer points to use in approximate mode?. */
	private int numTracers;

	/** Starting trace step in approximate mode?. */
	private double traceStep;

	/** The random gen. */
	private final WB_RandomOnSphere randomGen;

	/**
	 * Instantiates a new HEC_VoronoiSphere.
	 *
	 */
	public HEC_VoronoiSphere() {
		super();
		level = 1;
		traceStep = 10;
		numTracers = 100;
		override = true;
		randomGen = new WB_RandomOnSphere();
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            array of vertex positions
	 * @return self
	 */
	public HEC_VoronoiSphere setPoints(final WB_Point[] points) {
		this.points = points;
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            2D array of double of vertex positions
	 * @return self
	 */
	public HEC_VoronoiSphere setPoints(final double[][] points) {
		final int n = points.length;
		this.points = new WB_Point[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            2D array of float of vertex positions
	 * @return self
	 */
	public HEC_VoronoiSphere setPoints(final float[][] points) {
		final int n = points.length;
		this.points = new WB_Point[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set number of points.
	 *
	 * @param N
	 *            number of points
	 * @return self
	 */
	public HEC_VoronoiSphere setN(final int N) {
		numberOfPoints = N;
		return this;
	}

	/**
	 * Set index of cell to create.
	 *
	 * @param i
	 *            index
	 * @return self
	 */
	public HEC_VoronoiSphere setCellIndex(final int i) {
		cellIndex = i;
		return this;
	}

	/**
	 * Set level of geodesic sphere in each cell.
	 *
	 * @param l
	 *            recursive level
	 * @return self
	 */
	public HEC_VoronoiSphere setLevel(final int l) {
		level = l;
		return this;
	}

	/**
	 * Set number of tracer points to use in approximate model.
	 *
	 * @param n
	 *            number of tracer points
	 * @return self
	 */
	public HEC_VoronoiSphere setNumTracers(final int n) {
		numTracers = n;
		return this;
	}

	/**
	 * Set initial trace step size.
	 *
	 * @param d
	 *            trace step
	 * @return self
	 */
	public HEC_VoronoiSphere setTraceStep(final double d) {
		traceStep = d;
		return this;
	}

	/**
	 * Set maximum radius of cell.
	 *
	 * @param c
	 *            cutoff radius
	 * @return self
	 */
	public HEC_VoronoiSphere setCutoff(final double c) {
		cutoff = Math.abs(c);
		return this;
	}

	/**
	 * Set approximate mode.
	 *
	 * @param a
	 *            true, false
	 * @return self
	 */
	public HEC_VoronoiSphere setApprox(final boolean a) {
		approx = a;
		return this;
	}

	/**
	 * Set seed of random generator.
	 *
	 * @param seed
	 *            seed
	 * @return self
	 */
	public HEC_VoronoiSphere setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		if (cutoff == 0) {
			return new HE_Mesh();
		}
		if (points == null) {
			return new HE_Mesh();
		}
		if (numberOfPoints == 0) {
			numberOfPoints = points.length;
		}
		if ((cellIndex < 0) || (cellIndex >= numberOfPoints)) {
			return new HE_Mesh();
		}

		HE_Mesh result;

		if (approx) {
			final WB_Point[] tracers = new WB_Point[numTracers];
			for (int i = 0; i < numTracers; i++) {
				tracers[i] = randomGen.nextPoint();

			}
			dir = new WB_Vector[numTracers];
			for (int i = 0; i < numTracers; i++) {
				dir[i] = new WB_Vector(tracers[i]);
				dir[i]._normalizeSelf();
				tracers[i]._addSelf(points[cellIndex]);
			}
			grow(tracers, cellIndex);
			final HEC_ConvexHull ch = new HEC_ConvexHull().setPoints(tracers)
					.setN(numTracers);
			result = new HE_Mesh(ch);
		}
		else {
			final HEC_Geodesic gc = new HEC_Geodesic().setB(level).setC(0);
			gc.setCenter(points[cellIndex]);
			gc.setRadius(cutoff);
			result = new HE_Mesh(gc);
			final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
			for (int j = 0; j < numberOfPoints; j++) {
				if (cellIndex != j) {

					final WB_Vector N = new WB_Vector(points[cellIndex]);
					N._subSelf(points[j]);

					N._normalizeSelf();
					final WB_Point O = new WB_Point(points[cellIndex]); // plane
					// origin=point
					// halfway
					// between point i and point j
					O._addSelf(points[j]);
					O._mulSelf(0.5);
					final WB_Plane P = new WB_Plane(O, N);
					cutPlanes.add(P);
				}
			}
			final HEM_MultiSlice msm = new HEM_MultiSlice();
			msm.setPlanes(cutPlanes).setSimpleCap(true)
			.setCenter(new WB_Point(points[cellIndex]));
			result.modify(msm);

		}
		return result;
	}

	/**
	 * Grow the tracers.
	 *
	 * @param tracers
	 *            the tracers
	 * @param index
	 *            the index
	 */
	private void grow(final WB_Point[] tracers, final int index) {
		final WB_KDTree<WB_Point, Integer> kdtree = new WB_KDTree<WB_Point, Integer>();
		for (int i = 0; i < numberOfPoints; i++) {
			kdtree.add(points[i], i);
		}

		final WB_Point c = new WB_Point(points[index]);
		WB_Point p;
		WB_Vector r;
		double d2self = 0;
		for (int i = 0; i < numTracers; i++) {
			p = tracers[i];
			r = dir[i];
			d2self = 0;
			double stepSize = traceStep;
			int j = index;
			while (stepSize > WB_Epsilon.EPSILON) {

				while ((j == index) && (d2self < cutoff * cutoff)) {

					p._addSelf(stepSize * r.xd(), stepSize * r.yd(), stepSize
							* r.zd());
					d2self = WB_Distance.getSqDistance3D(p, c);
					final WB_KDEntry<WB_Point, Integer>[] closest = kdtree
							.getNearestNeighbors(p, 2);
					j = closest[1].value;
				}
				if (j != index) {
					p._subSelf(stepSize * r.xd(), stepSize * r.yd(), stepSize
							* r.zd());
					d2self = 0;
					stepSize /= 2;
				}
				else {
					p._set(c.xd() + cutoff * r.xd(), c.yd() + cutoff * r.yd(),
							c.zd() + cutoff * r.zd());
					stepSize = -1;

				}
			}
		}
	}

}
