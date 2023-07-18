package com.framework.baseFunctions.baseInterface;

import java.awt.*;
import java.io.IOException;

/**
 * Interface for BaseFunctionClass
 */
public interface BaseFunctionsInterface {


    void start_chrome_driver();

    void save_data();

    void go_to_value();

    void scroll_to_element();

    void anchor_go_to_value();

    void go_to_sheet();

    void start_mobile_driver();

    void switch_driver();

    void switch_driver_to();

    void start_kobiton_driver();

    void screenshot();

    void screenshot_by_object();

    void screenshot_full_part();

    void screenshot_full_whole();

    void activated_dev_tools();

    void click();

    void double_click();

    void right_clicK();

    void tap_by_coordinate();

    void hover_to_element();

    void hover_and_click();

    void wait_until_web_element_exist();

    void wait_until_web_element_gone();

    void get_text();

    void get_value();

    void select();

    void go_to_url();

    void click_replace();

    void set_text();

    void change_iframe();

    void change_iframe_default();

    void wait_for_seconds();

    void for_data_by_sheet();

    void end_data_for_sheet();

    void key_press_mobile();

    void input_text() throws InterruptedException, AWTException;

    void send_key();

    void screenshot_ahk() throws InterruptedException, IOException;

    void screenshot_desktop();

    void activate_bds() throws IOException, InterruptedException;

    void login_bds();

    void logout_bds();

    void get_windows_pop_up();

    void change_windows_pop_up();

}
