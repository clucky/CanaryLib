package net.canarymod.commandsys.commands.system.whitelist;

import net.canarymod.chat.ChatFormat;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.NativeCommand;

import static net.canarymod.Canary.whitelist;
import static net.canarymod.Translator.sendTranslatedMessage;

/**
 * Whitelist Remove Command
 *
 * @author Jason Jones (darkdiplomat)
 */
public class WhitelistRemove implements NativeCommand {

    @Override
    public void execute(MessageReceiver caller, String[] args) {
        whitelist().removePlayer(args[0]);
        sendTranslatedMessage(caller, ChatFormat.YELLOW, "whitelist player removed", args[0]);
    }
}
