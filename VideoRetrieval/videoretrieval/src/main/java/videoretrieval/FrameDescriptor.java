package videoretrieval;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class FrameDescriptor {

    public FrameDescriptor() {
    }

    private ObjectId _id;

    @JsonProperty("fileName")
    public String fileName;

    @JsonProperty("frameNumber")
    public int frameNumber;

    @JsonProperty("histogram")
    public double[] histogram;

    @JsonProperty("dominantColours")
    public Colour[] dominantColours;

    @JsonProperty("labels")
    public String[] labels;

    public static FrameDescriptor create(String fileName, int frameNumber, double[] histogram, Colour[] dominantColours, String[] labels) {
        FrameDescriptor descriptor = new FrameDescriptor();
        descriptor.fileName = fileName;
        descriptor.frameNumber = frameNumber;
        descriptor.histogram = histogram;
        descriptor.dominantColours = dominantColours;
        descriptor.labels = labels;
        return descriptor;
    }
}
