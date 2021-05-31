package socialnetwork.domain;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{
    private final String firstName;
    private final String lastName;
    private final List<User> friends;
    private Image profilePic;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<User>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void addFriend(User user){
        friends.add(user);
    }

    public void setPicture(Image img){profilePic = img;}
    public Image getPicture(){ return profilePic;}

    private String friendsToString(){
        String res="";
        for(User a: friends) {
            String b = a.getFirstName() + " " + a.getLastName()+"; ";
            res = res.concat(b);
        }
        return res;
    }

    @Override
    public String toString() {
        return "Utilizator{" +"id="+getId()+
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friendsToString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }

    public void removeFriend(Long id) {
        for(User u:friends)
            if(u.getId().equals(id)){
                friends.remove(u);
                break;
            }
    }
}