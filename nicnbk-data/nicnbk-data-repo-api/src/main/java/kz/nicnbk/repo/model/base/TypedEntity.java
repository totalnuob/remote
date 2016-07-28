package kz.nicnbk.repo.model.base;

/**
 * A base interface to represent entities which can have different types.
 *
 * @param <T> class, that used to represent type of the entity
 * @author magzumov
 */
public interface TypedEntity<T extends BaseTypeEntity>{

    T getType();

    void setType(T type);

}
