package project;

import java.io.IOException;
import java.text.DecimalFormat;

public class BinaryTree {

	static final DecimalFormat NF = new DecimalFormat("0.0000");
	static final String LS = System.lineSeparator();

	final BinaryTree mLeft;
	final BinaryTree mRight;
	final double mLeftDistance;
	final double mRightDistance;
	final String mLabel;
	int mNodeNumber;

	BinaryTree(final BinaryTree left, final BinaryTree right, final double leftDistance, final double rightDistance,
			final String label) {
		mLeft = left;
		mRight = right;
		mLeftDistance = leftDistance;
		mRightDistance = rightDistance;
		mLabel = label;
	}

	private int nodeNames(final StringBuilder names, final int nodeNumber) {
		int nodeNmb = nodeNumber;
		// In order traversal
		if (mLeft != null)
			nodeNmb = mLeft.nodeNames(names, nodeNmb);
		mNodeNumber = nodeNmb++;
		names.append("node ").append(mNodeNumber).append(" \"").append(mLabel).append("\"").append(LS);
		if (mRight != null)
			nodeNmb = mRight.nodeNames(names, nodeNmb);
		return nodeNmb;
	}

	private void edges(final StringBuilder s) {
		// In order traversal
		if (mLeft != null) {
			s.append("edge ").append(mNodeNumber).append("-").append(mLeft.mNodeNumber).append(LS);
			mLeft.edges(s);
		}
		if (mRight != null) {
			s.append("edge ").append(mNodeNumber).append("-").append(mRight.mNodeNumber).append(LS);
			mRight.edges(s);
		}
	}

	private synchronized void toString(final StringBuilder sb) {
		nodeNames(sb, 0);
		edges(sb);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	String getLabel() {
		return mLabel;
	}

	/**
	 * Return the tree in New Hampshire format. 
	 * @param out
	 *            where to write the tree.
	 * @throws IOException
	 *             if error while writing to out.
	 */
	public void newick(final Appendable out) throws IOException {
		newick(0, out);
		out.append(LS);
	}

	private void newick(final int indent, final Appendable out) throws IOException {
		for (int k = 0; k < indent; k++)
			out.append(' ');
		if (mLeft == null)
			out.append(mLabel);
		else {
			out.append("(").append(LS);
			mLeft.newick(indent + 1, out);
			out.append(':');
			out.append(NF.format(mLeftDistance));
			out.append(",").append(LS);
			mRight.newick(indent + 1, out);
			out.append(':');
			out.append(NF.format(mRightDistance));
			out.append(LS);
			for (int k = 0; k < indent; k++)
				out.append(' ');
			out.append(")");
		}
	}

}