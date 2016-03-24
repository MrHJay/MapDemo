package com.hejie.mapdemo.util;

import com.baidu.mapapi.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * GPX文件解析工具类
 *
 * @author hejie
 *         Created by hejie on 2016/3/24.
 */
public class GPXUtils {

    /**
     * 解析位置信息
     *
     * @param gpxFile GPX文件输入流
     * @return 位置列表
     */
    public static List<LatLng> parseLoc(InputStream gpxFile) {
        ArrayList<LatLng> locList = new ArrayList<>();
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser pullParser = xmlPullParserFactory.newPullParser();
            pullParser.setInput(gpxFile, "UTF-8");

            int eventType = pullParser.getEventType();
            LatLng latLng = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = pullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 开始节点
                        if ("trkpt".equals(nodeName)) {
                            double lat = Double.valueOf(pullParser.getAttributeValue(0));
                            double lng = Double.valueOf(pullParser.getAttributeValue(1));
                            latLng = new LatLng(lat, lng);
//                            Log.i("parse", latLng.toString());
                        }
                        break;
                    case XmlPullParser.END_TAG: // 结束节点
                        if ("trkpt".equals(nodeName)) {
                            locList.add(latLng);
                        }
                        break;
                }

                eventType = pullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locList;
    }
}
