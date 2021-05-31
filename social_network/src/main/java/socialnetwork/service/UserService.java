package socialnetwork.service;

import jdk.vm.ci.meta.Local;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.LoginCredentials;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Constants;
import socialnetwork.utils.Graph;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class UserService {
    private final Repository<Long, User> repo;
    private final Repository<Long, Friendship> friendships;
    private final Repository<Long, Message> messages;
    private final Repository<String, LoginCredentials> loginCreds;
    private Long friendshipID;
    private User loggedUser=null;

    public UserService(Repository<Long, User> repo,
                       Repository<Long, Friendship> friendships,
                       Repository<Long, Message> messages, Repository<String, LoginCredentials> loginCreds) {
        this.repo = repo;
        this.friendships = friendships;
        this.messages = messages;
        this.loginCreds = loginCreds;
        //setFriendships();
        friendshipID = getLastID();
    }

    public Optional<User> addUser(User messageTask) {
        try{return repo.save(messageTask);}
        catch(ValidationException e){
            throw e;
        }
    }

    public Boolean tryLogin(String email, String password){
        Optional<LoginCredentials> userLogRequest = loginCreds.findOne(email);
        if(!userLogRequest.isPresent())
            return Boolean.FALSE;
        Optional<User> userOptional = repo.findOne(userLogRequest.get().getUserID());
        if(!userOptional.isPresent())
            return Boolean.FALSE;
        loggedUser = userOptional.get();
        return Boolean.TRUE;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void updateUser(User user){repo.update(user);}

    /**
     * @return last id of friendships
     */
    private Long getLastID(){
        Long max;
        max=Long.parseLong("0");
        for(Friendship f:friendships.findAll())
            if(f.getId()>max)
                max=f.getId();
        return max+1;
    }

    /**
     * @param id1 Integer - 1st user's id
     * @param id2 Integer - 2nd user's id
     * @return users' friendship
     */
    public Optional<Friendship> addFriendship(Long id1, Long id2){
        Friendship f = new Friendship(id1,id2, LocalDate.now());
        f.setId(friendshipID);
        friendshipID++;
        try{
        return friendships.save(f);}
        catch(ValidationException e){
            throw e;}
    }

    public Optional<Friendship> addFriend(Long id){
        Friendship f = new Friendship(loggedUser.getId(),id, LocalDate.now());
        f.setId(friendshipID);
        friendshipID++;
        try{
            return friendships.save(f);}
        catch(ValidationException e){
            throw e;}
    }

    private Long getLastIDMessage(){
        Long max;
        max=Long.parseLong("0");
        for(Message m:messages.findAll())
            if(m.getId()>max)
                max=m.getId();
        return max+1;
    }

    /**
     * Sends a message from one users to 1+ other user(s)
     * @param from message sender
     * @param to message receivers
     * @param message message text
     */
    public void sendMessage(Long from, List<Long> to, String message){
        for(Long x : to){
            Message m = new Message(message,from);
            Optional<User> user = repo.findOne(x);
            if(user.isPresent()){
                m.addReceiver(x);
                m.setId(getLastIDMessage());
                messages.save(m);
            }
        }
    }

    public Optional<Message> sendMessage(Long from, Long to, String message){
        Message m = new Message(message,from);
        Optional<User> user = repo.findOne(to);
        if(user.isPresent()){
            m.addReceiver(to);
            m.setId(getLastIDMessage());
            return messages.save(m);
        }
        return Optional.empty();
    }

    /**
     * Replies to a message
     * @param from  sender's id
     * @param to receiver's id
     * @param message reply's text
     */
    public void replyMessage(Long from, Long to, String message){
        Message m = new Message(message,from);
        Optional<User> user = repo.findOne(to);
        if(user.isPresent()){
            m.addReceiver(to);
            m.setId(getLastIDMessage());
            messages.save(m);
        }
    }

    private void setFriendships(){
        for(Friendship fr: friendships.findAll()) {
            repo.findOne(fr.getFriendship().getLeft()).get().addFriend(repo.findOne(fr.getFriendship().getRight()).get());
            repo.findOne(fr.getFriendship().getRight()).get().addFriend(repo.findOne(fr.getFriendship().getLeft()).get());
        }
    }

    public Iterable<User> getAll(){
        return repo.findAll();
    }
    public Iterable<Friendship> getFriendships(){return friendships.findAll();}
    public Friendship getFriendshipRequest(Long idUser){
        return StreamSupport.stream(friendships.findAll().spliterator(),false)
                .filter(x->{
                    if(x.getFriendship().getLeft().equals(idUser) && x.getFriendship().getRight().equals(loggedUser.getId()))
                        return true;
                    return false;
                })
                .collect(Collectors.toList()).get(0);
    }

    public Optional<Friendship> updateFriendship(Long id1,Long id2,String state){
        Friendship f = new Friendship(id1,id2,LocalDate.now(),state);
        return friendships.update(f);
    }

    public Optional<Friendship> updateFriendship(Long id,String state){
        Friendship f = new Friendship(loggedUser.getId(),id,LocalDate.now(),state);
        return friendships.update(f);
    }

    /**
     * Deletes an user
     * @param ID user's id
     * @return deteled user
     */
    public Optional<User> removeUser(Long ID){
        // for file
        /*for(int i=0;i<repo.findOne(ID).get().getFriends().size();i++)
                if(removeFriendship(ID,repo.findOne(ID).get().getFriends().get(i).getId()))
                    i--;*/
        // for database
        for(Friendship f:friendships.findAll())
            if(f.getFriendship().getLeft().equals(ID)) {
                friendships.delete(f.getId());
            }
            else if(f.getFriendship().getRight().equals(ID)) {
                friendships.delete(f.getId());
            }
        return repo.delete(ID);
    }

    /**
     * Deletes a friendship between 2 users
     * @param id1
     * @param id2
     * @return
     */
    public boolean removeFriendship(Long id1, Long id2) {
        boolean ok = false;
        for (Friendship f : friendships.findAll()) {
            if (f.getFriendship().getLeft().equals(id1) && f.getFriendship().getRight().equals(id2)) {
                friendships.delete(f.getId());
                ok = true;
                break;
            } else if (f.getFriendship().getLeft().equals(id2) && f.getFriendship().getRight().equals(id1)) {
                friendships.delete(f.getId());
                ok = true;
                break;
            }
        }
        //repo.findOne(id1).get().removeFriend(id2);
        //repo.findOne(id2).get().removeFriend(id1);
        return ok;
    }

    public List<String> getFriendsOf(Long id){
        Iterable<Friendship> list = friendships.findAll();
        return StreamSupport.stream(list.spliterator(),false)
                .filter(x->x.getState().equals("ACCEPTED") &&
                        (x.getFriendship().getLeft().equals(id)||x.getFriendship().getRight().equals(id)))
                .map(x->{
                    if(id.equals(x.getFriendship().getLeft())){
                        User u = repo.findOne(x.getFriendship()
                                .getLeft()).get();
                        return u.getFirstName()+" | "+
                        u.getLastName()+" | "+
                        x.getDate().format(Constants.DATE_FORMATTER);}
                else{
                        User u = repo.findOne(x.getFriendship()
                                .getRight()).get();
                    return u.getFirstName()+" | "+
                                u.getLastName()+" | "+
                                x.getDate().format(Constants.DATE_FORMATTER);}})
                .collect(Collectors.toList());
    }

    public List<User> getFriends(){
        Iterable<Friendship> list = friendships.findAll();
        Long id = loggedUser.getId();
        return StreamSupport.stream(list.spliterator(),false)
                .filter(x->x.getState().equals("ACCEPTED") &&
                        (x.getFriendship().getLeft().equals(id)||x.getFriendship().getRight().equals(id)))
                .map(x->{
                    if(id.equals(x.getFriendship().getLeft())){
                        User u = repo.findOne(x.getFriendship()
                                .getRight()).get();
                        return u;}
                    else{
                        User u = repo.findOne(x.getFriendship()
                                .getLeft()).get();
                        return u;}})
                .collect(Collectors.toList());
    }

    public List<String> getFriendsByMonth(Long id,Integer month){
        Iterable<Friendship> list = friendships.findAll();
        return StreamSupport.stream(list.spliterator(),false)
                .filter(x->x.getState().equals("ACCEPTED") &&
                        (x.getFriendship().getLeft().equals(id)||x.getFriendship().getRight().equals(id)) &&
                        x.getDate().getMonth().equals(Month.of(month)))
                .map(x->{
                    if(id.equals(x.getFriendship().getLeft())){
                        User u = repo.findOne(x.getFriendship()
                                .getLeft()).get();
                        return u.getFirstName()+" | "+
                                u.getLastName()+" | "+
                                x.getDate().format(Constants.DATE_FORMATTER);}
                    else{
                        User u = repo.findOne(x.getFriendship()
                                .getRight()).get();
                        return u.getFirstName()+" | "+
                                        u.getLastName()+" | "+
                                        x.getDate().format(Constants.DATE_FORMATTER);}})
                .collect(Collectors.toList());
    }

    public List<String> getConversationOf(Long id1, Long id2){
        List<String> conversation = new ArrayList<>();
        Iterable<Message> conv = StreamSupport.stream(messages.findAll().spliterator(),false)
                .filter(x->((x.getSender().equals(id1) && x.getReceivers().get(0).equals(id2))
                        ||
                        (x.getReceivers().get(0).equals(id1)
                        && x.getSender().equals(id2))))
                .sorted((x,y)->x.getDate().compareTo(y.getDate()))
                .collect(Collectors.toList());
        for(Message message : conv)
            conversation.add(repo.findOne(message.getSender()).get().getFirstName()+
                    " "+repo.findOne(message.getSender()).get().getLastName()+
                    ": "+ message.getMessage());

        return conversation;
    }

    public List<Message> getConversationWith(Long id){
        Long id1 = loggedUser.getId();
        List<Message> conv = StreamSupport.stream(messages.findAll().spliterator(),false)
                .filter(x->((x.getSender().equals(id1) && x.getReceivers().get(0).equals(id))
                        ||
                        (x.getReceivers().get(0).equals(id1)
                                && x.getSender().equals(id))))
                .sorted((x,y)->x.getDate().compareTo(y.getDate()))
                .collect(Collectors.toList());

        return conv;
    }

    public long communities(){
        Graph g = new Graph();
        for(User u: repo.findAll()){
            g.addVertex(u.getId());
        }
        for(Friendship f: friendships.findAll()){
            g.addEdge(f.getFriendship().getLeft(),f.getFriendship().getRight());
        }
        return g.connectedComponents();
    }

    public String sociability(){
        Graph g = new Graph();
        for(User u: repo.findAll()){
            g.addVertex(u.getId());
        }
        for(Friendship f: friendships.findAll()){
            g.addEdge(f.getFriendship().getLeft(),f.getFriendship().getRight());
        }
        List<Long> community=new ArrayList<>();
        for(Long x:g.longestPath())
            if(!community.contains(x))
                community.add(x);
        String s="";
        for(Long x:community){
            User u = repo.findOne(x).get();
            s=s.concat(u.getFirstName()+" "+u.getLastName()+"; ");
        }
        return s;
    }

    public Iterable<Object> showFriendRequests(Long id) {
        Iterable<Friendship> list = friendships.findAll();
        return StreamSupport.stream(list.spliterator(),false)
                .filter(x->x.getState().equals("PENDING") &&
                        (x.getFriendship().getLeft().equals(id)||x.getFriendship().getRight().equals(id)))
                .map(x->{
                    if(!id.equals(x.getFriendship().getLeft())){
                        User u = repo.findOne(x.getFriendship()
                                .getLeft()).get();
                        return u.getId()+" | "+u.getFirstName()+" | "+
                                u.getLastName()+" | "+
                                x.getDate().format(Constants.DATE_FORMATTER);}
                    else{
                        User u = repo.findOne(x.getFriendship()
                                .getRight()).get();
                        return u.getId()+" | "+u.getFirstName()+" | "+
                                u.getLastName()+" | "+
                                x.getDate().format(Constants.DATE_FORMATTER);}})
                .collect(Collectors.toList());
    }
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    public List<String> getMyConversations(Long id){
        return StreamSupport.stream(messages.findAll().spliterator(),false)
                .filter(x->(x.getSender().equals(id)||x.getReceivers().get(0).equals(id)))
                .map(x->{
                    if(x.getSender().equals(id)){
                        User user = repo.findOne(x.getReceivers().get(0)).get();
                        return user.getId()+" | "+user.getFirstName()+" "+user.getLastName();}
                    else{
                        User user = repo.findOne(x.getSender()).get();
                        return user.getId()+" | "+user.getFirstName()+" "+user.getLastName();}
                })
                .filter(distinctByKey(String::toString))
                .collect(Collectors.toList());
    }
    public List<User> getMyConversations(){
        Long id = loggedUser.getId();
        return StreamSupport.stream(messages.findAll().spliterator(),false)
                .filter(x->(x.getSender().equals(id)||x.getReceivers().get(0).equals(id)))
                .map(x->{
                    if(x.getSender().equals(id)){
                        User user = repo.findOne(x.getReceivers().get(0)).get();
                        return user;}
                    else{
                        User user = repo.findOne(x.getSender()).get();
                        return user;}
                })
                .filter(distinctByKey(User::getId))
                .collect(Collectors.toList());
    }

    public List<User> getFriendRequests() {
        Iterable<Friendship> list = friendships.findAll();
        Long id = loggedUser.getId();
        return StreamSupport.stream(list.spliterator(),false)
                .filter(x->x.getState().equals("PENDING") &&
                        (x.getFriendship().getRight().equals(id)))
                .map(x->{
                    User u = repo.findOne(x.getFriendship()
                            .getLeft()).get();
                    return u;
                })
                .collect(Collectors.toList());
    }

    public List<User> getFriendRequestsSent() {
        Iterable<Friendship> list = friendships.findAll();
        Long id = loggedUser.getId();
        return StreamSupport.stream(list.spliterator(),false)
                .filter(x->x.getState().equals("PENDING") &&
                        (x.getFriendship().getLeft().equals(id)))
                .map(x->{
                    User u = repo.findOne(x.getFriendship()
                            .getRight()).get();
                    return u;
                })
                .collect(Collectors.toList());
    }

    public List<String> raportActivitati(LocalDate date1, LocalDate date2){
        Long id = loggedUser.getId();
        List<String> prietenii = StreamSupport.stream(friendships.findAll().spliterator(),false)
                .filter(x->x.getState().equals("ACCEPTED") &&
                        (x.getFriendship().getLeft().equals(id)||x.getFriendship().getRight().equals(id)) &&
                        (x.getDate().isAfter(date1)&&x.getDate().isBefore(date2)))
        .map(x->{
            if(x.getFriendship().getLeft().equals(id)){
                User u = repo.findOne(x.getFriendship().getRight()).get();
                return new String(u.getLastName()+" "+u.getFirstName()+" "+x.getDate());
            }
            User u = repo.findOne(x.getFriendship().getLeft()).get();
            return new String(u.getLastName()+" "+u.getFirstName()+" "+x.getDate().format(Constants.DATE_FORMATTER));
        })
        .collect(Collectors.toList());

        List<String> mesaje = StreamSupport.stream(messages.findAll().spliterator(),false)
                .filter(x->(x.getSender().equals(id)&&x.getReceivers().get(0).equals(id)))
                .map(x-> {
                    if (x.getSender().equals(id)) {
                        User u = repo.findOne(x.getReceivers().get(0)).get();
                        return new String(u.getFirstName() + " " + u.getLastName() + ": "
                                + x.getMessage());
                    }
                    if (x.getReceivers().get(0).equals(id)) {
                        User u = repo.findOne(x.getSender()).get();
                        return new String(u.getFirstName() + " " + u.getLastName() + ": "
                                + x.getMessage());
                    }
                    return null;
                })
                .collect(Collectors.toList());
        List<String> rez = new ArrayList<>();
        rez.addAll(prietenii);
        rez.addAll(mesaje);
        return rez;
    }

    public List<String> rapoarteMesaje(LocalDate date1, LocalDate date2, Long id){
        List<String> mesaje = StreamSupport.stream(getConversationWith(id).spliterator(),false)
                .filter(x->(x.getDate().isAfter((LocalDateTime)date1.atTime(0,0))||x.getDate().isBefore((LocalDateTime)date2.atTime(23,59))))
                .map(x-> {
                    if (x.getSender().equals(id)) {
                        User u = repo.findOne(x.getSender()).get();
                        return u.getFirstName() + " " + u.getLastName() + ": "
                                + x.getMessage();
                    }

                    return loggedUser.getFirstName() + " " + loggedUser.getLastName() + ": "
                            + x.getMessage();
                })
                .collect(Collectors.toList());
        return mesaje;
    }
}
