package org.talky.platform.support.auth;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.talky.auth.UserRole;

import java.util.List;
import java.util.Set;

/**
 * API 경로별 접근 권한 중앙 관리.
 *
 * AntPathMatcher 패턴 규칙:
 * - * : 한 경로 세그먼트만 매칭 (/users/* → /users/abc 매칭, /users/a/b 안 됨)
 * - ** : 하위 경로 전체 매칭 (/users/** → /users/a/b/c 매칭).
 *        되도록 사용하지 말고, *를 사용해서 명시적으로 관리할 것
 *
 * - 정확한 경로가 패턴보다 우선 매칭된다.
 * - 새 API 추가 시 반드시 등록할 것.
 * - 미등록 경로는 ADMIN 권한만 접근 가능.
 */
public class ApiAccessRegistry {

    private static final Set<UserRole> DEFAULT_ROLES = Set.of(UserRole.ADMIN);
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<Route> PUBLIC_ROUTES = List.of(
            Route.of(HttpMethod.GET, "/api/v1/auth/check-login-id"),
            Route.of(HttpMethod.POST, "/api/v1/auth/register"),
            Route.of(HttpMethod.POST, "/api/v1/auth/login"),
            Route.of(HttpMethod.POST, "/api/v1/auth/refresh"),
            Route.of(HttpMethod.POST, "/api/v1/internal/chats/create-check")
    );

    private static final List<ProtectedRoute> PROTECTED_ROUTES = List.of(
            ProtectedRoute.of(HttpMethod.POST, "/api/v1/auth/logout", UserRole.USER, UserRole.ADMIN),
            ProtectedRoute.of(HttpMethod.GET, "/api/v1/users/@me", UserRole.USER, UserRole.ADMIN),
            ProtectedRoute.of(HttpMethod.GET, "/api/v1/users/*/profile", UserRole.USER, UserRole.ADMIN)
    );

    public static boolean isPublic(String method, String requestUri) {
        // 정확한 경로 우선 매칭
        for (Route route : PUBLIC_ROUTES) {
            if (route.method.name().equals(method) && route.path.equals(requestUri)) {
                return true;
            }
        }

        // 패턴 매칭 fallback
        return PUBLIC_ROUTES.stream()
                .anyMatch(route -> route.matches(method, requestUri));
    }

    public static Set<UserRole> getAllowedRoles(String method, String requestUri) {
        // 정확한 경로 우선 매칭
        for (ProtectedRoute route : PROTECTED_ROUTES) {
            if (route.method.name().equals(method) && route.path.equals(requestUri)) {
                return route.allowedRoles;
            }
        }

        // 패턴 매칭 fallback
        for (ProtectedRoute route : PROTECTED_ROUTES) {
            if (route.matches(method, requestUri)) {
                return route.allowedRoles;
            }
        }

        return DEFAULT_ROLES;
    }

    private record Route(HttpMethod method, String path) {

        static Route of(HttpMethod method, String path) {
            return new Route(method, path);
        }

        boolean matches(String method, String requestUri) {
            return this.method.name().equals(method)
                    && pathMatcher.match(this.path, requestUri);
        }
    }

    private record ProtectedRoute(HttpMethod method, String path, Set<UserRole> allowedRoles) {

        static ProtectedRoute of(HttpMethod method, String path, UserRole... roles) {
            return new ProtectedRoute(method, path, Set.of(roles));
        }

        boolean matches(String method, String requestUri) {
            return this.method.name().equals(method)
                    && pathMatcher.match(this.path, requestUri);
        }
    }
}
