package videoretrieval;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class Colour {

    public Colour() {
    }

    private ObjectId _id;

    @JsonProperty("red")
    public int red;

    @JsonProperty("green")
    public int green;

    @JsonProperty("blue")
    public int blue;

    public static Colour create(int red, int green, int blue) {
        Colour colour = new Colour();
        colour.red = red;
        colour.green = green;
        colour.blue = blue;
        return colour;
    }
}
