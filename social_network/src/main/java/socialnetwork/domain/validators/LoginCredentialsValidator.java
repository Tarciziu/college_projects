package socialnetwork.domain.validators;

import socialnetwork.domain.LoginCredentials;

public class LoginCredentialsValidator implements Validator<LoginCredentials> {
    @Override
    public void validate(LoginCredentials entity) throws ValidationException {
        if(!entity.getId().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
            throw new ValidationException("Invalid email adress");
    }
}
