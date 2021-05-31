package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlocui solutia propusa cu un Factory (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sau cu lambda - curs 4, sem 4 si 5
//        Path path = Paths.get(fileName);
//        try {
//            List<String> lines = Files.readAllLines(path);
//            lines.forEach(linie -> {
//                E entity=extractEntity(Arrays.asList(linie.split(";")));
//                super.save(entity);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes - attributes of entity
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    @Override
    public Optional<E> save(E entity){
        Optional<E> optional = super.save(entity);
        if (optional.isPresent()) {
            return optional;
        }
        writeToFile(entity);
        return Optional.empty();
    }

    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<E> delete(ID id){
        Optional<E> optional = super.delete(id);
        updateFile();
        return optional;
    }

    protected void updateFile(){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName))) {
            for(E entity : findAll()){
                bW.write(createEntityAsString(entity));
                bW.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

