package topn;

import domain.URLEntry;
import util.FixedSizeHeap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;

/**
 * Algorithm for URL frequency top n problem.
 */
public class FreqTopN {
    /**
     * The maximum memory limit (bytes) of program.
     */
    private static final long MEMORY_LIMIT = 1024 * 1024 * 1024;
    /**
     * A safety file split size baseline, for program running will always not exceed the MEMORY_LIMIT.
     */
    private static final long QUARTER_MEMORY = MEMORY_LIMIT / 4;
    /**
     * A fixed size min heap for collecting statistical results.
     */
    private FixedSizeHeap<URLEntry> fixedSizeHeap;

    public FreqTopN(int n) {
        fixedSizeHeap = new FixedSizeHeap<>(n);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: [data-filename] [top-n] [result-filename] \n" +
                    "       data-filename is required. \n " +
                    "       top-n default 100. \n " +
                    "       result-filename default is <data-filename>-result \n");
            return;
        }
        int n = 100;
        if (args.length > 1) {
            n = Integer.parseInt(args[1]);
        }
        FreqTopN freqTopN = new FreqTopN(n);
        freqTopN.fit(args[0]);
        String resultFileName = args[0] + "-result";
        if (args.length > 2) {
            resultFileName = args[2];
        }
        try {
            freqTopN.emitResult(resultFileName);
        } catch (IOException e) {
            System.err.println("emit result to file failed!");
            e.printStackTrace();
        }
    }

    /**
     * Collecting strings from string stream, group its frequency into map.
     */
    private Map<String, Long> lineFreqCount(Stream<String> lines) {
        return lines.collect(Collectors.groupingBy(Function.identity(), counting()));
    }
    /**
     * Link lineFreqCount method, collecting lines from file, group its frequency into map.
     */
    private Map<String, Long> fileLineFreqCount(File file) throws FileNotFoundException {
        Stream<String> lines = new BufferedReader(new InputStreamReader(new FileInputStream(file))).lines();
        return lineFreqCount(lines);
    }

    private void urlCount(File file) throws IOException {
        long start = System.currentTimeMillis();
        Map<String, Long> urlCountPairs = fileLineFreqCount(file);
        urlCountPairs.forEach((key, value) -> fixedSizeHeap.insert(new URLEntry(key, value)));
        long time = System.currentTimeMillis() - start;
        System.out.println(String.format("handle in memory spend %s (s)", time / 1000));
    }

    /**
     * Try to split the big file into many part of files which file size will not exceed the QUARTER_MEMORY limit.
     * i.e. when call this method, program will create many temporary files, this need disk have at least origin
     * file size free space for saving.
     * The number of part file will be file_size / quarter_memory.
     * For each line of origin file, will calculate its hash value to decide which file part it will to split into.
     * Also This may cause IOException for file opening, reading, writing, creating exception.
     */
    private File[] fileSplit(File file) throws IOException {
        // compute bucket num
        int bucketNum = (int) (file.length() / QUARTER_MEMORY);

        File[] slots = new File[bucketNum];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new File(String.format("%s-part%04d.tmp", file.getName(), i));
            if (!slots[i].exists() && !slots[i].createNewFile()) {
                return null;
            }
        }
        BufferedWriter[] writers = new BufferedWriter[bucketNum];
        for (int i = 0; i < writers.length; i++) {
            writers[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(slots[i])));
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String url = reader.readLine();
        while (url != null) {
            int hash = Math.abs(url.hashCode()) % bucketNum;
            writers[hash].write(url + '\n');
            url = reader.readLine();
        }
        reader.close();
        for (BufferedWriter writer : writers) {
            writer.flush();
            writer.close();
        }
        return slots;
    }

    /**
     * Try to split file into pieces then conquer each piece of file in memory.
     * This method will always clear the temporary file which created during task dividing.
     */
    private void divideAndConquer(File file) throws IOException {

        File[] files = fileSplit(file);
        if (files == null) {
            System.err.println("file split error, programming exit!");
            return;
        }

        for (File slot : files) {
            System.out.println(String.format("handle %s ...", slot.getName()));
            urlCount(slot);
            slot.deleteOnExit();
        }
    }

    /**
     * Fit the dataset, this method behavior will depend on the param file size.
     * If file size is small enough, it will be handle in memory directly.
     * Otherwise, task will be divided and conquered.
     */
    public void fit(File file) {
        if (!file.exists()) {
            System.err.println(String.format("file [%s] not found", file.getName()));
            return;
        }
        long start = System.currentTimeMillis();
        long fileSize = file.length();
        try {
            if (fileSize <= QUARTER_MEMORY) {
                urlCount(file);
            } else {
                divideAndConquer(file);
            }
            long time = System.currentTimeMillis() - start;
            System.out.println(String.format("Task complete: total time %s (s)", time / 1000));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Iterator<URLEntry> getResult() {
        return fixedSizeHeap.iterator();
    }

    public void fit(String filename) {
        fit(new File(filename));
    }

    /**
     * Emit statistical results in heap into specific file. Before calling this method, should always call fit first.
     * The result is guaranteed to be ordered
     */
    private void emitResult(String filename) throws IOException {
        Stack<URLEntry> stack = new Stack<>();
        while (!fixedSizeHeap.isEmpty()) {
            stack.push(fixedSizeHeap.pop());
        }

        File resultFile = new File(filename);
        if (!resultFile.exists() && !resultFile.createNewFile()) {
            System.err.println(String.format("create %s result file failed", resultFile.getName()));
            return;
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile)));
        while (!stack.isEmpty()) {
            URLEntry element = stack.pop();
            writer.write(element.getUrl() + " " + element.getCount() + '\n');
        }
        writer.flush();
        writer.close();
    }
}
