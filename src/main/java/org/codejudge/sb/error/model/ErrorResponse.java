package org.codejudge.sb.error.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class ErrorResponse {

    private String status;
    private String reason;

    public ErrorResponse(String reason) {
        this.status = "failure";
        this.reason = reason;
    }
}
