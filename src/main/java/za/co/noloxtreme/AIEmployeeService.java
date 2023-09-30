package za.co.noloxtreme;

import za.co.noloxtreme.dto.OpenAIRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AIEmployeeService {
    private final EmployeeService employeeService = new EmployeeService();
    private final OpenApiService openApiService = new OpenApiService();

    private final List<OpenAIRequest.FunctionDescription> employeeServiceFunctions;

    public AIEmployeeService() {
        OpenAIRequest.FunctionDescription genericFilter =
                OpenAIRequest.FunctionDescription.builder()
                .name("filterArrayByKey")
                .description("Filter map list by key")
                .parameters(OpenAIRequest.Parameters.builder()
                        .type("object")
                        .properties(Map.of(
                                "key", OpenAIRequest.Property.builder()
                                        .type("string")
                                        .description("The key to filter by")
                                        .enumValues(List.of("department","country","age"))
                                        .build(),
                                "value", OpenAIRequest.Property.builder().type("string")
                                            .description("The value to filter by")
                                        .build(),
                                "list", OpenAIRequest.Property.builder()
                                        .description("The current state of the list to filter")
                                        .build()

                        ))
                        .required(List.of("key", "value"))
                        .build())
                .build();


        employeeServiceFunctions = List.of(genericFilter);
    }

    public String queryEmployees(String query) throws Exception {
        int maxIterations = 10;
        String result = null;

        List< OpenAIRequest.Message> messages = new ArrayList<>();
        messages.add(OpenAIRequest.Message.builder()
                        .role(OpenAIRequest.Roles.user)
                        .content(query)
                        .build());

        while (maxIterations-- > 0) {
            Map map = openApiService.makeFunctionCall(messages, employeeServiceFunctions);
            System.out.println(map);
            break;
        }

        return result;
    }
}
