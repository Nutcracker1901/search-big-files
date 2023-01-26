import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong arguments. Required: <String> path, <int> filesize");
            System.exit(1);
        }

        String path = args[0];
        long fileSize = Long.parseLong(args[1]) * 1024 * 1024;

        String[] values = {" bytes", " Kb", " Mb", " Gb", " Tb", " Pb"};

        TreeMap<Long, String> files;
        files = new ForkJoinPool().invoke(new RecursiveFileSearcher(path, fileSize));

        files.forEach((l, s) -> {

            int i = BigDecimal.valueOf(Math.log(l) / Math.log(1024)).intValue();
            BigDecimal size = BigDecimal.valueOf(l);
            for (int j = 0; j < i; j++) {
                size = size.divide(BigDecimal.valueOf(1024));
            }
            DecimalFormat format = new DecimalFormat("###.#");
            System.out.println(format.format(size) + values[i] + "\t" + s);
        });
    }
}
