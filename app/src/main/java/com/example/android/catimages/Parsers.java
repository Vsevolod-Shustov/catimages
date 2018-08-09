package com.example.android.catimages;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class Parsers {
    public static ArrayList<String> parseXMLForTag(String xml, String tag) {
        try {
            // Create XMLPullParserFactory & XMLPullParser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            // boolean to indicate desired tag has been found
            boolean foundTag = false;
            // variable to fill contents
            ArrayList<String> result = new ArrayList<String>();
            StringBuilder tagContents = new StringBuilder();

            // loop over document
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(tag)) {
                            // Found tag, start appending to tagContents
                            //Log.d("xmlpullparser", "Found tag, start appending to tagContents");
                            foundTag = true;
                        } else if (foundTag) {
                            // New start tag inside desired tag
                            //Log.d("xmlpullparser", "New start tag inside desired tag");
                            tagContents.append("<" + parser.getName() + ">");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(tag)) {
                            // Finished gathering text for tag
                            //Log.d("xmlpullparser", "Finished gathering text for tag");
                            result.add(tagContents.toString());
                            foundTag = false;
                            tagContents.setLength(0);
                        } else if (foundTag) {
                            // end tag inside desired tag
                            //Log.d("xmlpullparser", "end tag inside desired tag");
                            tagContents.append("</" + parser.getName() + ">");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (foundTag) {
                            // text inside desired tag
                            //Log.d("xmlpullparser", "text inside desired tag");
                            tagContents.append(parser.getText());
                        }
                        break;
                }
                // Get next event type
                eventType = parser.next();
            }
            //Log.d("xmlpullparser", result.toString());
            return result;
            //return null;
        } catch (Exception e) {
            return null;
        }
    }
}
