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
 * @author ������ ���������ؼ�ģ��,����Ϊһ��TextView����Ϊһ��EeitText��С�ؼ��� �������õ�
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

	// �˴�Ϊ�趨������ڵ������Ƿ�Ϊ��������
	public void setEditText(boolean isPassword) {
		if (isPassword) {
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
	}
	
	public void setEditNum(boolean isNum) {
		if (isNum) {
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_CLASS_NUMBER);
		} else {
			edit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		}
	}
}
