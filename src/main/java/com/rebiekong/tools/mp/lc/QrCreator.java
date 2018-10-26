package com.rebiekong.tools.mp.lc;

import com.alibaba.fastjson.JSON;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.rebiekong.tools.mp.entry.Output;
import com.rebiekong.tools.mp.entry.QRRequest;
import com.rebiekong.tools.mp.entry.Event;
import sun.misc.BASE64Encoder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class QrCreator implements StreamRequestHandler {

    private static final int readQRBuffSize = 4096;

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        Event event = JSON.parseObject(sb.toString(), Event.class);
        Output outputBean = new Output();
        String scene = event.pathParameters.get("scene_png").split("\\.")[0];
        String path = event.queryParameters.get("path");

        String body = new BASE64Encoder().encode(getSceneQR(path, scene));
        outputBean.setStatusCode(200);
        outputBean.setBody(body);
        outputBean.setBase64Encoded(true);
        output.write(JSON.toJSONString(outputBean).getBytes());
    }

    private String getAT() {
        // TODO it need to implement before online
        throw new NotImplementedException();
    }

    private byte[] getSceneQR(String path, String scene) throws IOException {
        return getSceneQR(path, scene, true, 500);
    }

    private byte[] getSceneQR(String path, String scene, boolean autoColor, int width) throws IOException {
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
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        InputStream in = connection.getInputStream();
        byte[] buf = new byte[QrCreator.readQRBuffSize];
        int length;
        while ((length = in.read(buf, 0, buf.length)) != -1) {
            data.write(buf, 0, length);
        }
        return data.toByteArray();
    }
}