package videoretrieval;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DBClient {

    private MongoCollection frameDescriptors;

    public DBClient() {
        MongoClient mongoClient = new MongoClient();
        Jongo jongoClient = new Jongo(mongoClient.getDB("VideoRetrieval"));
        this.frameDescriptors = jongoClient.getCollection("FrameDescriptors");
    }

    public void clear() {
        System.out.println("Please type in \"yes please\" to drop all frame descriptors");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        try {
            input = reader.readLine();
        } catch (Exception ignore) {
        }

        if (input.equals("yes please")) {
            this.frameDescriptors.drop();
            System.out.println("dropped all frame descriptors");
        }
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
