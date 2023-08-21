package com.api.PortfoGram.portfolio.controller;

import com.api.PortfoGram.Image.dto.Image;
import com.api.PortfoGram.comment.dto.Comment;
import com.api.PortfoGram.comment.dto.Comments;
import com.api.PortfoGram.comment.entity.CommentEntity;
import com.api.PortfoGram.comment.service.CommentService;
import com.api.PortfoGram.portfolio.dto.Portfolio;
import com.api.PortfoGram.portfolio.dto.PortfolioImage;
import com.api.PortfoGram.portfolio.service.PortfolioLikeService;
import com.api.PortfoGram.portfolio.service.PortfolioService;
import com.api.PortfoGram.user.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.util.DateUtil.now;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@WebMvcTest(PortfolioController.class)
public class PortfolioControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    private static final String BASE_URL = "/api/v1/portfolios";
    private User user;
    private List<PortfolioImage> portfolioImages;
    private Portfolio portfolioWithImages1;
    private Portfolio portfolioWithImages2;
    private Portfolio portfolio;
    @MockBean
    private PortfolioService portfolioService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private PortfolioLikeService portfolioLikeService;

    @BeforeEach
    void setup() {
        user = new User(1L, "minina", "minina1868@gmail.com", "minina", "123456789");
        Image image1 = new Image(1L, "파일1", 1000L, "testImage", "/file/endpointtest", LocalDateTime.now(), null, 0);
        Image image2 = new Image(2L, "파일2", 1000L, "testImage2", "/file/endpointtest", LocalDateTime.now(), null, 0);
        PortfolioImage portfolioImage1 = new PortfolioImage(1L, image1);
        PortfolioImage portfolioImage2 = new PortfolioImage(2L, image2);
        portfolioImages = Arrays.asList(portfolioImage1, portfolioImage2);

        portfolioWithImages1 = Portfolio.builder()
                .id(1L)
                .userId(1L)
                .content("테스트 포트폴리오1")
                .createdAt(now())
                .portfolioImages(portfolioImages)
                .build();

        portfolioWithImages2 = Portfolio.builder()
                .id(2L)
                .userId(1L)
                .content("테스트 포트폴리오2")
                .createdAt(now())
                .portfolioImages(portfolioImages)
                .build();   }

    private List<MultipartFile> getMockMultipartFileList() {
        MockMultipartFile mockFile1 = new MockMultipartFile("images", "testImage", "image/jpeg", "testImage".getBytes());
        MockMultipartFile mockFile2 = new MockMultipartFile("images", "testImage2", "image/jpeg", "testImage2".getBytes());
        return Arrays.asList(mockFile1, mockFile2);
    }
    @Test
    @WithMockUser
    @DisplayName("인증된 사용자는 게시글을 작성할 수 있다.")
    public void 인증된_사용자는_게시글을_작성할_수_있다() throws Exception {
        List<MultipartFile> mockImageFiles = getMockMultipartFileList();
        String content = "content";
        doNothing().when(portfolioService).savePortfolio(content, mockImageFiles);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(BASE_URL)
                                .file((MockMultipartFile) mockImageFiles.get(0))
                                .file((MockMultipartFile) mockImageFiles.get(1))
                                .param("content", content)
                                .with(csrf())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(portfolioService).savePortfolio(content, mockImageFiles);
    }


    @Test
    @WithAnonymousUser
    @DisplayName("인증되지않은 사용자는 포트폴리오를 작성할 수 없다.")
    public void createdPortfolioByUnauthorizedUser()  throws Exception{
        //given
        List<MultipartFile> mockImageFiles = getMockMultipartFileList();
        String content = "content";
        //when
        doNothing().when(portfolioService).savePortfolio(content, mockImageFiles);
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(BASE_URL)
                                .file((MockMultipartFile) mockImageFiles.get(0))
                                .file((MockMultipartFile) mockImageFiles.get(1))
                                .param("content", content)
                                .with(csrf())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser
    @DisplayName("이미지가 없는 포트폴리오를 작성할 수 없다.")
    public void createdPortfolioEmptyException() throws Exception {
        // given
        String content = "포트폴리오 설명";

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(BASE_URL)
                                .param("content", content)
                                .with(csrf())
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is5xxServerError());
    }
    @Test
    @WithMockUser
    @DisplayName("포트폴리오를 조회할 수 있다.")
    public void getPortfolioByIdTest() throws Exception {
        // given
        final Long portfolioId = 1L;
        given(portfolioService.getPortfolioById(portfolioId)).willReturn(portfolioWithImages1);

        // when
        ResultActions result = mockMvc.perform(
                get(BASE_URL + "/{portfolioId}", portfolioId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(portfolioId))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("모든 포트폴리오를 조회할 수 있다.")
    public void getAllPortfoliosTest() throws Exception {
        // given
        List<Portfolio> portfolios = new ArrayList<>();
        portfolios.add(portfolioWithImages1);
        portfolios.add(portfolioWithImages2);
        given(portfolioService.getAllPortfolios()).willReturn(portfolios);

        // when
        ResultActions result = mockMvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(portfolios.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(portfolios.get(1).getId()))
                .andDo(print());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("작성자가 아닌 사용자는 특정 포트폴리오를 수정할 수 없다.")
    public void updatePortfolioTestByUnauthorizedUser() throws Exception {
        // when
        mockMvc.perform(
                        put("/api/portfolios/{portfolioId}", 1L)
                                .content("새로운 컨텐츠") // 변경된 컨텐츠
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());

        // then
        verify(portfolioService, never()).updatePortfolio(eq(1L), any());
    }


    @Test
    @WithMockUser
    @DisplayName("특정 포트폴리오를 삭제할 수 있다.")
    public void deletePortfolioTest() throws Exception {
        // when
        mockMvc.perform(
                        delete(BASE_URL+"/{portfolioId}", 1L)
                                .with(csrf())
                )
                .andExpect(status().isOk());

        // then
        verify(portfolioService).deletePortfolio(1L);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("작성자가 아닌 사용자는 특정 포트폴리오를 삭제할 수 없다")
    public void deletePortfolioTestByUnauthorizedUser() throws Exception {
        //when
        doNothing().when(portfolioService).deletePortfolio(1L);
        //then
        mockMvc.perform(
                        delete("/api/portfolios/{portfolioId}", 1L)
                                .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }
}
