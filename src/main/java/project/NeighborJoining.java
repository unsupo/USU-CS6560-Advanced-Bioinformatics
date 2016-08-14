package project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NeighborJoining {
	public static void main(String[] args) throws IOException {
		Double[][] matrix = new Double[][] { // wiki example
				{ 0., 5., 9., 9., 8. }, 
				{ 5., 0., 10., 10., 9. }, 
				{ 9., 10., 0., 8., 7. }, 
				{ 9., 10., 8., 0., 3. },
				{ 8., 9., 7., 3., 0. } 
		};
		join(Arrays.asList("a", "b", "c", "d", "e" ), convert(matrix)).newick(System.out);
	}

	private static ArrayList<ArrayList<Double>> convert(Double[][] distanceMatrix) {
		return new ArrayList<>(Arrays.asList(distanceMatrix).stream().map(a -> new ArrayList<>(Arrays.asList(a)))
				.collect(Collectors.toList()));
	}
	private static double distance(final int i, final int j, final ArrayList<ArrayList<Double>> d) {
		return i == j ? 0 : (i > j ? d.get(i).get(j) : d.get(j).get(i));
	}

	
	public static BinaryTree join(final List<String> nodeNames, final ArrayList<ArrayList<Double>> d) {
		final ArrayList<BinaryTree> s = new ArrayList<>(nodeNames.stream().map(a->new BinaryTree(null, null, 0, 0, a)).collect(Collectors.toList()));
		
		while (s.size() > 1) {
			final double[] colSum = getColumnSum(d);

			final int r = d.size() - 2;
			double best = Double.POSITIVE_INFINITY;
			int c = 0, f = -1, g = -1;
			for (int k = 1; k < d.size(); k++) {
				for (int j = 0; j < k; j++) {
					final double q = r * distance(k, j, d) - colSum[k] - colSum[j];
					if (Double.doubleToRawLongBits(q) == Double.doubleToRawLongBits(best)
							&& new Random().nextInt(++c) == 0) {
						// Break ties fairly
						f = k;
						g = j;
					} else if (q < best) {
						c = 1;
						f = k;
						g = j;
						best = q;
					}
				}
			}

			// Compute distance of merged node to new node
			final double dfu, dgu, dfg = 0.5 * distance(f, g, d);
			if (d.size() > 2) {
				dfu = dfg + 0.5 * (colSum[f] - colSum[g]) / (d.size() - 2);
				dgu = dfg + 0.5 * (colSum[g] - colSum[f]) / (d.size() - 2);
			} else {
				dfu = dfg;
				dgu = dfg;
			}

			// Compute distance of merged node to all other nodes
			final ArrayList<Double> newRow = new ArrayList<>();
			for (int i = 0; i < d.size(); i++) 
				if (i != f && i != g) 
					newRow.add(0.5 * (distance(f, i, d) - dfu + distance(g, i, d) - dgu));
				
			final BinaryTree newNode = new BinaryTree(s.get(f), s.get(g), dfu, dgu, String.valueOf(d.size()));

			// Remove merged nodes starting with the larger one
			if (f > g) {
				remove(f, s, d);
				remove(g, s, d);
			} else {
				remove(g, s, d);
				remove(f, s, d);
			}

			// Add combined node at end of name list and matrix
			d.add(newRow);
			s.add(newNode);
		}
		return s.get(0);
	}

	private static double[] getColumnSum(ArrayList<ArrayList<Double>> d) {
		double[] columnSum = new double[d.size()];
		for (int j = 0; j < d.size(); j++) {
			double sum = 0;
			for (int k = 0; k < d.size(); k++) 
				sum += distance(j, k, d);
			columnSum[j] = sum;
		}
		return columnSum;
	}

	/* Remove a given item from the distance matrix. */
	private static void remove(final int f, final ArrayList<BinaryTree> s, final ArrayList<ArrayList<Double>> d) {
		for (int k = f + 1; k < d.size(); k++)
			d.get(k).remove(f);
		s.remove(f);
		d.remove(f);
	}

}
