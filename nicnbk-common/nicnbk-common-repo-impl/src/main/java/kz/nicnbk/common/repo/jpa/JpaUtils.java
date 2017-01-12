package kz.nicnbk.common.repo.jpa;

import org.hibernate.annotations.QueryHints;

import javax.persistence.TypedQuery;
import java.util.List;


@Deprecated
public class JpaUtils {
    private JpaUtils() {
    }

    public static <T> T singleResultOrNull(TypedQuery<T> query) {
        // Apply caching
        query.setHint(QueryHints.CACHEABLE, Boolean.TRUE);
        List<T> resultList = query.getResultList();
        return (resultList.isEmpty() || resultList.size() > 1) ? null : resultList.get(0);
    }

    public static <T> List<T> multipleResult(TypedQuery<T> query) {
        // Apply caching
        query.setHint(QueryHints.CACHEABLE, Boolean.TRUE);
        List<T> resultList = query.getResultList();
        return resultList;
    }
}
