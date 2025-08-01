package fragmentation.fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import com.zsp.core.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @decs: DefaultVerticalAnimator
 * @author: 郑少鹏
 * @date: 2019/5/20 9:23
 */
public class DefaultVerticalAnimator extends FragmentAnimator implements Parcelable {
    public static final Creator<DefaultVerticalAnimator> CREATOR = new Creator<>() {
        @Contract("_ -> new")
        @Override
        public @NotNull DefaultVerticalAnimator createFromParcel(Parcel in) {
            return new DefaultVerticalAnimator(in);
        }

        @Contract(value = "_ -> new", pure = true)
        @Override
        public DefaultVerticalAnimator @NotNull [] newArray(int size) {
            return new DefaultVerticalAnimator[size];
        }
    };

    private DefaultVerticalAnimator(Parcel in) {
        super(in);
    }

    public DefaultVerticalAnimator() {
        enter = R.anim.fragmentation_veritical_fragment_enter;
        exit = R.anim.fragmentation_veritical__fragment_exit;
        popEnter = R.anim.fragmentation_veritical__fragment_pop_enter;
        popExit = R.anim.fragmentation_veritical__fragment_pop_exit;
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
