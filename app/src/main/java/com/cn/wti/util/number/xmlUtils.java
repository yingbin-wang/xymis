package com.cn.wti.util.number;

import com.cn.wti.util.app.AppUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wyb on 2017/7/7.
 */

public class xmlUtils {

    private static List<String> list = new ArrayList<String>();

    public static List<String> read (InputStream inputstram) {

        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(inputstram);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        if (doc != null) {
            Element rootElm = doc.getRootElement();
            /*Element applicationElement = (Element) rootElm.elements("application").get(0);
            List nodes = applicationElement.elements("activity");
            String className = "";
            for (Iterator it = nodes.iterator(); it.hasNext(); ) {
                Element elm = (Element) it.next();
                className = elm.attribute("name").getValue();
                list.add(className);
            }*/
        }
        return  list;
    }

}
