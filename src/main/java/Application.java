

public class Application {
    public static void main(String[] args)  {
        Database database = new Database();
        Thread tt = new TwitterFeed(database);
        tt.start();

        Thread thread = new DataFeedSimulator(database);
        thread.start();

        Thread readerThread = new DataReader(database);
        readerThread.start();
    }
}
