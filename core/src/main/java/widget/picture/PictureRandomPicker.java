package widget.picture;

import com.zsp.core.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created on 2025/9/22.
 *
 * @author 郑少鹏
 * @desc 图片随机选择器
 */
public class PictureRandomPicker {
    private static final List<Integer> PICTURES = Arrays.asList(R.drawable.picture_one, R.drawable.picture_two, R.drawable.picture_three);
    private static final Random RANDOM = new Random();

    /**
     * 获取随机图片
     *
     * @return 随机图片
     */
    public static int getRandomPicture() {
        return PICTURES.get(RANDOM.nextInt(PICTURES.size()));
    }
}