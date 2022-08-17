import java.util.Scanner;

public class Duke {
    private static String[] items = new String[100];
    private static int id;

    public static void main(String[] args) {
        String logo = " ____        _        \n"
                + "\t|  _ \\ _   _| | _____ \n"
                + "\t| | | | | | | |/ / _ \\\n"
                + "\t| |_| | |_| |   <  __/\n"
                + "\t|____/ \\__,_|_|\\_\\___|\n";
        String introduction = "\tHello! I'm Duke\n" + "\tWhat can I do for you?";
        Duke.echo(logo + introduction);

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s = sc.nextLine();
            if (s.equals("bye")) {
                Duke.echo("Bye. Hope to see you again soon!");
                break;
            } else if (s.equals("list")) {
                Duke.list();
            } else {
                Duke.add(s);
            }
        }
    }

    private static void echo(String s) {
        System.out.println("\t_________________________________________________");
        System.out.println("\t" + s);
        System.out.println("\t_________________________________________________");
    }

    private static void add(String item) {
        Duke.items[id] = item;
        Duke.id++;
        Duke.echo("added: " + item);
    }

    private static void list() {
        if (id == 0) {
            Duke.echo("no items stored");
        } else {
            String s = "";
            for (int i = 0; i < id; i++) {
                s = s + "\t" + (i + 1) + ". " + items[i] + "\n";
            }
            Duke.echo(s.trim());
        }
    }
}
