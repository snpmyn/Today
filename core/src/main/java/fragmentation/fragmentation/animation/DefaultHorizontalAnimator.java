package fragmentation.fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import com.zsp.core.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @decs: DefaultHorizontalAnimator
 * @author: 郑少鹏
 * @date: 2019/5/20 9:23
 */
public class DefaultHorizontalAnimator extends FragmentAnimator implements Parcelable {
    public static final Creator<DefaultHorizontalAnimator> CREATOR = new Creator<>() {
        @Contract("_ -> new")
        @Override
        public @NotNull DefaultHorizontalAnimator createFromParcel(Parcel in) {
            return new DefaultHorizontalAnimator(in);
        }

        @Contract(value = "_ -> new", pure = true)
        @Override
        public DefaultHorizontalAnimator @NotNull [] newArray(int size) {
            return new DefaultHorizontalAnimator[size];
        }
    };

    private DefaultHorizontalAnimator(Parcel in) {
        super(in);
    }

    public DefaultHorizontalAnimator() {
        enter = R.anim.fragmentation_horizontal_fragment_enter;
        exit = R.anim.fragmentation_horizontal_fragment_exit;
        popEnter = R.anim.fragmentation_horizontal_fragment_pop_enter;
        popExit = R.anim.fragmentation_horizontal_fragment_pop_exit;
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