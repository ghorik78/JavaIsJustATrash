package Classes;

import Commands.*;
import Interfaces.CommandData;
import ServerPackages.Commander;

import java.io.IOException;

public class Invoker {
    private final CommandManager manager = new CommandManager();
    private final Commander commander = new Commander();

    @CommandData(name="help",
    description="вывести справку по доступным командам")
    public void help(String[] args) {
        HelpCommand helpCommand = new HelpCommand("help", args, commander);
        manager.invoke(helpCommand, args);
    }

    @CommandData(name="info",
    description="вывести информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)")
    public void info(String[] args) {
        InfoCommand infoCommand = new InfoCommand("info", args, commander);
        manager.invoke(infoCommand, args);
    }

    @CommandData(name="show",
    description="вывести все элементы коллекции в строковом представлении")
    public void show(String[] args) {
        ShowCommand showCommand = new ShowCommand("show", args, commander);
        manager.invoke(showCommand, args);
    }

    @CommandData(name="add",
    description="добавить новый элемент в коллекцию")
    public void add(Route route) throws NoSuchFieldException, IllegalAccessException, IOException {
        AddCommand addCommand = new AddCommand("add", null, commander);
        manager.invoke(addCommand, route);
    }

    @CommandData(name="update",
    description="обновить значение элемента коллекции, id которого равен заданному")
    public void update(String[] args) throws IOException {
        UpdateCommand updateCommand = new UpdateCommand("update", args, commander);
        manager.invoke(updateCommand, args);
    }

    @CommandData(name="remove_by_id",
    description="удалить элемент из коллекции по его id")
    public void remove_by_id(String[] args) {
        RemoveByIdCommand removeByIdCommand = new RemoveByIdCommand("remove_by_id", args, commander);
        manager.invoke(removeByIdCommand, args);
    }

    @CommandData(name="clear",
    description="очистить коллекцию")
    public void clear(String[] args) {
        ClearCommand clearCommand = new ClearCommand("clear", args, commander);
        manager.invoke(clearCommand, args);
    }

    @CommandData(name="execute_script",
    description="считать и исполнить скрипт из указанного файла")
    public void execute_script(String[] args) {

    }

    @CommandData(name="remove_first",
    description="удалить первый элемент из коллекции")
    public void remove_first(String[] args) throws IOException {
        RemoveFirstCommand removeFirstCommand = new RemoveFirstCommand("remove_first", args, commander);
        manager.invoke(removeFirstCommand, args);
    }

    @CommandData(name="head",
    description="вывести первый элемент коллекции")
    public void head(String[] args) throws IOException {
        HeadCommand headCommand = new HeadCommand("head", args, commander);
        manager.invoke(headCommand, args);
    }

    @CommandData(name="add_if_max",
    description="добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции")
    public void add_if_max(String[] args) {
        AddIfMaxCommand addIfMaxCommand = new AddIfMaxCommand("add_if_max", args, commander);
        manager.invoke(addIfMaxCommand, args);
    }

    @CommandData(name="remove_all_by_distance",
    description="удалить из коллекции все элементы, значение поля distance которого эквивалентно заданному")
    public void remove_all_by_distance(String[] args) throws IOException {
        RemoveAllByDistanceCommand removeAllByDistanceCommand = new RemoveAllByDistanceCommand("remove_all_by_distance", args, commander);
        manager.invoke(removeAllByDistanceCommand, args);
    }

    @CommandData(name="print_unique_distance",
    description="вывести уникальные значения поля distance всех элементов в коллекции")
    public void print_unique_distance(String[] args) {
        PrintUniqueDistanceCommand printUniqueDistanceCommand = new PrintUniqueDistanceCommand("print_unique_distance", args, commander);
        manager.invoke(printUniqueDistanceCommand, args);
    }

    @CommandData(name="print_field_ascending_distance",
    description="вывести значения поля distance всех элементов в порядке возрастания")
    public void print_field_ascending_distance(String[] args) throws IOException {
        PrintFieldAscendingDistanceCommand printFieldAscendingDistanceCommand = new PrintFieldAscendingDistanceCommand("print_field_ascending_distance", args, commander);
        manager.invoke(printFieldAscendingDistanceCommand, args);
    }
}
