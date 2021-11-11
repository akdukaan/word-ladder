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


    public static void main(String[] args) throws IOException {
        initializeDictionary();
        printWordLadder("which", "there");
        printWordLadder("their", "about");
        printWordLadder("would", "other");
        printWordLadder("three", "place");
        printWordLadder("again", "still");
        printWordLadder("hound", "sound");
    }

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

    public static void printWordLadder(String start, String end) {
        reachedWords1 = new HashMap<>();
        reachedWords2 = new HashMap<>();
        reachedWords1.put(start, null);
        reachedWords2.put(end, null);

        Queue<String> queue1 = new PriorityQueue<>();
        Queue<String> queue2 = new PriorityQueue<>();
        queue1.add(start);
        queue2.add(end);

        // While both queues have something in them
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            // Check if it has been reached on the other end
            String current1 = queue1.remove();
            if (reachedWords2.containsKey(current1)) {
                printPath(current1);
                return;
            }
            String current2 = queue2.remove();
            if (reachedWords1.containsKey(current2)) {
                printPath(current2);
                return;
            }
            for (int i = 0; i < start.length(); i++) {
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c != current1.charAt(i)) {
                        String s = current1.substring(0, i) + c + current1.substring(i + 1);
                        if (dictionary.contains(s) && !reachedWords1.containsKey(s)) {
                            queue1.add(s);
                            reachedWords1.put(s, current1);
                            if (reachedWords2.containsKey(s)) {
                                printPath(current1);
                                return;
                            }
                        }
                    }
                    if (c != current2.charAt(i)) {
                        String s = current2.substring(0, i) + c + current2.substring(i + 1);
                        if (dictionary.contains(s) && !reachedWords2.containsKey(s)) {
                            queue2.add(s);
                            reachedWords2.put(s, current2);
                            if (reachedWords1.containsKey(current2)) {
                                printPath(s);
                                return;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("No path between " + start + " and " + end + " exists.\n");
    }

    public static void printPath(String centerWord) {
        printLeftPath(centerWord);
        printRightPath(centerWord);
        System.out.println("\n");
    }

    public static void printLeftPath(String word) {
        Stack<String> stack = new Stack<>();
        while (word != null) {
            stack.add(word);
            word = reachedWords1.get(word);
        }
        while (!stack.isEmpty()) {
            String pop = stack.pop();
            if (!stack.isEmpty()) {
                System.out.print(pop + " -> ");
            }
        }
    }

    public static void printRightPath(String word) {
        while (word != null) {
            System.out.print(word);
            word = reachedWords2.get(word);
            if (word != null) {
                System.out.print(" -> ");
            }
        }
    }
}
