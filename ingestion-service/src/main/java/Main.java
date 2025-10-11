import control.utils.CrawlerConfig;
import control.CrawlerController;

public class Main {
    public static void main(String[] args) {
        CrawlerConfig config = parseArguments(args);
        CrawlerController crawler = new CrawlerController(config);
        crawler.crawlRange();
    }

    private static CrawlerConfig parseArguments(String[] args) {
        int startId = getArgument(args, 0, 1);
        int endId = getArgument(args, 1, 1000);
        long delay = getArgument(args, 2, 1000L);
        return new CrawlerConfig(startId, endId, delay);
    }

    private static int getArgument(String[] args, int index, int defaultValue) {
        return args.length > index ? Integer.parseInt(args[index]) : defaultValue;
    }

    private static long getArgument(String[] args, int index, long defaultValue) {
        return args.length > index ? Long.parseLong(args[index]) : defaultValue;
    }
}
