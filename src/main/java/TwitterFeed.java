import com.google.common.collect.Lists;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TwitterFeed extends Thread {
    private String consumerKey = "";
    private String consumerSecret = "";
    private String token = "";
    private String secret = "";
    private String COLLECTION_NAME = "twitter";
    private Database database;
    Logger logger = LoggerFactory.getLogger(TwitterFeed.class.getName());

    //    Logger logger = LoggerFactory.getLogger(TwitterFeed.class.getName());
//    ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
//    Lock readLock = rwLock.readLock();
//    Lock writeLock = rwLock.writeLock();
    MongoCollection<Document> collection;

    public TwitterFeed(Database database) {
        this.database = database;
        this.collection = database.getMongoDatabase().getCollection(COLLECTION_NAME).withWriteConcern(WriteConcern.MAJORITY).withReadPreference(ReadPreference.primaryPreferred());
    }

    //create a twitter client
    public Client createTwitterClient(BlockingQueue<String> msgQueue) {
        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        // Optional: set up some followings and track terms - following terms
        List<String> terms = Lists.newArrayList("coffee", "price of oil", "price of gold",
                "mergers", "acquisition", "c++", "football tactics", "football transfers", "business ideas", "programming", "neuroscience", "philosophy");
        hosebirdEndpoint.trackTerms(terms);
        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
        ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01").hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint).processor(new StringDelimitedProcessor(msgQueue));
        Client hosebirdClient = builder.build();
        // Attempts to establish a connection.
        return hosebirdClient;
    }

    public void run() {
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
        //create twitter client & establish connection
        Client client = createTwitterClient(msgQueue);
        client.connect();
        // on a different thread, or multiple different threads....
        while (!client.isDone()) {
            String msg = "";
            try {
                msg = msgQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
//            logger.info(msg);
            //parse to get msg if url exists
            JSONObject obj = new JSONObject(msg);
            JSONArray urlArray = obj.getJSONObject("entities").getJSONArray("urls");
            //select tweets that only have url - higher probability that this a news agency
            if (urlArray.length() > 1) {
                Document doc = new Document();
                doc.put("text", obj.getString("text"));
                doc.put("url", urlArray.getJSONObject(0).getString("expanded_url"));
                doc.put("read",0);
//                logger.info(msg);
//                writeLock.lock();
//                try{
                    collection.insertOne(doc);
//                } finally {
//                    writeLock.unlock();
//                }
            }
        }
    }
}
