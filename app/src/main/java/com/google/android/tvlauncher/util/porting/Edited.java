package com.google.android.tvlauncher.util.porting;

/**
 * Created by rockon999 on 2/23/18.
 */

public @interface Edited {
    Reason[] reason();

    String comment() default "";
}
