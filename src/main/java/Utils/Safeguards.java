package Utils;

import java.util.*;

/**
 * Special utils class only used for some redundant work
 */
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
                String input = scanner.nextLine();
                choice = Integer.parseInt(input);
                if (choice <= b && choice >= a) {
                    valid = true;
                }
                else {
                    System.out.println("Invalid choice");
                }
            }
            catch (NumberFormatException | NoSuchElementException e) {
                System.out.println("Invalid choice");
            }
        }
        return choice;
    }

    /**
     * Returns a valid integer option
     * @return option
     */
    public static int getInputInteger(){
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        boolean valid = false;
        while (!valid) {
            try{
                String input = scanner.nextLine();
                choice = Integer.parseInt(input);
                valid = true;
            }
            catch (NumberFormatException | NoSuchElementException e) {
                System.out.println("Invalid choice");
            }
        }
        return choice;
    }

    /**
     * Returns a slice of a given size for a given list. If there's no more element, then the size will be lesser than the input list
     * @param list list to slice
     * @param page which page to select
     * @param maxPerPage how many items per page
     * @return sliced list
     * @param <E> type of the items
     */
    public static <E> List<E> cutInto(List<E> list, int page, int maxPerPage){
        int i = (page-1)*maxPerPage; // Offset by 1 to be coherent with the database methods
        List<E> res = new ArrayList<>();
        while (i < list.size() && res.size() < maxPerPage && list.get(i) != null) {
            res.add(list.get(i));
            i++;
        }
        return res;
    }
}
