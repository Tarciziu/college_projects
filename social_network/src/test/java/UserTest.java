import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.User;

public class UserTest {
    private static User user;
    private static User friend;

    @BeforeAll
    public static void setUp(){
        // tested user
        user = new User("Ionut","Aprogramatoarei");
        user.setId(Long.parseLong("10000"));

        // user's friend
        friend = new User("Alex","Anutei");
        friend.setId(Long.parseLong("10001"));
    }

    @Test
    public void testFirstName(){
        Assertions.assertEquals("Ionut", user.getFirstName());
    }

    @Test
    public void testLastName(){
        Assertions.assertEquals("Aprogramatoarei", user.getLastName());
    }

    @Test
    public void testId(){
        Assertions.assertEquals(10000, user.getId());
    }

    @Test
    public void testAddAFriend(){
        user.addFriend(friend);
        Assertions.assertEquals(user.getFriends().get(0).getId(),friend.getId());
        Assertions.assertEquals(user.getFriends().size(),1);
    }

    @Test
    public void testRemoveFriend(){
        user.removeFriend(friend.getId());
        Assertions.assertEquals(user.getFriends().size(),0);
    }
}
