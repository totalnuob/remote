package kz.nicnbk.repo.model.lookup;

public enum PortfolioVarLookup {

    VAR95("1M VaR 95%"),
    VAR99("1M VaR 99%");

    private String code;

    PortfolioVarLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
