package org.sparta.delivery.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.presentation.exception.BadRequestException;
import org.sparta.delivery.global.presentation.exception.ErrorResponse;
import org.sparta.delivery.user.application.*;
import org.sparta.delivery.user.presentation.dto.*;
import org.sparta.delivery.user.presentation.validator.RegisterUserValidator;
import org.sparta.delivery.user.presentation.validator.UpdateUserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
@Tag(name = "회원 API", description = "회원의 인증/인가, 가입, 수정 및 권한 관리 기능을 제공합니다.")
public class UserController {
    private final GenerateTokenService tokenService;
    private final RegisterUserService registerService;
    private final UpdateUserService updateService;

    @Operation(summary = "인증 토큰 발급", description = "사용자 계정 정보를 인증하여 Access Token 및 Refresh Token을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발급 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (아이디/비밀번호 불일치)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/token")
    public TokenResponse generateToken(@Valid @RequestBody TokenRequest req) {
        TokenInfo tokenInfo = tokenService.generate(req.username(), req.password());
        return new TokenResponse(tokenInfo.access_token(), tokenInfo.expires_in(),
                tokenInfo.refresh_expires_in(), tokenInfo.refresh_token(), tokenInfo.token_type());
    }

    @Operation(summary = "내 프로필 정보 조회", description = "현재 로그인한 사용자의 상세 정보를 조회합니다.")
    @Parameter(name = "Authorization", description = "Bearer {token}", required = true, in = ParameterIn.HEADER)
    @GetMapping("/profile")
    public UserResponse getProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Map<String, Object> claims = jwt.getClaims();
        String name = (String) claims.getOrDefault("family_name", "") + (String) claims.getOrDefault("given_name", "");

        return new UserResponse(userId, (String) claims.getOrDefault("preferred_username", ""),
                (String) claims.getOrDefault("email", ""), name, (String) claims.getOrDefault("mobile", ""));
    }

    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패 / 중복 아이디 등")
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody RegisterUserRequest req) {
        new RegisterUserValidator().validate(req);
        UserRegister dto = UserRegister.builder()
                .username(req.username()).password(req.password())
                .email(req.email()).firstName(req.firstName())
                .lastName(req.lastName()).mobile(req.mobile()).build();
        registerService.register(dto);
    }

    @Operation(summary = "회원 정보 수정", description = "이메일, 성명, 연락처 등 프로필 정보를 변경합니다.")
    @PatchMapping("/profile")
    public void updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserRequest req) {
        new UpdateUserValidator().validateUpdateProfile(req);
        UUID userId = UUID.fromString(jwt.getSubject());
        UserUpdate dto = UserUpdate.builder()
                .email(req.email()).firstName(req.firstName())
                .lastName(req.lastName()).mobile(req.mobile()).build();
        updateService.update(userId, dto);
    }

    @Operation(summary = "비밀번호 변경", description = "기존 비밀번호를 확인하고 새로운 비밀번호로 교체합니다.")
    @PatchMapping("/password")
    public void changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest req) {
        new UpdateUserValidator().validateChangePassword(req);
        updateService.updatePassword(UUID.fromString(jwt.getSubject()), req.password());
    }

    @Operation(summary = "사용자 권한(Role) 변경", description = "[관리자 전용] 특정 사용자에게 새로운 역할을 부여합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "권한 변경 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자 아님)")
    })
    @PatchMapping("/role")
    public void changeRole(@AuthenticationPrincipal Jwt jwt,
                           @Schema(description = "변경할 권한 리스트", example = "[\"OWNER\", \"MANAGER\"]")
                           @RequestBody List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new BadRequestException("변경할 ROLE을 전송해 주세요.");
        }
        updateService.updateUserRole(UUID.fromString(jwt.getSubject()), roles);
    }

    @Operation(summary = "회원 상세 정보 조회", description = "[관리자 전용] 회원 고유 ID를 통해 특정 회원의 프로필을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/profile/{userId}")
    public UserResponse userProfile(@Parameter(description = "회원 고유 식별자 (UUID)", required = true)
                                    @PathVariable("userId") String userId) {
        // 실제 구현 시 서비스 호출 로직 추가 필요
        return null;
    }
}