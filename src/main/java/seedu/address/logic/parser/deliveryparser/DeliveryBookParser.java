package seedu.address.logic.parser.deliveryparser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.SetCommand;
import seedu.address.logic.commands.deliverycommands.AddCommand;
import seedu.address.logic.commands.deliverycommands.ClearCommand;
import seedu.address.logic.commands.deliverycommands.DeleteCommand;
import seedu.address.logic.commands.deliverycommands.EditCommand;
import seedu.address.logic.commands.deliverycommands.FilterCommand;
import seedu.address.logic.commands.deliverycommands.ListCommand;
import seedu.address.logic.commands.deliverycommands.MarkCommand;
import seedu.address.logic.commands.deliverycommands.RouteCommand;
import seedu.address.logic.commands.deliverycommands.SelectCommand;
import seedu.address.logic.commands.deliverycommands.SortCommand;
import seedu.address.logic.commands.deliverycommands.SwitchCommand;
import seedu.address.logic.commands.deliverycommands.UnfilterCommand;
import seedu.address.logic.commands.deliverycommands.UnmarkCommand;
import seedu.address.logic.commands.uicommand.ExitCommand;
import seedu.address.logic.commands.uicommand.HelpCommand;
import seedu.address.logic.parser.SetCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class DeliveryBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    public static final String PACKAGE = "deliverybookparser";
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(DeliveryBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change the log level in config.json to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case MarkCommand.COMMAND_WORD:
            return new MarkCommandParser().parse(arguments);

        case UnmarkCommand.COMMAND_WORD:
            return new UnmarkCommandParser().parse(arguments);

        case RouteCommand.COMMAND_WORD:
            return new RouteCommandParser().parse(arguments);

        case SelectCommand.COMMAND_WORD:
            return new SelectCommandParser().parse(arguments);

        case FilterCommand.COMMAND_WORD:
            return new FilterCommandParser().parse(arguments);

        case UnfilterCommand.COMMAND_WORD:
            return new UnfilterCommand();

        case SortCommand.COMMAND_WORD:
            return new SortCommandParser().parse(arguments);

        case SwitchCommand.COMMAND_WORD:
            return new SwitchCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case SetCommand.COMMAND_WORD:
            return new SetCommandParser().parse(arguments);

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
