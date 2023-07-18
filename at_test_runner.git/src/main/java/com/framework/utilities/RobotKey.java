package com.framework.utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class RobotKey {
    protected static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * pressing a single key using robot
     * pressing SHIFT key first if want to press upper case alphabet/second option in keyboard button
     * @param strKey the key that want to press
     */
    public static void keyPress(String strKey) {
        if (strKey.equals("@")){
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_2);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_2);
        } else if (strKey.equals(",")) {
            robot.keyPress(KeyEvent.VK_COMMA);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_COMMA);
        } else if (strKey.equals(".")) {
            robot.keyPress(KeyEvent.VK_PERIOD);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_PERIOD);
        } else if (strKey.equals("_")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_MINUS);
        } else if (strKey.equals("-")) {
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.delay(100);
            robot.keyRelease(KeyEvent.VK_MINUS);
        } else if (Character.isDigit(strKey.charAt(0)) || Character.isLetter(strKey.charAt(0))) {
            if (isUpperCase(strKey)) {
                int intKeyCode = KeyStroke.getKeyStroke(strKey.toUpperCase()).getKeyCode();
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(intKeyCode);
                robot.delay(50);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.keyRelease(intKeyCode);
            } else {
                int intKeyCode = KeyStroke.getKeyStroke(strKey.toUpperCase()).getKeyCode();
                robot.keyPress(intKeyCode);
                robot.delay(50);
                robot.keyRelease(intKeyCode);
            }
        } else {
            symbolKeyPress(strKey);
        }
    }

    /**
     * pressing 2 keys simultaneously
     * pressing the fisrt key (strHeldKey) - hold it, then pressing the second key (strKey)
     * @param strHeldKey first key
     * @param strKey second key
     */
    public static void keyPressWith (String strHeldKey, String strKey) {
        int intHeldKeyCode = KeyStroke.getKeyStroke(strHeldKey.toUpperCase()).getKeyCode();
        int intKeyCode = KeyStroke.getKeyStroke(strKey.toUpperCase()).getKeyCode();

        robot.keyPress(intHeldKeyCode);
        robot.keyPress(intKeyCode);
        robot.delay(50);
        robot.keyRelease(intHeldKeyCode);
        robot.keyRelease(intKeyCode);
    }

    /**
     * pressing 3 keys simultaneously
     * pressing the fisrt key (strHeldKey) - hold it, pressing the second key (strHeldKey1) - hold it, and then pressing the third key (strKey)
     * @param strHeldKey first key
     * @param strHeldKey1 second key
     * @param strKey third key
     */
    public static void multipleKeyPress (String strHeldKey, String strHeldKey1, String strKey) {
        int intHeldKeyCode = KeyStroke.getKeyStroke(strHeldKey.toUpperCase()).getKeyCode();
        int intHeldKey1Code = KeyStroke.getKeyStroke(strHeldKey1.toUpperCase()).getKeyCode();
        int intKeyCode = KeyStroke.getKeyStroke(strKey.toUpperCase()).getKeyCode();

        robot.keyPress(intHeldKeyCode);
        robot.keyPress(intHeldKey1Code);
        robot.keyPress(intKeyCode);
        robot.delay(50);
        robot.keyRelease(intHeldKeyCode);
        robot.keyRelease(intHeldKey1Code);
        robot.keyRelease(intKeyCode);
    }

    /**
     * Click mouse button
     * @param strValue position of button that will be clicked
     * @throws InterruptedException
     */
    public static void mousePress(String strValue) throws InterruptedException {
        strValue = strValue.toUpperCase();

        switch (strValue) {
            case "LEFT":
                robot.mousePress(InputEvent.BUTTON1_MASK);
                Thread.sleep(50);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case "MIDDLE":
                robot.mousePress(InputEvent.BUTTON2_MASK);
                Thread.sleep(50);
                robot.mouseRelease(InputEvent.BUTTON2_MASK);
                break;
            case "RIGHT":
                robot.mousePress(InputEvent.BUTTON3_MASK);
                Thread.sleep(50);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
        }
    }

    /**
     * input text using robot
     */
    public static void inputText(String strValue) {
        String[] arr = strValue.split("");
        for (Integer i = 0; i < arr.length; i++) {
            keyPress(arr[i]);
        }
    }

    /**
     * conditional statements -> check if key are a single alphabet and upper case
     * @param strValue key that want to press
     * @return true or false based on key condition
     */
    private static boolean isUpperCase(String strValue){
        char[] charValue = strValue.toCharArray();
        if(charValue.length==1 && Character.isUpperCase(charValue[0])) {
            return true;
        }
        return false;
    }

    /**
     * pressing symbol character key using robot
     * @param strKey
     * @throws AWTException
     */
    private static void symbolKeyPress(String strKey) {
        if (strKey.equalsIgnoreCase("+")) {
            robot.keyPress(KeyEvent.VK_ADD);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_ADD);
        } else if (strKey.equalsIgnoreCase(",")) {
            robot.keyPress(KeyEvent.VK_COMMA);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_COMMA);
        } else if (strKey.equalsIgnoreCase(".")) {
            robot.keyPress(KeyEvent.VK_PERIOD);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_PERIOD);
        } else if (strKey.equalsIgnoreCase("<")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_COMMA);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_COMMA);
        } else if (strKey.equals(">")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_PERIOD);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_PERIOD);
        } else if (strKey.equals("!")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_1);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_1);
        } else if (strKey.equals("@")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_2);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_2);
        } else if (strKey.equals("#")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_3);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_3);
        } else if (strKey.equals("$")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_4);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_4);
        } else if (strKey.equals("%")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_5);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_5);
        } else if (strKey.equals("^")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_6);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_6);
        } else if (strKey.equals("&")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_7);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_7);
        } else if (strKey.equals("*")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_8);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_8);
        } else if (strKey.equals("(")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_9);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_9);
        } else if (strKey.equals(")")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_0);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_0);
        } else if (strKey.equals("_")) {
            robot.keyPress(KeyEvent.VK_UNDERSCORE);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_UNDERSCORE);
        } else if (strKey.equals("=")) {
            robot.keyPress(KeyEvent.VK_EQUALS);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_EQUALS);
        } else if (strKey.equals(";")) {
            robot.keyPress(KeyEvent.VK_SEMICOLON);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SEMICOLON);
        } else if (strKey.equals("/")) {
            robot.keyPress(KeyEvent.VK_SLASH);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SLASH);
        } else if (strKey.equals("?")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_SLASH);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_SLASH);
        } else if (strKey.equals("[")) {
            robot.keyPress(KeyEvent.VK_OPEN_BRACKET);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
        } else if (strKey.equals("]")) {
            robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
        } else if (strKey.equals("~")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_BACK_QUOTE);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
        } else if (strKey.equals("'")) {
            robot.keyPress(KeyEvent.VK_QUOTE);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_QUOTE);
        } else if (strKey.equals("\"")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_QUOTE);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_QUOTE);
        } else if (strKey.equals("|")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_BACK_SLASH);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_BACK_SLASH);
        } else if (strKey.equals("-")) {
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_MINUS);
        } else if (strKey.equals(" ")) {
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SPACE);
        } else if (strKey.equals("_")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } else if (strKey.equals(":")) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_SEMICOLON);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_SEMICOLON);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } else if (strKey.equals("\\")) {
            robot.keyPress(KeyEvent.VK_BACK_SLASH);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_BACK_SLASH);
        }
    }
}