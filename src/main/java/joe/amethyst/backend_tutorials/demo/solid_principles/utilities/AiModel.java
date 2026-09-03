package joe.amethyst.backend_tutorials.demo.solid_principles.utilities;

import java.util.ArrayList;
import java.util.List;

public class AiModel {
    public static final int MAX_MODELS_PER_REQUEST = 4;
    public static final String DEFAULT_OLLAMA_URL = "http://localhost:11434";

    private String modelName;
    private String modelUrl = DEFAULT_OLLAMA_URL;
    private String apiKey;
    private String apiSecret;
    private String userAgent;
    private boolean trainingMode;
    private boolean isDocumentProvided;
    private List<String> availableModels = new ArrayList<>();
    private List<String> selectedModels = new ArrayList<>();
    private String defaultModel;

    private boolean initialized = false;

    public void configure(String modelName, String modelUrl) {
        if (modelName == null || modelName.isBlank() || modelUrl == null || modelUrl.isBlank()) {
            throw new IllegalArgumentException("modelName and modelUrl must not be null or blank");
        }

        List<String> modelList = List.of(modelName);
        configure(modelList, modelUrl);
    }

    public void configure(List<String> modelNames) {
        configure(modelNames, DEFAULT_OLLAMA_URL);
    }

    public void configure(List<String> modelNames, String modelUrl) {
        if (modelUrl == null || modelUrl.isBlank()) {
            throw new IllegalArgumentException("modelUrl must not be null or blank");
        }

        List<String> validModels = sanitizeModels(modelNames);
        if (validModels.isEmpty()) {
            throw new IllegalArgumentException("At least one AI model must be configured");
        }

        if (validModels.size() > MAX_MODELS_PER_REQUEST) {
            throw new IllegalArgumentException("You can configure between 1 and 4 AI models at a time");
        }

        if (initialized) {
            this.availableModels = validModels;
            this.selectedModels = new ArrayList<>(validModels);
            this.defaultModel = validModels.get(0);
            this.modelName = validModels.get(0);
            this.modelUrl = modelUrl;
            return;
        }

        this.availableModels = validModels;
        this.selectedModels = new ArrayList<>(validModels);
        this.defaultModel = validModels.get(0);
        this.modelName = validModels.get(0);
        this.modelUrl = modelUrl;
        this.initialized = true;
    }

    public void setSelectedModels(List<String> selectedModels) {
        List<String> validModels = sanitizeModels(selectedModels);
        if (validModels.isEmpty()) {
            throw new IllegalArgumentException("At least one model must be selected");
        }
        if (validModels.size() > MAX_MODELS_PER_REQUEST) {
            throw new IllegalArgumentException("You can select between 1 and 4 models at a time");
        }

        this.selectedModels = validModels;
        this.modelName = validModels.get(0);
        this.defaultModel = validModels.get(0);
        this.initialized = true;
    }

    public List<String> getAvailableModels() {
        ensureInitialized();
        return new ArrayList<>(availableModels);
    }

    public List<String> getSelectedModels() {
        if (selectedModels.isEmpty()) {
            return new ArrayList<>(availableModels);
        }
        return new ArrayList<>(selectedModels);
    }

    public String getDefaultModel() {
        ensureInitialized();
        return defaultModel != null ? defaultModel : modelName;
    }

    public String getModelName() {
        ensureInitialized();
        return modelName;
    }

    public String getModelUrl() {
        ensureInitialized();
        return modelUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isTrainingMode() {
        return trainingMode;
    }

    public boolean isDocumentProvided() {
        return isDocumentProvided;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setTrainingMode(boolean trainingMode) {
        this.trainingMode = trainingMode;
    }

    public void setDocumentProvided(boolean isDocumentProvided) {
        this.isDocumentProvided = isDocumentProvided;
    }

    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException(
                "AiModel is not configured yet. Call configure(modelName, modelUrl) first.");
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    private List<String> sanitizeModels(List<String> modelNames) {
        if (modelNames == null || modelNames.isEmpty()) {
            return List.of();
        }

        List<String> sanitized = new ArrayList<>();
        for (String model : modelNames) {
            if (model == null || model.isBlank()) {
                continue;
            }
            String cleanModel = model.trim();
            if (!sanitized.contains(cleanModel)) {
                sanitized.add(cleanModel);
            }
        }
        return sanitized;
    }

    @Override
    public String toString() {
        return "AiModel{" +
                "modelName='" + modelName + '\'' +
                ", modelUrl='" + modelUrl + '\'' +
                ", availableModels=" + availableModels +
                ", selectedModels=" + selectedModels +
                '}';
    }
}