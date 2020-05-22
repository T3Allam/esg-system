import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Database {
    private static String mongoDbUrl = "mongodb://127.0.0.1:27018,127.0.0.1:2019,127.0.0.1:27020/?replicaSet=rs0"; //address
    private static String DBNAME = "esg"; //name of db
    private MongoDatabase mongoDatabase;

    public Database() {
        this.mongoDatabase = connectToMongoDb(mongoDbUrl);
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoDatabase connectToMongoDb(String url) {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(url));
        return mongoClient.getDatabase(DBNAME);
    }
}
