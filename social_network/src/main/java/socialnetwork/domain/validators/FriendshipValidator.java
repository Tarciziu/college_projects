package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getFriendship().getLeft().equals(entity.getFriendship().getRight()))
            throw new ValidationException("Users can't add themselves as a friend");

    }
}
