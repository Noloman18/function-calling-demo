package za.co.noloxtreme;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeService {

    private DataSource dataSource = new DataSource();

    public List<Map<String,Object>> getAllEmployees() {
        return dataSource.getEmployeeDatabase();
    }

    public List<Map<String,Object>> filterByDepartment(String department, List<Map<String,Object>> list) {
        return list.stream()
                .filter(employee -> department.equalsIgnoreCase((String) employee.get("department")))
                .collect(Collectors.toList());
    }

    public List<Map<String,Object>> filterByCountry(String country,List<Map<String,Object>> list) {
        return list.stream()
                .filter(employee -> country.equalsIgnoreCase((String) employee.get("country")))
                .collect(Collectors.toList());
    }

    public List<Map<String,Object>> filterByAgeGTE(int age,List<Map<String,Object>> list) {
        return list.stream()
                .filter(employee -> (int)employee.get("age") >= age)
                .collect(Collectors.toList());
    }

    public List<Map<String,Object>> filterByAgeLTE(int age,List<Map<String,Object>> list) {
        return list.stream()
                .filter(employee -> (int)employee.get("age") <= age)
                .collect(Collectors.toList());
    }
    public List<Map<String,Object>> filterByAgeEQ(int age,List<Map<String,Object>> list) {
        return list.stream()
                .filter(employee -> (int)employee.get("age") == age)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> aggregateByKeys(List<String> keys, List<String> properties, List<String> aggregateOperation,List<Map<String,Object>> list) {
        return list.stream()
                .collect(Collectors.groupingBy(employee -> {
                    StringBuilder sb = new StringBuilder();
                    for (String key : keys) {
                        sb.append(employee.get(key));
                    }
                    return sb.toString();
                }))
                .entrySet().stream()
                .map(entry -> {
                    Map<String,Object> aggregates = new LinkedHashMap<>();
                    List<Map<String, Object>> groupedList = entry.getValue();

                    for (int i = 0; i < properties.size(); i++) {
                        String property = properties.get(i);
                        String operation = aggregateOperation.get(i);

                        if ("sum".equalsIgnoreCase(operation)) {
                            aggregates.put("sum_"+property, groupedList.stream().mapToDouble(item->Double.parseDouble(""+item.get(property))).sum());
                        }
                        else if ("avg".equalsIgnoreCase(operation)) {
                            aggregates.put("avg_"+property, groupedList.stream().mapToDouble(item->Double.parseDouble(""+item.get(property))).average().orElse(0D));
                        }
                        else if ("min".equalsIgnoreCase(operation)) {
                            aggregates.put("min_"+property, groupedList.stream().mapToDouble(item->Double.parseDouble(""+item.get(property))).min().orElse(0D));
                        }
                        else if ("max".equalsIgnoreCase(operation)) {
                            aggregates.put("max_"+property, groupedList.stream().mapToDouble(item->Double.parseDouble(""+item.get(property))).max().orElse(0D));
                        }
                    }

                    return aggregates;
                })
                .collect(Collectors.toList());
    }

}
