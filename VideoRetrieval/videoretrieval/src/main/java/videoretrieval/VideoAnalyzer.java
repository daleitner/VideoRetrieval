package videoretrieval;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
public class VideoAnalyzer {
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	private String videoPath;
	private VideoCapture video;
	public VideoAnalyzer(String videoPath) {
		super();
		this.videoPath = videoPath;
		video = new VideoCapture(videoPath);
		System.out.println("FPS: " + video.get(Videoio.CAP_PROP_FPS));
		System.out.println("Frames: " + video.get(Videoio.CAP_PROP_FRAME_COUNT));
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
	
	public ArrayList<Mat> extractKeyFrames() {
		Mat frame = new Mat();
		int distance = 60;
		int numOfFrames = 0;
		ArrayList<Mat> frames = new ArrayList<Mat>();
	    while(video.read(frame)) { 
	    	if(!frame.empty())
	    	{
	    		numOfFrames++;
	    		if(numOfFrames%60 == 1)
	    			frames.add(frame);
	    	}
	        	
	   }
	   return frames;
	}
	
}
