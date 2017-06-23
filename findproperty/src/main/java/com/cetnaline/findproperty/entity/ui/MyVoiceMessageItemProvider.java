package com.cetnaline.findproperty.entity.ui;
import android.content.Context;
import android.view.View;

import com.cetnaline.findproperty.utils.MyUtils;

import java.io.IOException;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.VoiceMessageItemProvider;
import io.rong.message.VoiceMessage;

/**
 * Created by diaoqf on 2017/4/13.
 */
@ProviderTag(messageContent = VoiceMessage.class,showProgress = false)
public class MyVoiceMessageItemProvider  extends VoiceMessageItemProvider {
    public MyVoiceMessageItemProvider(Context context) {
        super(context);
    }

    @Override
    public void bindView(View v, int position, VoiceMessage content, UIMessage message) {
        try {
            if (content != null && content.getUri() != null) {
                long duration = MyUtils.getAmrDuration(content.getUri().getPath());
                content.setDuration((int) duration / 1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.bindView(v, position, content, message);
    }
}
