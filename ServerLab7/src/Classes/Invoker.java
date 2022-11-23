package Classes;

import Commands.*;
import Interfaces.CommandData;
import ServerPackages.Commander;

import java.io.IOException;
import java.sql.SQLException;

public class Invoker {
    private final CommandManager manager = new CommandManager();
    private String usr;
    private Commander commander;

    public Invoker(Commander commander) {
        this.commander = commander;
        this.usr = commander.getCurrentUsr();
    }

    public Invoker() {}

    @CommandData(name="help",
    description="вывести справку по доступным командам")
    public void help(String[] args) throws SQLException, IOException {
        HelpCommand helpCommand = new HelpCommand("help", args, commander);
        manager.invoke(helpCommand, args, usr);
    }

    @CommandData(name="info",
    description="вывести информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)")
    public void info(String[] args) throws SQLException, IOException {
        InfoCommand infoCommand = new InfoCommand("info", args, commander);
        manager.invoke(infoCommand, args, usr);
    }

    @CommandData(name="show",
    description="вывести все элементы коллекции в строковом представлении")
    public void show(String[] args) throws IOException, SQLException {
        ShowCommand showCommand = new ShowCommand("show", args, commander);
        manager.invoke(showCommand, args, usr);
    }

    @CommandData(name="add",
    description="добавить новый элемент в коллекцию")
    public void add(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {
        AddCommand addCommand = new AddCommand("add", usr, commander);
        manager.invoke(addCommand, route, args, usr);
    }

    @CommandData(name="update",
    description="обновить значение элемента коллекции, id которого равен заданному")
    public void update(Route route, String[] args, String usr) throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
        UpdateCommand updateCommand = new UpdateCommand("update", args, commander);
        manager.invoke(updateCommand, route, args, usr);
    }

    @CommandData(name="remove_by_id",
    description="удалить элемент из коллекции по его id")
    public void remove_by_id(String[] args) throws SQLException, IOException {
        RemoveByIdCommand removeByIdCommand = new RemoveByIdCommand("remove_by_id", args, commander);
        manager.invoke(removeByIdCommand, args, usr);
    }

    @CommandData(name="clear",
    description="очистить коллекцию")
    public void clear(String[] args) throws SQLException, IOException {
        ClearCommand clearCommand = new ClearCommand("clear", args, commander);
        manager.invoke(clearCommand, args, usr);
    }

    @CommandData(name="execute_script",
    description="считать и исполнить скрипт из указанного файла")
    public void execute_script(String[] args) {

    }

    @CommandData(name="remove_first",
    description="удалить первый элемент из коллекции")
    public void remove_first(String[] args) throws IOException, SQLException {
        RemoveFirstCommand removeFirstCommand = new RemoveFirstCommand("remove_first", args, commander);
        manager.invoke(removeFirstCommand, args, usr);
    }

    @CommandData(name="head",
    description="вывести первый элемент коллекции")
    public void head(String[] args) throws IOException, SQLException {
        HeadCommand headCommand = new HeadCommand("head", args, commander);
        manager.invoke(headCommand, args, usr);
    }

    @CommandData(name="add_if_max",
    description="добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции")
    public void add_if_max(Route route, String[] args, String usr) throws IOException, NoSuchFieldException, IllegalAccessException {
        AddIfMaxCommand addIfMaxCommand = new AddIfMaxCommand("add_if_max", args, commander);
        manager.invoke(addIfMaxCommand, route, args, usr);
    }

    @CommandData(name="remove_all_by_distance",
    description="удалить из коллекции все элементы, значение поля distance которого эквивалентно заданному")
    public void remove_all_by_distance(String[] args) throws IOException, SQLException {
        RemoveAllByDistanceCommand removeAllByDistanceCommand = new RemoveAllByDistanceCommand("remove_all_by_distance", args, commander);
        manager.invoke(removeAllByDistanceCommand, args, usr);
    }

    @CommandData(name="print_unique_distance",
    description="вывести уникальные значения поля distance всех элементов в коллекции")
    public void print_unique_distance(String[] args) throws SQLException, IOException {
        PrintUniqueDistanceCommand printUniqueDistanceCommand = new PrintUniqueDistanceCommand("print_unique_distance", args, commander);
        manager.invoke(printUniqueDistanceCommand, args, usr);
    }

    @CommandData(name="print_field_ascending_distance",
    description="вывести значения поля distance всех элементов в порядке возрастания")
    public void print_field_ascending_distance(String[] args) throws IOException, SQLException {
        PrintFieldAscendingDistanceCommand printFieldAscendingDistanceCommand = new PrintFieldAscendingDistanceCommand("print_field_ascending_distance", args, commander);
        manager.invoke(printFieldAscendingDistanceCommand, args, usr);
    }
}
