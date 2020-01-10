package main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/*
 * This command manages the value of the boolean "fill",
 * which determines if the triangle should get filled or
 * not.
 */
public class ToggleFillCommand implements CommandExecutor {
	
	public static boolean fill = false;

	public ToggleFillCommand() {
		fill = false;
	}

	// Sets the boolean "fill" to the argument that the player passes
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(arg3[0].equals("false") || arg3[0].equals("true")) {
			fill = Boolean.parseBoolean(arg3[0]);
			System.out.println("fill changed to " + fill);
			return true;
		}
		return false;
	}

	public static boolean isFill() {
		return fill;
	}
}
