package taoyuan.taipower.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * @author leolin
 */
public class AnalogNumberPicker extends View {
    private static final String LOG_TAG = AnalogNumberPicker.class.getSimpleName();
    private boolean clockwise = true;
    private int degree = 0;
    private ValueChangeListener valueChangeListener;
    private boolean dontSetValueToEditText = false;

    private static final int[] CLOCK_WISE_MAPPING = new int[]{
            0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 0
    };
    private static final int[] COUNTER_CLOCK_WISE_MAPPING = new int[]{
            0, 9, 9, 8, 8, 7, 7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 0
    };

    public AnalogNumberPicker(Context context) {
        super(context);
    }

    public AnalogNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnalogNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AnalogNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(5f);
        blackPaint.setAntiAlias(true);
        blackPaint.setStyle(Paint.Style.STROKE);


        final float height = getHeight();
        final float width = getWidth();
        float fact = 10f;
        final float radius = (height / 2) - fact;

        RectF rect = new RectF(fact, fact, width - fact, height - fact);
        canvas.drawArc(rect, 0, 360, true, blackPaint);


        canvas.drawLine(
                height / 2,
                width / 2,
                height / 2 + (float) (radius * sin(toRadians(degree))),
                width / 2 - (float) (radius * cos(toRadians(degree))),
                blackPaint
        );

        int value = degree / 18;
        if (clockwise) {
            value = CLOCK_WISE_MAPPING[value];
        } else {
            value = COUNTER_CLOCK_WISE_MAPPING[value];
        }

        if (valueChangeListener != null) {
            if (dontSetValueToEditText) {
                dontSetValueToEditText = false;
            } else {
                valueChangeListener.onValueChanged(value);
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float centerX = getHeight() / 2;
        float centerY = getWidth() / 2;
        float x = event.getX();
        float y = event.getY();

        float convertedX = x - centerX;
        float convertedY = -(y - centerY);

        double length = sqrt(
                pow(abs(convertedX), 2) + pow(abs(convertedY), 2)
        );

        double degrees = 0;
        if (convertedX >= 0 && convertedY >= 0) {
            degrees = toDegrees(asin(convertedX / length));
        } else if (convertedX >= 0 && convertedY < 0) {
            degrees = 90 - toDegrees(asin(convertedY / length));
        } else if (convertedX < 0 && convertedY < 0) {
            degrees = 180 - toDegrees(asin(convertedX / length));
        } else {
            degrees = 270 + toDegrees(asin(convertedY / length));
        }

        setDegree((int) degrees);
        return true;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public void setDegree(int degree) {
        this.degree = degree;
        invalidate();
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    public void bindToEditText(final EditText editText) {

        processTextToClock(editText.getText().toString(), editText);

        setValueChangeListener(new ValueChangeListener() {
            @Override
            public void onValueChanged(int value) {
                editText.setText(String.valueOf(value));
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("coutn=" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                processTextToClock(text, editText);
            }
        });
    }

    private void processTextToClock(String text, EditText editText) {
        if (text.isEmpty()) {
            return;
        }
        int value = 0;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {

        }
        if (value >= 10) {
            value = 9;
            editText.setText(String.valueOf(value));
        }
        if (value < 0) {
            value = 0;
            editText.setText(String.valueOf(value));
        }

        int degree = 0;
        if (clockwise) {
            degree = value * 36;
        } else {
            degree = 359 - value * 36;
        }

        setDegree(degree);
        dontSetValueToEditText = true;
    }

    public interface ValueChangeListener {
        void onValueChanged(int value);
    }

}
