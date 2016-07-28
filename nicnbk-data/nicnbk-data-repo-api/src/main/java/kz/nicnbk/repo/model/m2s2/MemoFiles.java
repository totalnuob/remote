package kz.nicnbk.repo.model.m2s2;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.files.Files;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by magzumov on 05.07.2016.
 */
@Entity(name="memo_files")
public class MemoFiles extends BaseEntity{

    private MeetingMemo memo;

    private Files file;

    public MemoFiles(){}

    public MemoFiles(Long memoId, Long fileId){
        MeetingMemo memo = new MeetingMemo();
        memo.setId(memoId);
        this.memo = memo;
        Files file = new Files();
        file.setId(fileId);
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memo_id", nullable = false)
    public MeetingMemo getMemo() {
        return memo;
    }

    public void setMemo(MeetingMemo memo) {
        this.memo = memo;
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
