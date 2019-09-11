package domain;

/**
 * A POJO representation of (url, count) 2-tuple for URL frequency statistic.
 */
public class URLEntry implements Comparable<URLEntry> {

    private String url;

    private long count;

    public URLEntry(String url, long count) {
        this.url = url;
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "URLEntry{" +
                "url='" + url + '\'' +
                ", count=" + count +
                '}';
    }

    @Override
    public int compareTo(URLEntry o) {
        long cmp = count - o.count;
        if (cmp == 0) {
            return 0;
        } else if (cmp < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
