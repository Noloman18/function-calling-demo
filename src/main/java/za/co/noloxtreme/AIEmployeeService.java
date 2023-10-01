package za.co.noloxtreme;

import com.google.gson.Gson;
import za.co.noloxtreme.dto.OpenAPIDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AIEmployeeService {
    private final EmployeeService employeeService = new EmployeeService();
    private final OpenApiService openApiService = new OpenApiService();

    private final List<OpenAPIDTO.FunctionDescription> employeeServiceFunctions;

    private final Gson gson = new Gson();

    public AIEmployeeService() {
        OpenAPIDTO.FunctionDescription genericFilter =
                OpenAPIDTO.FunctionDescription.builder()
                .name("filterArrayByKey")
                .description("Filter map list by key")
                .parameters(OpenAPIDTO.Parameters.builder()
                        .type("object")
                        .properties(Map.of(
                                "key", OpenAPIDTO.Property.builder()
                                        .type("string")
                                        .description("The key to filter by")
                                        .enumValues(List.of("department","country","age"))
                                        .build(),
                                "value", OpenAPIDTO.Property.builder().type("string")
                                            .description("The value to filter by")
                                        .build()

                        ))
                        .required(List.of("key", "value"))
                        .build())
                .build();


        employeeServiceFunctions = List.of(genericFilter);
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
                if ("filterArrayByKey".equalsIgnoreCase(response.getFunctionName())) {
                    Map map = response.getFunctionArguments();
                    String key = (String) map.get("key");
                    String value = (String) map.get("value");
                    if ("department".equalsIgnoreCase(key)) {
                        filteredList = employeeService.filterByDepartment(value, filteredList);
                    } else if ("country".equalsIgnoreCase(key)) {
                        filteredList = employeeService.filterByCountry(value, filteredList);
                    } else if ("age".equalsIgnoreCase(key)) {
                        filteredList = employeeService.filterByAgeEQ(Integer.parseInt(value), filteredList);
                    }
                    messages.add(OpenAPIDTO.Message.builder()
                            .role(OpenAPIDTO.Roles.function)
                            .name("filterArrayByKey")
                                    .content("List filtered by " + key + " using value " + value)
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
