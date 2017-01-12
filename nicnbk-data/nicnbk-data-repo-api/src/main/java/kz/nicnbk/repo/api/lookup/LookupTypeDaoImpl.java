package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.common.repo.jpa.JpaUtils;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import org.hibernate.annotations.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


@Deprecated
@Repository
public class LookupTypeDaoImpl implements LookupTypeDao {
    @PersistenceContext(unitName = "default")
    private EntityManager em;

    private class CQR<T> {
        private CriteriaBuilder critBuilder;
        private CriteriaQuery<T> query;
        private Root<T> root;

        public CQR(Class<T> clazz) {
            critBuilder = em.getCriteriaBuilder();
            query = critBuilder.createQuery(clazz);
            root = query.from(clazz);
        }
    }

    @Override
    public <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code) {
        CQR<T> c = new CQR<T>(clazz);
        c.query.where(c.critBuilder.equal(c.root.get("code"), code));

        return JpaUtils.singleResultOrNull(em.createQuery(c.query).setHint(QueryHints.CACHEABLE, Boolean.TRUE));
    }
}
