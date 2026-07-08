package com.example.quote.validation;

/**
 * バリデーションのグループ化用マーカーインターフェース群。
 *
 * <p>リクエストパラメータの検証ルールを条件（作成、検索など）によって切り替えるために使用します。
 */
public final class ValidationGroups {

    private ValidationGroups() {
    }

    public interface Create {
    }

    public interface Search {
    }
}
