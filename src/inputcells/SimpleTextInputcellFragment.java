package inputcells;

import com.example.bbook.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author 刘世杰 基本输入框控件模板,上面为一个TextView下面为一个EeitText的小控件组 经常能用到
 */
public class SimpleTextInputcellFragment extends BaseInputCellFragment {

	TextView label;
	EditText edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_inputcell_simpleted, container);
		label = (TextView) view.findViewById(R.id.label);
		edit = (EditText) view.findViewById(R.id.edit);
		return view;

	}

	public void setLabelText(String labelText) {
		label.setText(labelText);
	}

	public void setHintText(String hintText) {
		edit.setHint(hintText);
	}

	public String getText() {
		return edit.getText().toString();
	}

	// 此处为设定输入框内的内容是否为密码类型
	public void setEditText(boolean isPassword) {
		if (isPassword) {
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
	}
}
