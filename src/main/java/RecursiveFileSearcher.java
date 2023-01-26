import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.RecursiveTask;

public class RecursiveFileSearcher extends RecursiveTask<TreeMap> {

    private static volatile long fileSize;
    private String path;

    public RecursiveFileSearcher(String path) {
        this.path = path;
    }

    public RecursiveFileSearcher(String path, long size) {
        this.path = path;
        fileSize = size;
    }

    @Override
    protected TreeMap compute() {
        List<RecursiveFileSearcher> taskList = new ArrayList<>();
        TreeMap<Long, String> files = new TreeMap<>();

        File dir = new File(path);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                RecursiveFileSearcher task = new RecursiveFileSearcher(file.getAbsolutePath());
                task.fork();
                taskList.add(task);
            }

            if (file.isFile() && file.length() > fileSize) {
                files.put(file.length(), file.getPath());
            }
        }

        for (RecursiveTask task : taskList) {
            files.putAll((Map<? extends Long, ? extends String>) task.join());
        }

        return files;
    }

//    protected String compute() {
//        Matcher matcher = Pattern.compile("/[A-Za-z0-9\\-_]+/[^\"?=]*")
//                .matcher(doc.body().toString());
//
//        List<UrlRecursiveSearcher> taskList = new ArrayList<>();
//
//        while (matcher.find()) {
//            String url = doc.body().toString().substring(matcher.start(), matcher.end());
//
//            if (!url.startsWith(siteUrl))
//                url = siteUrl.concat(url);
//
//            if (!url.matches(".+\\.ru/.+\\..+") && !url.contains("#")
//                    && !copy.contains(url.trim()) && url.startsWith(urlOrigin)) {
//                copy.add(url.trim());
//                UrlRecursiveSearcher task = new UrlRecursiveSearcher(url, copy, tab + "\t");
//                task.fork();
//                taskList.add(task);
//
//                System.out.println(tab + "\t" + url);
//            }
//        }
//        for (RecursiveTask task : taskList) {
//            urlOrigin = (task.join().equals("")) ? urlOrigin : urlOrigin.concat("\n" + task.join());
//        }
//
//        return tab.concat(urlOrigin);
//    }

}
