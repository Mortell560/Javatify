package Utils;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Safeguards {
    /**
     * Returns an option between a and b inclusive
     * @param a lower bound
     * @param b higher bound
     * @return option
     */
    public static int getInputInterval(int a, int b){
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        boolean valid = false;
        while (!valid) {
            try{
                choice = scanner.nextInt();
                if (choice <= b && choice >= a) {
                    valid = true;
                }
                else {
                    System.out.println("Invalid choice");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid choice");
            }
        }
        return choice;
    }
}
