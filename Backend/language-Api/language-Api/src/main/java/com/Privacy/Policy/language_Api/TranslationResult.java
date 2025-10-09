package com.Privacy.Policy.language_Api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslationResult {
    @JsonProperty("translation_text")
    private String translation_text;


    public String getTranslationText() {
        return translation_text;
    }

    public void setTranslationText(String translation_text) {
        this.translation_text = translation_text;
    }
}
