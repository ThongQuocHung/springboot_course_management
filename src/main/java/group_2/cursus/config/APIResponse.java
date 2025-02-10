package group_2.cursus.config;

import com.fasterxml.jackson.annotation.JsonInclude;

// nếu trường đó null thì json trả về không bao gồm
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    private String message;
    private T data;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
