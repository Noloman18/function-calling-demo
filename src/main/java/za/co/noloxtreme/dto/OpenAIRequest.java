package za.co.noloxtreme.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class OpenAIRequest {
    private String model;

    private List<Message> messages;
    private float temperature;
    @SerializedName("max_tokens")
    private int maxTokens;
    @SerializedName("function_call")
    private String functionCall;

    private List<FunctionDescription> functions;
    @Data
    @Builder
    public static class FunctionDescription {
        private String name;
        private String description;
        private Parameters parameters;
    }

    @Data
    @Builder
    public static class Parameters {
        private String type = "object";
        private Map<String, Property> properties;
        private List<String> required;
    }

    @Data
    @Builder
    public static class Property {
        private String type;
        private String description;
        @SerializedName("enum")
        private List<String> enumValues;
    }

    @Data
    @Builder
    public static class Message {
        private Roles role;
        private String content;
        private String name;
    }

    public enum Roles {
        system,user, assistant, function;
    }
}
