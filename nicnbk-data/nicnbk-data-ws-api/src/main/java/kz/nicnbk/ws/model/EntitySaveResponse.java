package kz.nicnbk.ws.model;

import java.util.Date;

/**
 * Created by magzumov on 09.08.2016.
 */

@Deprecated
public class EntitySaveResponse extends Response {

    private Long entityId;
    private Date creationDate;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
