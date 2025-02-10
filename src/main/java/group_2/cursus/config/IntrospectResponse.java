package group_2.cursus.config;

public class IntrospectResponse {

    private boolean valid;

    public IntrospectResponse() {

    }

    public IntrospectResponse(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
