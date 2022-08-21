import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A basic CLI application for managing tasks.
 */
public class Duke {
    /**
     * Enum for task type.
     */
    public enum TaskType {
        EVENT, DEADLINE, TODO
    }

    /**
     * ArrayList for storing the different tasks entered
     * by the user.
     */
    private static List<Task> items = new ArrayList<>();

    /**
     * Main method for starting the program.
     * @param args Optional command line arguments
     */
    public static void main(String[] args) {
        String introduction = "Hello! I'm Duke\n" + "\tWhat can I do for you?";
        Duke.echo(introduction);
        Duke.load();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s = sc.nextLine();
            String[] arr = s.split(" ");
            if (s.equals("bye")) {
                Duke.echo("Bye. Hope to see you again soon!");
                break;
            }
            try {
                Duke.decide(s, arr);
            } catch (DukeException e) {
                Duke.echo(e.toString());
            }
        }
    }

    /**
     * Decision tree for Duke to process, store and echo text input from the user.
     * @param s The entire string entered by the user, line-by-line.
     * @param arr An array of strings (words) obtained from splitting the above string.
     * @throws DukeException A custom exception for handling errors unique to Duke.
     */
    private static void decide(String s, String[] arr) throws DukeException {
        if (arr.length == 0) {
            return;
        } else if (s.equals("list")) {
            Duke.list();
        } else {
            int i;
            switch (arr[0]) {
            case "mark":
                if (arr.length <= 1) {
                    throw new DukeException("Error. Please enter an argument after \"mark\".");
                }
                try {
                    i = Integer.parseInt(arr[1]) - 1;
                    if (i >= 0 && i < items.size()) {
                        Task t = items.get(i);
                        t.markAsDone();
                        Duke.save();
                        Duke.echo("Nice! I've marked this task as done:\n" +
                                "\t  " + t);
                    } else {
                        Duke.echo("Please enter an integer within range.");
                    }
                } catch (NumberFormatException e) {
                    Duke.echo("Please enter an integer id after \"mark\"");
                }
                break;
            case "unmark":
                if (arr.length <= 1) {
                    throw new DukeException("Error. Please enter an argument after \"unmark\".");
                }
                try {
                    i = Integer.parseInt(arr[1]) - 1;
                    if (i >= 0 && i < items.size()) {
                        Task t = items.get(i);
                        t.markAsUndone();
                        Duke.save();
                        Duke.echo("OK! I've marked this task as not done yet:\n" +
                                "\t  " + t);
                    } else {
                        Duke.echo("Please enter an integer within range.");
                    }
                } catch (NumberFormatException e) {
                    Duke.echo("Please enter an integer id after \"ummark\"");
                }
                break;
            case "delete":
                try {
                    if (arr.length <= 1) {
                        throw new DukeException("Error. Please enter an argument after \"delete\".");
                    }
                    i = Integer.parseInt(arr[1]) - 1;
                    if (i >= 0 && i < items.size()) {
                        Task t = items.get(i);
                        Duke.delete(i);
                        Duke.echo("Noted. I've removed this task:\n" +
                                "\t  " + t + "\n\tNow you have " + (items.size())
                                + (items.size() == 1 ? " task" : " tasks") + " in the list.");
                    } else {
                        Duke.echo("Please enter an integer within range.");
                    }
                } catch (NumberFormatException e) {
                    Duke.echo("Please enter an integer id after \"delete\"");
                }
                break;
            case "todo":
                if (arr.length == 1) {
                    throw new DukeException("Error. The description of a todo cannot be empty.");
                }
                String todo = s.substring(4).trim();
                Duke.add(todo, TaskType.TODO, "");
                break;
            case "deadline":
                String[] deadlineBy = s.substring(8).trim().split("/by");
                if (deadlineBy.length <= 1) {
                    throw new DukeException("Error. The description and due date of a deadline\n\tshould be separated" +
                            " by a \"/by\".");
                }
                String deadline = deadlineBy[0].trim();
                String by = deadlineBy[1].trim();
                Duke.add(deadline, TaskType.DEADLINE, by);
                break;
            case "event":
                String[] eventAt = s.substring(5).trim().split("/at");
                if (eventAt.length <= 1) {
                    throw new DukeException("Error. The description and time of an event\n\tshould be separated" +
                            " by a \"/at\".");
                }
                String event = eventAt[0].trim();
                String at = eventAt[1].trim();
                Duke.add(event, TaskType.EVENT, at);
                break;
            default:
                throw new DukeException("Error. Sorry, but I don't know what that means.");
            }
        }
    }

    /**
     * Utility function for Duke to print responses to the user.
     * @param s The string to be formatted and indented within the enclosing border.
     */
    private static void echo(String s) {
        System.out.println("\t_________________________________________________");
        System.out.println("\t" + s);
        System.out.println("\t_________________________________________________");
        System.out.println();
    }

    /**
     * Utility function with logic for adding tasks to the user's task list.
     * @param description The description of the task to be added.
     * @param type The type of task to be added.
     * @param remarks The remarks to be added for events or deadlines.
     */
    private static void add(String description, TaskType type, String remarks) {
        String s = "Got it. I've added this task:\n\t";
        switch (type) {
            case TODO:
                Todo t = new Todo(description);
                Duke.items.add(t);
                s = s + "  " + t;
                break;
            case DEADLINE:
                Deadline d = new Deadline(description, remarks);
                Duke.items.add(d);
                s = s + "  " + d;
                break;
            case EVENT:
                Event e = new Event(description, remarks);
                Duke.items.add(e);
                s = s + "  " + e;
                break;
            default:
                break;
        }
        int size = items.size();
        s = s + "\n\tNow you have " + (size) + (size == 1 ? " task" : " tasks") + " in the list.";
        Duke.echo(s);
        Duke.save();
    }

    /**
     * Utility function for retrieving and printing the tasks in the user's task list.
     */
    private static void list() {
        if (items.size() == 0) {
            Duke.echo("No items stored");
        } else {
            String s = "Here are the tasks in your list:\n";
            for (int i = 0; i < items.size(); i++) {
                Task t = items.get(i);
                s = s + "\t" + (i + 1) + "." + t + "\n";
            }
            Duke.echo(s.trim());
        }
    }

    /**
     * Utility function for deleting an item in the user's task list.
     * @param index The position of the task to be deleted in the ArrayList.
     */
    private static void delete(int index) {
        items.remove(index);
        Duke.save();
    }

    private static void save() {
        // Create directory if it doesn't exist
        File dir = new File("./../../../data");
        if (!dir.exists()) {
            dir.mkdir();
        }
        // Create file if it doesn't exist
        File file = new File("./../../../data/duke.txt");
        if (dir.exists() && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        // Overwrite file contents with latest task data
        try {
            FileWriter fw = new FileWriter(file.getPath());
            String data = "";
            for (Task t : items) {
                String s = "";
                String taskType = t.getClass().toString().split(" ")[1];
                String status = t.isDone ? "d" : "nd";
                String desc = t.description;
                String remarks = "";
                int index;
                if (taskType.equals("Event")) {
                    index = t.toString().indexOf("at: ") + 4;
                    remarks = t.toString().substring(index, t.toString().length() - 1);
                } else if (taskType.equals("Deadline")) {
                    index = t.toString().indexOf("by: ") + 4;
                    remarks = t.toString().substring(index, t.toString().length() - 1);
                }
                s = s + taskType.toLowerCase() + " | " + status + " | " + desc;
                if (!remarks.equals("")) {
                    s = s + " | " + remarks;
                }
                data = data + s + "\n";
            }
            fw.write(data);
            fw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void load() {
        // Check if directory and file exists
        File file = new File("./../../../data/duke.txt");
        try {
            // Load the data into the task array
            Scanner sc = new Scanner(file);
            while (sc.hasNext()) {
                String[] taskData = sc.nextLine().split(" \\| ");
                String taskType = taskData[0];
                boolean isDone = taskData[1].equals("d");
                String desc = taskData[2];
                String remarks = "";
                if (taskData.length == 4) {
                    remarks = taskData[3];
                }
                switch (taskType) {
                case "todo":
                    Todo t = new Todo(desc);
                    if (isDone) {
                        t.markAsDone();
                    }
                    items.add(t);
                    break;
                case "event":
                    Event e = new Event(desc, remarks);
                    if (isDone) {
                        e.markAsDone();
                    }
                    items.add(e);
                    break;
                case "deadline":
                    Deadline d = new Deadline(desc, remarks);
                    if (isDone) {
                        d.markAsDone();
                    }
                    items.add(d);
                    break;
                default:
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
