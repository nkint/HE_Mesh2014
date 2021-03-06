package wblut.geom;

public abstract class WB_AbstractSeqVector implements
Comparable<WB_Coordinate>, WB_MutableCoordinate {

	/** Coordinates. */
	private int i;
	private final WB_CoordinateSequence seq;
	private int id;

	public WB_AbstractSeqVector(final int i, final WB_CoordinateSequence seq) {
		this.i = i;
		this.id = 4 * i;
		this.seq = seq;
	}

	@Override
	public void _set(final double x, final double y) {
		seq._setRaw(id, x);
		seq._setRaw(id + 1, y);
		seq._setRaw(id + 2, 0);
	}

	@Override
	public void _set(final double x, final double y, final double z) {
		seq._setRaw(id, x);
		seq._setRaw(id + 1, y);
		seq._setRaw(id + 2, z);
	}

	@Override
	public void _set(final double x, final double y, final double z,
			final double w) {
		_set(x, y, z);

	}

	@Override
	public void _set(final WB_Coordinate v) {
		_set(v.xd(), v.yd(), v.zd());
	}

	@Override
	public void _setW(final double w) {

	}

	@Override
	public void _setX(final double x) {
		seq._setRaw(id, x);

	}

	@Override
	public void _setY(final double y) {
		seq._setRaw(id + 1, y);

	}

	@Override
	public void _setZ(final double z) {
		seq._setRaw(id + 2, z);

	}

	@Override
	public void _setCoord(final int i, final double v) {
		if (i == 0) {
			seq._setRaw(id, v);
		}
		if (i == 1) {
			seq._setRaw(id + 1, v);
		}
		if (i == 2) {
			seq._setRaw(id + 2, v);
		}

	}

	@Override
	public double wd() {
		return 0;
	}

	@Override
	public float wf() {

		return 0;
	}

	@Override
	public double xd() {
		return seq.getRaw(id);
	}

	@Override
	public float xf() {
		return (float) seq.getRaw(id);
	}

	@Override
	public double yd() {
		return seq.getRaw(id + 1);
	}

	@Override
	public float yf() {
		return (float) seq.getRaw(id + 1);
	}

	@Override
	public double zd() {
		return seq.getRaw(id + 2);
	}

	@Override
	public float zf() {
		return (float) seq.getRaw(id + 2);
	}

	@Override
	public double getd(final int i) {
		if (i == 0) {
			return seq.getRaw(id);
		}
		if (i == 1) {
			return seq.getRaw(id + 1);
		}
		if (i == 2) {
			return seq.getRaw(id + 2);
		}
		return Double.NaN;
	}

	@Override
	public float getf(final int i) {
		if (i == 0) {
			return (float) seq.getRaw(id);
		}
		if (i == 1) {
			return (float) seq.getRaw(id + 1);
		}
		if (i == 2) {
			return (float) seq.getRaw(id + 2);
		}
		return Float.NaN;
	}

	@Override
	public int compareTo(final WB_Coordinate p) {
		int cmp = Double.compare(xd(), p.xd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(yd(), p.yd());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Double.compare(zd(), p.zd());
		if (cmp != 0) {
			return cmp;
		}
		return Double.compare(wd(), p.wd());
	}

	public int getIndex() {
		return i;
	}

	public void setIndex(final int i) {
		this.i = i;
		this.id = 4 * i;

	}

}
