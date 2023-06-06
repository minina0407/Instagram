package com.api.PortfoGram.image.dto;

import com.api.PortfoGram.image.entity.ImageEntity;
import com.api.PortfoGram.post.entity.PostEntity;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class Images {
    private List<Image> images;

}