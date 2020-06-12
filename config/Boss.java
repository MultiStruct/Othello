import java.util.*;
import java.io.*;
import java.math.*;

class Player {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();
        int height = in.nextInt();

        Random rand = new Random(13);
        // game loop
        while (true) {
            for (int i = 0; i < height; i++) {
                String line = in.next();
            }
            int actionCount = in.nextInt();
            int index = rand.nextInt(actionCount);
            String s = "";
            for (int i = 0; i < actionCount; i++) {
                String action = in.next();
                if(index == i) {
                    s = action;
                }
            }

            // Write an answer using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(s);
        }
    }
}