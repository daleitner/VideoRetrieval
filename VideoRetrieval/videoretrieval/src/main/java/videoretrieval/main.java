package videoretrieval;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class main {
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	private static final String path = "C:/Videos";
	private static final String testvideo = path + "/35771.mp4";
	public static void main(String[] args) throws Exception {
		System.out.println(testvideo);
		VideoAnalyzer va = new VideoAnalyzer(testvideo);
		ArrayList<Mat> frames = va.extractKeyFrames();
		String imgPath = path + "/Imgs/";
		for(int i = 0; i<frames.size(); i++) {
			/*BufferedImage bi = Mat2BufferedImage(frames.get(i));
			File outputfile = new File(imgPath + (i+1) + ".jpg");
			ImageIO.write(bi, "jpg", outputfile);*/


			Imgcodecs.imwrite(imgPath + (i+1) + ".jpg", frames.get(i));
		}
	}
	
	/*static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {        
	    MatOfByte mob=new MatOfByte();
	    Imgcodecs.imencode(".jpg", matrix, mob);
	    byte ba[]=mob.toArray();

	    BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
	    return bi;
	}*/

}
