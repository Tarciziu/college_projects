package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDate;
import java.util.List;

import static socialnetwork.utils.Constants.DATE_FORMATTER;

public class FriendshipFile extends AbstractFileRepository<Long, Friendship> {

    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    public Friendship extractEntity(List<String> attributes){
        LocalDate date = LocalDate.parse(attributes.get(3));
        Friendship f = new Friendship(Long.parseLong(attributes.get(1)),
                Long.parseLong(attributes.get(2)),
                date);
        f.setId(Long.parseLong(attributes.get(0)));
        return f;
    }

    public String createEntityAsString(Friendship friendship){
        return friendship.getId().toString()+';'+friendship.getFriendship().getLeft()+';'+
                friendship.getFriendship().getRight()+';'+friendship.getDate().format(DATE_FORMATTER);
    }
}
