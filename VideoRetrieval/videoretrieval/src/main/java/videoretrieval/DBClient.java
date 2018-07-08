package videoretrieval;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import java.util.ArrayList;

public class DBClient {

    private MongoCollection frameDescriptors;

    public DBClient() {
        MongoClient mongoClient = new MongoClient();
        Jongo jongoClient = new Jongo(mongoClient.getDB("VideoRetrieval"));
        this.frameDescriptors = jongoClient.getCollection("FrameDescriptors");
    }

    public void saveFrameDescriptor(FrameDescriptor descriptor) {
        this.frameDescriptors.save(descriptor);
    }

    public ArrayList<FrameDescriptor> getAllDescriptors() {
        MongoCursor<FrameDescriptor> descriptors = this.frameDescriptors.find().as(FrameDescriptor.class);
        ArrayList<FrameDescriptor> descriptorList = new ArrayList<>(descriptors.count());

        while(descriptors.hasNext()) {
            descriptorList.add(descriptors.next());
        }

        return descriptorList;
    }
}
