package socialnetwork.ui;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class UserInterface {
    private final UserService srv;

    public UserInterface(UserService srv) {
        this.srv = srv;
    }
    public void menu(){
        System.out.println("0.Exit\n1.Add user\n2.Remove user\n" +
                "3.Add friend\n4.Remove friend\n5.Number of communities\n6.Most sociable community" +
                "\n7.Show friends of\n8.Friends by month\n9.My friend requests\n" +
                "10.Conversation");
    }
    public void run(){
        int option=-1;
        String opt;
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        while(option!=0){
            srv.getAll().forEach(System.out::println);
            srv.getFriendships().forEach(System.out::println);
            menu();
            try {
                System.out.println("Choose an option: ");
                opt = reader.readLine();
                try {
                    option = Integer.parseInt(opt);
                }
                catch(Exception e){
                    System.out.println("Invalid command");
                }
                switch (option){
                    case 1: addUser();break;
                    case 2: removeUser();break;
                    case 3: addFriend();break;
                    case 4: removeFriend();break;
                    case 5: System.out.println(srv.communities());break;
                    case 6:System.out.println(srv.sociability());break;
                    case 7: showMyFriends();break;
                    case 8: showFriendsByMonthOfFriendship();break;
                    case 9: showMyFriendRequests();break;
                    case 10: conversation();break;
                    default: break;
                }
            }
            catch(IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    private void conversation() {
        long id;
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("ID: ");
            id = Long.parseLong(reader.readLine());
            int opt=9;
            while(opt!=0) {
                System.out.println("1.Send message\n2.Conversations\n0.Back");
                opt=Integer.parseInt(reader.readLine());
                if(opt==1){
                    System.out.println("Insert message");
                    String messageText=reader.readLine();
                    System.out.println("Send the message to:");
                    String recv = reader.readLine();
                    List<Long> listUsers=new ArrayList<>();
                    for(String user:recv.split(" "))
                        listUsers.add(Long.parseLong(user));
                    srv.sendMessage(id,listUsers,messageText);
                }
                if(opt==2){
                    srv.getMyConversations(id).forEach(System.out::println);
                    System.out.println("Choose one:");
                    Long conv = Long.parseLong(reader.readLine());
                    int conv_opt=9;
                    while(conv_opt!=0){
                        srv.getConversationOf(id,conv).forEach(System.out::println);
                        System.out.println("1.Reply\n0.Back");
                        conv_opt= Integer.parseInt(reader.readLine());
                        if(conv_opt==1){
                            System.out.println("Insert message");
                            String messageText = reader.readLine();
                            srv.replyMessage(id, conv, messageText);
                        }
                    }
                }
            }
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
        catch (ValidationException e){
            System.out.println(e.toString());
        }
    }

    private void showMyFriendRequests() {
        long id;
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("ID: ");
            id = Long.parseLong(reader.readLine());
            int ok=9;
            while(ok>0){
                srv.showFriendRequests(id).forEach(System.out::println);
                System.out.println("0.Back\n1.Accept friendship\n2.Reject friendship");
                System.out.println("Command: ");
                ok = Integer.parseInt(reader.readLine());
                if(ok == 1){
                    System.out.println("id:");
                    srv.updateFriendship(id,Long.parseLong(reader.readLine()),"ACCEPTED");
                }
                if(ok == 2){
                    System.out.println("id:");
                    srv.updateFriendship(id,Long.parseLong(reader.readLine()),"Rejected");
                }
            }
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
        catch (ValidationException e){
            System.out.println(e.toString());
        }
    }

    private void addUser(){
        String fn,ln;
        long id;
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("ID: ");
            id = Long.parseLong(reader.readLine());
            System.out.println("First name: ");
            fn = reader.readLine();
            System.out.println("Last name: ");
            ln = reader.readLine();
            User u = new User(fn,ln);
            u.setId(id);
            if(srv.addUser(u)==null)
                System.out.println("User added");
            else
                System.out.println("ID already existing");
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
        catch (ValidationException e){
            System.out.println(e.toString());
        }
    }

    private void removeUser(){
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        long id_p;
        try{
            System.out.println("ID User: ");
            String s;
            s=reader.readLine();
            id_p = Long.parseLong(s);
            if(srv.removeUser(id_p)!=null)
                System.out.println("User deleted");
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
    }

    private void addFriend(){
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        long id_p1, id_p2;
        try{
            System.out.println("ID User1: ");
            String s1;
            s1=reader.readLine();
            id_p1 = Long.parseLong(s1);

            System.out.println("ID User2: ");
            String s2;
            s2=reader.readLine();
            id_p2 = Long.parseLong(s2);
            if(!srv.addFriendship(id_p1,id_p2).isPresent())
                System.out.println("Friendship added");
            else
                System.out.println("Friendship already exists");
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
        catch(ValidationException e){
            System.out.println(e.toString());
        }
    }
    private void removeFriend(){
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        long id_p1, id_p2;
        try{
            System.out.println("ID User 1: ");
            String s;
            s=reader.readLine();
            id_p1 = Long.parseLong(s);

            System.out.println("ID User 2: ");
            String s2;
            s2=reader.readLine();
            id_p2 = Long.parseLong(s2);
            srv.removeFriendship(id_p1,id_p2);
            System.out.println("Friendship removed");
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
    }

    private void showMyFriends(){
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        long id;
        try {
            System.out.println("ID User: ");
            String s;
            s = reader.readLine();
            id = Long.parseLong(s);
            srv.getFriendsOf(id).forEach(System.out::println);
        }catch(IOException e){
            System.out.println(e.toString());
        }
    }

    private void showFriendsByMonthOfFriendship(){
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        long id;
        int month;
        try {
            System.out.println("ID User: ");
            String s;
            s = reader.readLine();
            id = Long.parseLong(s);

            System.out.println("Month: ");
            String s2;
            s2 = reader.readLine();
            month = Integer.parseInt(s2);
            srv.getFriendsByMonth(id,month).forEach(System.out::println);
        }catch(IOException e){
            System.out.println(e.toString());
        }
    }
}
