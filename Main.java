package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {
    private static final HashSet<String> dictionary = new HashSet<>();
    private static HashMap<String, String> reachedWords1 = new HashMap<>();
    private static HashMap<String, String> reachedWords2 = new HashMap<>();

    // Create queues to keep track of the words we need to expand
    private static Queue<String> queue1 = new PriorityQueue<>();
    private static Queue<String> queue2 = new PriorityQueue<>();

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        initializeDictionary();
        findPath("which", "there");
        findPath("their", "their");
        findPath("would", "other");
        findPath("three", "place");
        findPath("again", "still");
        findPath("hound", "sound");
    }

    /**
     * Takes all 5 letter words from a URL and adds them to the dictionary
     * @throws IOException
     */
    public static void initializeDictionary() throws IOException {
        URL url = new URL("https://www-cs-faculty.stanford.edu/~knuth/sgb-words.txt");
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {
            dictionary.add(line);
        }
    }

    /**
     * Finds a path between two words
     * @param start the first word in the word ladder
     * @param end the last word in the word ladder
     */
    public static void findPath(String start, String end) {
        // Clear the reachedWords hashmap so we can call findPath multiple times from main
        reachedWords1 = new HashMap<>();
        reachedWords2 = new HashMap<>();
        queue1 = new PriorityQueue<>();
        queue2 = new PriorityQueue<>();

        // Add start and end to the reachedWords
        reachedWords1.put(start, null);
        reachedWords2.put(end, null);

        // Add start and end to the queues
        queue1.add(start);
        queue2.add(end);

        // Check if start and end are the same word
        if (start.equals(end)) {
            printPath(start);
            return;
        }

        // While both queues have something in them
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            // Get the first word in the queues
            String current1 = queue1.remove();
            String current2 = queue2.remove();

            // Expand words in the queue
            if (isConnectedAfterExpanding(current1, reachedWords1, reachedWords2, queue1)) {
                return;
            }
            if (isConnectedAfterExpanding(current2, reachedWords2, reachedWords1, queue2)) {
                return;
            }
        }
        System.out.println("No path between " + start + " and " + end + " exists.\n");
    }

    /**
     * Expands the word into the queue. If we complete the path, it prints the path.
     * @param word the word that will be epanded
     * @param thisMap the hashmap representing the reached words on this side
     * @param otherMap the hashmap representing the reached words on the other side
     * @param queue the queue for this side
     * @return if we have found a path
     */
    public static boolean isConnectedAfterExpanding(String word, HashMap<String, String> thisMap, HashMap<String, String> otherMap, Queue<String> queue) {
        // Go through each letter in the word
        for (int i = 0; i < word.length(); i++) {
            // Go through each letter in the alphabet
            for (char c = 'a'; c <= 'z'; c++) {
                // If no swap will happen, ignore this word
                if (c != word.charAt(i)) {
                    // Build one of the neighboring words
                    String s = word.substring(0, i) + c + word.substring(i + 1);
                    // If it's a real word and we haven't already visited it from this end, consider it
                    if (dictionary.contains(s) && !thisMap.containsKey(s)) {
                        queue.add(s);
                        thisMap.put(s, word);
                        // If we've visited this word when building from the other end, we know the path
                        if (otherMap.containsKey(s)) {
                            otherMap.put(word, s);
                            printPath(word);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Prints the entire path
     * @param centerWord word to build from
     */
    public static void printPath(String centerWord) {
        printLeftPath(centerWord);
        printRightPath(centerWord);
        System.out.println("\n");
    }

    /**
     * Prints the left side of the path
     * @param word the word to start from
     */
    public static void printLeftPath(String word) {
        Stack<String> stack = new Stack<>();

        // Add each word to a stack to reverse the order
        while (word != null) {
            stack.add(word);
            word = reachedWords1.get(word);
        }
        // Print all words in the stack
        while (!stack.isEmpty()) {
            String pop = stack.pop();
            // Print every word except the last
            if (!stack.isEmpty()) {
                System.out.print(pop + " -> ");
            }
        }
    }

    /**
     * Prints the right side of the path
     * @param word the word to start from
     */
    public static void printRightPath(String word) {
        while (word != null) {
            // Print the word and set word to the next word
            System.out.print(word);
            word = reachedWords2.get(word);
            // Print an arrow on all words except the last
            if (word != null) {
                System.out.print(" -> ");
            }
        }
    }
}
