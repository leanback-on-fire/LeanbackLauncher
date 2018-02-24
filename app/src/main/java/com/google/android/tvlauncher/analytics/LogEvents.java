package com.google.android.tvlauncher.analytics;

public abstract interface LogEvents
{
  public static final String ADD_CHANNEL = "add_channel";
  public static final String ADD_PROGRAM_TO_WATCH_NEXT = "add_program_to_watch_next";
  public static final String APP_PLACEMENT_APPS_VIEW = "apps_view";
  public static final String APP_PLACEMENT_CHANNEL_LOGO = "channel_logo";
  public static final String APP_PLACEMENT_HOME_APPS_ROW = "home_apps_row";
  public static final String APP_PLACEMENT_LAST_PROGRAM = "last_program";
  public static final String APP_PLACEMENT_STORE_BUTTON = "store_button";
  public static final String DISMISS_NOTIFICATION = "dismiss_notification";
  public static final String ENTER_CHANNEL_ACTIONS_MODE = "enter_channel_actions_mode";
  public static final String ENTER_EDIT_APPS_MODE = "enter_edit_apps_mode";
  public static final String ENTER_MOVE_CHANNEL_MODE = "enter_move_channel_mode";
  public static final String ENTER_ZOOMED_OUT_MODE = "enter_zoomed_out_mode";
  public static final String EXIT_CHANNEL_ACTIONS_MODE = "exit_channel_actions_mode";
  public static final String EXIT_EDIT_APPS_MODE = "exit_edit_apps_mode";
  public static final String EXIT_MOVE_CHANNEL_MODE = "exit_move_channel_mode";
  public static final String EXIT_ZOOMED_OUT_MODE = "exit_zoomed_out_mode";
  public static final String FAVORITE_APP = "favorite_app";
  public static final String GET_APP_INFO = "get_app_info";
  public static final String HIDE_NOTIFICATION = "hide_notification";
  public static final String MOVE_CHANNEL_DOWN = "move_channel_down";
  public static final String MOVE_CHANNEL_UP = "move_channel_up";
  public static final String NOTIFICATION_PLACEMENT_PANEL = "panel";
  public static final String NOTIFICATION_PLACEMENT_TRAY = "tray";
  public static final String OPEN_APPS_VIEW = "open_apps_view";
  public static final String OPEN_HOME = "open_home";
  public static final String OPEN_INPUTS_VIEW = "open_inputs_view";
  public static final String OPEN_MANAGE_APP_CHANNELS = "open_manage_app_channels";
  public static final String OPEN_MANAGE_CHANNELS = "open_manage_channels";
  public static final String OPEN_NOTIFICATION = "open_notification";
  public static final String OPEN_NOTIFICATION_PANEL = "open_notification_panel";
  public static final String PACKAGE_CATEGORY_APP = "app";
  public static final String PACKAGE_CATEGORY_GAME = "game";
  public static final String[] PACKAGE_NAME_PARAMETERS = { "package_name_01", "package_name_02", "package_name_03", "package_name_04", "package_name_05", "package_name_06", "package_name_07", "package_name_08", "package_name_09", "package_name_10" };
  public static final String PARAMETER_APP_COUNT = "app_count";
  public static final String PARAMETER_APP_PLACEMENT = "placement";
  public static final String PARAMETER_BROWSABLE_CHANNEL_COUNT = "browsable_channel_count";
  public static final String PARAMETER_CHANNEL_COUNT = "channel_count";
  public static final String PARAMETER_COUNT = "count";
  public static final String PARAMETER_GAME_COUNT = "game_count";
  public static final String PARAMETER_HAS_PREVIEW_VIDEO = "has_preview_video";
  public static final String PARAMETER_INDEX = "index";
  public static final String PARAMETER_INPUT_TYPE = "input_type";
  public static final String PARAMETER_IS_LEGACY = "is_legacy";
  public static final String PARAMETER_NOTIFICATION_INDICATOR_NEW = "notification_indicator_new";
  public static final String PARAMETER_NOTIFICATION_INDICATOR_TOTAL = "notification_indicator_total";
  public static final String PARAMETER_NOTIFICATION_KEY = "key";
  public static final String PARAMETER_NOTIFICATION_PLACEMENT = "placement";
  public static final String PARAMETER_PACKAGE_CATEGORY = "package_category";
  public static final String PARAMETER_PACKAGE_NAME = "package_name";
  public static final String PARAMETER_PROGRAM_TYPE = "program_type";
  public static final String PARAMETER_SHOWN_CHANNEL_COUNT = "shown_channel_count";
  public static final String PARAMETER_TRAY_NOTIFICATION_COUNT = "tray_notification_count";
  public static final String PARAMETER_WATCHED_PREVIEW_VIDEO_SECONDS = "watched_preview_video_seconds";
  public static final String REMOVE_CHANNEL = "remove_channel";
  public static final String REMOVE_CHANNEL_FROM_HOME = "remove_channel_from_home";
  public static final String REMOVE_PROGRAM_FROM_CHANNEL = "remove_program_from_channel";
  public static final String REMOVE_PROGRAM_FROM_WATCH_NEXT = "remove_program_from_watch_next";
  public static final String SELECT_INPUT = "select_input";
  public static final String START_APP = "start_app";
  public static final String START_PROGRAM = "start_program";
  public static final String START_SETTINGS = "start_settings";
  public static final String UNFAVORITE_APP = "unfavorite_app";
  public static final String UNINSTALL_APP = "uninstall_app";
  public static final String WATCH_PREVIEW = "watch_preview";
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/analytics/LogEvents.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */