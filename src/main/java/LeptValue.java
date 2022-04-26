public class LeptValue {
    private LeptType leptType;
    private String str;

    public LeptValue() {
    }

    public void setLeptType(LeptType leptType) {
        this.leptType = leptType;
    }

    public LeptType getLeptType() {
        return leptType;
    }

    public double getLeptNumber() {
        if (leptType != LeptType.LEPT_NUMBER) {
            return Double.NaN;
        }
        return Double.parseDouble(str);
    }

    public void setLeptDouble(String value) {
        this.str = value;
    }
}
