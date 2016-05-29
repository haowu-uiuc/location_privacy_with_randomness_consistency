package Utils;

/**
 * The absolute error under defense. The average distance between a square
 * center (X, Y) to a post X_post which is uniformly distributed in a square
 * whose center is at (0, 0) the length of the side of square is exact 1.
 * 
 * @author HaoWu
 *
 */
public class DefenseAbsErrCalculator {

	static private DefenseAbsErrCalculator instance;
	private double[][] dist;
	private int size;
	private int k;	// sample scale

	private DefenseAbsErrCalculator() {
		size = 200;
		k = 10;
		dist = new double[size][size];

		// calculate dist matrix
		// because of symmetric we only need to calculate a triangle, and assign
		// the value to the coordinating one one the other side of the diagonal
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size - x; y++) {
				// sample (2k+1)*(2k+1) points uniformly in the square at (0,0)
				double sum = 0.0;
				for (int i = -k; i <= k; i++) {
					for (int j = -k; j <= k; j++) {
						double x_post = (double)i/(double)(k*2+1);
						double y_post = (double)j/(double)(k*2+1);
						sum += Math.sqrt((x_post - x) * (x_post - x) + (y_post - y) * (y_post - y));
					}
				}
				sum = sum / (2 * k + 1) / (2 * k + 1);
				dist[x][y] = sum;
				dist[size - y - 1][size - x - 1] = sum;
			}
		}

	}

	/**
	 * Singlton
	 * 
	 * @return
	 */
	static public DefenseAbsErrCalculator getInstance() {
		if (instance == null) {
			instance = new DefenseAbsErrCalculator();
			return instance;
		}
		return instance;
	}

	/**
	 * Get the absolute error when the attacker estimate X_est as the post's
	 * location, while the real post is in the square with the center X_post
	 * 
	 * @param X_est
	 * @param X_post
	 */
	public double getAbsErrBetween(Point X_est, Point X_post) {
		int center_x_est = (int) Math.round(X_est.getX());
		int center_y_est = (int) Math.round(X_est.getY());
		int center_x_post = (int) Math.round(X_post.getX());
		int center_y_post = (int) Math.round(X_post.getY());

		// check whether X_est and X_post are the center of the square
		// namely, check whether the X_est and X_post are integers
		double e_x_est = Math.abs(center_x_est - X_est.getX());
		double e_y_est = Math.abs(center_y_est - X_est.getY());
		double e_x_post = Math.abs(center_x_post - X_post.getX());
		double e_y_post = Math.abs(center_y_post - X_post.getY());
		double e_thre = 0.0001;
		
		if (e_x_est > e_thre || e_x_post > e_thre || e_y_est > e_thre
				|| e_y_post > e_thre) {
			System.out.println("X_post = " + X_post);
			System.out.println("X_est = " + X_est);
			System.out
					.println("The getAbsErrBetween need the input points to be center of square!");
		}

		// because of symetical we only consider one phase.
		int x = Math.abs(center_x_est - center_x_post);
		int y = Math.abs(center_y_est - center_y_post);

		if (x > size - 1 || y > size - 1) {
			System.out
					.println("The X_est is too far aways to X_post so that the distance is out of range!");
			return -1;
		}

		return dist[x][y];
	}
}
