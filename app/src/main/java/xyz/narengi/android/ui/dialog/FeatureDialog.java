//package xyz.narengi.android.ui.dialog;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.AppCompatDialog;
//import android.support.v7.widget.AppCompatButton;
//import android.support.v7.widget.AppCompatImageView;
//import android.support.v7.widget.AppCompatTextView;
//import android.view.View;
//
//import com.echo.echomybiz.R;
//import com.echo.echomybiz.StaticFields;
//import com.echo.echomybiz.appInterface.DialogCallBack;
//import com.echo.echomybiz.view.activity.AuthenticationActivity;
//import com.pixplicity.easyprefs.library.Prefs;
//
//public class FeatureDialog extends AppCompatDialog implements View.OnClickListener {
//	public static final int SERVAY_FINISH = 21312;
//	public static final int CHOOSE_REVIEW = 12312;
//	private AppCompatTextView title;
//	private AppCompatTextView description;
//	private AppCompatImageView icon;
//	private AppCompatButton button;
//
//	private String titleText;
//	private String descriptionText;
//	private int code;
//	private Activity activity;
//
//	public FeatureDialog(Activity activity, String title, String description, int code) {
//		super(activity);
//		this.titleText = title;
//		this.descriptionText = description;
//		this.code = code;
//		this.activity = activity;
//	}
//
//
//	public FeatureDialog(Activity activity, String title, String description, int code) {
//		super(activity);
//		this.titleText = title;
//		this.descriptionText = description;
//		this.code = code;
//		this.activity = activity;
//		this.dialogCallBack = dialogCallBack;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//		setContentView(R.layout.dialog_alert);
//
//		title = (AppCompatTextView) findViewById(R.id.alert_title);
//		icon = (AppCompatImageView) findViewById(R.id.alert_icon);
//		description = (AppCompatTextView) findViewById(R.id.alert_description);
//		button = (AppCompatButton) findViewById(R.id.alert_button);
//
//		if (code == SERVAY_FINISH) {
//			icon.setImageResource(R.drawable.icon_earned_points);
//		} else if (code == CHOOSE_REVIEW) {
//			icon.setImageResource(R.drawable.icon_accept);
//		} else if (code >= 500) {
//			descriptionText = getContext().getString(R.string.error_connection);
//		}
//
//		title.setText(titleText);
//		description.setText(descriptionText);
//
//		button.setOnClickListener(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		dismiss();
//		if (dialogCallBack != null) {
//			dialogCallBack.onOkClick();
//		}
//
//		if (code == 299) {
//			((AppCompatActivity) activity).getSupportFragmentManager().popBackStack();
//		}
//		if (code == 416) {
//			Prefs.putString(StaticFields.PREF_ACCESS_TOKEN, "");
//			Prefs.putString(StaticFields.PREF_EXPIRES_IN, "");
//			Prefs.putString(StaticFields.PREF_REFRESH_TOKEN, "");
//			Prefs.putString(StaticFields.PREF_SCOPE, "");
//			Prefs.putString(StaticFields.PREF_TOKEN_TYPE, "");
//			Prefs.putBoolean(StaticFields.PREF_ACTIVE_USER, false);
//			getContext().startActivity(new Intent(getContext(), AuthenticationActivity.class));
//			activity.finish();
//		}
//	}
//}
