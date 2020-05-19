import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Database {
    private static String mongoDbUrl = "mongodb://127.0.0.1:27017"; //address
    private String dbName; //name of db
    private MongoDatabase mongoDatabase;

    public Database() {
        this.dbName = "esg"; //name of db
        MongoDatabase mongoDatabase = connectToMongoDb(mongoDbUrl, dbName);
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoDatabase connectToMongoDb(String url, String dbName) {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(url));
        return mongoClient.getDatabase(dbName);
    }
}
