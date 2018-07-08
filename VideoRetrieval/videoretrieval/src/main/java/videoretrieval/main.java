package videoretrieval;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;

public class main {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static DBClient dbClient;
    private static ImageClassifier classifier;

    // Wolfram's setup
    private static final String basePath = "F:/Privat/DLVideoRetrieval/VideoRetrieval/videoretrieval/videos/";

    // Jameson's setup
    // private static final String basePath = "C:/Users/Admin/vids/";

    public static void main(String[] args) throws Exception {
        dbClient = new DBClient();
        classifier = new ImageClassifier("data/syn_set.txt");
        displayMainMenu();
    }

    private static void displayMainMenu() throws Exception {
        System.out.println("What do you want to do?");
        System.out.println("  [0] Run classification on database");
        System.out.println("  [1] Execute query based on labels");
        System.out.println("  [2] Execute query based on labels (strict)");
        System.out.println("  [3] Execute query based on labels and dominant colors");
        System.out.println("  [4] Execute query based on labels (strict) and dominant colors");
        System.out.println("  [5] Execute query based on query image");
        System.out.println("  [6] Execute query based on query image (strict)");
        System.out.println("  [7] Execute query based on query image URL");
        System.out.println("  [8] Execute query based on query image URL (strict)");
        System.out.println("  [C] Clear the image descriptor database");
        System.out.println("  [q] quit");
        readMenuInput();
    }

    private static void readMenuInput() throws Exception {
        System.out.print("> ");
        String input = readLine();

        if (input == null) {
            displayMainMenu();
            return;
        }

        switch (input.charAt(0)) {
            case '0':
                runClassification();
                break;

            case '1':
                executeQuery(readLabels(), new Colour[0], false);
                break;

            case '2':
                executeQuery(readLabels(), new Colour[0], true);
                break;

            case '3':
                executeQuery(readLabels(), readColours(), false);
                break;

            case '4':
                executeQuery(readLabels(), readColours(), true);
                break;

            case '5':
                executeQuery(getImageFromFile(readString("filename")), false);
                break;

            case '6':
                executeQuery(getImageFromFile(readString("filename")), true);
                break;

            case '7':
                executeQuery(getImageFromURL(readString("URL")), false);
                break;

            case '8':
                executeQuery(getImageFromURL(readString("URL")), true);
                break;

            case 'C':
                dbClient.clear();
                break;

            case 'q':
                return;

            default:
                System.out.println("Unknown option " + input);
        }

        displayMainMenu();
    }

    private static String readLine() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            return reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readString(String message) {
        System.out.print("Enter a valid " + message + ": ");
        return readLine();
    }

    private static Mat getImageFromFile(String fileName) {
        return Imgcodecs.imread(fileName);
    }

    private static Mat getImageFromURL(String url) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(ImageIO.read(new URL(url)), "png", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    private static String[] readLabels() {
        String message = "Input a label (leave blank to end): ";
        ArrayList<String> labels = new ArrayList<>();
        String input;

        System.out.print(message);
        while (!(input = readLine()).equals("")) {
            labels.add(input);
            System.out.print(message);
        }

        return labels.toArray(new String[0]);
    }

    private static Colour[] readColours() {
        String message = "Enter a color [r, g, b] (leave blank to end): ";
        ArrayList<Colour> colours = new ArrayList<>();
        String input;

        System.out.print(message);
        while (!(input = readLine().replace(" ", "")).equals("")) {
            String[] components = input.split(",");
            colours.add(Colour.create(Integer.parseInt(components[0]), Integer.parseInt(components[1]), Integer.parseInt(components[2])));
            System.out.print(message);
        }

        return colours.toArray(new Colour[0]);
    }

    private static void executeQuery(String[] labels, Colour[] dominantColours, boolean strict) {
        FrameDescriptor[] matches = dbClient.query(labels, strict);

        System.out.println(matches.length);
        System.out.println("not implemented");
    }

    private static void executeQuery(Mat image, boolean strict) {
        // TODO: get labels
        String[] labels = new String[0];
        FrameDescriptor[] matches = dbClient.query(labels, strict);

        System.out.println(matches.length);
        System.out.println("not implemented");
    }

    private static Colour[] getDominantColours(Mat image, int k) {
        ArrayList<Colour> dominantColours = new ArrayList<>(k);
        Mat samples = image.reshape(1, image.cols() * image.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        for (int i = 0; i < k; i++) {
            int red = (int)Math.round(centers.get(i, 2)[0] * 255);
            int green = (int)Math.round(centers.get(i, 1)[0] * 255);
            int blue = (int)Math.round(centers.get(i, 0)[0] * 255);

            dominantColours.add(Colour.create(red, green, blue));
        }

        return dominantColours.toArray(new Colour[0]);
    }

	private static void runClassification() {
		List<String> videoFileNames = readFileNames(basePath);

		// TODO loop over all videos, now using LEGO video only for testing
		String videoFileName = videoFileNames.get(23);
		videoFileName = "35368.mp4";
		int fileId = Integer.parseInt(videoFileName.replaceAll(".mp4",""));

		VideoAnalyzer va = new VideoAnalyzer();
		ArrayList<Frame> frames = va.extractKeyFrames(basePath + videoFileName, fileId, 1, 0.6);

		for (Frame f: frames) {
			String[] rawLabels = classifier.getLabels(classifier.classify(f.data, 5));
			ArrayList<String> labelsList = new ArrayList<>();

			for (String label: rawLabels) {
			    if (label.indexOf(',') > -1) {
			        for (String subLabel: label.split(",")) {
			            labelsList.add(subLabel.trim());
                    }
                } else {
			        labelsList.add(label);
                }
            }

			String[] labels = labelsList.toArray(new String[0]);
			System.out.println("Labels: " + Arrays.toString(labels));

			// Convert histogram Mat into double array
			// Some "workaround" taken from
            // http://answers.opencv.org/question/14961/using-get-and-put-to-access-pixel-values-in-java/
            f.histogram.convertTo(f.histogram, CvType.CV_64FC3);
            int histArrSize = (int) (f.histogram.total() * f.histogram.channels());
            double[] hist = new double[histArrSize];
            f.histogram.get(0,0, hist);
            for (int i = 0; i < histArrSize; i++) {
                hist[i] = (hist[i] / 2);
            }

            dbClient.saveFrameDescriptor(FrameDescriptor.create(f.fileId, f.number, hist, getDominantColours(f.data, 5), labels));
		}
	}

	private static List<String> readFileNames(String directoryPath) {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		List<String> videoFileNames = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].getName().endsWith(".mp4")) {
				videoFileNames.add(listOfFiles[i].getName());
			}
		}

		return videoFileNames;
	}
}
