package com.example.quote.config;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jacksonの数値デシリアライズ挙動を厳格化するための設定クラス。
 *
 * <p>デフォルトのJacksonは、{@code Integer}/{@code int} 等の整数型フィールドに対して
 * {@code "driverAge": 35.5} のような小数値のJSONを渡された場合、エラーにせず小数部を
 * 静默截断（黙って切り捨てて）35として受理してしまう。これは
 * 「年齢35.5歳の見積が35歳として保存される」という利用者に気付かれない不正データを
 * 生み出す重大な入力検証漏れであるため、{@link CoercionAction#Fail} を明示的に設定し、
 * 400 VALIDATION_ERROR（{@code HttpMessageNotReadableException} 経由）で拒否する。
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer integerCoercionCustomizer() {
        return builder -> builder.postConfigurer(objectMapper ->
                objectMapper.coercionConfigFor(LogicalType.Integer)
                        .setCoercion(CoercionInputShape.Float, CoercionAction.Fail));
    }
}
