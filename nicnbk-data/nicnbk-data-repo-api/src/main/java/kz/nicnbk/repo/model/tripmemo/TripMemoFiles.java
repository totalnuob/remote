package kz.nicnbk.repo.model.tripmemo;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by zhambyl on 04-Aug-16.
 */

@Entity(name = "trip_memo_files")
public class TripMemoFiles extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="trip_memo_id", nullable = false)
    private TripMemo tripMemo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="file_id", nullable = false)
    private Files file;

    public TripMemoFiles(){}

    public TripMemoFiles(Long tripMemoId, Long fileId){
        TripMemo tripMemo = new TripMemo();
        tripMemo.setId(tripMemoId);
        Files file = new Files();
        file.setId(fileId);
        this.tripMemo = tripMemo;
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="trip_memo_id", nullable = false)
    public TripMemo getTripMemo() {
        return tripMemo;
    }

    public void setTripMemo(TripMemo tripMemo) {
        this.tripMemo = tripMemo;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="file_id", nullable = false)
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }
}
