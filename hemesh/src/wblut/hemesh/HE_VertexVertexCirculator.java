package wblut.hemesh;

import java.util.Iterator;

public class HE_VertexVertexCirculator<V extends HE_Vertex> implements
		Iterator<HE_Vertex> {

	private HE_Halfedge _start;
	private HE_Halfedge _current;

	public HE_VertexVertexCirculator(HE_Vertex v) {
		_start = v.getHalfedge();
		_current = null;

	}

	@Override
	public boolean hasNext() {
		if (_start == null)
			return false;
		return (_current == null) || (_current.getNextInVertex() != _start);
	}

	@Override
	public HE_Vertex next() {
		if (_current == null) {
			_current = _start;
		} else {
			_current = _current.getNextInVertex();
		}
		return _current.getEndVertex();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}