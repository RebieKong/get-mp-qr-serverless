package com.rebiekong.tools.mp.lc;

import com.alibaba.fastjson.JSON;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.rebiekong.tools.mp.entry.Event;
import com.rebiekong.tools.mp.entry.Output;
import com.rebiekong.tools.mp.entry.QRRequest;
import com.rebiekong.tools.mp.entry.wx.Error;
import sun.misc.BASE64Encoder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class QrCreator implements StreamRequestHandler {

    private static final int byteReadBuffSize = 4096;
    private Context context;

    private byte[] readIS(InputStream inputStream) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] buf = new byte[QrCreator.byteReadBuffSize];
        int length;
        while ((length = inputStream.read(buf, 0, buf.length)) != -1) {
            data.write(buf, 0, length);
        }
        return data.toByteArray();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        this.context = context;
        Event event = JSON.parseObject(new String(readIS(input)), Event.class);

        String scene = event.pathParameters.get("scene_png").split("\\.")[0];
        String path = event.queryParameters.get("path");

        Output outputBean = getSceneQR(path, scene);
        output.write(JSON.toJSONString(outputBean).getBytes());
    }

    private String getAT() {
        // TODO it need to implement before online
        throw new NotImplementedException();
    }

    private Output getSceneQR(String path, String scene) throws IOException {
        return getSceneQR(path, scene, true, 500);
    }

    private Output getSceneQR(String path, String scene, boolean autoColor, int width) throws IOException {
        Output outputBean = new Output();
        URL uri = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + getAT());
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        QRRequest qrRequest = new QRRequest();
        qrRequest.scene = scene;
        qrRequest.auto_color = autoColor;
        qrRequest.page = path;
        qrRequest.width = width;
        out.writeBytes(JSON.toJSONString(qrRequest));
        out.flush();
        out.close();
        byte[] data = readIS(connection.getInputStream());
        if (connection.getHeaderField("Content-Type").equals("application/json; charset=UTF-8")) {
            Error error = JSON.parseObject(new String(data), Error.class);
            outputBean.setStatusCode(502);
            outputBean.setBody(JSON.toJSONString(error));
            outputBean.setBase64Encoded(false);
            outputBean.addHeader("Content-Type", "application/json; charset=UTF-8");
        } else {
            String body = new BASE64Encoder().encode(data);
            outputBean.setStatusCode(200);
            outputBean.setBody(body);
            outputBean.setBase64Encoded(true);
            outputBean.addHeader("Content-Type", connection.getHeaderField("Content-Type"));
        }
        return outputBean;
    }
}