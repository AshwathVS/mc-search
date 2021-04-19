package org.mcsearch.search;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryTokenizer {

    private static final String STRICT_QUERY_REGEX = "\".*\"";
    private static final Pattern pattern = Pattern.compile(STRICT_QUERY_REGEX);

    public static class QueryTokenizedResult {
        private List<String> tokens;
        private boolean isStrict;

        private QueryTokenizedResult(List<String> tokens, boolean isStrict) {
            this.tokens = tokens;
            this.isStrict = isStrict;
        }

        public List<String> getTokens() {
            return tokens;
        }

        public boolean isStrict() {
            return isStrict;
        }

        public int getTokenCount() {
            return CollectionUtils.isEmpty(this.tokens) ? 0 : this.tokens.size();
        }
    }

    private static boolean isStrictQuery(String query) {
        Matcher matcher = pattern.matcher(query);
        return matcher.matches();
    }

    private static String cleanse(String query) {
        return query.trim();
    }

    private static boolean isSpecialCharacter(char c) {
        return !(
                Character.isDigit(c)
                        ||
                        Character.isLetter(c));
    }

    private static String removeTrailingSpecialCharacters(String query) {
        int left = 0, right = query.length() - 1;

        while(left <= right) {
            if (isSpecialCharacter(query.charAt(left))) left++;
            else if (isSpecialCharacter(query.charAt(right))) right--;
            else break;
        }

        if(left <= right) return query.substring(left, right + 1);
        else return "";
    }

    public static QueryTokenizedResult tokenizeQuery(String query) {
        query = cleanse(query);

        boolean isStrict = isStrictQuery(query);

        // remove trailing special characters...
        query = removeTrailingSpecialCharacters(query);

        // tokenize the remaining string
        List<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(query, " ");
        while(tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        return new QueryTokenizedResult(tokens, isStrict);
    }


    public static void main(String[] args) {
        String inp = "";

        QueryTokenizedResult result = tokenizeQuery(inp);

        System.out.println("Strict: " + result.isStrict);
        System.out.println(result.getTokens().toString());
    }
}
