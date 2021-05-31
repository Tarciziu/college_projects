package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if(!entity.getFirstName().matches("^[a-zA-Z-]+$"))
            throw new ValidationException("First name must contain only letters");
        if(!entity.getLastName().matches("^[a-zA-Z]+$"))
            throw new ValidationException("First name must contain only letters");

    }
}
