package videoretrieval;


import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgproc.Imgproc;
public class VideoAnalyzer {
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	private VideoCapture video;
	public VideoAnalyzer() {
	}
	
	/**
	 * Extracts frames from given video.
	 * @param videoPath path to video file.
	 * @param fps Determines how many Frames per second are inspected.
	 * @param th Threshold for Histogram comparison between inspeced frames (correlation metric). [-1;1]
	 * @return Extracted Frames.
	 */
	public ArrayList<Mat> extractKeyFrames(String videoPath, int fps, double th) {
		video = new VideoCapture(videoPath);
		System.out.println("FPS: " + video.get(Videoio.CAP_PROP_FPS));
		System.out.println("Frames: " + video.get(Videoio.CAP_PROP_FRAME_COUNT));
		
		Mat frame = new Mat();
		Mat hsvHist = new Mat();
		boolean isFirstFrame = true;
		int distance = (int) Math.round(video.get(Videoio.CAP_PROP_FPS))/fps;
		int numOfFrames = 0;
		
	    int hbins = 30, sbins = 32;
	    MatOfInt histSize = new MatOfInt(hbins, sbins);
	    MatOfFloat histRange = new MatOfFloat(0f, 180f, 0f, 256f);
	    List<Mat> imageList = new ArrayList<Mat>();
	    
		ArrayList<Mat> frames = new ArrayList<Mat>();
	    while(video.read(frame)) { 
	    	if(!frame.empty())
	    	{
	    		numOfFrames++;
	    		if(numOfFrames%distance == 1) {
	    			if(isFirstFrame) {
	    				isFirstFrame = false;
	    				frames.add(frame.clone());
	    				Mat hsvframe = new Mat();
	    				Imgproc.cvtColor(frame, hsvframe, Imgproc.COLOR_BGR2HSV);
	    				imageList = new ArrayList<Mat>();
	    			    imageList.add(hsvframe);
	    			    Imgproc.calcHist(imageList, new MatOfInt(0, 1), new Mat(), hsvHist, histSize, histRange);
	    			} else {
	    				Mat newhsvframe = new Mat();
	    				Imgproc.cvtColor(frame, newhsvframe, Imgproc.COLOR_BGR2HSV);
	    				
	    			    imageList = new ArrayList<Mat>();
	    			    imageList.add(newhsvframe);
	    			    Mat newhsvHist = new Mat();
	    			    Imgproc.calcHist(imageList, new MatOfInt(0, 1), new Mat(), newhsvHist, histSize, histRange);
	    			    
	    			    double dist = Imgproc.compareHist(hsvHist, newhsvHist, Imgproc.CV_COMP_CORREL);
	    				if(dist < th) {
		    			    System.out.println("distance (" + frames.size() + "," + (frames.size() + 1) + "): " + dist);
	    					frames.add(frame.clone());
	    					hsvHist = newhsvHist;
	    				}
	    			}
	    		}
	    	}
	        	
	   }
	   return frames;
	}	
}
