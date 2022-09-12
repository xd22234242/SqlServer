package com.example.sqlserver.ControlConfig;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumberTextField extends PlainDocument {
    public NumberTextField() {
        super();
    }

    public void insertString (int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }

        char[] s = str.toCharArray();
        int length = 0;
        for (int i = 0; i < s.length; i++) {
            //判断-号
            if (i == 0 && s[i] == '-' && offset == 0) {
                s[length++] = s[i];
                continue;
            }

            //过滤非数字
            if((s[i] >= '0') && (s[i] <= '9') || s[i] == '.') {
                s[length++] = s[i];
                continue;
            }
        }

        //插入内容
        super.insertString(offset, new String(s, 0, length), attr);
    }

    public int charRepeatCount(String str, char c) {
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }

        return count;
    }
}
