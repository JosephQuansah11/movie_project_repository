package joe.amethyst.backend_tutorials.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import joe.amethyst.backend_tutorials.demo.solid_principles.utilities.AiModel;

class AiModelTest {

    @Test
    void supportsMultipleConfiguredModelsAndCacheEntries() {
        AiModel model = new AiModel();
        model.configure(List.of("codeqwen:latest", "mistral:latest"));

        assertEquals(2, model.getAvailableModels().size());
        assertTrue(model.getAvailableModels().contains("codeqwen:latest"));
        assertTrue(model.getAvailableModels().contains("mistral:latest"));
        assertEquals("codeqwen:latest", model.getDefaultModel());

        model.setSelectedModels(List.of("codeqwen:latest", "mistral:latest"));
        assertEquals(List.of("codeqwen:latest", "mistral:latest"), model.getSelectedModels());
    }
}
