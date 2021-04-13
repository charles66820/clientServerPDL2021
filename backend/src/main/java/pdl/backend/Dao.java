package pdl.backend;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
  
  void create(final T t);

  Optional<T> retrieve(final long id);

  List<T> retrieveAll();

  void update(final T t, final String[] params);

  void delete(final T t);
}
