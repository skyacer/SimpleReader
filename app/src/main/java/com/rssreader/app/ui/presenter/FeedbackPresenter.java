package com.rssreader.app.ui.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.rssreader.app.commons.util.ResourcesUtil;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.FeedbackUIActivity;
import com.rssreader.app.ui.base.BasePresenter;

/**
 * Created by LuoChangAn on 16/4/5.
 */
public class FeedbackPresenter extends BasePresenter<FeedbackUIActivity> implements View.OnClickListener {

    public FeedbackPresenter(FeedbackUIActivity target) {
        super(target);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_right_text :
            {
                String msg = ((EditText) target.findViewById(R.id.feedback_edit))
                        .getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    ToastUtil.makeShortToast(ResourcesUtil.getString(R.string.please_input_content));
                    return;
                }
                sendEmail(msg);
                target.finish();
            }
                break;
            default:
                break;
        }
    }

    private void sendEmail(String msg){
        Intent intent = new Intent();
        intent.setType("message/rfc822");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ResourcesUtil.getString(R.string.feedback_to_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, ResourcesUtil.getString(R.string.feedback_title));
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        target.startActivity(Intent.createChooser(intent, "sending mail"));
        ((EditText) target.findViewById(R.id.feedback_edit))
                .setText("");
    }

}
