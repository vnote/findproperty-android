package com.cetnaline.findproperty.api.request;

import com.cetnaline.findproperty.entity.event.UploadEvent;
import com.cetnaline.findproperty.utils.RxBus;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 下载文件
 * Created by diaoqf on 2017/5/25.
 */

public class UploadFileBody extends RequestBody {

    private RequestBody requestBody;
    private BufferedSink bufferedSink;

    private String uploadTag;

    public UploadFileBody(RequestBody requestBody, String uploadTag) {
        super();
        this.requestBody = requestBody;
        this.uploadTag = uploadTag;
    }

    @Override
    public long contentLength() throws IOException {
//        long slength = 0;
//        try {
//            slength = super.contentLength();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Math.min(requestBody.contentLength(), slength);
        return  requestBody.contentLength();
    }

    @Override
    public MediaType contentType() {

        return requestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
//        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
//        }
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }



    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

                if (contentLength == 0) {
                    contentLength = requestBody.contentLength();
                }
                bytesWritten += byteCount;
                //更新进度
                Logger.i("buobao write:"+bytesWritten+","+contentLength);
                RxBus.getDefault().send(new UploadEvent(uploadTag, (int) (bytesWritten*1.0f/contentLength) * 50));
            }
        };
    }
}































