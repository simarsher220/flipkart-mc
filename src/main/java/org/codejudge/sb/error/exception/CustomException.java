package org.codejudge.sb.error.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
public class CustomException extends Throwable {

    private HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
