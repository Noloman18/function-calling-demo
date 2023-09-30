package za.co.noloxtreme;

import java.util.*;

public class DataSource {

    private static final List<String> NAMES = Arrays.asList("John", "Jane", "Jack", "Jill", "Abba", "Preach", "Sydney", "Salah");
    private static final List<String> DEPARTMENTS = Arrays.asList("IT", "HR", "Finance", "Marketing", "Sales", "Operations", "Legal", "Procurement");
    private static final Double MIN_SALARY = 10_000.00;
    private static final Double MAX_SALARY = 100_000.00;
    private static final List<String> COUNTRIES = Arrays.asList("South Africa", "United States", "United Kingdom", "Australia", "New Zealand", "Canada", "Germany", "France");

    private static final List<Map<String,Object>> employeeDatabase;
    private static final int DB_SIZE = 100;
    static {
        employeeDatabase = new java.util.ArrayList<>(DB_SIZE);
        for (int i = 0; i < DB_SIZE; i++) {
            employeeDatabase.add(Map.of(
                    "name", NAMES.get((int) (Math.random() * NAMES.size())),
                    "department", DEPARTMENTS.get((int) (Math.random() * DEPARTMENTS.size())),
                    "age", ((int) (Math.random() * 42))+18,
                    "salary", MIN_SALARY + (Math.random() * (MAX_SALARY - MIN_SALARY)),
                    "country", COUNTRIES.get((int) (Math.random() * COUNTRIES.size()))
            ));
        }
    }


    public List<Map<String,Object>> getEmployeeDatabase() {
        ArrayList<Map<String, Object>> result = new ArrayList<>(employeeDatabase);
        Collections.shuffle(result);
        return result;
    }
}
