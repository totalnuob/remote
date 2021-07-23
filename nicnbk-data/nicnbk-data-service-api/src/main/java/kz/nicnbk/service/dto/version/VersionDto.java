package kz.nicnbk.service.dto.version;

import java.util.List;

/**
 * Created by Pak on 08.11.2019.
 */

public class VersionDto implements Comparable<VersionDto> {

    private List<String> numFmt;

    private List<String> description;

    public VersionDto(List<String> numFmt, List<String> description) {
        this.numFmt = numFmt;
        this.description = description;
    }

    public List<String> getNumFmt() {
        return numFmt;
    }

    public void setNumFmt(List<String> numFmt) {
        this.numFmt = numFmt;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public int compareTo(VersionDto other) {
        if(this.getVersionNumberScore() > 0 && other.getVersionNumberScore() < 0){
            return 1;
        }else if(this.getVersionNumberScore() < 0 && other.getVersionNumberScore() > 0) {
            return -1;
        }else{
            return this.getVersionNumberScore() - other.getVersionNumberScore();
        }
        //return this.description.get(0).compareTo(dto.getDescription().get(0)); }
    }

    public int getVersionNumberScore(){
        if (this.description != null && this.description.get(0) != null) {
            String versionName = this.description.get(0);
            if(versionName.startsWith("Версия:")){
                String versionNum = versionName.substring(7).trim();
                String[] versionNums = versionNum.split("\\.");
                if(versionNums.length == 3){
                    return Integer.parseInt(versionNums[0]) * 1000 + Integer.parseInt(versionNums[1]) * 100 +
                            Integer.parseInt(versionNums[2]) * 10;
                }
            }
        }
        return -1;
    }
}
