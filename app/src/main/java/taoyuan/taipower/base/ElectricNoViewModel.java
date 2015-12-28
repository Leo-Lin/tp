package taoyuan.taipower.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import taoyuan.taipower.MainActivity;
import taoyuan.taipower.R;

/**
 * @author leolin
 */
public class ElectricNoViewModel {
    private final SharedPreferences sharedPreferences;
    private TextView textViewOfElectricNo;
    private List<ParseObject> electricNoObjects;
    private int currentElectricNoPosition;
    private ElectricNoSelectedListener electricNoSelectedListener;

    public ElectricNoViewModel(final MainActivity activity, final List<ParseObject> electricNoObjects, ElectricNoSelectedListener electricNoSelectedListener) {
        this.electricNoSelectedListener = electricNoSelectedListener;
        this.textViewOfElectricNo = (TextView) activity.findViewById(R.id.textViewElectricNo);
        this.electricNoObjects = electricNoObjects;

        sharedPreferences = activity.getSharedPreferences("", Context.MODE_PRIVATE);
        currentElectricNoPosition = sharedPreferences.getInt("currentElectricNoPosition", -1);
        activity.findViewById(R.id.electricNoContainer).setOnTouchListener(new OnSwipeTouchListener(activity) {
            @Override
            public void onSwipeRight() {
                triggerSwipeRight();
            }

            @Override
            public void onSwipeLeft() {
                triggerSwipeLeft();
            }
        });

        triggerDisplay();
    }

    private void triggerSwipeRight() {
        currentElectricNoPosition--;
        triggerDisplay();
    }


    private void triggerSwipeLeft() {
        currentElectricNoPosition++;
        triggerDisplay();
    }

    public ParseObject getCurrentElectricNoObject() {
        return electricNoObjects.get(currentElectricNoPosition);
    }

    private void triggerDisplay() {

        if (currentElectricNoPosition == -1) {
            currentElectricNoPosition = 0;
        } else {
            if (currentElectricNoPosition > electricNoObjects.size() - 1) {
                currentElectricNoPosition = electricNoObjects.size() - 1;
            }
        }

        if (!electricNoObjects.isEmpty()) {
            ParseObject parseObject = electricNoObjects.get(currentElectricNoPosition);
            textViewOfElectricNo.setText(parseObject.getString("number"));
            if (electricNoSelectedListener != null) {
                electricNoSelectedListener.call(electricNoObjects.get(currentElectricNoPosition));
            }
        }

        sharedPreferences.edit().putInt("currentElectricNoPosition", currentElectricNoPosition).commit();


    }


    public interface ElectricNoSelectedListener {
        void call(ParseObject electricNo);
    }
}
