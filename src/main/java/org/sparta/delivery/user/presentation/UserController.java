package org.sparta.delivery.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sparta.delivery.global.presentation.exception.BadRequestException;
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
@Tag(name="회원 API", description = "회원의 인증/인가, 가입, 수정 기능 제공")
public class UserController {
    private final GenerateTokenService tokenService;
    private final RegisterUserService registerService;
    private final UpdateUserService updateService;

    // 토큰 발급
    @Operation(summary = "인증 토근 발급", description = "username, password 인증을 통해서 승인된 회원이 접근할 수 있는 토큰을 발급해 줍니다.")
    @PostMapping("/token")
    public TokenResponse generateToken(@Valid @RequestBody TokenRequest req) {
        TokenInfo tokenInfo = tokenService.generate(req.username(), req.password());

        return new TokenResponse(tokenInfo.access_token(),
                tokenInfo.expires_in(),
                tokenInfo.refresh_expires_in(),
                tokenInfo.refresh_token(),
                tokenInfo.token_type());
    }

    // 로그인한 사용자 정보 조회
    @GetMapping("/profile")
    @Parameter(name="Authorization", description = "인증 방식 및 토큰을 위한 헤더", example = "Bearer 인증토큰", in = ParameterIn.HEADER, schema = @Schema(format = "string"))
    public UserResponse getProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Map<String, Object> claims = jwt.getClaims();
        String name = claims.getOrDefault("family_name", "") + (String)claims.getOrDefault("given_name", "");

        return new UserResponse(userId,
                (String)claims.getOrDefault("preferred_username", ""),
                (String)claims.getOrDefault("email", ""),
                name,
                (String)claims.getOrDefault("mobile", ""));
    }

    // 회원 가입
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody RegisterUserRequest req) {

        new RegisterUserValidator().validate(req); // 추가 검증 처리

        UserRegister dto = UserRegister.builder()
                .username(req.username())
                .password(req.password())
                .email(req.email())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .mobile(req.mobile())
                .build();
        registerService.register(dto);
    }

    // 회원정보 수정
    @PatchMapping("/profile")
    public void updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserRequest req) {

        new UpdateUserValidator().validateUpdateProfile(req); // 추가 검증 처리

        UUID userId = UUID.fromString(jwt.getSubject());
        UserUpdate dto = UserUpdate
                .builder()
                .email(req.email())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .mobile(req.mobile())
                .build();
        updateService.update(userId, dto);
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public void changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest req) {
        new UpdateUserValidator().validateChangePassword(req); // 추가 검증 처리

        updateService.updatePassword(UUID.fromString(jwt.getSubject()), req.password());
    }

    // 새 Role 부여
    @PatchMapping("role")
    @PreAuthorize("hasRole('ADMIN')")
    @Parameter(name="Authorization", description = "인증 방식 및 토큰을 위한 헤더", example = "Bearer 인증토큰", in = ParameterIn.HEADER, schema = @Schema(format = "string"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role 변경 성공"),
            @ApiResponse(responseCode = "401", description = "관리자가 아닌 경우")
    })
    public void changeRole(@AuthenticationPrincipal Jwt jwt, @RequestBody List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new BadRequestException("변경할 ROLE을 전송해 주세요.");
        }

        updateService.updateUserRole(UUID.fromString(jwt.getSubject()), roles);
    }

    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자 기능] 회원 ID로 회원 정보 조회")
    @Parameters({
            @Parameter(name="Authorization", description = "인증 방식 및 토큰을 위한 헤더", example = "Bearer 인증토큰", in = ParameterIn.HEADER, schema = @Schema(format = "string")),
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "회원 UID")
    })
    public UserResponse userProfile(@PathVariable("userId") String userId) {

        return null;
    }
}
