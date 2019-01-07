package com.example.testkontur;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Pattern;

public class UrlValidator {
    private final static Pattern UrlPattern = Pattern.compile("(?i)\\b(?:(?:https?|ftp|file)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?\\b");
    private final static String scheme = "http://";

    public static Optional<String> getValidUrlIfPossible(String url) {
        try {
            URL a = new URI(url).toURL();
            return Optional.of(url);
        } catch (IllegalArgumentException e) {
            url = scheme + url;
            if (UrlPattern.matcher(url).find())
                return Optional.of(url);
            else
                return Optional.empty();
        } catch (MalformedURLException | URISyntaxException e) {
            return Optional.empty();
        }
    }
}
