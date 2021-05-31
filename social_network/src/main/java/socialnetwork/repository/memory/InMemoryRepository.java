package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private final Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        try {
            validator.validate(entity);
        }
        catch(ValidationException e){
            throw e;
        }
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        for(Map.Entry<ID,E> e: entities.entrySet())
            if(id==e.getKey())
                return Optional.ofNullable(entities.remove(id));
        return null;
    }

    @Override
    public Optional<E> update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return Optional.ofNullable(entities.computeIfPresent(entity.getId(), (k, v) -> entity));

    }

}
