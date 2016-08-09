package kz.nicnbk.ws.model;

/**
 * Created by magzumov on 09.08.2016.
 */
public class EntitySaveResponse extends Response {

    private Long entityId;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
