import java.util.List;
import java.util.Map;

public class LeptValue {
    private LeptType leptType;
    private String str;
    private List<LeptValue> list;

    private List<Pair<String, LeptValue>> pairs;

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
        if (leptType == LeptType.LEPT_ARRAY) {
            this.list = list;
        }
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

    public int get_object_size() {
        if (leptType != LeptType.LEPT_OBJECT || pairs == null) {
            return 0;
        }
        return pairs.size();
    }

    public void setPairs(List<Pair<String, LeptValue>> pairs) {
        if (leptType == LeptType.LEPT_OBJECT) {
            this.pairs = pairs;
        }
    }

    public Pair<String, LeptValue> getIndexPair(int index) {
        if (leptType != LeptType.LEPT_OBJECT || pairs == null || index >= pairs.size()) {
            return null;
        }
        return pairs.get(index);
    }

}
