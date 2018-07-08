package videoretrieval;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class RankedFrameDescriptor extends FrameDescriptor {

    public double score;

    public RankedFrameDescriptor(FrameDescriptor descriptor) {
        this.fileId = descriptor.fileId;
        this.frameNumber = descriptor.frameNumber;
        this.histogram = descriptor.histogram;
        this.dominantColours = descriptor.dominantColours;
        this.labels = descriptor.labels;
    }

    public void calculateScore(String[] targetLabels, double[] targetHistogram, Colour[] targetDominantColours) {
        this.score = 0;
    }
}
