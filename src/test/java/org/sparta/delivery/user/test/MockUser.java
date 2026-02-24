package org.sparta.delivery.user.test;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
public @interface MockUser {
    String username() default "testuser";
    String[] roles() default {"USER"};
    String clientId() default "spring-app";
    String subject() default "009f60a3-619f-4cc5-9577-390878c4856e";
    String email() default "testuser@test.org";
    String name() default "테스트사용자";
    String mobile() default "01010001000";
    String issuer() default "http://localhost:3300/auth/realms/test";
    long issuedAt() default 0L;
    long expiresAt() default 3600L;
}