package za.co.noloxtreme.dto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class OpenAPIDTO {
    private static final Gson GSON = new Gson();

    @Data
    @Builder
    public static class OpenAPIRequest {
        private String model;

        private List<Message> messages;
        private float temperature;
        @SerializedName("max_tokens")
        private int maxTokens;
        @SerializedName("function_call")
        private String functionCall;

        private List<FunctionDescription> functions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenAPIResponse {
        private String id;
        private String object;
        private List<Choice> choices;

        public boolean shouldMakeFunctionCall() {
            return "function_call".equalsIgnoreCase(choices.get(0).getFinishReason());
        }
        public boolean shouldStopFunctionCalls() {
            return "stop".equalsIgnoreCase(choices.get(0).getFinishReason());
        }

        public String getFunctionName() {
            return choices.get(0).getMessage().getFunctionCall().getName();
        }

        public Map getFunctionArguments() {
            return GSON.fromJson(choices.get(0).getMessage().getFunctionCall().getArguments(), Map.class);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;
        private Message message;
        @SerializedName("finish_reason")
        private String finishReason;
    }

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
        private ArrayType items;
    }

    @Data
    @Builder
    public static class ArrayType {
        private String type;
        @SerializedName("enum")
        private List<String> enumValues;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private Roles role;
        private String content;
        private String name;
        private String function;
        @SerializedName("function_call")
        private FunctionCall functionCall;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionCall {
        private String name;
        private String arguments;
    }

    public enum Roles {
        system,user, assistant, function;
    }
}
