package persistence;

public interface IRepository<Entity> {
    void save(Entity entity);
    void delete(Entity entity);
    void update(Entity entity);
    Iterable<Entity> findAll();
    Entity findOne(Long id);
}
