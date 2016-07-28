package kz.nicnbk.repo.model.base;

import kz.nicnbk.repo.model.markers.Creator;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class CreatorBaseEntity extends HistoryBaseEntity implements Creator<Long> {

    // TODO: User
    private Long creator;


    // TODO: User
//    @Override
//    @JoinColumn(name = "InsertedBy", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    public User getCreator() {
//        return creator;
//    }

//    @Override
//    public void setCreator(User creator) {
//        this.creator = creator;
//    }


    @Override
    @Column(name = "InsertedBy")
    public Long getCreator() {
        return creator;
    }

    @Override
    public void setCreator(Long creator) {
        this.creator = creator;
    }

}
