package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    static StringBuilder sb = new StringBuilder();
    static int sentences;
    static int words;
    static int chars;
    static int syllables;
    static int polysyllables;

    public static void main(String[] args) {
        String path = args[0];
//        String path = "C:\\Users\\Mirek\\Documents\\hyperskill\\in.txt";
        String text = importText(path);
        System.out.printf("The text is: %n%s%n%n", text);
        analyzeText(text);

    }

    @Deprecated
    public static void estimateReadability(String text) {
        int numberOfSentences = countSentences(text);
        int numberOfWords = countWords(text);
        double wordToSentenceRatio = (double) numberOfWords / numberOfSentences;

        if (wordToSentenceRatio > 10.0) {
            System.out.println("HARD");
        } else {
            System.out.println("EASY");
        }
    }

    public static String importText(String path){
        File file = new File(path);
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            sb.append(scanner.nextLine());
        } catch (FileNotFoundException e) {
            System.out.println("File not found :(");
        }
        return sb.toString();
    }

    public static int countSentences(String text) {
        int numberOfSentences = text.split("[.\\?!]").length;
        return numberOfSentences;
    }

    public static int countWords(String text) {
        int numberOfWords = text.split(" ").length;
        return numberOfWords;
    }

    public static int countCharacters(String text) {
        int numberOfChars = text.strip().replaceAll("[\\t\\s\\n]+", "").toCharArray().length;
        return numberOfChars;
    }

    // CORRECT THIS METHOD (specifically the regex!)
    public static int countSyllables(String text) {
        int numberOfSyllables = 0;
        String[] words = text.strip().split(" ");
        for (String word : words) {
            numberOfSyllables += syllablesPerWord(word);
        }

        return numberOfSyllables;
    }

    public static int syllablesPerWord(String word){
        int numberOfVowels = countVowels(word);
        int numberOfFinalEs = countFinalEs(word);
        int numberOfConsecutiveVowels = countConsecutiveVowels(word);
        int numberOfSyllables = numberOfVowels-numberOfFinalEs-numberOfConsecutiveVowels;
//        System.out.printf("%s : %d\n", word, numberOfSyllables);
        return numberOfSyllables == 0 ? 1 : numberOfSyllables;
    }

    public static int countVowels(String text) {
        String[] words = text.strip().split(" ");
        Pattern pattern = Pattern.compile("(?i)[aeiou]");
        int numberOfVowels = 0;
        Matcher matcher;
        for (String word : words) {
            matcher = pattern.matcher(word);
            while (matcher.find()) {
                numberOfVowels++;
            }
        }

        return numberOfVowels;
    }

    public static int countFinalEs(String text) {
        String[] words = text.strip().split(" ");
        Pattern pattern = Pattern.compile("(?i)e\\b");
        int numberOfFinalEs = 0;
        Matcher matcher;
        for (String word : words) {
            matcher = pattern.matcher(word);
            while (matcher.find()) {
                numberOfFinalEs++;
            }
        }

        return numberOfFinalEs;
    }

    public static int countConsecutiveVowels(String text) {
        String[] words = text.strip().split(" ");
        Pattern pattern = Pattern.compile("(?i)[aeiou]{2,2}");
        int numberOfConsecutiveVowels = 0;
        Matcher matcher;
        for (String word : words) {
            matcher = pattern.matcher(word);
            while (matcher.find()) {
                numberOfConsecutiveVowels++;
            }
        }

        return numberOfConsecutiveVowels;
    }

    // CORRECT THIS METHOD
    public static int countPolysyllables(String text) {
        int numberOfPolysyllables = 0;
        int numberOfSyllables;
        String[] words = text.strip().split(" ");

        for (String word : words) {
            numberOfSyllables = syllablesPerWord(word);
            if (numberOfSyllables >= 3) {
                numberOfPolysyllables++;
            }
        }

        return numberOfPolysyllables;
    }


    public static void analyzeText(String text){
        sentences = countSentences(text);
        words = countWords(text);
        chars = countCharacters(text);
        syllables = countSyllables(text);
        polysyllables = countPolysyllables(text);
        System.out.printf(
                "Words: %d\n" +
                "Sentences: %d\n" +
                "Characters: %d\n" +
                "Syllables: %d\n" +
                "Polysyllables: %d\n", words, sentences, chars, syllables, polysyllables);
        calculateScore();
    }

    public static double calculateARI(int words, int characters, int sentences) {
        double score = (4.71 * ((double)characters/words)) + (0.5 * ((double)words/sentences)) - 21.43;
        System.out.printf("\nAutomated Readability Index: %s", lookUpAge(score));
        return score;
    }

    public static double calculateFK(int words, int sentences, int syllables) {
        double score = 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
        System.out.printf("Flesch" + (char)(8211) + "Kincaid" + " readability tests: %s", lookUpAge(score));
        return score;
    }

    public static double calculateSMOG(int polysyllables, int sentences) {
        double score = 1.043 * Math.sqrt(polysyllables * ((double) 30 / sentences)) + 3.1291;
        System.out.printf("Simple Measure of Gobbledygook: %s", lookUpAge(score));
        return score;
    }

    public static double calculateCL(int chars, int words, int sentences) {
        double L = (double) chars / words * 100;
        double S = (double) sentences / words * 100;
        double score = 0.0588 * L - 0.296 * S - 15.8;
        System.out.printf("Coleman" + (char)(8211) + "Liau index: %s", lookUpAge(score));
        return score;
    }


    public static void calculateScore(){
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String type = "";
        try (Scanner scanner = new Scanner(System.in)) {
             type = scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Something went wrong :(");
        }
        switch (type.toUpperCase()) {
            case "ARI" : {
                calculateARI(words, chars, sentences);
                break;
            }
            case "FK" : {
                calculateFK(words, sentences, syllables);
                break;
            }
            case "SMOG" : {
                calculateSMOG(polysyllables, sentences);
                break;
            }
            case "CL" : {
                calculateCL(chars, words, sentences);
                break;
            }
            case "ALL" : {
                calculateARI(words, chars, sentences);
                calculateFK(words, sentences, syllables);
                calculateSMOG(polysyllables, sentences);
                calculateCL(chars, words, sentences);
                break;
            }
            default: {
                System.out.println("Unknown type. Bye");
            };
        }
    }

    public static String lookUpAge(double score) {
        StringBuilder sb = new StringBuilder("(about ");
        sb.insert(0, String.format("%.2f ", score));
        switch ((int) Math.ceil(score)) {
            case 1: sb.append("6"); break;
            case 2: sb.append("7"); break;
            case 3: sb.append("9"); break;
            case 4: sb.append("10"); break;
            case 5: sb.append("11"); break;
            case 6: sb.append("12"); break;
            case 7: sb.append("13"); break;
            case 8: sb.append("14"); break;
            case 9: sb.append("15"); break;
            case 10: sb.append("16"); break;
            case 11: sb.append("17"); break;
            case 12: sb.append("18"); break;
            case 13: sb.append("24"); break;
            case 14: sb.append("24+"); break;
            default: sb.append("???");
        }
        sb.append(" year olds).\n");
        return sb.toString();
    }
}
