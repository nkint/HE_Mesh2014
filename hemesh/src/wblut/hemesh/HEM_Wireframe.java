package wblut.hemesh;

import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;

public class HEM_Wireframe extends HEM_Modifier {

	private WB_Parameter<Double> strutR;

	private WB_Parameter<Double> maxStrutOffset;

	private int facetN;

	private WB_Parameter<Double> angleFactor;

	private double fillFactor;

	private double fidget;

	private boolean cap;

	private boolean taper;

	public HEM_Wireframe() {
		facetN = 4;
		angleFactor = new WB_ConstantParameter<Double>(0.5);
		fidget = 1.0001;
		fillFactor = 0.99;
		maxStrutOffset = new WB_ConstantParameter<Double>(Double.MAX_VALUE);
		cap = true;
		taper = false;
	}

	public HEM_Wireframe setStrutRadius(final double r) {
		strutR = new WB_ConstantParameter<Double>(r);
		return this;
	}

	public HEM_Wireframe setStrutRadius(final WB_Parameter<Double> r) {
		strutR = r;
		return this;
	}

	public HEM_Wireframe setMaximumStrutOffset(final double r) {
		maxStrutOffset = new WB_ConstantParameter<Double>(r);
		return this;
	}

	public HEM_Wireframe setMaximumStrutOffset(final WB_Parameter<Double> r) {
		maxStrutOffset = r;
		return this;
	}

	public HEM_Wireframe setStrutFacets(final int N) {
		facetN = N;
		return this;
	}

	public HEM_Wireframe setAngleOffset(final double af) {
		angleFactor = new WB_ConstantParameter<Double>(af);
		return this;
	}

	public HEM_Wireframe setAngleOffset(final WB_Parameter<Double> af) {
		angleFactor = af;
		return this;
	}

	public HEM_Wireframe setFidget(final double f) {
		fidget = f;
		return this;
	}

	public HEM_Wireframe setFillFactor(final double ff) {
		fillFactor = ff;
		return this;
	}

	public HEM_Wireframe setCap(final boolean b) {
		cap = b;
		return this;
	}

	public HEM_Wireframe setTaper(final boolean b) {
		taper = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.creators.HEC_Creator#createBase()
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if ((strutR == null) || (facetN < 3)) {
			return mesh;
		}

		final HEC_FromFrame ff = new HEC_FromFrame();
		ff.setFrame(mesh);
		ff.setAngleOffset(angleFactor);
		ff.setCap(cap);
		ff.setStrutFacets(facetN);
		ff.setFidget(fidget);
		ff.setFillFactor(fillFactor);
		ff.setTaper(taper);
		ff.setStrutRadius(strutR);
		ff.setMaximumStrutOffset(maxStrutOffset);
		mesh.set(ff.create());
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seewblut.hemesh.modifiers.HEM_Modifier#applySelected(wblut.hemesh.core.
	 * HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {

		return apply(selection.parent);
	}
}
