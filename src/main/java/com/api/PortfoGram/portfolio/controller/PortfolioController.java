package com.api.PortfoGram.portfolio.controller;

import com.api.PortfoGram.Image.service.PortfolioImageService;
import com.api.PortfoGram.comment.dto.Comment;
import com.api.PortfoGram.comment.dto.Comments;
import com.api.PortfoGram.comment.service.CommentService;
import com.api.PortfoGram.portfolio.dto.Portfolio;
import com.api.PortfoGram.portfolio.dto.PortfolioLike;
import com.api.PortfoGram.portfolio.service.PortfolioService;
import com.api.PortfoGram.portfolio.service.PortfolioLikeService;

import javax.validation.Valid;

import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "포트폴리오 API", description = "포트폴리오 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
@Tag(name = "포트폴리오 API", description = "포트폴리오 관련 API")
public class PortfolioController {
    private final UserService userService;
    private final PortfolioService portfolioService;
    private final PortfolioImageService portfolioImageService;
    private final CommentService commentService;
    private final PortfolioLikeService portfolioLikeService;

    @GetMapping("/{portfolioId}")
    @Operation(summary = "포트폴리오 조회", description = "ID를 통해 포트폴리오를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 조회 성공"),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다")
    })
    public ResponseEntity<Portfolio> getPortfolioById(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        return new ResponseEntity<>(portfolio, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "모든 포트폴리오 조회", description = "모든 포트폴리오를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    public ResponseEntity<Page<Portfolio>> getAllPortfolios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Portfolio> portfolios = portfolioService.getAllPortfolios(  pageable);

        return new ResponseEntity<>(portfolios, HttpStatus.OK);

    }

    @GetMapping("/{portfolioId}/comments")
    @Operation(summary = "포트폴리오 댓글 조회", description = "특정 포트폴리오의 댓글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "포트폴리오 댓글 목록 조회 성공")
    public ResponseEntity<Comments> getCommentsByPortfolioId(
            @PathVariable("portfolioId") Long portfolioId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Comments comments = commentService.getCommentsByPortfolioId(portfolioId, pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "포트폴리오 저장", description = "포트폴리오를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 저장 성공",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Portfolio.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청: 이미지 파일이나 내용이 비어 있습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다.")})

    public ResponseEntity<Portfolio> savePortfolio(
            @RequestParam(value = "images", required = true) List<MultipartFile> imageFiles,
            @RequestParam(value = "content", required = true) String content) {

        portfolioService.savePortfolio(content, imageFiles);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{portfolioId}/likes")
    @Operation(summary = "포트폴리오 좋아요", description = "특정 포트폴리오에 좋아요를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 좋아요 추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<PortfolioLike> likePortfolio(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        portfolioLikeService.likePortfolio(portfolioId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/portfolios/images/{image-id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "포트폴리오 이미지 조회", description = "포트폴리오 이미지를 조회합니다.")
    public ResponseEntity<byte[]> getPortfolioImage(
            @PathVariable("image-id") Long Id
    ) {
        byte[] image = portfolioImageService.getByName(Id);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @GetMapping("/latest")
    @Operation(summary = "팔로우한 유저들의 최신 포트폴리오 조회", description = "사용자가 팔로우하는 유저들의 최신 포트폴리오 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청이 성공하고 팔로우한 유저들의 최신 포트폴리오 목록을 반환합니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<Portfolio>> getFollowedUsersLatestPortfolios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UserEntity userEntity = userService.getMyUserWithAuthorities();
        Pageable pageable = PageRequest.of(page, size);
        Page<Portfolio> portfolioPage = portfolioService.getLatestPortfolios(userEntity, pageable);
        return new ResponseEntity<>(portfolioPage, HttpStatus.OK);
    }


    @PutMapping("/{portfolioId}")
    @Operation(summary = "포트폴리오 수정", description = "특정 포트폴리오를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청:  내용이 비어 있습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")})
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Portfolio> updatePortfolio(
            @PathVariable("portfolioId") Long portfolioId,
            @Valid @RequestBody Portfolio portfolio
    ) {
        portfolioService.updatePortfolio(portfolioId, portfolio);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{portfolioId}")
    @Operation(summary = "포트폴리오 삭제", description = "특정 포트폴리오를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없습니다.")})
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity deletePortfolio(
            @PathVariable("portfolioId") Long portfolioId
    ) {
        portfolioService.deletePortfolio(portfolioId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
