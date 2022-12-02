import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.sql.ClientInfoStatus;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> wordsMap;

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        wordsMap = new HashMap<>();

        for (File file : pdfsDir.listFiles()) {
            PdfDocument doc = new PdfDocument(new PdfReader(file));
            for (int pageNum = 1; pageNum <= doc.getNumberOfPages(); pageNum++) {
                PdfPage page = doc.getPage(pageNum);
                String text = PdfTextExtractor.getTextFromPage(page);
                String[] words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> wordsFreq = new HashMap<>();

                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    wordsFreq.put(word, wordsFreq.getOrDefault(word, 0) + 1);
                }

                for (String word : wordsFreq.keySet()) {
                    PageEntry pageEntry = new PageEntry(file.getName(), pageNum, wordsFreq.get(word));
                    if (!wordsMap.containsKey(word)) {
                        List<PageEntry> pageEntryList = new ArrayList<>();
                        pageEntryList.add(pageEntry);
                        wordsMap.put(word, pageEntryList);
                    } else {
                        wordsMap.get(word).add(pageEntry);
                    }
                }
            }
        }
    }


    @Override
    public List<PageEntry> search(String line) {

        String[] words = line.toLowerCase().split(" ");

        List<PageEntry> listEntries = new ArrayList<>();

        for (String word : words) {
            if (wordsMap.get(word) != null) {
                listEntries.addAll(wordsMap.get(word));
            }
        }

        Map<String, PageEntry> mapEntries = new HashMap<>();

        for (PageEntry pageEntry : listEntries) {
            String key = pageEntry.getPdfName() + ", " + pageEntry.getPage();
            if (!mapEntries.containsKey(key)) {
                mapEntries.put(key, pageEntry);
            } else {
                int count = mapEntries.get(key).getCount();
                mapEntries.put(key, new PageEntry(pageEntry.getPdfName(), pageEntry.getPage(), pageEntry.getCount() + count));
            }
        }

        List<PageEntry> resultList = new ArrayList<>(mapEntries.values());

        if (resultList != null) {
            Collections.sort(resultList);
        }
        return resultList;
    }
}
