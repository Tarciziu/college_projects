package socialnetwork.domain;

import java.time.LocalDate;


public class Friendship extends Entity<Long> {

    private final LocalDate date;
    private final Tuple<Long,Long> friendship;
    private final String state;

    public Friendship(Long id1, Long id2,LocalDate date) {
        friendship = new Tuple<>(id1, id2);
        this.date = date;
        state="PENDING";
    }

    public Friendship(Long id1, Long id2, LocalDate date, String state) {
        friendship = new Tuple<>(id1, id2);
        this.date = date;
        this.state = state;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDate getDate() {
        return date;
    }


    /**
     * Gets state of the Friendship.
     *
     * @return the state of friendship
     */
    public String getState() { return state; }

    public Tuple<Long, Long> getFriendship() {
        return friendship;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                friendship.getLeft()+
                ", " +
                friendship.getRight()+
                ", date:" + date +
                ", state:" + state +
                '}';
    }
}
