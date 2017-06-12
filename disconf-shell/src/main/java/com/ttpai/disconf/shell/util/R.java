package com.ttpai.disconf.shell.util;

/**
 * Created by Kail on 2017/6/12.
 */
public class R {

    public static class Sout {

        public static void println(Object o) {
            System.out.println(o);
        }

    }

    public static class Serr {

        public static void println(Object o) {
            System.err.println(o);
        }

        public static void interrupt(Object o) {
            println(o);

            System.exit(1);
        }

    }

}
