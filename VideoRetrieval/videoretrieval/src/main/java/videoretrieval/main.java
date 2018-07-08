package videoretrieval;

import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class main {
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	private static final String path = "F:/Privat/DLVideoRetrieval/VideoRetrieval/videoretrieval/videos";
	private static final String testvideo = path + "/35368.mp4";
	public static void main(String[] args) throws Exception {
		System.out.println(testvideo);
		VideoAnalyzer va = new VideoAnalyzer();
		ArrayList<Mat> frames = va.extractKeyFrames(testvideo, 1, 0.4);
		saveFrames(frames);
	}
	
	private static void saveFrames(ArrayList<Mat> frames) {
		String imgPath = path + "/Imgs/";
		for(int i = 0; i<frames.size(); i++) {
			Imgcodecs.imwrite(imgPath + (i+1) + ".jpg", frames.get(i));
		}
	}

	private static void testDB() {
		DB db = new MongoClient().getDB("dbname");

		Jongo jongo = new Jongo(db);
		MongoCollection friends = jongo.getCollection("friends");

		// MongoCursor<Friend> all = friends.find("{name: 'Joe'}").as(Friend.class);
		// Friend one = friends.findOne("{name: 'Joe'}").as(Friend.class);
	}
}
