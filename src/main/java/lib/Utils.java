package lib;

import com.applitools.Commands.AnimatedDiffs;
import com.applitools.Commands.DownloadDiffs;
import com.applitools.Commands.DownloadImages;
import com.applitools.eyes.TestResults;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Utils {

    //Not empty
    public static boolean ne(String... strings) {
        for (String s : strings) {
            if (s == null) return false;
            if (s.isEmpty()) return false;
        }
        return true;
    }

    public static <T extends Enum<T>> T parseEnum(Class<T> c, String string) {
        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
            }
        }
        throw new RuntimeException(String.format("Unable to parse value %s for enum %s", string, c.getName()));
    }

    public static String getEnumValues(Class type) {
        StringBuilder sb = new StringBuilder();
        for (Object val : EnumSet.allOf(type)) {
            sb.append(StringUtils.capitalize(val.toString().toLowerCase()));
            sb.append('|');
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static List<Integer> parsePagesNotation(String input) {
        if (input == null || input.isEmpty()) return null;
        ArrayList<Integer> pagesToInclude = new ArrayList<Integer>();
        String[] inputPages = input.split(",");
        for (int i = 0; i < inputPages.length; i++) {
            if (inputPages[i].contains("-")) {
                String[] splits = inputPages[i].split("-");
                int left = Integer.valueOf(splits[0]);
                int right = Integer.valueOf(splits[1]);
                if (left <= right)
                    for (int j = left; j <= right; pagesToInclude.add(j++)) ;
                else
                    for (int j = left; j >= right; pagesToInclude.add(j--)) ;
            } else
                pagesToInclude.add(Integer.valueOf(inputPages[i]));
        }
        return pagesToInclude;

    }

    public static List<Integer> generateRanage(int range, int start) {
        List<Integer> retRange = new ArrayList<>(range);
        for (int i = start; i < range; ++i)
            retRange.add(i);
        return retRange;
    }

    public static void handleResultsDownload(EyesUtilitiesConfig config, TestResults results) throws Exception {
        if (config == null) return;
        if (config.getDownloadDiffs() || config.getGetGifs() || config.getGetImages()) {
            if (config.getViewKey() == null) throw new RuntimeException("The view-key cannot be null");
            if (config.getDownloadDiffs() && !results.isNew() && !results.isPassed())
                new DownloadDiffs(results.getUrl(), config.getDestinationFolder(), config.getViewKey()).run();
            if (config.getGetGifs() && !results.isNew() && !results.isPassed())
                new AnimatedDiffs(results.getUrl(), config.getDestinationFolder(), config.getViewKey()).run();
            if (config.getGetImages())
                new DownloadImages(results.getUrl(), config.getDestinationFolder(), config.getViewKey(), false, false).run();
        }
    }
}
