package org.sparta.delivery.global.infrastructure.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("1. 회원 관리")
                .displayName("회원 API")
                .pathsToMatch("/v1/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi storeApi() {
        return GroupedOpenApi.builder()
                .group("2. 매장 및 상품 관리")
                .displayName("매장/상품 API")
                // 매장(/v1/stores)과 상품(/v1/stores/**/products)을 하나의 그룹으로 묶음
                .pathsToMatch("/v1/stores/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("3. 주문 관리")
                .displayName("주문 API")
                .pathsToMatch("/v1/order/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "BearerAuth";

        // 전역 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // JWT SecurityScheme 설정
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("발급받은 Access Token을 입력하세요. (Bearer 키워드 제외)"));

        return new OpenAPI()
                .info(new Info()
                        .title("배달 서비스 REST API")
                        .description("스파르타 배달 서비스 프로젝트의 API 명세서입니다.")
                        .version("1.0")
                        .contact(new Contact().email("yonggyo00@kakao.com")))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}