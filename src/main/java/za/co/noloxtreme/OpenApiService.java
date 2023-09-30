package za.co.noloxtreme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import za.co.noloxtreme.dto.OpenAIRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class OpenApiService {
    private final Gson gson;

    public OpenApiService() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        this.gson = builder.create();
    }



    public Map makeFunctionCall(List<OpenAIRequest.Message> messages,List<OpenAIRequest.FunctionDescription> functions) throws Exception {

        OpenAIRequest request = OpenAIRequest.builder()
                .model("gpt-3.5-turbo-0613")
                .functionCall("auto")
                .temperature(0.2f)
                .maxTokens(1024)
                .messages(messages)
                .functions(functions)
                .build();

        var httpClient = HttpClient.newHttpClient();
        String stringBody = gson.toJson(request);
        System.out.println(stringBody);
        var httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " +  System.getenv("OPENAPI_KEY"))
                .POST(HttpRequest.BodyPublishers.ofString(stringBody))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Map.class);
    }
}
