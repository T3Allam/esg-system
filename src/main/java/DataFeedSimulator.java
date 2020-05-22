import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataFeedSimulator extends Thread  {

    private static final String characters = "abcdefghijklmnopqrstuvwxyz";
    private static final String COLLECTION_NAME = "twitter";
    private Database database;
    private MongoCollection<Document> collection;
    Logger logger = LoggerFactory.getLogger(DataFeedSimulator.class.getName());
    public DataFeedSimulator(Database database) {
        this.database = database;
        this.collection = database.getMongoDatabase().getCollection(COLLECTION_NAME).withWriteConcern(WriteConcern.MAJORITY).withReadPreference(ReadPreference.primaryPreferred());
    }

    public void run() {
        while (true) {
            String documentName = "";
            int titleLength = 20;
            Random random = new Random();
            char[] title = new char[titleLength];
            for (int i = 0; i < titleLength; i++) {
                if (i > 0 && i % 5 == 0) {
                    title[i] = ' ';
                    documentName += title[i];
                    continue;
                }
                title[i] = characters.charAt(random.nextInt(characters.length()));
                documentName += title[i];
            }
            Document doc = new Document();
            doc.put("text", documentName);
            doc.put("read",0);
//            logger.info(doc.toJson());
            collection.insertOne(doc);
        }
    }
}
