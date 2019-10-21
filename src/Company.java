
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Company {

    // Создание Логгера, который ведёт запись в файл "log.txt" в корне проекта
    // для удобочитаемости используется SimpleFormatter

    private static Logger logger = Logger.getLogger(Company.class.getName());
    private static FileHandler fileHandler;

    static {
        try {
            fileHandler = new FileHandler("log.txt", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Для реализации Singleton используется внутренний класс
    // (решение Била Пью(Bill Pugh) “Initialization on Demand Holder”)

    private Company() {
    }

    private static class LazyHolder {
        private static final Company INSTANCE = new Company();
    }

    public static Company getInstance() {
        doRefresh();
        return LazyHolder.INSTANCE;
    }


    // Объявление полей класса и аннотаций к ним

    @Property(fieldName = "com.mycompany.name", defaultValue = "Tesla")
    private volatile String myCompanyName;

    @Property(fieldName = "com.mycompany.address",
            defaultValue = "{\"street\":\"California, Palo Alto, Deer Creek Road\",\"home\":\"3500\"}")
    private volatile Address myCompanyAddress;

    @Property(fieldName = "com.mycompany.years.old", defaultValue = "16")
    private volatile Integer myCompanyYearsOld;

    @Property(fieldName = "com.mycompany.owner", defaultValue = "Elon Musk")
    private volatile String myCompanyOwner;

    @Property(fieldName = "com.mycompany.capital", defaultValue = "4923.00")
    private volatile Double myCompanyCapital;

    @Property(fieldName = "com.mycompany.employees.count", defaultValue = "48817")
    private volatile Integer myCompanyEmployeesCount;

    @Property(fieldName = "com.mycompany.Symbol", defaultValue = "T")
    // обработка для этого поля отсутствует для примера работы логгера
    private volatile Character myCompanySymbol;


    // Объявление геттеров

    public String getMyCompanyName() {
        return myCompanyName;
    }

    public Address getMyCompanyAddress() {
        return myCompanyAddress;
    }

    public Integer getMyCompanyAge() {
        return myCompanyYearsOld;
    }

    public String getMyCompanyOwner() {
        return myCompanyOwner;
    }

    public Double getMyCompanyCapital() {
        return myCompanyCapital;
    }

    public Integer getMyCompanyEmployeesCount() {
        return myCompanyEmployeesCount;
    }

    public Character getMyCompanySymbol() {
        return myCompanySymbol;
    }


    // Метод doRefresh()
    // 1) выбирает аннотированные поля
    // 2) выполняет попытку чтения из файла "company.properties" и записи в сет "properties"
    // 3) если к файлу "company.properties" нет доступа или он пустой
    //  3.1) проходит по аннотированным полям,
    //  3.2) на каждом поле вызывает метод fieldSetter(поле, дефолт. значение),
    //       который присваивает значение по умолчанию переданному полю
    //   3.2.1) если fieldSetter не может обработать тип поля,
    //          полю будет присвоено значение null)
    //  3.3) при отсутствии значения по умолчанию,
    //       полю будет присвоено значение null
    // 4) если к файлу "company.properties" есть доступ и он не пустой
    //  4.1) проходит по аннотированным полям,
    //  4.2) на каждом поле вызывает метод fieldSetter(поле, значение),
    //       который присваивает значение из файла "company.properties" переднному полю
    //   4.2.1) если fieldSetter не может обработать тип поля,
    //          полю будет присвоено значение null
    //  4.3) при отсутствии значения в файле "company.properties",
    //       полю будет присвоено значение по умолчанию,
    //  4.4) при отсутствии значения по умолчанию,
    //       полю будет присвоено значение null

    public static synchronized void doRefresh() {
        Company company = LazyHolder.INSTANCE;
        Properties properties = new Properties(); // Сет, в который будут загружены данные из файла "company.properties"
        ArrayList<Field> annotatedFields;         // Список всех полей с аннотацией @Property
        String fieldName, defaultValue, value;    // атрибут_1, атрибут_2, параметр из "company.properties" соответствующий атрибуту_1

        // Считывает все поля и удаляем поля без аннотации @Property

        annotatedFields = new ArrayList<>(Arrays.asList(Company.class.getDeclaredFields()));
        annotatedFields.removeIf(f -> !f.isAnnotationPresent(Property.class));

        // Считывает файл "company.properties" в "properties"

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("company.properties"));
            properties.load(bufferedReader);
            bufferedReader.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception: ", e);
        }

        // Если к файлу "company.properties" нет доступа или он пустой,
        // Проходим по аннотированным полям класса Company,
        // Присваем полям значение по умолчанию.
        // При отсутствии значения по умолчанию будет присвоен null

        if (properties.isEmpty()) {
            logger.log(Level.INFO, "File \"company.properties\" cannot be accessed, or it is empty");

            for (Field field : annotatedFields) {

                defaultValue = field.getAnnotation(Property.class).defaultValue();

                if (!defaultValue.equals("")) {
                    fieldSetter(field, defaultValue);
                } else {
                    try {
                        field.set(company, null);
                    } catch (IllegalAccessException e) {
                        logger.log(Level.SEVERE, "Exception: ", e);
                    }
                }
            }
        }

        // Если к файлу "company.properties" есть доступ и он не пустой
        // присваиваем полю значение параметра из файла "company.properties",
        // соответствующего аргументу аннотации к данному полю класса Company.
        // При отсутствии параметра, будет присвоено значение по умолчанию.
        // При отсутствии значения по умолчанию будет присвоен null

        else {
            for (Field field : annotatedFields) {

                fieldName = field.getAnnotation(Property.class).fieldName();       // атрибут_1 аннотации
                defaultValue = field.getAnnotation(Property.class).defaultValue(); // атрибут_2 аннотации (дефолтное значение)
                value = properties.getProperty(fieldName);                         // значение параметра из "company.properties" соответствующее атрибуту_1

                if (value != null) {
                    fieldSetter(field, value);
                } else if (!defaultValue.equals("")) {
                    logger.log(Level.INFO, "In file \"company.properties\" field: \"" + fieldName + "\" is absent");
                    fieldSetter(field, defaultValue);
                } else {
                    logger.log(Level.INFO, "In file \"company.properties\" field: \"" + fieldName + "\" is absent");
                    try {
                        field.set(company, null);
                    } catch (IllegalAccessException e) {
                        logger.log(Level.SEVERE, "Exception: ", e);
                    }
                }
            }
        }
    }

    // Метод fieldSetter
    // проверяет тип переданного поля (field) класса "Company",
    // приводит значение параметра "value" из файла "company.properties" к типу переданного поля "field"
    // присваивает это значение переданному полю "field" класса "Company"

    private static void fieldSetter(Field field, String value) {
        ObjectMapper mapper = new ObjectMapper();
        Company company = LazyHolder.INSTANCE;

        switch (field.getType().getSimpleName()) {
            case "String":
                try {
                    field.set(company, value);
                } catch (IllegalAccessException e) {
                    logger.log(Level.SEVERE, "Exception: ", e);
                }
                break;
            case "Integer":
                try {
                    field.set(company, Integer.parseInt(value));
                } catch (IllegalAccessException | NumberFormatException e) {
                    logger.log(Level.SEVERE, "Exception: ", e);
                }
                break;
            case "Double":
                try {
                    field.set(company, Double.parseDouble(value));
                } catch (IllegalAccessException | NumberFormatException e) {
                    logger.log(Level.SEVERE, "Exception: ", e);
                }
                break;
            case "Address":
                try {
                    Address address = mapper.readValue(value, Address.class);
                    field.set(company, address);
                } catch (JsonProcessingException | IllegalAccessException e) {
                    logger.log(Level.SEVERE, "Exception: ", e);
                }
                break;

            // Если метод fieldSetter "не знает" тип поля,
            // он выведет в лог сообщение с просьбой добавить обработку этого типа в метод,
            // а затем присвоит полю значение null

            default:
                try {
                    logger.log(Level.INFO, "Please add processing of \"" + field.getType() + "\" to fieldSetter method");
                    field.set(company, null);
                } catch (IllegalAccessException e) {
                    logger.log(Level.SEVERE, "Exception: ", e);
                }
        }
    }
}
