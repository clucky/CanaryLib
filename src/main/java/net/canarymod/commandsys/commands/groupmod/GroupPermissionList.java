package net.canarymod.commandsys.commands.groupmod;

import net.canarymod.Canary;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.NativeCommand;
import net.canarymod.user.Group;

import static net.canarymod.Translator.sendTranslatedNotice;

/**
 * Command to list all permissions of a group
 *
 * @author Chris (damagefilter)
 */
public class GroupPermissionList implements NativeCommand {
    // groupmod permission add group value
    public void execute(MessageReceiver caller, String[] args) {
        Group group = Canary.usersAndGroups().getGroup(args[0]);
        if (group == null || !group.getName().equals(args[0])) {
            sendTranslatedNotice(caller, "unknown group", args[0]);
            return;
        }
        group.getPermissionProvider().printPermissionsToCaller(caller);
    }
}
