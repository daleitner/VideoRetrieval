package videoretrieval;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
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
    private static final String basePath = "F:/Privat/DLVideoRetrieval/VideoRetrieval/videoretrieval/videos";

    public static void main(String[] args) throws Exception {
        dbClient = new DBClient();
        classifier = new ImageClassifier("data/syn_set.txt");
        displayMainMenu();
    }

    private static void displayMainMenu() throws Exception {
        System.out.println("What do you want to do?");
        System.out.println("  [0] Run classification on database");
        System.out.println("  [1] Execute query based on labels");
        System.out.println("  [2] Execute query based on labels and dominant colors");
        System.out.println("  [3] Execute query based on query image");
        System.out.println("  [4] Execute query based on query image URL");
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
                executeQuery(readLabels(), new Colour[0]);
                break;

            case '2':
                executeQuery(readLabels(), readColours());
                break;

            case '3':
                executeQuery(getImageFromFile(readString("filename")));
                break;

            case '4':
                executeQuery(getImageFromURL(readString("URL")));
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

    private static void executeQuery(String[] labels, Colour[] dominantColours) {
        FrameDescriptor[] matches = dbClient.query(labels, false);

        System.out.println("not implemented");
    }

    private static void executeQuery(Mat image) {
        // TODO: get labels
        String[] labels = new String[0];
        FrameDescriptor[] matches = dbClient.query(labels, false);

        System.out.println("not implemented");
    }

	private static void runClassification() {
		List<String> videoFileNames = readFileNames(basePath);

		// TODO run over all videos
		String videoFileName = videoFileNames.get(23);
		int fileId = Integer.parseInt(videoFileName.replaceAll(".mp4",""));

		VideoAnalyzer va = new VideoAnalyzer();
		ArrayList<Frame> frames = va.extractKeyFrames(basePath + videoFileName, fileId, 1, 0.6);

		for (Frame f: frames) {
			String[] labels = classifier.getLabels(classifier.classify(f.data, 5));
			System.out.println("Labels: " + Arrays.toString(labels));
			// TODO convert f.histogram to array
			// TODO get dominant colors
			// TODO save to DB
			FrameDescriptor.create(f.fileId, f.number, null /*f.histogram*/, null, labels);
		}

		// Save frames
		String imgPath = basePath + "img" + videoFileName.replaceAll(".mp4","") + "/" ;
		for(int i = 0; i < frames.size(); i++) {
			Mat frame = frames.get(i).data;
			Imgcodecs.imwrite(imgPath + (i+1) + ".jpg", frame);
		}
	}

	private static List<String> readFileNames(String directoryPath) {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		List<String> videoFileNames = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				videoFileNames.add(listOfFiles[i].getName());
			}
		}

		return videoFileNames;
	}
}
