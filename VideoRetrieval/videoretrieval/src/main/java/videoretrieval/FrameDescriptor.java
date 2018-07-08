package videoretrieval;

import de.undercouch.bson4jackson.types.ObjectId;

public class FrameDescriptor {

    private ObjectId _id;

    private String fileName;

    private int frameNumber;

    private double[] histogram;

    private Colour[] dominantColours;

}
