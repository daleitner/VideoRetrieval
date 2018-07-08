package videoretrieval;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;

import java.util.ArrayList;
import java.util.Arrays;

import static org.opencv.core.Core.NORM_L2;
import static org.opencv.core.Core.max;
import static org.opencv.core.Core.norm;
import static org.opencv.core.CvType.CV_32FC1;

public class RankedFrameDescriptor extends FrameDescriptor {

    public double score;

    public RankedFrameDescriptor(FrameDescriptor descriptor) {
        this.fileId = descriptor.fileId;
        this.frameNumber = descriptor.frameNumber;
        this.histogram = descriptor.histogram;
        this.dominantColours = descriptor.dominantColours;
        this.labels = descriptor.labels;
    }

    private Mat arrayToMat(double[] array) {
        Mat mat = new Mat(1, array.length, CV_32FC1);
        mat.put(0, 0, array);
        return mat;
    }

    public void calculateScore(String[] targetLabels, double[] targetHistogram, Colour[] targetDominantColours) {
        double histogramScore = 1;
        double dominantColorScore = 1;
        double numLabels = this.labels.length;
        double numTargetLabels = targetLabels.length;
        double numMatches = Arrays.stream(this.labels)
                .distinct()
                .filter(x -> Arrays.stream(targetLabels).anyMatch(y -> y.equals(x)))
                .toArray().length;

        double labelScore = numMatches / ((numLabels + numTargetLabels) - numMatches);

        if (targetHistogram.length > 0) {
            histogramScore = 1 / Math.max(norm(
                    arrayToMat(this.histogram),
                    arrayToMat(targetHistogram),
                    NORM_L2
            ), 1e-5);
        }

        if (targetDominantColours.length > 0) {
            for (Colour targetDominantColour: targetDominantColours) {
                double maxSimilarity = Double.MIN_VALUE;

                for (Colour dominantColour: this.dominantColours) {
                    double score = 1 / Math.max(norm(
                            arrayToMat(new double[] {dominantColour.red, dominantColour.green, dominantColour.blue}),
                            arrayToMat(new double[] {targetDominantColour.red, targetDominantColour.green, targetDominantColour.blue}),
                            NORM_L2
                    ), 1e-5);

                    if (score > maxSimilarity) {
                        maxSimilarity = score;
                    }
                }

                dominantColorScore *= maxSimilarity;
            }
        }

        this.score = histogramScore * dominantColorScore * labelScore;
    }
}
