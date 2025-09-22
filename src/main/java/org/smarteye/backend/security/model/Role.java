package org.smarteye.backend.security.model;

/** Роль пользователя для авторизации. */
public enum Role {
    ADMIN,       // полный доступ
    SUPERVISOR,  // просмотр отчетов/решений, ограниченное управление
    OPERATOR,    // оператор линии
    DEVICE       // технический пользователь (если понадобится)
}
