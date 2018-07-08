package videoretrieval;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Util {
    public static double[] mat2Arr(Mat hist) {
        // Convert histogram Mat into double array
        // Some "workaround" taken from
        // http://answers.opencv.org/question/14961/using-get-and-put-to-access-pixel-values-in-java/
        hist.convertTo(hist, CvType.CV_64FC3);
        int histArrSize = (int) (hist.total() * hist.channels());
        double[] newHist = new double[histArrSize];
        hist.get(0,0, newHist);

        for (int i = 0; i < histArrSize; i++) {
            newHist[i] = (newHist[i] / 2);
        }

        return newHist;
    }

    public static Mat arr2Mat(double[] arr){
        Mat hist = new Mat(30, 32, CvType.CV_32FC1);
        hist.convertTo(hist, CvType.CV_64FC3);

        for (int i = 0; i < arr.length; i++) {
            arr[i] = (arr[i] * 2);
        }

        hist.put(0,0, arr);
        hist.convertTo(hist, CvType.CV_32FC1);

        return hist;
    }

    private static double[] renormHist(double[] unnormHistArr) {
        Mat hist = Util.arr2Mat(unnormHistArr);
        Core.normalize(hist, hist, 0, 1 , Core.NORM_MINMAX);
        return Util.mat2Arr(hist);
    }
}
