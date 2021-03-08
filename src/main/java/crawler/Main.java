package crawler;

public class Main {
    public static void main(String[] args) {
        String rootUrl = args[args.length - 1];
        if(!isValidHttpUrl(rootUrl)) {
            System.err.printf("\"%s\" is not a valid Http URL!", rootUrl);
            System.exit(1);
        }
    }

    public static boolean isValidHttpUrl(String url) {
        //https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
        return url.matches("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    }
}
