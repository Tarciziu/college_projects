import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.Friendship;

import java.time.LocalDate;

public class FriendshipTest {
    private static Friendship friendship;
    @BeforeAll
    public static void setUp(){
        friendship = new Friendship((long)0,(long)1, LocalDate.now(),"ACCEPTED");
    }

    @Test
    public void test(){
        Assertions.assertEquals(0,friendship.getFriendship().getLeft());
        Assertions.assertEquals(1,friendship.getFriendship().getRight());
        Assertions.assertEquals("ACCEPTED",friendship.getState());
    }
}
