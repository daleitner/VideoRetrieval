package videoretrieval;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgproc.Imgproc;

public class VideoAnalyzer {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private int hbins = 30, sbins = 32;
    private MatOfInt histSize = new MatOfInt(this.hbins, this.sbins);

    public VideoAnalyzer() {
    }

    private Mat calcHist(Mat frame) {
        Mat hsvHist = new Mat();
        MatOfFloat histRange = new MatOfFloat(0f, 180f, 0f, 256f);

        Mat hsvframe = new Mat();
        Imgproc.cvtColor(frame, hsvframe, Imgproc.COLOR_BGR2HSV);
        Imgproc.calcHist(Arrays.asList(hsvframe), new MatOfInt(0, 1), new Mat(), hsvHist, this.histSize, histRange);

        return hsvHist;
    }

    /**
     * Extracts frames from given video.
     *
     * @param videoPath path to video file.
     * @param fps       Determines how many Frames per second are inspected.
     * @param th        Threshold for Histogram comparison between inspected frames (correlation metric). [-1;1]
     * @return Extracted Frames.
     */
    public ArrayList<Mat> extractKeyFrames(String videoPath, int fps, double th) {
        VideoCapture video = new VideoCapture(videoPath);

        System.out.println("FPS: " + video.get(Videoio.CAP_PROP_FPS));
        System.out.println("Frames: " + video.get(Videoio.CAP_PROP_FRAME_COUNT));

        Mat frame = new Mat();
        ArrayList<Mat> frames = new ArrayList<Mat>();
        Mat previousFrameHist = new Mat();
        Mat nextFrameHist = new Mat();

        int samplingDistance = 1;

        if (fps > 0) {
            samplingDistance = (int) Math.round(video.get(Videoio.CAP_PROP_FPS)) / fps;
        }

        System.out.println("Using frame sampling interval: " + samplingDistance);

        boolean isFirstFrame = true;
        int frameCount = 0;

        while (video.read(frame)) {
            if (frame.empty()) { continue; }
            frameCount++;

            if ((frameCount % samplingDistance == 1) || samplingDistance == 1) {
                if (isFirstFrame) {
                    isFirstFrame = false;
                    frames.add(frame.clone());
                    previousFrameHist = this.calcHist(frame);
                } else {
                    nextFrameHist = this.calcHist(frame);
                    double distance = Imgproc.compareHist(previousFrameHist, nextFrameHist, Imgproc.CV_COMP_CORREL);

                    if (distance < th) {
                        System.out.println("distance (" + frames.size() + "," + (frames.size() + 1) + "): " + distance);
                        frames.add(frame.clone());

                        // TODO this must be moved outside the if?
                        previousFrameHist = nextFrameHist;
                    }
                }
            }
        }
        return frames;
    }
}
