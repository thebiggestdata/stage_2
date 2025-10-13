package control.textProccesing;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ga.IrishAnalyzer;
import org.apache.lucene.analysis.gl.GalicianAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lt.LithuanianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;

import java.util.*;
import java.util.stream.Collectors;

public class StopWordFilter {
    private final Set<String> stopwords;

    public StopWordFilter() {
        this.stopwords = convertToSet(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    public StopWordFilter(String language) {
        this.stopwords = loadStopwordsByLanguage(language);
    }

    private Set<String> loadStopwordsByLanguage(String language) {
        CharArraySet luceneStopwords = switch (language.toLowerCase()) {
            case "en", "english" -> EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
            case "es", "spanish", "español" -> SpanishAnalyzer.getDefaultStopSet();
            case "fr", "french", "français" -> FrenchAnalyzer.getDefaultStopSet();
            case "it", "italian", "italiano" -> ItalianAnalyzer.getDefaultStopSet();
            case "de", "german", "deutsch" -> GermanAnalyzer.getDefaultStopSet();
            case "pt", "portuguese", "português" -> PortugueseAnalyzer.getDefaultStopSet();
            case "nl", "dutch", "nederlands" -> DutchAnalyzer.getDefaultStopSet();
            case "ru", "russian", "русский" -> RussianAnalyzer.getDefaultStopSet();
            case "el", "greek", "ελληνικά" -> GreekAnalyzer.getDefaultStopSet();
            case "ar", "arabic", "العربية" -> ArabicAnalyzer.getDefaultStopSet();
            case "bg", "bulgarian" -> BulgarianAnalyzer.getDefaultStopSet();
            case "ca", "catalan", "català" -> CatalanAnalyzer.getDefaultStopSet();
            case "cz", "czech", "čeština" -> CzechAnalyzer.getDefaultStopSet();
            case "da", "danish", "dansk" -> DanishAnalyzer.getDefaultStopSet();
            case "eu", "basque", "euskara" -> BasqueAnalyzer.getDefaultStopSet();
            case "fa", "persian", "فارسی" -> PersianAnalyzer.getDefaultStopSet();
            case "fi", "finnish", "suomi" -> FinnishAnalyzer.getDefaultStopSet();
            case "ga", "irish", "gaeilge" -> IrishAnalyzer.getDefaultStopSet();
            case "gl", "galician", "galego" -> GalicianAnalyzer.getDefaultStopSet();
            case "hi", "hindi", "हिन्दी" -> HindiAnalyzer.getDefaultStopSet();
            case "hu", "hungarian", "magyar" -> HungarianAnalyzer.getDefaultStopSet();
            case "hy", "armenian" -> ArmenianAnalyzer.getDefaultStopSet();
            case "id", "indonesian" -> IndonesianAnalyzer.getDefaultStopSet();
            case "lt", "lithuanian" -> LithuanianAnalyzer.getDefaultStopSet();
            case "lv", "latvian" -> LatvianAnalyzer.getDefaultStopSet();
            case "no", "norwegian", "norsk" -> NorwegianAnalyzer.getDefaultStopSet();
            case "ro", "romanian", "română" -> RomanianAnalyzer.getDefaultStopSet();
            case "sv", "swedish", "svenska" -> SwedishAnalyzer.getDefaultStopSet();
            case "th", "thai", "ไทย" -> ThaiAnalyzer.getDefaultStopSet();
            case "tr", "turkish", "türkçe" -> TurkishAnalyzer.getDefaultStopSet();
            case "la", "latin" -> createLatinStopwords();
            default -> EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;
        };
        return convertToSet(luceneStopwords);
    }

    private CharArraySet createLatinStopwords() {
        Set<String> latinStopwords = Set.of(
                "et", "in", "est", "non", "ad", "cum", "ex", "de", "per", "sed",
                "qui", "quod", "ut", "ne", "si", "ab", "ac", "atque", "aut",
                "autem", "enim", "etiam", "hic", "ille", "nam", "nec", "qua",
                "quae", "quam", "que", "quo", "sunt", "tamen", "vel"
        );
        return new CharArraySet(latinStopwords, true);
    }

    private Set<String> convertToSet(CharArraySet charArraySet) {
        return charArraySet.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public List<String> filter(List<String> tokens) {
        return tokens.stream()
                .filter(token -> !stopwords.contains(token))
                .collect(Collectors.toList());
    }

    public boolean isStopword(String token) {return stopwords.contains(token);}
}
