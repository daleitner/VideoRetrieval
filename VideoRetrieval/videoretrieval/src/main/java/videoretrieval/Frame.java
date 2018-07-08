package videoretrieval;

import org.opencv.core.Mat;

public class Frame {
    public Frame(int fileId, int number, Mat data, Mat histogram) {
        this.fileId = fileId;
        this.number = number;
        this.histogram = histogram;
        this.data = data;
    }

    public int fileId;

    public int number;

    public Mat histogram;

    public Mat data;
}
