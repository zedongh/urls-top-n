package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

/**
 * A tools for gen random URL (Uniform Resource Locator).
 * Reference: https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#Generic_syntax
 * URI syntax:
 *  URI = scheme:[//authority]path[?query][#fragment]
 *  authority = [userinfo@]host[:port]
 * For simplify, this generator will always generate:
 *  scheme:host[:post]path[?query][#fragment]
 */
public class URLGenerator {

    private static final String[] SCHEMES = new String[]{"http", "https", "ftp", "mailto", "file", "data", "irc"};

    private static final String[] TOP_LEVEL_DOMAIN = new String[]{
            "com", "org", "net", "int", "edu", "gov", "mil", "arpa"
    };

    private static final int SUB_DOMAIN_NUM = 2;

    private static final int MAX_PATH_NUM = 3;

    private static final int MAX_QUERY_NUM = 3;

    private static final double DEFAULT_REPEAT_PROB = 0.001;

    private static final int DEFAULT_REPEAT_FACTOR = 5000;

    private static Random random = new Random();

    private static String genFakeWord() {
        int length = 1 + random.nextInt(10);
        char[] seq = new char[length];
        for (int i = 0; i < length; i++) {
            seq[i] = (char) (random.nextInt(26) + 'a');
        }
        return new String(seq);
    }

    public static String genURL() {
        StringBuilder builder = new StringBuilder();
        // gen scheme
        int protoIdx = random.nextInt(SCHEMES.length);
        builder.append(SCHEMES[protoIdx]).append("://");
        // gen domain
        for (int j = 0; j < SUB_DOMAIN_NUM; j++) {
            builder.append(genFakeWord()).append('.');
        }
        int tldIdx = random.nextInt(TOP_LEVEL_DOMAIN.length);
        builder.append(TOP_LEVEL_DOMAIN[tldIdx]);
        // gen port
        if (random.nextDouble() > 0.9) {
            builder.append(':').append(random.nextInt(10000) + 1000);
        }
        // gen path
        int pathLen = 1 + random.nextInt(MAX_PATH_NUM);
        for (int i = 0; i < pathLen; i++) {
            builder.append("/").append(genFakeWord());
        }
        // gen query
        if (random.nextBoolean()) {
            int queryLen = 1 + random.nextInt(MAX_QUERY_NUM);
            builder.append("?").append(genFakeWord()).append('=').append(genFakeWord());
            for (int i = 0; i < queryLen - 1; i++) {
                builder.append('&').append(genFakeWord()).append('=').append(genFakeWord());
            }
        }
        // gen fragment
        if (random.nextBoolean()) {
            builder.append("#").append(genFakeWord());
        }
        return builder.toString();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: [total-url-num] [filename] \n");
            return;
        }
        long total = Long.parseLong(args[0]);
        File file = new File(args[1]);
        if (!file.exists() && !file.createNewFile()) {
            System.err.println("cannot create new file");
            return;
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        System.out.println(String.format("start generating %s urls into %s ...", total, file.getName()));
        long start = System.currentTimeMillis();
        for (long i = 0; i < total; i++) {
            String url = genURL();
            writer.write(url + '\n');
            double lucky = random.nextDouble();
            if (lucky < DEFAULT_REPEAT_PROB) {
                int max = (int) ((DEFAULT_REPEAT_PROB + random.nextDouble()) * DEFAULT_REPEAT_FACTOR);
                long repeatTimes = random.nextInt(max);
                repeatTimes = Math.min(repeatTimes, total - 2 - i);
                for (int j = 0; j < repeatTimes; j++) {
                    writer.write(url + '\n');
                }
                i += repeatTimes;
            }
        }
        writer.flush();
        writer.close();
        long timeSpend = System.currentTimeMillis() - start;
        System.out.println(String.format("generating complete ! total time %s (s) .", timeSpend / 1000));
    }
}
