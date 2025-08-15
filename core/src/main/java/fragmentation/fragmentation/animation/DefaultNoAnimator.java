package fragmentation.fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @decs: DefaultNoAnimator
 * @author: 郑少鹏
 * @date: 2019/5/20 9:23
 */
public class DefaultNoAnimator extends FragmentAnimator implements Parcelable {
    public static final Creator<DefaultNoAnimator> CREATOR = new Creator<>() {
        @Contract("_ -> new")
        @Override
        public @NotNull DefaultNoAnimator createFromParcel(Parcel in) {
            return new DefaultNoAnimator(in);
        }

        @Contract(value = "_ -> new", pure = true)
        @Override
        public DefaultNoAnimator @NotNull [] newArray(int size) {
            return new DefaultNoAnimator[size];
        }
    };

    public DefaultNoAnimator() {
        enter = 0;
        exit = 0;
        popEnter = 0;
        popExit = 0;
    }

    private DefaultNoAnimator(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}