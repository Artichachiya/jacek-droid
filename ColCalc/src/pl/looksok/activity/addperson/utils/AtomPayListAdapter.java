package pl.looksok.activity.addperson.utils;

import java.util.List;

import pl.looksok.R;
import pl.looksok.currencyedittext.CurrencyEditText;
import pl.looksok.currencyedittext.utils.FormatterHelper;
import pl.looksok.logic.AtomPayment;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

public class AtomPayListAdapter extends ArrayAdapter<AtomPayment> {
	protected static final String LOG_TAG = AtomPayListAdapter.class.getSimpleName();

	private List<AtomPayment> items;
	private int layoutResourceId;
	private Context context;

	private OnKeyListener keyboardHiderListener = null;

	public AtomPayListAdapter(Context context, int layoutResourceId, List<AtomPayment> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AtomPaymentHolder holder = null;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new AtomPaymentHolder();
		holder.atomPayment = items.get(position);
		holder.removePaymentButton = (ImageButton)row.findViewById(R.id.atomPay_removePay);
		holder.removePaymentButton.setTag(holder.atomPayment);

		holder.name = (EditText)row.findViewById(R.id.atomPay_name);
		setNameTextChangeListener(holder);
		holder.value = (CurrencyEditText)row.findViewById(R.id.atomPay_value);
		setValueTextListeners(holder);
		
		if(keyboardHiderListener != null){
			holder.name.setOnKeyListener(keyboardHiderListener);
			holder.value.setOnKeyListener(keyboardHiderListener);
		}

		setupItem(holder);
		return row;
	}

	private void setupItem(AtomPaymentHolder holder) {
		holder.name.setText(holder.atomPayment.getName());
		holder.value.setText(FormatterHelper.currencyFormat(holder.atomPayment.getValue(), 2));
	}

	public class AtomPaymentHolder {
		AtomPayment atomPayment;
		EditText name;
		CurrencyEditText value;
		ImageButton removePaymentButton;
	}

	private void setNameTextChangeListener(final AtomPaymentHolder holder) {
		holder.name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				holder.atomPayment.setName(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}

	private void setValueTextListeners(final AtomPaymentHolder holder) {
		holder.value.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try{
					holder.atomPayment.setValue(Double.parseDouble(s.toString()));
				}catch (NumberFormatException e) {
//					Log.d(LOG_TAG, "this is not correct double number (s = " + s + "). It will not be persisted: " + e.getMessage());
					holder.atomPayment.setValue(FormatterHelper.decodeValueFromCurrency(s.toString()));
				}
//				Log.i(LOG_TAG, "s: " + s + ", atomPayment.value: " + holder.atomPayment.getValue());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}

	public List<AtomPayment> getItems() {
		return items;
	}

	public double getTotalPay() {
		double result = 0;
		for (AtomPayment item : items) {
			result += item.getValue();
		}
		return result;
	}

	public void setKeyboardHiderListener(OnKeyListener hideKeyboardListener) {
		this.keyboardHiderListener = hideKeyboardListener;
	}
}
