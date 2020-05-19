import com.mongodb.client.MongoCollection;
//import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;

public class Webcrawler implements Runnable  {

    private String COLLECTION_NAME = "crawledarticles";
    Database database;

    String esgNoun;
    String entityNoun;

    public Webcrawler(Database database, String entityNoun, String esgNoun){
        this.database = database;
        this.entityNoun = entityNoun;
        this.esgNoun = esgNoun;
    }

    //crawl the internet using Jsoup and Save to MongoDB
    public void crawlAndSaveToDB() throws IOException {
        String url = "https://www.google.ca/search?q=%22"+esgNoun+"%22+%22"+entityNoun+"%22&tbm=nws&source=lnt&tbs=qdr:w&sa=X&ved=0ahUKEwijociu_L_pAhWOUt8KHfuNCgwQpwUIJA&biw=2560&bih=1225&dpr=1";
        //connecting to Jsoup
        Document doc = Jsoup.connect(url).get();
        //Selecting href from search results
        Elements links = doc.select("#search a");
        //inserting elements into database
        for (Element e: links){
            System.out.println(e.attr("href"));
//            crawledDocuments.insertOne(new org.bson.Document("href", e));
        }
    }

    public void run() {
//        MongoCollection<org.bson.Document> crawledDocuments =  database.getMongoDatabase().getCollection(COLLECTION_NAME);
        try {
            crawlAndSaveToDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
