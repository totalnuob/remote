package kz.nicnbk.repo.model.markers;

public interface Creator<T> {

    T getCreator();

    void setCreator(T creator);

}
