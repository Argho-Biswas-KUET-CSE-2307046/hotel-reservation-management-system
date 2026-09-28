package hotel.service;

public interface DataService<T> {

    void save(T object);

    void delete(int id);
}