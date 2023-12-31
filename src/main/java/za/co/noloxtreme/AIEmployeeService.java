package za.co.noloxtreme;

import com.google.gson.Gson;
import za.co.noloxtreme.dto.OpenAPIDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AIEmployeeService {
    private static final String FUNCTION_CALL_FILTER_ARRAY_BY_KEY = "filterArrayByKey";
    private static final String FUNCTION_CALL_RANGE_FILTER = "rangeFilterArrayByKey";

    private static final String FUNCTION_CALL_REDUCER = "reduceFields";
    private final EmployeeService employeeService = new EmployeeService();
    private final OpenApiService openApiService = new OpenApiService();

    private final List<OpenAPIDTO.FunctionDescription> employeeServiceFunctions;

    private final Gson gson = new Gson();

    public AIEmployeeService() {
        OpenAPIDTO.FunctionDescription genericFilter =
                OpenAPIDTO.FunctionDescription.builder()
                .name(FUNCTION_CALL_FILTER_ARRAY_BY_KEY)
                .description("Filter map list by key")
                .parameters(OpenAPIDTO.Parameters.builder()
                        .type("object")
                        .properties(Map.of(
                                "key", OpenAPIDTO.Property.builder()
                                        .type("string")
                                        .description("The key to filter by")
                                        .enumValues(List.of("department","country"))
                                        .build(),
                                "value", OpenAPIDTO.Property.builder()
                                        .type("array").items(OpenAPIDTO.ArrayType.builder().type("string")
                                                .build())
                                            .description("The values to filter by")
                                        .build()
                        ))
                        .required(List.of("key", "value"))
                        .build())
                .build();

        OpenAPIDTO.FunctionDescription rangeFilter =
                OpenAPIDTO.FunctionDescription.builder()
                        .name(FUNCTION_CALL_RANGE_FILTER)
                        .description("Filter map list by key in a particular range...")
                        .parameters(OpenAPIDTO.Parameters.builder()
                                .type("object")
                                .properties(Map.of(
                                        "key", OpenAPIDTO.Property.builder()
                                                .type("string")
                                                .description("The key to filter by")
                                                .enumValues(List.of("age","salary"))
                                                .build(),
                                        "operation", OpenAPIDTO.Property.builder()
                                                .type("string")
                                                .description("The operation, the value")
                                                .enumValues(List.of("gte","lte","eq","lt","gt"))
                                                .build(),
                                        "value", OpenAPIDTO.Property.builder()
                                                .type("number")
                                                .description("The values to filter by")
                                                .build()
                                ))
                                .required(List.of("key", "operation","value"))
                                .build())
                        .build();

        OpenAPIDTO.FunctionDescription reducerFunctions =
                OpenAPIDTO.FunctionDescription.builder()
                        .name(FUNCTION_CALL_REDUCER)
                        .description("Perform a reduction operation on specified fields")
                        .parameters(OpenAPIDTO.Parameters.builder()
                                .type("object")
                                .properties(Map.of(
                                        "groupByKeys", OpenAPIDTO.Property.builder()
                                                .type("array")
                                                .description("List of fields to group by")
                                                .items(OpenAPIDTO.ArrayType.builder()
                                                        .type("string")
                                                        .build())
                                                .build(),
                                        "reductionKeys", OpenAPIDTO.Property.builder()
                                                .type("array")
                                                .description("List of fields to reduce, this will correspond to the operations")
                                                .items(OpenAPIDTO.ArrayType.builder()
                                                        .type("string")
                                                        .build())
                                                .build(),
                                        "operations", OpenAPIDTO.Property.builder()
                                                .type("array")
                                                .description("List of aggregation operations, this will correspond to the reduction keys")
                                                .items(OpenAPIDTO.ArrayType.builder()
                                                        .type("string")
                                                        .enumValues(List.of("sum","count","avg","min","max"))
                                                        .build())
                                                .build()
                                ))
                                .required(List.of("groupByKeys", "reductionKeys","operations"))
                                .build())
                        .build();


        employeeServiceFunctions = List.of(genericFilter,rangeFilter, reducerFunctions);
    }

    public String queryEmployees(String query) throws Exception {
        List<Map<String, Object>> filteredList = employeeService.getAllEmployees();

        int maxIterations = 15;
        String result = null;

        List< OpenAPIDTO.Message> messages = new ArrayList<>();
        messages.add(OpenAPIDTO.Message.builder()
                        .role(OpenAPIDTO.Roles.user)
                        .content(query)
                        .build());

        while (maxIterations-- > 0) {
            OpenAPIDTO.OpenAPIResponse response = openApiService.makeFunctionCall(messages, employeeServiceFunctions);

            if (response.shouldMakeFunctionCall()) {
                OpenAPIDTO.Message responseMessage = response.getChoices().get(0).getMessage();
                responseMessage.setContent("");
                messages.add(responseMessage);
                if (FUNCTION_CALL_FILTER_ARRAY_BY_KEY.equalsIgnoreCase(response.getFunctionName())) {
                    Map map = response.getFunctionArguments();
                    String key = (String) map.get("key");
                    List<String> value = (List<String>) map.get("value");
                    if ("department".equalsIgnoreCase(key)) {
                        filteredList = employeeService.filterByDepartment(filteredList,value.toArray(new String[0]));
                    } else if ("country".equalsIgnoreCase(key)) {
                        filteredList = employeeService.filterByCountry(filteredList,value.toArray(new String[0]));
                    } else if ("age".equalsIgnoreCase(key)) {
                        filteredList = employeeService.filterByAgeEQ(Integer.parseInt(value.get(0)), filteredList);
                    }
                    messages.add(OpenAPIDTO.Message.builder()
                            .role(OpenAPIDTO.Roles.function)
                            .name(FUNCTION_CALL_FILTER_ARRAY_BY_KEY)
                                    .content("List filtered by " + key + " using value " + value)
                            .build());
                }
                if (FUNCTION_CALL_RANGE_FILTER.equalsIgnoreCase(response.getFunctionName())) {
                    Map map = response.getFunctionArguments();
                    String key = (String) map.get("key");
                    String operation = (String) map.get("operation");
                    double value = Double.parseDouble(String.valueOf(map.get("value")));
                    if ("age".equalsIgnoreCase(key)) {
                        if ("gte".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterByAgeGTE((int) value, filteredList);
                        }
                        if ("gt".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterByAgeGTE((int) value +1, filteredList);
                        } else if ("lte".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterByAgeLTE((int) value, filteredList);
                        }
                        else if ("lt".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterByAgeLTE((int) value -1, filteredList);
                        }
                        else if ("eq".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterByAgeEQ((int) value, filteredList);
                        }
                    } else if ("salary".equalsIgnoreCase(key)) {
                        if ("gte".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterBySalaryGTE(value, filteredList);
                        }
                        if ("gt".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterBySalaryGTE(value +0.1, filteredList);
                        }
                        else if ("lte".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterBySalaryLTE(value, filteredList);
                        }
                        else if ("lt".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterBySalaryLTE(value-0.1, filteredList);
                        }
                        else if ("eq".equalsIgnoreCase(operation)) {
                            filteredList = employeeService.filterBySalaryEQ(value, filteredList);
                        }
                    }

                    messages.add(OpenAPIDTO.Message.builder()
                            .role(OpenAPIDTO.Roles.function)
                            .name(FUNCTION_CALL_RANGE_FILTER)
                            .content("List filtered by " + key + " using value " + value)
                            .build());
                }
                if (FUNCTION_CALL_REDUCER.equalsIgnoreCase(response.getFunctionName())) {
                    Map map = response.getFunctionArguments();
                    List<String> groupByKeys = (List<String>) map.get("groupByKeys");
                    List<String> reductionKeys = (List<String>) map.get("reductionKeys");
                    List<String> operations = (List<String>) map.get("operations");

                    filteredList = employeeService.aggregateByKeys(groupByKeys, reductionKeys, operations, filteredList);

                    messages.add(OpenAPIDTO.Message.builder()
                            .role(OpenAPIDTO.Roles.function)
                            .name(FUNCTION_CALL_REDUCER)
                            .content("List aggregated by " + groupByKeys + " using value " + reductionKeys+" And performing the operations "+operations)
                            .build());
                }
            }
            if (response.shouldStopFunctionCalls()) {
                if (filteredList.isEmpty()) {
                    result = "No results found";
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < filteredList.size(); i++) {
                        Map<String, Object> employee = filteredList.get(i);
                        sb.append(i + 1).append(". ")
                                .append(employee.toString())
                                .append("\n");
                    }
                    result = sb.toString();
                }

                break;
            }
        }

        return result;
    }
}
