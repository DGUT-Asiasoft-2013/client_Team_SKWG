package inputcells;

/**
 * @author 刘世杰
 * 几乎每次都要用到的方法把他提取出来作为其他Fragment类的父类
 * 使得继承他的子类都必须要实现这些方法,否则报错.
 */

import android.app.Fragment;

public abstract class BaseInputCellFragment extends Fragment {
	public abstract void setLabelText(String labelText);
	public abstract void setHintText(String hintText);
}
