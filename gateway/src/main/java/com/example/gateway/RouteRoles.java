package com.example.gateway;

import java.util.List;
import java.util.Map;

public class RouteRoles {

    private static final Map<String, List<String>> routeRoleMap = Map.of(
            "/admin/**", List.of("ADMIN"),
            "/user/**", List.of("USER", "ADMIN")
            // Add more routes and their required roles here
    );

    public static List<String> requiredRolesForPath(String path) {
        for (Map.Entry<String, List<String>> entry : routeRoleMap.entrySet()) {
            String route = entry.getKey().replace("/**", "");
            if (path.startsWith(route)) {
                return entry.getValue();
            }
        }
        return null; // No role-based restriction
    }
}