package widget.dialog.customdialog;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * @decs: BaseViewConvertListener
 * @author: 郑少鹏
 * @date: 2018/4/4 13:47
 */
public abstract class BaseViewConvertListener implements Parcelable {
    public static final Creator<BaseViewConvertListener> CREATOR = new Creator<>() {
        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public BaseViewConvertListener createFromParcel(Parcel source) {
            return new BaseViewConvertListener(source) {
                @Override
                protected void convertView(ViewHolder holder, BaseDialog dialog) {

                }
            };
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public BaseViewConvertListener[] newArray(int size) {
            return new BaseViewConvertListener[size];
        }
    };

    protected BaseViewConvertListener() {

    }

    private BaseViewConvertListener(Parcel parcel) {

    }

    /**
     * xxx
     *
     * @param holder viewHolder
     * @param dialog baseDialog
     */
    protected abstract void convertView(ViewHolder holder, BaseDialog dialog);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }
}