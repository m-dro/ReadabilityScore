package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Course:  JetBrains Academy, Java Developer Track
 * Project: Readability Score
 * Purpose: A console-based program to calculate the number of
 *          characters, syllables, polysyllables, words, and sentences in a text,
 *          and how easy it is  to read (which age group could handle it).
 * Rules:
 *          1. To count the number of syllables, the program uses
 *             letters a, e, i, o, u, y as vowels.
 *          2. Double-vowels do not count (for example, "rain" has 2 vowels
 *             but only 1 syllable).
 *          3. If the last letter in the word is 'e'
 *             it does not count as a vowel (for example, "side" has 1 syllable).
 *          4. If at the end it turns out that the word contains 0 vowels,
 *             then this word is considered as a 1-syllable one.
 *
 * @author Mirek Drozd
 * @version 1.1
 */
public class Main {
    static StringBuilder sb = new StringBuilder();
    static int sentences;
    static int words;
    static int chars;
    static int syllables;
    static int polysyllables;

    /**
     * The main method begins execution of the program.
     *
     * @param args First argument is the name of the file
     *             with the text to be analysed.
     */
    public static void main(String[] args) {
        String path = args[0];
        String text = importText(path);
        System.out.printf("The text is: %n%s%n%n", text);
        analyzeText(text);
    }

    /**
     * Method used early on in program development to determine
     * whether the text is EASY or HARD, based on word-to-sentence ratio.
     *
     * @param text The text to be analysed.
     */
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

    /**
     * Utility method to import text from file.
     *
     * @param path The path to the file.
     * @return  The text from the file.
     */
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

    /**
     * Counts sentences in a text.
     *
     * @param text The text to be analysed.
     * @return The number of sentences.
     */
    public static int countSentences(String text) {
        int numberOfSentences = text.split("[.\\?!]").length;
        return numberOfSentences;
    }

    /**
     * Counts words in a text.
     *
     * @param text The text to be analysed.
     * @return The number of sentences.
     */
    public static int countWords(String text) {
        int numberOfWords = text.split(" ").length;
        return numberOfWords;
    }

    /**
     * Counts characters in a text.
     *
     * @param text The text to be analysed.
     * @return The number of characters.
     */
    public static int countCharacters(String text) {
        int numberOfChars = text.strip().replaceAll("[\\t\\s\\n]+", "").toCharArray().length;
        return numberOfChars;
    }

    /**
     * Counts syllables in a text.
     *
     * @param text The text to be analysed.
     * @return The number of syllables.
     */
    public static int countSyllables(String text) {
        int numberOfSyllables = 0;
        String[] words = text.strip().split(" ");
        for (String word : words) {
            numberOfSyllables += syllablesPerWord(word);
        }

        return numberOfSyllables;
    }

    /**
     * Counts syllables in a word.
     *
     * @param word The word to be analysed.
     * @return The number of syllables.
     */
    public static int syllablesPerWord(String word){
        int numberOfVowels = countVowels(word);
        int numberOfFinalEs = countFinalEs(word);
        int numberOfConsecutiveVowels = countConsecutiveVowels(word);
        int numberOfSyllables = numberOfVowels-numberOfFinalEs-numberOfConsecutiveVowels;

        return numberOfSyllables == 0 ? 1 : numberOfSyllables;
    }

    /**
     * Counts vowels in a text.
     *
     * @param text The text to be analysed.
     * @return The number of vowels.
     */
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

    /**
     * Counts word-final 's'.
     *
     * @param text The text to be analysed.
     * @return The number of word-final 's'.
     */
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

    /**
     * Counts consecutive vowels.
     *
     * @param text The text to be analysed.
     * @return The number of consecutive vowels.
     */
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

    /**
     * Counts polysyllables (number of words with more than 2 syllables).
     * 
     * @param text The text to be analysed.
     * @return The number of polysyllables.
     */
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

    /**
     * Calls specific methods to count:
     *  - sentences
     *  - words
     *  - characters
     *  - syllables, and
     *  - polysyllables
     * in a text. And prints these numbers to standard output.
     *
     * @param text The text to be analysed.
     */
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

    /**
     * Calculates Automated Readability Index.
     *
     * @param words The number of words in a text.
     * @param characters The number of characters in a text.
     * @param sentences The number of sentences in a text.
     * @return The ARI for that text.
     */
    public static double calculateARI(int words, int characters, int sentences) {
        double score = (4.71 * ((double)characters/words)) + (0.5 * ((double)words/sentences)) - 21.43;
        System.out.printf("\nAutomated Readability Index: %s", lookUpAge(score));
        return score;
    }

    /**
     * Runs the Flesch-Kincaid (FK) readability tests
     * (Flesch Reading Ease & Flesch–Kincaid Grade Level)
     *
     * @param words The number of words in a text.
     * @param sentences The number of sentences in a text.
     * @param syllables The number of syllables in a text.
     * @return The FK index.
     */
    public static double calculateFK(int words, int sentences, int syllables) {
        double score = 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
        System.out.printf("Flesch" + (char)(8211) + "Kincaid" + " readability tests: %s", lookUpAge(score));
        return score;
    }

    /**
     * Calculates Simple Measure of Gobbledygook (SMOG).
     *
     * @param polysyllables The number of polysyllables in a text.
     * @param sentences The number of sentences in a text.
     * @return The SMOG index.
     */
    public static double calculateSMOG(int polysyllables, int sentences) {
        double score = 1.043 * Math.sqrt(polysyllables * ((double) 30 / sentences)) + 3.1291;
        System.out.printf("Simple Measure of Gobbledygook: %s", lookUpAge(score));
        return score;
    }

    /**
     * Calculates the Coleman–Liau (CL) index.
     *
     * @param chars The number of characters in a text.
     * @param words The number of words in a text.
     * @param sentences The number of sentences in a text.
     * @return The CL index.
     */
    public static double calculateCL(int chars, int words, int sentences) {
        double L = (double) chars / words * 100;
        double S = (double) sentences / words * 100;
        double score = 0.0588 * L - 0.296 * S - 15.8;
        System.out.printf("Coleman" + (char)(8211) + "Liau index: %s", lookUpAge(score));
        return score;
    }

    /**
     * The entry point to the program.
     * Allows the user to choose which index to calculate.
     */
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

    /**
     * Based on the calculated score, determines the age group
     * for which the text is appropriate.
     *
     * @param score The score calculated for the text.
     * @return Information about the age group.
     */
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
