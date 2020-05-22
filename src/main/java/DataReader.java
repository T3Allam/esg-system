import com.mongodb.BasicDBObject;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataReader extends Thread {
    private static String COLLECTION_NAME="twitter";
    private Database database;
//    Logger logger = LoggerFactory.getLogger(DataReader.class.getName());
    MongoCollection<Document> collection;

    public DataReader (Database database) {
        this.database = database;
        this.collection = database.getMongoDatabase().getCollection(COLLECTION_NAME).withWriteConcern(WriteConcern.MAJORITY).withReadPreference(ReadPreference.primaryPreferred());
    }

    public void run () {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Query unread documents
            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put("read", 0);
            //Updating data by changing read to 1 to mark document as read
            Bson markAsRead = new Document ("read", 1);
            Bson updateoperation = new Document("$set", markAsRead);
            //Looping through matching documents and updating to read
            FindIterable<Document> cursor = collection.find(whereQuery).limit(5);
            for (Document document: cursor) {
                collection.updateOne(document, updateoperation);
            }
        }
    }
}
