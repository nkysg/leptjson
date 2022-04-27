import java.util.List;

public class LeptValue {
    private LeptType leptType;
    private String str;
    private List<LeptValue> list;

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

    public void setLeptValue(LeptType type, String value) {
        leptType = type;
        this.str = value;
    }

    public String getStr() {
        return str;
    }

    public void setList(List<LeptValue> list) {
        this.list = list;
    }

    public LeptValue getIndexValue(int index) {
        if (leptType != LeptType.LEPT_ARRAY || list == null || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public int get_array_size() {
        if (leptType != LeptType.LEPT_ARRAY || list == null) {
            return 0;
        }
        return list.size();
    }
}
