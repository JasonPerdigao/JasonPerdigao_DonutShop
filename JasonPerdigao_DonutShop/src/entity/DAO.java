package entity;
import java.util.*;
public interface DAO<T>{Optional<T> get(int id);List<T> getAll();void insert(T value);void update(T value);void delete(T value);List<String> getColumnNames();}
