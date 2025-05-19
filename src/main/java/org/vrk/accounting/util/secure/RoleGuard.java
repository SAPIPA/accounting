package org.vrk.accounting.util.secure;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.vrk.accounting.domain.enums.Role;

public class RoleGuard {
    /**
     * Бросает 403, если actualRole не входит в allowedRoles.
     */
    public static void require(Role actualRole, Role... allowedRoles) {
        for (Role r : allowedRoles) {
            if (r == actualRole) {
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Required one of roles " + java.util.Arrays.toString(allowedRoles) +
                        ", but was " + actualRole);
    }
}
