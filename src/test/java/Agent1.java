import java.util.Random;
import java.util.Scanner;

public class Agent1 {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int id = in.nextInt();
        int height = in.nextInt();
        int turn = 0;
        Random rand = new Random(14);
        // game loop
        while (true) {
            ++turn;
            for (int i = 0; i < height; i++) {
                String line = in.next();
                //System.err.println(line);
            }


            if (turn > 1) {
                String str = in.next();
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

            System.out.println("EXPERT " + s + " MSG message");
        }
    }
}
