package se.kth.infosys.smx.alma.internal;

public final class AlmaMessage {
    public static final class Header {
        public static final String Operation = "almaOperation";
        public static final String Api = "almaApi";
        public static final String UserId = "almaUserId";
    }
    public static final class Operation {
        public static final String Create = "create";
        public static final String Read = "read";
        public static final String Update = "update";
        public static final String Delete = "delete";
        public static final String CreateOrUpdate = "createOrUpdate";
    }
    public static final class Api {
        public static final String Users = "users";
    }
}
