public class Application {
    private static String[] entityNouns = {"شركة", "مشروع"};
    private static String[] esgNouns = {"محكمة","اتهامات","قضية","رشوة","رشاوي", "ظروف عمل" ,"غسيل أموال","التآمر","تزوير","إحتيال","إختلاس"
            ,"ضرر","فساد","تهرب ضريبي" ,"احتجاجات","إهمال","حادث", "تعسفي" ,"رشوة","غسيل أموال","حقوق الإنسان"};

    public static void main(String[] args) throws InterruptedException {
        Database database = new Database();
//        for (String entityN: entityNouns) {
//            for (String esgN : esgNouns){
        String entityN = "شركة";
        String esgN = "محكمة";
                Thread thread = new Thread(new Webcrawler(database, entityN, esgN));
                thread.start();
                thread.join();
//            }
//        }
    }
}
