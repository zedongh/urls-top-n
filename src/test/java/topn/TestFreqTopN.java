package topn;

import domain.URLEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.URLGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestFreqTopN {

    private Map<String, Long> urlMap = new HashMap<>();

    private static final File TEST_1W = new File("test1w.tmp");

    private Random random = new Random();

    @Before
    public void genDataSet() throws IOException {
        if (!TEST_1W.exists()) {
            boolean created = TEST_1W.createNewFile();
            if (!created) {
                throw new RuntimeException("cannot create test file!");
            }
        }

        for (int i = 0; i < 100; i++) {
            urlMap.put(URLGenerator.genURL(), 50L + random.nextInt(50));
        }

        List<String> urlList = new ArrayList<>(10010);
        int i = 0;
        urlMap.forEach((k, v) -> {
            for (int j = 0; j < v; j++) {
                urlList.add(k);
            }
        });
        while (urlList.size() < 10000) {
            urlList.add(URLGenerator.genURL());
        }
        Collections.shuffle(urlList);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(TEST_1W));
        for (String url : urlList) {
            writer.write(url + '\n');
        }
        writer.flush();
        writer.close();
    }

    @After
    public void deleteDataSet() {
        TEST_1W.deleteOnExit();
    }

    @Test
    public void test10KUrls() {
        FreqTopN topN = new FreqTopN(100);
        topN.fit(TEST_1W);
        Iterator<URLEntry> result = topN.getResult();
        while (result.hasNext()) {
            URLEntry tup = result.next();
            Assert.assertEquals(urlMap.get(tup.getUrl()), Long.valueOf(tup.getCount()));
        }
    }
}
