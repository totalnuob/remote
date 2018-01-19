package kz.nicnbk.service.dto.common;

import java.util.Date;

/**
 * Dto class for entity save response.
 *
 * Created by magzumov.
 */
public class EntitySaveResponseDto extends ResponseDto {

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
