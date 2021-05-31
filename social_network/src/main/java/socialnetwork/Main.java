package socialnetwork;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.LoginCredentials;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.LoginCredentialsValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDatabaseRepository;
import socialnetwork.repository.database.LoginCredentialsDatabaseRepository;
import socialnetwork.repository.database.MessageDatabaseRepository;
import socialnetwork.repository.database.UserDatabaseRepository;
import socialnetwork.service.UserService;
import socialnetwork.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}


