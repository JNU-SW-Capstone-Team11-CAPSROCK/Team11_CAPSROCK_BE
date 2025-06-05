package capsrock.clothing.enums;

import lombok.Getter;

@Getter
public enum GeminiModel {
    FLASH_2_5_PREVIEW_05_20("gemini-2.5-flash-preview-05-20"),
    PRO_2_5_EXP_03_25("gemini-2.5-pro-exp-03-25"),
    FLASH_2_0("gemini-2.0-flash"),
    FLASH_2_0_LITE("gemini-2.0-flash-lite");

    private final String modelName;

    GeminiModel(String modelName) {
        this.modelName = modelName;
    }

}
