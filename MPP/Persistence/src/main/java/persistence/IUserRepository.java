package persistence;

import model.User;

public interface IUserRepository extends IRepository<User> {
    User findByUsername(String username);
}
