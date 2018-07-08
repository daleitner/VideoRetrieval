package videoretrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

/***
 * Determines a set of best matching content classes to an image.
 */
public class ImageClassifier {

	/***
	 * Path to the file "deploy.prototxt" -> defines the CNN (input, layers, ...)
	 */
	public static final String PATH_PROTOTXT = "data/deploy.prototxt";
	/***
	 * Path to the trained CNN, i. e. the GoogLeNet
	 */
	public static final String PATH_MODEL = "data/bvlc_googlenet.caffemodel";
	
	/***
	 * Represents the mapping between class ids and names
	 */
	private String[] classNames;
	/***
	 * Represents the CNN to be used
	 */
	private Net net;
	
	public ImageClassifier(String pathToSynSet) throws Exception {
		initClassNames(pathToSynSet);
		initCnn();
	}
	
	@SuppressWarnings("unused")
	private ImageClassifier() {
	}
	
	/***
	 * Initializes the CNN that is used for classification.
	 */
	private void initCnn() throws Exception {
		String rootPath = new File(".").getCanonicalPath().replace("\\", "/") + "/";
		setNet(Dnn.readNetFromCaffe(rootPath + PATH_PROTOTXT, rootPath + PATH_MODEL));
	}

	/***
	 * Initializes a set of classes used for categorization. The classes are 
	 * obtained from the given file.
	 * @param pathToSynSet
	 * @throws Exception
	 */
	private void initClassNames(String pathToSynSet) throws Exception {
		List<String> items = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pathToSynSet));
			String line = null;
			while ((line = br.readLine()) != null) {
				items.add(line.substring(line.indexOf(' ') + 1));
			}
			setClassNames(new String[items.size()]);
			for (int i = 0; i < items.size(); i++) {
				getClassNames()[i] = items.get(i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// silent catch
				}
			}
		}
	}

	/***
	 * Determines a list of k tuples representing the highest values  
	 * of the given array and their position. Hence, returns 
	 * the last k (considering the order of the array) classes that are 
	 * best matching a categorized frame.
	 * @param frame
	 * @param k
	 * @return
	 */
	private List<Tuple<Integer, Double>> getMaxClasses(int k, double[] probabilities) {
		List<Tuple<Integer, Double>> classes = new ArrayList<Tuple<Integer, Double>>();
		for (int i = 0; i < k; i++) {
			Tuple<Integer, Double> tuple = getMaxClass(probabilities);
			classes.add(tuple);
			probabilities[tuple.getItem1()] = -1;
		}
		return classes;
	}

	/***
	 * Determines a tuple representing the maximal value 
	 * of the given array and its position. Hence, returns 
	 * the last (considering the order of the array) class that is 
	 * best matching a categorized frame.
	 * @param probabilities
	 * @return
	 */
	private Tuple<Integer, Double> getMaxClass(double[] probabilities) {
		int pos = 0;
		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] > probabilities[pos]) {
				pos = i;
			}
		}
		return new Tuple<Integer, Double>(pos, probabilities[pos]);
	}
	
	/***
	 * Prints a list of classifications.
	 * @param classes
	 */
	public void printClassification(List<Tuple<Integer, Double>> classes) {
		String[] mappings = getClassNames();
		for (Tuple<Integer, Double> s : classes) {
			if (s.getItem1() >= 0 && s.getItem1() < mappings.length) {
				System.out.println(mappings[s.getItem1()] + ": "+ s.getItem2());
			}
		}
	}

	public String[] getLabels(List<Tuple<Integer, Double>> classes) {
		ArrayList<String> labels = new ArrayList<>();

		String[] mappings = getClassNames();
		for (Tuple<Integer, Double> s : classes) {
			if (s.getItem1() > 0 && s.getItem1() < mappings.length) {
				labels.add(mappings[s.getItem1()]);
			}
		}

		return labels.toArray(new String[0]);
	}
	
	/***
	 * Determines the k classes the given frame suits best.
	 * @param frame
	 * @param k
	 * @return
	 */
	public List<Tuple<Integer, Double>> classify(Mat frame, int k) {
		// resize image to 224 x 224 (GoogLeNet only accepts this dimension)
		Mat frameResized = new Mat();
		Size sz = new Size(224, 224);
		Imgproc.resize(frame, frameResized, sz);
		// retrieve blob from resize data
		Mat inputBlob = Dnn.blobFromImage(frameResized);
		// specify input for network
		getNet().setInput(inputBlob, "data");
		// process frame through CNN
		Mat prob = getNet().forward("prob");
		// reshape matrix to 1 x 1000 vector to provide easier
		// access to the results, i. e. the probabilities of classification
		prob = prob.reshape(1, 1);
		// stores all classes and their probabilities into 1D array
		double[] probabilities = new double[prob.cols()];
		for (int i = 0; i < prob.rows(); i++) {
			for (int j = 0; j < prob.cols(); j++) {
				probabilities[j] = prob.get(i, j)[0];
			}
		}
		// determines the k best matching classes and the corresponding probabilities
		List<Tuple<Integer, Double>> classes = getMaxClasses(k, probabilities);
		return classes;
	}

	public String[] getClassNames() {
		return classNames;
	}

	private void setClassNames(String[] classNames) {
		this.classNames = classNames;
	}

	private Net getNet() {
		return net;
	}

	private void setNet(Net net) {
		this.net = net;
	}

}
