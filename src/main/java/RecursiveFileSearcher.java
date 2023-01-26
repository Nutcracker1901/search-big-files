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
}
