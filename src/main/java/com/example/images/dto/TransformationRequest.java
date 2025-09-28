package com.example.images.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransformationRequest {

    @JsonProperty("transformations")
    private Transformations transformations;

    @Data
    public static class Transformations {
        private Resize resize;
        private Crop crop;
        private Integer rotate;
        private Boolean flip;
        private Boolean mirror;
        private Filters filters;
        private String watermark;
        private String format;
    }

    @Data
    public static class Resize {
        private Integer width;
        private Integer height;
    }

    @Data
    public static class Crop {
        private Integer width;
        private Integer height;
        private Integer x;
        private Integer y;
    }

    @Data
    public static class Filters {
        private Boolean grayscale;
        private Boolean sepia;
    }

}

