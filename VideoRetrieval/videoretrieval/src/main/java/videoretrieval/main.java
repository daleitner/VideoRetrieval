package videoretrieval;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class main {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	private static DBClient dbClient;
	private static ImageClassifier classifier;
	private static final String path = "F:/Privat/DLVideoRetrieval/VideoRetrieval/videoretrieval/videos";
	private static final String testvideo = path + "/35368.mp4";

	public static void main(String[] args) throws Exception {
		dbClient = new DBClient();
		classifier = new ImageClassifier("data/syn_set.txt");
		displayMainMenu();
	}

	private static void displayMainMenu() {
		System.out.println("What do you want to do?");
		System.out.println("  [0] Run classification on database");
		System.out.println("  [1] Execute query based on labels");
		System.out.println("  [2] Execute query based on labels and dominant colors");
		System.out.println("  [3] Execute query based on query image");
		System.out.println("  [4] Execute query based on query image URL");
		System.out.println("  [C] Clear the image descriptor database");
		readMenuInput();
	}

	private static void readMenuInput() {
		System.out.print("> ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		char input;

		try {
			input = reader.readLine().charAt(0);
		} catch (Exception e) {
			e.printStackTrace();
			readMenuInput();
			return;
		}

		switch (input) {
			case '0':
				runClassification();
				break;

			case '1':
				System.out.println("not implemented");
				break;

			case '2':
				System.out.println("not implemented");
				break;

			case '3':
				System.out.println("not implemented");
				break;

			case '4':
				System.out.println("not implemented");
				break;

			case 'C':
				dbClient.clear();
				break;

			default:
				System.out.println("Unknown option " + input);
		}

		displayMainMenu();
	}

	private static void runClassification() {
		VideoAnalyzer va = new VideoAnalyzer();
		ArrayList<Mat> frames = va.extractKeyFrames(testvideo, 1, 0.4);

		String imgPath = path + "/Imgs/";
		for(int i = 0; i<frames.size(); i++) {
			Mat frame = frames.get(i);
			System.out.println("Labels: " + Arrays.toString(classifier.getLabels(classifier.classify(frame, 5))));
			Imgcodecs.imwrite(imgPath + (i+1) + ".jpg", frame);
		}
	}
}
