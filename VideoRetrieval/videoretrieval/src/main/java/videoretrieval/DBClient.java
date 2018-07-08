package videoretrieval;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBClient {

    private MongoCollection frameDescriptors;

    public DBClient() {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
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

    public FrameDescriptor[] getAllDescriptors() {
        MongoCursor<FrameDescriptor> descriptors = this.frameDescriptors.find().as(FrameDescriptor.class);
        ArrayList<FrameDescriptor> descriptorList = new ArrayList<>(descriptors.count());

        while(descriptors.hasNext()) {
            descriptorList.add(descriptors.next());
        }

        return descriptorList.toArray(new FrameDescriptor[0]);
    }

    public FrameDescriptor[] query(String[] labels, Boolean matchAll) {
        String query = "{ labels : { " + (matchAll ? "$all" : "$in") + " : [ ";

        for (int i = 0; i < labels.length; i++) {
            query += "'" + labels[i] + "'" + (i == labels.length - 1 ? " " : ", ");
        }

        query += " ] } }";
        MongoCursor<FrameDescriptor> matches = this.frameDescriptors.find(query).as(FrameDescriptor.class);
        ArrayList<FrameDescriptor> matchesList = new ArrayList<>(matches.count());

        while(matches.hasNext()) {
            matchesList.add(matches.next());
        }

        return matchesList.toArray(new FrameDescriptor[0]);
    }
}
