package taoyuan.taipower.base;

import com.parse.ParseObject;

import java.util.Comparator;

/**
 * @author leolin
 */
public class BillComparator implements Comparator<ParseObject>{
    @Override
    public int compare(ParseObject lhs, ParseObject rhs) {
        int lhsYear = lhs.getInt("year");
        int rhsYear = rhs.getInt("year");
        if (lhsYear > rhsYear) {
            return -1;
        } else if (lhsYear < rhsYear) {
            return 1;
        } else {
            int lhsMonth = lhs.getInt("month");
            int rhsMonth = rhs.getInt("month");
            if (lhsMonth > rhsMonth) {
                return -1;
            } else if (lhsMonth < rhsMonth) {
                return 1;
            }
        }

        return 0;
    }
}
