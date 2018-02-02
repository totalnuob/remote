package kz.nicnbk.common.service.model;

/**
 * Created by magzumov on 09.10.2017.
 */
public class HierarchicalBaseDictionaryDto extends BaseDictionaryDto {
    private HierarchicalBaseDictionaryDto parent;

    public HierarchicalBaseDictionaryDto getParent() {
        return parent;
    }

    public void setParent(HierarchicalBaseDictionaryDto parent) {
        this.parent = parent;
    }
}
