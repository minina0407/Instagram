package com.api.instagram.image.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Images {
    private List<Image> images;
}
