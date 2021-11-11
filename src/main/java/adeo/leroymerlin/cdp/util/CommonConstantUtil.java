package adeo.leroymerlin.cdp.util;

public final class CommonConstantUtil {

    private CommonConstantUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String EVENT_BASE_ENDPOINT_PATH = "/api/events";
    public static final String EVENT_FILTER_BY_QUERY_API_PATH = "search/{query}";
    public static final String EVENT_DELETE_BY_ID_PATH = "{id}";
    public static final String EVENT_UPDATE_BY_ID_PATH = "{id}";
}
