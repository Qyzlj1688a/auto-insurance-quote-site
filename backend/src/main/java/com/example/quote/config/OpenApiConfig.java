package com.example.quote.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 定義情報を生成するための設定クラス。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 実際の管理者API認証方式はJWT（Authorization: Bearer <token>）であるため、
        // Swagger UIの「Authorize」ダイアログもBearer Token入力形式で表示されるように
        // bearerAuth（HTTP Bearer / JWT）として定義する。以前はtype=http, scheme=basicと
        // 誤って設定されており、Swagger UI上でBasic認証（ユーザー名・パスワード）の
        // 入力欄が表示されてしまい、実際のJWTトークンを貼り付けても認証できない状態だった。
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("自動車保険見積サイト API")
                        .version("1.0")
                        .description("自動車保険の見積計算および管理者機能のAPI仕様書\n\n"
                                + "管理者向けAPI（/api/admin/**、/api/master/**）はJWT認証が必要です。"
                                + "「Authorize」ボタンから、POST /api/admin/login で取得したtoken（JWT文字列そのもの。"
                                + "\"Bearer \"は付与不要）を入力してください。"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
