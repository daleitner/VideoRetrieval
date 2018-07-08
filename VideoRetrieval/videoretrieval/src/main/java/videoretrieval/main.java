package videoretrieval;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;

public class main {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static DBClient dbClient;
    private static ImageClassifier classifier;
    private static final String path = "F:/Privat/DLVideoRetrieval/VideoRetrieval/videoretrieval/videos";
    private static final String testvideo = path + "/35368.mp4";

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

        System.out.println(matches.length);
        System.out.println("not implemented");
    }

    private static void executeQuery(Mat image) {
        // TODO: get labels
        String[] labels = new String[0];
        FrameDescriptor[] matches = dbClient.query(labels, false);

        System.out.println(matches.length);
        System.out.println("not implemented");
    }

    private static void runClassification() {
        VideoAnalyzer va = new VideoAnalyzer();
        ArrayList<Mat> frames = va.extractKeyFrames(testvideo, 1, 0.6);

        String imgPath = path + "/Imgs/";
        for(int i = 0; i<frames.size(); i++) {
            Mat frame = frames.get(i);
            System.out.println("Labels: " + Arrays.toString(classifier.getLabels(classifier.classify(frame, 5))));
            Colour[] colours = getDominantColours(frame, 5);
            for (int j = 0; i < colours.length; i++) {
                System.out.println("r: " + colours[i].red + ", g: " + colours[i].green + ", blue: " + colours[i].blue);
            }
            Imgcodecs.imwrite(imgPath + (i+1) + ".jpg", frame);
        }
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
}
